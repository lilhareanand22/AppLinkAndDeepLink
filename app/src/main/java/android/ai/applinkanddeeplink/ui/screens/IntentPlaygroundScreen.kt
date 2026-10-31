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
fun IntentPlaygroundScreen() {
    val context = LocalContext.current
    var inputUri by remember { mutableStateOf("https://www.example.com/details/456?discount=30&ref=playground") }

    val resolutionResult = remember(inputUri) {
        AppRouter.resolveIntent(context, inputUri)
    }

    val presets = listOf(
        "myapp://deeplink/product/101?discount=20",
        "myapp://deeplink/profile/varsha_dev",
        "https://www.example.com/details/456?discount=30",
        "https://www.example.com/user/alex_coder?role=Staff_Engineer",
        "https://www.example.com/order/ORD-99124?status=in_transit",
        "https://www.example.com/guide"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🧪 Live Intent Sandbox & Resolver",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Test implicit ACTION_VIEW intents and observe how the Android OS resolves them to :deeplink or :applink feature modules using PackageManager.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Preset Chips
        Text(
            text = "⚡ Preset URIs",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            presets.chunked(2).forEach { rowPresets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowPresets.forEach { preset ->
                        AssistChip(
                            onClick = { inputUri = preset },
                            label = {
                                Text(
                                    text = preset.substringBefore("?").replace("https://www.example.com", "..."),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowPresets.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Custom URI Input
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "URI Under Test",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = inputUri,
                    onValueChange = { inputUri = it },
                    label = { Text("Enter custom scheme or HTTPS URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        val result = AppRouter.launchImplicitUri(context, inputUri)
                        result.fold(
                            onSuccess = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { err ->
                                Toast.makeText(context, err.message, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🚀 Dispatch Intent (ACTION_VIEW)")
                }
            }
        }

        // OS Resolution Inspection Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔍 PackageManager Resolution Preview",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                ResolutionItem("Link Classification", resolutionResult.linkTypeDescription)
                ResolutionItem("Scheme", resolutionResult.scheme ?: "(null)")
                ResolutionItem("Host", resolutionResult.host ?: "(null)")
                ResolutionItem("Path", resolutionResult.path ?: "(null)")
                ResolutionItem(
                    "Resolvable by OS",
                    if (resolutionResult.isResolvable) "YES ✅" else "NO ❌"
                )
                ResolutionItem(
                    "Target Package",
                    resolutionResult.targetPackage ?: "(None / External)"
                )
                ResolutionItem(
                    "Target Activity",
                    resolutionResult.targetActivity?.substringAfterLast(".") ?: "(None / Browser)"
                )
                ResolutionItem(
                    "Scope",
                    if (resolutionResult.isInternalTarget) "Internal App Component" else "External Component"
                )
            }
        }

        // Architectural Note
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💡 Best Practice: Safe Implicit Intent Launching",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Always query PackageManager.resolveActivity() or wrap startActivity() in a try-catch for ActivityNotFoundException before launching implicit intents.\n" +
                            "Starting in Android 11 (API 30), querying other applications via PackageManager requires <queries> declarations in AndroidManifest.xml.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun ResolutionItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
