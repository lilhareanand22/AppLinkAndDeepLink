package android.ai.applinkanddeeplink.navigation

/**
 * Type-safe representation of application navigation graphs and destinations.
 * Supports conditional transitions between Auth Graph and Main Graph.
 */
sealed class NavGraphRoute(val route: String) {
    sealed class Auth(route: String) : NavGraphRoute(route) {
        object Login : Auth("auth/login")
        object Register : Auth("auth/register")
    }

    sealed class Main(route: String) : NavGraphRoute(route) {
        object Dashboard : Main("main/dashboard")
        object Playground : Main("main/playground")
        object KnowledgeHub : Main("main/knowledge_hub")
    }
}
