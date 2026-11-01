package android.ai.applinkanddeeplink.model

/**
 * BottomNavItem represents an item on the bottom navigation bar.
 */
data class BottomNavItem(
    val title: String,
    val iconEmoji: String,
    val route: String,
    val badgeCount: Int = 0
)
