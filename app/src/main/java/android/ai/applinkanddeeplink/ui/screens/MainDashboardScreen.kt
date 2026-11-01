package android.ai.applinkanddeeplink.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.ai.applinkanddeeplink.router.AppRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onOpenDeepLinkDemo: () -> Unit,
    onOpenAppLinkDemo: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Dashboard", "Intent Sandbox", "Knowledge Hub")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Links & Deep Links Hub") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabTitles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title) },
                        icon = {
                            val iconEmoji = when (index) {
                                0 -> "📱"
                                1 -> "🧪"
                                else -> "📚"
                            }
                            Text(iconEmoji, fontSize = 18.sp)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardHomeContent(
                    onOpenDeepLinkDemo = onOpenDeepLinkDemo,
                    onOpenAppLinkDemo = onOpenAppLinkDemo
                )
                1 -> IntentPlaygroundScreen()
                2 -> KnowledgeHubScreen()
            }
        }
    }
}

@Composable
private fun DashboardHomeContent(
    onOpenDeepLinkDemo: () -> Unit,
    onOpenAppLinkDemo: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Multi-Module Architecture Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏗️ Multi-Module Architecture",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• :app (Phase 4): Central Launcher, Navigation Hub, and Intent Dispatcher\n" +
                            "• :deeplink (Phase 2): Custom Scheme Deep Links (myapp://deeplink/*)\n" +
                            "• :applink (Phase 3): Verified Android App Links (https://www.example.com/*)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }

        // Phase 2: Deep Link Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📱 Phase 2: Deep Link Module",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("myapp://") }
                    )
                }
                Text(
                    text = "Custom URI scheme deep link handling. Demonstrates URI segmentation, query parsing, singleTask launchMode, onNewIntent routing, and ADB triggers.",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = onOpenDeepLinkDemo,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Launch :deeplink Module")
                }
            }
        }

        // Phase 3: App Link Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🌐 Phase 3: App Link Module",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("https://") }
                    )
                }
                Text(
                    text = "Verified Android App Links with android:autoVerify=\"true\", Digital Asset Links (assetlinks.json), direct app opening (zero dialogs), and web fallback.",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = onOpenAppLinkDemo,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Launch :applink Module")
                }
            }
        }

        // Quick Triggers
        Text(
            text = "⚡ Instant Intent Dispatchers",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val result = AppRouter.launchImplicitUri(context, "myapp://deeplink/product/101?ref=dashboard")
                        handleLaunchResult(context, result)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("▶️ Deep Link: myapp://deeplink/product/101")
                }

                OutlinedButton(
                    onClick = {
                        val uri = "https://www.example.com/details/456?discount=20&ref=dashboard"
                        val result = AppRouter.launchImplicitUri(context, uri)
                        handleLaunchResult(context, result)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("▶️ App Link: https://www.example.com/details/456")
                }

                OutlinedButton(
                    onClick = {
                        val result = AppRouter.launchImplicitUri(context, "https://www.example.com/order/ORD-99124?status=delivered")
                        handleLaunchResult(context, result)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("▶️ App Link: https://www.example.com/order/ORD-99124")
                }
            }
        }
    }
}

private fun handleLaunchResult(context: android.content.Context, result: Result<String>) {
    result.fold(
        onSuccess = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
        onFailure = { err -> Toast.makeText(context, err.message, Toast.LENGTH_LONG).show() }
    )
}
