package android.ai.applinkanddeeplink

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.ai.applinkanddeeplink.router.AppRouter
import android.ai.applinkanddeeplink.ui.screens.MainDashboardScreen
import android.ai.applinkanddeeplink.ui.theme.AppLinkAndDeepLinkTheme

/**
 * MainActivity serves as the central application launcher and navigation hub (Phase 4).
 *
 * Responsibilities:
 * 1. Hosts the central Navigation Hub across feature modules (:deeplink and :applink).
 * 2. Provides the Intent Playground & Resolver for live debugging of custom schemes and verified URLs.
 * 3. Houses the Senior/Staff Android Engineering Knowledge Hub.
 * 4. Dispatches explicit and implicit intents safely via AppRouter.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppLinkAndDeepLinkTheme {
                MainDashboardScreen(
                    onOpenDeepLinkDemo = {
                        AppRouter.launchDeepLinkModule(this)
                    },
                    onOpenAppLinkDemo = {
                        AppRouter.launchAppLinkModule(this)
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
