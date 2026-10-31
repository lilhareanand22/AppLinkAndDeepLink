package android.ai.applinkanddeeplink.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KnowledgeHubScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📚 Deep Link & App Link Knowledge Hub",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Staff & Senior Android Engineer reference guide covering architecture, Android 12 domain verification, task stacks, backstack synthesis, and interview questions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Expandable Topic 1: Comparison Matrix
        ExpandableKnowledgeCard(
            title = "1. Deep Links vs App Links vs Web Links",
            subtitle = "Core Architectural Matrix",
            initiallyExpanded = true
        ) {
            Text(
                text = "• Scheme:\n" +
                        "  - Deep Link: Custom scheme (e.g., myapp://)\n" +
                        "  - App Link: HTTP/HTTPS verified against website\n" +
                        "  - Web Link: Standard HTTP/HTTPS\n\n" +
                        "• Verification:\n" +
                        "  - Deep Link: None. Multiple apps can register same scheme.\n" +
                        "  - App Link: Digital Asset Links (assetlinks.json) at /.well-known/assetlinks.json\n" +
                        "  - Web Link: None\n\n" +
                        "• User Experience:\n" +
                        "  - Deep Link: Shows 'Open with' chooser dialog if multiple apps match.\n" +
                        "  - App Link: Directly opens native app (zero popups) if verified.\n" +
                        "  - Web Link: Opens in browser or prompts user.\n\n" +
                        "• Fallback:\n" +
                        "  - Deep Link: Broken link / Error if app not installed.\n" +
                        "  - App Link: Opens web page seamlessly on browser.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Expandable Topic 2: Android 12 Strict Verification
        ExpandableKnowledgeCard(
            title = "2. Android 12 (API 31+) Domain Verification",
            subtitle = "Strict verification changes & DomainVerificationManager"
        ) {
            Text(
                text = "Starting with Android 12:\n" +
                        "1. Elimination of Chooser: If domain verification fails, Android 12 NEVER presents the chooser dialog; it opens the URL in the default browser.\n\n" +
                        "2. All-or-Nothing Rule: If an intent filter lists multiple hosts (e.g. www.example.com and blog.example.com) and ANY host fails verification, ALL hosts in that filter fail.\n\n" +
                        "3. DomainVerificationManager: Apps can programmatically inspect verification states (STATE_VERIFIED, STATE_SELECTED, STATE_DENIED).\n\n" +
                        "4. User Settings: Users can manually grant web link opening permissions in App Info > Open by default.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Expandable Topic 3: Task Stacks & Launch Modes
        ExpandableKnowledgeCard(
            title = "3. Launch Modes & Lifecycle (onCreate vs onNewIntent)",
            subtitle = "singleTask, singleTop, and setIntent(intent)"
        ) {
            Text(
                text = "• launchMode=\"singleTask\":\n" +
                        "  If an instance already exists, Android routes the new deep link to onNewIntent(Intent) instead of creating a new Activity.\n\n" +
                        "• Critical Rule - setIntent(intent):\n" +
                        "  Inside onNewIntent(intent), you MUST invoke setIntent(intent). If omitted, calling getIntent() later (e.g. after screen rotation) returns the stale initial intent!\n\n" +
                        "• launchMode=\"standard\":\n" +
                        "  Spawns a new Activity instance each time a link is clicked, creating memory bloat and duplicate screens.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Expandable Topic 4: Synthetic Back Stack
        ExpandableKnowledgeCard(
            title = "4. Synthetic Back Stack & Up Navigation",
            subtitle = "TaskStackBuilder & Hierarchical Navigation"
        ) {
            Text(
                text = "When a deep link drops a user deep into a detail screen (e.g. /details/456), pressing the system Back button should not exit the app if the user expects an in-app Home screen.\n\n" +
                        "• Traditional Solution: TaskStackBuilder.create(context).addNextIntentWithParentStack(detailIntent).startActivities()\n" +
                        "• Jetpack Compose Solution: In NavHost, maintain the root destination ('home') at the base of the backstack, and navigate to ('details/id') on top so popBackStack() returns to Home.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Expandable Topic 5: Deferred Deep Linking
        ExpandableKnowledgeCard(
            title = "5. Deferred Deep Linking & Play Install Referrer",
            subtitle = "Handling deep links when the app is not yet installed"
        ) {
            Text(
                text = "What happens when a new user clicks an App Link without having the app installed?\n\n" +
                        "1. Browser opens webpage: https://www.example.com/details/456\n" +
                        "2. Webpage detects mobile device and redirects to Google Play Store:\n" +
                        "   https://play.google.com/store/apps/details?id=pkg&referrer=utm_content%3Ddetails_456\n" +
                        "3. User installs app and launches it.\n" +
                        "4. App uses Google Play Install Referrer API to read 'utm_content' on first startup and navigates directly to the target product!",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Expandable Topic 6: Top Interview Questions
        ExpandableKnowledgeCard(
            title = "6. Top Staff Android Interview Questions",
            subtitle = "Direct answers to key screening questions"
        ) {
            Text(
                text = "Q1: Why are App Links preferred over custom deep links in E-Commerce?\n" +
                        "A: App Links eliminate the drop-off causing disambiguation dialog, prevent competitors from spoofing links, and work seamlessly in SMS and emails.\n\n" +
                        "Q2: What is the exact path where assetlinks.json must reside?\n" +
                        "A: https://<domain>/.well-known/assetlinks.json with Content-Type application/json, direct HTTP 200 (no 301/302 redirects), and valid SSL certificate.\n\n" +
                        "Q3: What happens if you forget android:exported=\"true\" on Android 12+?\n" +
                        "A: The build fails with a Manifest Merger error because Android 12 strictly requires explicit exported declaration for any component with intent filters.\n\n" +
                        "Q4: What is the purpose of android.intent.category.BROWSABLE?\n" +
                        "A: It indicates that the intent can be safely invoked from a web browser or hyperlink.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ExpandableKnowledgeCard(
    title: String,
    subtitle: String,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Text(
                    text = if (expanded) "▲" else "▼",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    content()
                }
            }
        }
    }
}
