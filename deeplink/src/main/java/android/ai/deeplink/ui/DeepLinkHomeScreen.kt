package android.ai.deeplink.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.ai.deeplink.data.DeepLinkInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepLinkHomeScreen(
    lastReceivedInfo: DeepLinkInfo?,
    onNavigateToProduct: (String, DeepLinkInfo?) -> Unit,
    onNavigateToProfile: (String, DeepLinkInfo?) -> Unit,
    onNavigateToInspector: (DeepLinkInfo?) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var customUriText by remember { mutableStateOf("myapp://deeplink/product/888?ref=in_app_test") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deep Link Module (Custom URI)") },
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
            // What is a Deep Link card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📱 Deep Link (Custom Scheme)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A Deep Link uses a custom URI scheme (like `myapp://`). It directs users directly to specific content inside the app.\n\n" +
                                "⚠️ Limitation: Because custom schemes are not verified, if another app registers the same scheme, Android presents a Disambiguation Dialog (App Chooser) asking the user to pick an app.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Last Received Deep Link notification
            if (lastReceivedInfo != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚡ Recently Received Deep Link",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tap to trigger an ACTION_VIEW Intent with custom scheme:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Button(
                        onClick = {
                            launchUriIntent(context, "myapp://deeplink/product/101?ref=promo_banner")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("▶️ Trigger: myapp://deeplink/product/101")
                    }

                    Button(
                        onClick = {
                            launchUriIntent(context, "myapp://deeplink/profile/varsha_dev")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("▶️ Trigger: myapp://deeplink/profile/varsha_dev")
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
                        text = "Custom URI Tester",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = customUriText,
                        onValueChange = { customUriText = it },
                        label = { Text("Enter custom deep link URI") },
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
                        text = "1. Test Product Deep Link:\n" +
                               "adb shell am start -a android.intent.action.VIEW -d \"myapp://deeplink/product/123\"\n\n" +
                               "2. Test Profile Deep Link:\n" +
                               "adb shell am start -a android.intent.action.VIEW -d \"myapp://deeplink/profile/john_doe\"\n\n" +
                               "3. Test with Query Params:\n" +
                               "adb shell am start -a android.intent.action.VIEW -d \"myapp://deeplink/product/456?ref=campaign\"",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
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
