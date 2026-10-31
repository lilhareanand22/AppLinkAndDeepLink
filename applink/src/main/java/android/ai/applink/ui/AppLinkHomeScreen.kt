package android.ai.applink.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import android.ai.applink.data.AppLinkInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLinkHomeScreen(
    lastReceivedInfo: AppLinkInfo?,
    onNavigateToDetail: (String, AppLinkInfo?) -> Unit,
    onNavigateToUser: (String, AppLinkInfo?) -> Unit,
    onNavigateToOrder: (String, AppLinkInfo?) -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToInspector: (AppLinkInfo?) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var customUriText by remember { mutableStateOf("https://www.example.com/details/789?discount=40&ref=in_app") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Links Module (autoVerify)") },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack) {
                            Text("← Back", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Core Concept Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌐 Android App Links (Verified URLs)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Android App Links are HTTP/HTTPS URLs that bring users directly to specific content in your Android app without prompting the user to select an app (No 'Open with' disambiguation dialog).\n\n" +
                                "✅ Verification: Handled via `android:autoVerify=\"true\"` and a `/.well-known/assetlinks.json` file on the domain.\n" +
                                "✅ Fallback: If the app is not installed, the link opens seamlessly in the user's web browser.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Comparison Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚖️ Deep Link vs App Link Comparison",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    ComparisonRow("Scheme", "myapp:// (Custom)", "https:// (Standard Web)")
                    ComparisonRow("User Prompt", "Disambiguation dialog", "Opens directly (No dialog)")
                    ComparisonRow("Domain Verification", "None (vulnerable to spoofing)", "Verified via assetlinks.json")
                    ComparisonRow("Web Fallback", "Fails / Unknown scheme", "Opens webpage in browser")
                }
            }

            // Recently Received App Link
            if (lastReceivedInfo != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚡ Recently Received App Link",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lastReceivedInfo.rawUri,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onNavigateToInspector(lastReceivedInfo) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Inspect Parsed Components")
                        }
                    }
                }
            }

            // Quick Test Buttons (Firing Intents)
            Text(
                text = "🧪 Interactive Test Triggers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Tap to launch ACTION_VIEW Intent with HTTPS App Link:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Button(
                        onClick = {
                            launchUriIntent(context, "https://www.example.com/details/456?discount=30&ref=google_search")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("▶️ Item Details: /details/456")
                    }

                    Button(
                        onClick = {
                            launchUriIntent(context, "https://www.example.com/user/alex_coder?role=Android_Architect")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("▶️ User Profile: /user/alex_coder")
                    }

                    Button(
                        onClick = {
                            launchUriIntent(context, "https://www.example.com/order/ORD-2026-8812?status=out_for_delivery")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("▶️ Order Status: /order/ORD-2026-8812")
                    }
                }
            }

            // Custom URI Tester
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Custom App Link URL Tester",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = customUriText,
                        onValueChange = { customUriText = it },
                        label = { Text("Enter HTTPS URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(
                        onClick = { launchUriIntent(context, customUriText) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔍 Launch Intent")
                    }
                }
            }

            // AssetLinks Guide Navigation Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔐 Digital Asset Links Configuration",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Learn how to generate SHA-256 cert fingerprints, configure assetlinks.json, and debug Android 12+ domain verification.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onNavigateToGuide,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open assetlinks.json Guide & Checklist")
                    }
                }
            }

            // ADB Command Practice Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💻 Interview ADB Test Commands",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Test App Link via ADB:\n" +
                                "adb shell am start -a android.intent.action.VIEW -d \"https://www.example.com/details/456\"\n\n" +
                                "2. Check Domain Verification Status:\n" +
                                "adb shell pm get-app-links android.ai.applinkanddeeplink\n\n" +
                                "3. Force Re-verification:\n" +
                                "adb shell pm verify-app-links --re-verify android.ai.applinkanddeeplink\n\n" +
                                "4. Set User Domain Approval (Manual Override):\n" +
                                "adb shell pm set-app-links --package android.ai.applinkanddeeplink 2 all",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(feature: String, deepLink: String, appLink: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            text = feature,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Deep: $deepLink", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text(text = "App: $appLink", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        }
    }
}

private fun launchUriIntent(context: Context, uriString: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
