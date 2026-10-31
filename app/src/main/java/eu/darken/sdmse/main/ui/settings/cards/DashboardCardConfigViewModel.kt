package eu.darken.sdmse.main.ui.settings.cards

import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.sdmse.common.coroutine.DispatcherProvider
import eu.darken.sdmse.common.datastore.value
import eu.darken.sdmse.common.debug.logging.log
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.uix.ViewModel4
import eu.darken.sdmse.main.core.DashboardCardConfig
import eu.darken.sdmse.main.core.DashboardCardType
import eu.darken.sdmse.main.core.GeneralSettings
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DashboardCardConfigViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val generalSettings: GeneralSettings,
) : ViewModel4(dispatcherProvider, TAG) {

    data class State(
        val entries: List<DashboardCardConfig.CardEntry> = emptyList(),
    )

    val state: StateFlow<State> = generalSettings.dashboardCardConfig.flow
        .map { config -> State(entries = config.cards.filter { it.type !in TRIMMED_TYPES }) }
        .safeStateIn(State()) { State() }

    fun applyReorder(newOrder: List<DashboardCardType>) = launch {
        val current = generalSettings.dashboardCardConfig.value()
        val byType = current.cards.associateBy { it.type }
        val shown = current.cards.filter { it.type !in TRIMMED_TYPES }
        val trimmed = current.cards.filter { it.type in TRIMMED_TYPES }
        val reordered = newOrder.mapNotNull { byType[it] }

        // If reorder does not include all shown types (drag-in-progress snapshot drift), bail out.
        if (reordered.size != shown.size) {
            log(TAG) { "applyReorder($newOrder) ignored: size mismatch with ${shown.size}" }
            return@launch
        }

        if (reordered.map { it.type } == shown.map { it.type }) return@launch

        log(TAG) { "applyReorder(): ${reordered.map { it.type }}" }
        generalSettings.dashboardCardConfig.value(DashboardCardConfig(cards = reordered + trimmed))
    }

    fun toggleVisibility(type: DashboardCardType) = launch {
        val current = generalSettings.dashboardCardConfig.value()
        val newCards = current.cards.map { entry ->
            if (entry.type == type) entry.copy(isVisible = !entry.isVisible) else entry
        }
        log(TAG) { "toggleVisibility($type): ${newCards.first { it.type == type }.isVisible}" }
        generalSettings.dashboardCardConfig.value(DashboardCardConfig(cards = newCards))
    }

    fun resetToDefaults() = launch {
        log(TAG) { "resetToDefaults()" }
        generalSettings.dashboardCardConfig.value(DashboardCardConfig())
    }

    companion object {
        private val TAG = logTag("Dashboard", "CardConfig", "ViewModel")

        // Tools removed from the UI in this build; their config entries are kept but never listed.
        private val TRIMMED_TYPES = setOf(
            DashboardCardType.DEDUPLICATOR,
            DashboardCardType.SWIPER,
            DashboardCardType.SQUEEZER,
            DashboardCardType.ANALYZER,
            DashboardCardType.SCHEDULER,
        )
    }
}
