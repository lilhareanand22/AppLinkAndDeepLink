package android.ai.applinkanddeeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.ai.applinkanddeeplink.ui.theme.AppLinkAndDeepLinkTheme
import android.ai.deeplink.DeepLinkActivity
import android.ai.applink.AppLinkActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppLinkAndDeepLinkTheme {
                MainDashboardScreen(
                    onOpenDeepLinkDemo = {
                        val intent = Intent(this, DeepLinkActivity::class.java)
                        startActivity(intent)
                    },
                    onOpenAppLinkDemo = {
                        val intent = Intent(this, AppLinkActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onOpenDeepLinkDemo: () -> Unit,
    onOpenAppLinkDemo: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Links & Deep Links Masterclass") },
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
            // Hero Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 Jetpack Compose Multi-Module Demo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Comprehensive implementation of Deep Links (Phase 2) and verified Android App Links (Phase 3) with production-ready architecture.",
                        style = MaterialTheme.typography.bodyMedium
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
                    Text(
                        text = "📱 Phase 2: Custom Scheme Deep Link",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Uses custom scheme (myapp://deeplink/...). Demonstrates URI parsing, singleTask launchMode, onNewIntent routing, and adb command triggers.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = onOpenDeepLinkDemo,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Deep Link Module Demo")
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
                    Text(
                        text = "🌐 Phase 3: Verified Android App Links",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Uses verified HTTPS URLs (https://www.example.com/...). Features autoVerify=\"true\", Digital Asset Links (assetlinks.json), no disambiguation dialog, and browser fallback.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = onOpenAppLinkDemo,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open App Link Module Demo")
                    }
                }
            }

            // Architecture Comparison Matrix
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📊 Deep Links vs App Links Comparison",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    ComparisonLine("Scheme", "Custom (e.g. myapp://)", "Standard (http/https)")
                    ComparisonLine("Verification", "None required", "Digital Asset Links (assetlinks.json)")
                    ComparisonLine("Disambiguation", "May show 'Open with' chooser", "Direct open (No chooser)")
                    ComparisonLine("Security", "Vulnerable to hijacking", "Domain ownership verified")
                    ComparisonLine("Fallback", "Broken / Error if no app", "Seamless webpage on browser")
                    ComparisonLine("Android 12+", "Unchanged", "Strict verification required")
                }
            }

            // Direct Intent Launchers
            Text(
                text = "⚡ Quick Intent Triggers",
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
                            triggerUriIntent(context, "myapp://deeplink/product/101?ref=home_dashboard")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Trigger Deep Link: myapp://deeplink/product/101")
                    }

                    OutlinedButton(
                        onClick = {
                            triggerUriIntent(context, "https://www.example.com/details/456?discount=20&ref=home_dashboard")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Trigger App Link: https://www.example.com/details/456")
                    }

                    OutlinedButton(
                        onClick = {
                            triggerUriIntent(context, "https://www.example.com/order/ORD-99124?status=delivered")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Trigger App Link: https://www.example.com/order/ORD-99124")
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonLine(label: String, deepLink: String, appLink: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "• Deep: $deepLink", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text(text = "• App: $appLink", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        }
    }
}

private fun triggerUriIntent(context: Context, uriString: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Intent error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
