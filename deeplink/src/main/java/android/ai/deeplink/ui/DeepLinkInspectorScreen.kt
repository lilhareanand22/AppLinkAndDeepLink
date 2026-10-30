package android.ai.deeplink.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.ai.deeplink.data.DeepLinkInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepLinkInspectorScreen(
    info: DeepLinkInfo?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("URI Inspector") },
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
            Text(
                text = "Deep Link URI Dissection",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Raw URI:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = info?.rawUri ?: "(No URI received)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Component breakdown
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ComponentRow("Scheme", info?.scheme ?: "null", "e.g. 'myapp'")
                    HorizontalDivider()
                    ComponentRow("Host / Authority", info?.host ?: "null", "e.g. 'deeplink'")
                    HorizontalDivider()
                    ComponentRow("Path", info?.path ?: "null", "e.g. '/product/123'")
                    HorizontalDivider()
                    ComponentRow(
                        "Path Segments",
                        info?.pathSegments?.joinToString(", ") ?: "[]",
                        "List of sub-paths split by '/'"
                    )
                    HorizontalDivider()
                    ComponentRow(
                        "Query Parameters",
                        if (info?.queryParams.isNullOrEmpty()) "None"
                        else info?.queryParams?.entries?.joinToString { "${it.key}=${it.value}" } ?: "",
                        "Key-value pairs after '?'"
                    )
                }
            }

            // Interview Insight
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 What Android Matches in Intent Filter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "When an Intent arrives, the Android OS matches in this order:\n" +
                                "1. Action: (e.g. android.intent.action.VIEW)\n" +
                                "2. Categories: (android.intent.category.DEFAULT and BROWSABLE)\n" +
                                "3. Data URI: Matches scheme first, then host, then port, then path.\n" +
                                "Note: Query parameters are NOT matched by intent filters! The Activity must extract query parameters programmatically.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Hub")
            }
        }
    }
}

@Composable
private fun ComponentRow(label: String, value: String, description: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
