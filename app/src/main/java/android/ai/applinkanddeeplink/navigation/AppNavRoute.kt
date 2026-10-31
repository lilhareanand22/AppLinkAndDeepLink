package android.ai.applinkanddeeplink.navigation

sealed class AppNavRoute(val route: String, val title: String) {
    object Dashboard : AppNavRoute("dashboard", "Dashboard")
    object Playground : AppNavRoute("playground", "Intent Sandbox")
    object KnowledgeHub : AppNavRoute("knowledge_hub", "Interview Hub")
}
