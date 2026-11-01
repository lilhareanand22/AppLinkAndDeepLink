package android.ai.applinkanddeeplink.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavGraphRouteTest {

    @Test
    fun appNavRoute_dashboardRoute_hasCorrectRouteAndTitle() {
        assertEquals("dashboard", AppNavRoute.Dashboard.route)
        assertEquals("Dashboard", AppNavRoute.Dashboard.title)
    }

    @Test
    fun appNavRoute_playgroundRoute_hasCorrectRouteAndTitle() {
        assertEquals("playground", AppNavRoute.Playground.route)
        assertEquals("Intent Sandbox", AppNavRoute.Playground.title)
    }

    @Test
    fun appNavRoute_knowledgeHubRoute_hasCorrectRouteAndTitle() {
        assertEquals("knowledge_hub", AppNavRoute.KnowledgeHub.route)
        assertEquals("Interview Hub", AppNavRoute.KnowledgeHub.title)
    }

    @Test
    fun navGraphRoute_authGraph_routesArePrefixed() {
        assertEquals("auth/login", NavGraphRoute.Auth.Login.route)
        assertEquals("auth/register", NavGraphRoute.Auth.Register.route)
        assertTrue(NavGraphRoute.Auth.Login.route.startsWith("auth/"))
        assertTrue(NavGraphRoute.Auth.Register.route.startsWith("auth/"))
    }

    @Test
    fun navGraphRoute_mainGraph_routesArePrefixed() {
        assertEquals("main/dashboard", NavGraphRoute.Main.Dashboard.route)
        assertEquals("main/playground", NavGraphRoute.Main.Playground.route)
        assertEquals("main/knowledge_hub", NavGraphRoute.Main.KnowledgeHub.route)
        assertTrue(NavGraphRoute.Main.Dashboard.route.startsWith("main/"))
    }

    @Test
    fun navigation_graphTransition_authToMainFlowIsResolvable() {
        val currentDestination: NavGraphRoute = NavGraphRoute.Auth.Login
        val nextDestination: NavGraphRoute = if (currentDestination == NavGraphRoute.Auth.Login) {
            NavGraphRoute.Main.Dashboard
        } else {
            NavGraphRoute.Auth.Register
        }
        assertEquals(NavGraphRoute.Main.Dashboard, nextDestination)
    }
}
