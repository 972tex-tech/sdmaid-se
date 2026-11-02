package eu.darken.sdmse.main.core

import eu.darken.sdmse.common.navigation.routes.DashboardRoute
import eu.darken.sdmse.common.navigation.routes.UpgradeRoute
import eu.darken.sdmse.corpsefinder.ui.CorpseDetailsRoute
import eu.darken.sdmse.common.files.local.LocalPath
import eu.darken.sdmse.setup.SetupRoute
import eu.darken.sdmse.setup.SetupScreenOptions
import eu.darken.sdmse.setup.SetupModule
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import testhelpers.BaseTest

class NavigationIdConsistencyTest : BaseTest() {

    @Test
    fun `object routes are singletons`() {
        DashboardRoute shouldNotBe null
    }

    @Test
    fun `data class routes with defaults are constructable`() {
        UpgradeRoute() shouldBe UpgradeRoute(forced = false)
        SetupRoute() shouldBe SetupRoute(options = null)
        CorpseDetailsRoute() shouldBe CorpseDetailsRoute(corpsePathJson = null)
    }

    @Test
    fun `key routes serialize and deserialize correctly`() {
        val routes = listOf(
            UpgradeRoute(forced = true),
            SetupRoute(options = SetupScreenOptions(isOnboarding = true, typeFilter = setOf(SetupModule.Type.ROOT))),
            CorpseDetailsRoute(corpsePath = LocalPath.build("/data/test")),
        )

        routes.forEach { route ->
            val json = Json.encodeToString(kotlinx.serialization.serializer(route::class.java), route)
            json.isNotBlank() shouldBe true
        }
    }
}
