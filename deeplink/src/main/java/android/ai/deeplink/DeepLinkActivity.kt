package android.ai.deeplink

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
import android.ai.deeplink.data.DeepLinkInfo
import android.ai.deeplink.ui.*

/**
 * DeepLinkActivity demonstrates how an Android Activity receives, handles, and routes
 * incoming custom scheme Deep Links (e.g., myapp://deeplink/product/123).
 *
 * Key Interview Concepts:
 * 1. Handling URI via intent.data in onCreate()
 * 2. Handling new URIs when already running via onNewIntent(intent: Intent)
 * 3. Extracting path segments and query parameters
 */
class DeepLinkActivity : ComponentActivity() {

    private var currentDeepLinkInfo = mutableStateOf<DeepLinkInfo?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Process intent received at Activity creation
        processIntent(intent)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val infoState by currentDeepLinkInfo

                // Whenever a new Deep Link arrives, navigate to the target screen
                LaunchedEffect(infoState) {
                    val info = infoState
                    if (info != null) {
                        val firstSegment = info.pathSegments.firstOrNull()
                        when (firstSegment) {
                            "product" -> {
                                val id = info.pathSegments.getOrNull(1) ?: "unknown"
                                navController.navigate("product/$id") {
                                    launchSingleTop = true
                                }
                            }
                            "profile" -> {
                                val username = info.pathSegments.getOrNull(1) ?: "guest"
                                navController.navigate("profile/$username") {
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
                        DeepLinkHomeScreen(
                            lastReceivedInfo = infoState,
                            onNavigateToProduct = { id, info ->
                                navController.navigate("product/$id")
                            },
                            onNavigateToProfile = { username, info ->
                                navController.navigate("profile/$username")
                            },
                            onNavigateToInspector = {
                                navController.navigate("inspector")
                            },
                            onBack = { finish() }
                        )
                    }

                    composable("product/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id") ?: "1"
                        ProductDetailScreen(
                            productId = id,
                            info = infoState,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("profile/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: "user"
                        ProfileScreen(
                            username = username,
                            info = infoState,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("inspector") {
                        DeepLinkInspectorScreen(
                            info = infoState,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    /**
     * Called when the Activity is launched while already on top of the task stack
     * (e.g. launchMode="singleTask" or "singleTop").
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Always update the activity's intent
        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
        val uri = intent?.data
        currentDeepLinkInfo.value = DeepLinkInfo.fromUri(uri)
    }
}
