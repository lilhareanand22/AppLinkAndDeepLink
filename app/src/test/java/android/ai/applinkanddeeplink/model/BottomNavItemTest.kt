package android.ai.applinkanddeeplink.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BottomNavItemTest {

    @Test
    fun bottomNavItem_initialization_setsProperties() {
        val item = BottomNavItem(title = "Dashboard", iconEmoji = "📱", route = "dashboard")
        assertEquals("Dashboard", item.title)
        assertEquals("📱", item.iconEmoji)
        assertEquals("dashboard", item.route)
        assertEquals(0, item.badgeCount)
    }

    @Test
    fun bottomNavItem_badgeCount_storesValue() {
        val item = BottomNavItem(title = "Alerts", iconEmoji = "🔔", route = "alerts", badgeCount = 5)
        assertEquals(5, item.badgeCount)
    }

    @Test
    fun bottomNavItem_equality_comparesAllFields() {
        val item1 = BottomNavItem(title = "Home", iconEmoji = "🏠", route = "home")
        val item2 = BottomNavItem(title = "Home", iconEmoji = "🏠", route = "home")
        assertEquals(item1, item2)
        assertEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    fun bottomNavItem_copy_modifiesRouteProperly() {
        val item = BottomNavItem(title = "Sandbox", iconEmoji = "🧪", route = "sandbox")
        val copied = item.copy(route = "new_sandbox", badgeCount = 2)
        assertEquals("Sandbox", copied.title)
        assertEquals("new_sandbox", copied.route)
        assertEquals(2, copied.badgeCount)
        assertNotEquals(item, copied)
    }
}
