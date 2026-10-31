package android.ai.applink

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.ai.applink.data.AppLinkInfo
import android.ai.applink.ui.*

/**
 * AppLinkActivity demonstrates how an Android Activity receives, verifies, handles,
 * and routes incoming Android App Links (e.g. https://www.example.com/details/456).
 *
 * Key Interview Concepts:
 * 1. Android App Links use HTTP/HTTPS scheme and domain verification.
 * 2. Intent filter with android:autoVerify="true" triggers domain verification via assetlinks.json.
 * 3. Verified App Links open immediately without an "Open with" app chooser dialog.
 * 4. Handling new incoming links via onNewIntent(Intent) when singleTask launchMode is used.
 */
class AppLinkActivity : ComponentActivity() {

    private var currentAppLinkInfo = mutableStateOf<AppLinkInfo?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Process intent received at Activity creation
        processIntent(intent)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val infoState by currentAppLinkInfo

                // Whenever a new App Link arrives, navigate to the target screen
                LaunchedEffect(infoState) {
                    val info = infoState
                    if (info != null) {
                        val firstSegment = info.pathSegments.firstOrNull()
                        when (firstSegment) {
                            "details" -> {
                                val id = info.pathSegments.getOrNull(1) ?: "1"
                                navController.navigate("details/$id") {
                                    launchSingleTop = true
                                }
                            }
                            "user" -> {
                                val username = info.pathSegments.getOrNull(1) ?: "guest"
                                navController.navigate("user/$username") {
                                    launchSingleTop = true
                                }
                            }
                            "order" -> {
                                val orderId = info.pathSegments.getOrNull(1) ?: "ORD-1"
                                navController.navigate("order/$orderId") {
                                    launchSingleTop = true
                                }
                            }
                            "guide" -> {
                                navController.navigate("guide") {
                                    launchSingleTop = true
                                }
                            }
                            else -> {
                                if (info.pathSegments.isNotEmpty()) {
                                    navController.navigate("inspector") {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        AppLinkHomeScreen(
                            lastReceivedInfo = infoState,
                            onNavigateToDetail = { id, info ->
                                navController.navigate("details/$id")
                            },
                            onNavigateToUser = { username, info ->
                                navController.navigate("user/$username")
                            },
                            onNavigateToOrder = { orderId, info ->
                                navController.navigate("order/$orderId")
                            },
                            onNavigateToGuide = {
                                navController.navigate("guide")
                            },
                            onNavigateToInspector = {
                                navController.navigate("inspector")
                            },
                            onBack = { finish() }
                        )
                    }

                    composable("details/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id") ?: "1"
                        AppLinkDetailScreen(
                            itemId = id,
                            info = infoState,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("user/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: "user"
                        AppLinkUserProfileScreen(
                            username = username,
                            info = infoState,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("order/{orderId}") { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId") ?: "ORD-1"
                        AppLinkOrderScreen(
                            orderId = orderId,
                            info = infoState,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("guide") {
                        AssetLinksGuideScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("inspector") {
                        AppLinkInspectorScreen(
                            info = infoState,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    /**
     * Called when the Activity is launched while already running (launchMode="singleTask").
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Always update the activity's intent
        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
        val uri = intent?.data
        currentAppLinkInfo.value = AppLinkInfo.fromUri(uri)
    }
}
