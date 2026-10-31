package android.ai.applink.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetLinksGuideScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("assetlinks.json Guide") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Back", fontWeight = FontWeight.Bold)
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
            // Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔐 Digital Asset Links Protocol",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Android App Links require bidirectional trust verification between the web domain and the native Android app.\n" +
                                "1. App points to domain in AndroidManifest.xml (<intent-filter android:autoVerify=\"true\">).\n" +
                                "2. Domain points back to app via assetlinks.json file.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Hosting Requirements
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🌐 Hosting Requirements",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("• Exact Location: https://<domain>/.well-known/assetlinks.json")
                    Text("• Protocol: Must be HTTPS with a valid SSL/TLS certificate (no self-signed).")
                    Text("• Content-Type: application/json")
                    Text("• HTTP Response: Direct 200 OK (HTTP 301/302 redirects are strictly forbidden).")
                    Text("• Accessibility: Must be publicly accessible without authentication, bot-blockers, or VPNs.")
                }
            }

            // JSON Template
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📄 assetlinks.json Structure",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
[
  {
    "relation": [
      "delegate_permission/common.handle_all_urls"
    ],
    "target": {
      "namespace": "android_app",
      "package_name": "android.ai.applinkanddeeplink",
      "sha256_cert_fingerprints": [
        "14:6D:E9:7E:0F:52:97:AE:CE:E7:5C:22:07:A0:64:29:49:E4:E6:1B:6F:09:2B:CA:77:25:B3:77:85:90:F0:4E"
      ]
    }
  }
]
                        """.trimIndent(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // How to extract SHA256 Fingerprint
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔑 Extracting SHA-256 Certificate Fingerprint",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. For Debug Keystore (Terminal):\n" +
                                "keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android\n\n" +
                                "2. Via Gradle (Project root):\n" +
                                "./gradlew signingReport\n\n" +
                                "3. For Google Play App Signing:\n" +
                                "Copy the SHA-256 fingerprint from Google Play Console > Release > Setup > App Signing.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            // Android 12+ Domain Verification Rules
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚠️ Android 12 (API 31+) Strict Domain Verification",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Prior to Android 12, unverified links fell back to the chooser dialog. Starting in Android 12:\n" +
                                "• If verification fails, Android NEVER shows the disambiguation dialog; it opens the URL in the default web browser.\n" +
                                "• All domains listed in intent filters marked autoVerify must verify successfully. If one fails, all fail.\n" +
                                "• Users can manually enable supported web links under App Settings > Open by default.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // ADB Debugging Commands
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🛠️ ADB Domain Verification Diagnostics",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Check domain verification status:\n" +
                                "adb shell pm get-app-links android.ai.applinkanddeeplink\n\n" +
                                "2. Re-trigger verification manually:\n" +
                                "adb shell pm verify-app-links --re-verify android.ai.applinkanddeeplink\n\n" +
                                "3. Manually approve domain for testing:\n" +
                                "adb shell pm set-app-links --package android.ai.applinkanddeeplink 2 all\n\n" +
                                "4. Reset domain approval:\n" +
                                "adb shell pm reset-app-links android.ai.applinkanddeeplink",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to App Link Home")
            }
        }
    }
}
