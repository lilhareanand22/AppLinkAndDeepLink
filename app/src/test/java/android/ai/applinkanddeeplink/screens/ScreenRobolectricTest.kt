package android.ai.applinkanddeeplink.screens

import android.ai.applinkanddeeplink.ui.screens.MainDashboardScreen
import android.ai.applinkanddeeplink.ui.theme.AppLinkAndDeepLinkTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ScreenRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainDashboardScreen_rendersTopAppBarAndTabs() {
        composeTestRule.setContent {
            AppLinkAndDeepLinkTheme {
                MainDashboardScreen(
                    onOpenDeepLinkDemo = {},
                    onOpenAppLinkDemo = {}
                )
            }
        }

        composeTestRule.onNodeWithText("App Links & Deep Links Hub").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Intent Sandbox").assertIsDisplayed()
        composeTestRule.onNodeWithText("Knowledge Hub").assertIsDisplayed()
    }

    @Test
    fun mainDashboardScreen_launchDeepLinkButtonClick_triggersCallback() {
        var deepLinkOpened = false

        composeTestRule.setContent {
            AppLinkAndDeepLinkTheme {
                MainDashboardScreen(
                    onOpenDeepLinkDemo = { deepLinkOpened = true },
                    onOpenAppLinkDemo = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Launch :deeplink Module").performScrollTo().performClick()
        assertTrue(deepLinkOpened)
    }

    @Test
    fun mainDashboardScreen_launchAppLinkButtonClick_triggersCallback() {
        var appLinkOpened = false

        composeTestRule.setContent {
            AppLinkAndDeepLinkTheme {
                MainDashboardScreen(
                    onOpenDeepLinkDemo = {},
                    onOpenAppLinkDemo = { appLinkOpened = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Launch :applink Module").performScrollTo().performClick()
        assertTrue(appLinkOpened)
    }

    @Test
    fun mainDashboardScreen_tabSwitching_showsPlaygroundAndKnowledgeHub() {
        composeTestRule.setContent {
            AppLinkAndDeepLinkTheme {
                MainDashboardScreen(
                    onOpenDeepLinkDemo = {},
                    onOpenAppLinkDemo = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Intent Sandbox").performClick()
        composeTestRule.onNodeWithText("⚡ Preset URIs").assertIsDisplayed()

        composeTestRule.onNodeWithText("Knowledge Hub").performClick()
        composeTestRule.onNodeWithText("1. Deep Links vs App Links vs Web Links").assertIsDisplayed()
    }
}
