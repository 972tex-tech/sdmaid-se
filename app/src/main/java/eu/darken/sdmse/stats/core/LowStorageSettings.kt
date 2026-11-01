package eu.darken.sdmse.stats.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.sdmse.common.datastore.createValue
import javax.inject.Inject
import javax.inject.Singleton

/** Low-storage values formerly held by the removed Analyzer tool's settings. */
@Singleton
class LowStorageSettings @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val Context.dataStore by preferencesDataStore(name = "settings_lowstorage")

    val dataStore: DataStore<Preferences>
        get() = context.dataStore

    /** `null` means automatic, see [LowStorage.resolveThreshold]. */
    val lowStorageThresholdBytes = dataStore.createValue<Long?>("storage.low.threshold.bytes", null)

    val lowSpaceNotificationEnabled = dataStore.createValue("storage.low.notification.enabled", false)

    /** Runtime latch: `true` means a crossing into the warning band may notify. */
    val lowSpaceNotificationArmed = dataStore.createValue("storage.low.notification.armed", true)
}
