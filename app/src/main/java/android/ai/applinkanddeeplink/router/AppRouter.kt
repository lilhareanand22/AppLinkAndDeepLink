package android.ai.applinkanddeeplink.router

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.ai.deeplink.DeepLinkActivity
import android.ai.applink.AppLinkActivity

/**
 * Result data class describing how Android OS resolves a given URI.
 */
data class IntentResolutionResult(
    val uriString: String,
    val scheme: String?,
    val host: String?,
    val path: String?,
    val isResolvable: Boolean,
    val targetPackage: String?,
    val targetActivity: String?,
    val isInternalTarget: Boolean,
    val linkTypeDescription: String
)

/**
 * AppRouter serves as the centralized Intent dispatcher and resolver across modules.
 *
 * Demonstrates:
 * 1. Safe implicit intent dispatching with ActivityNotFoundException handling.
 * 2. Intent resolution via PackageManager.resolveActivity().
 * 3. Explicit intent dispatching to feature modules (:deeplink and :applink).
 */
object AppRouter {

    /**
     * Inspects how the Android OS would resolve the given URI without necessarily launching it.
     */
    fun resolveIntent(context: Context, uriString: String): IntentResolutionResult {
        val uri = try {
            Uri.parse(uriString)
        } catch (e: Exception) {
            null
        }

        if (uri == null || uri.scheme.isNullOrEmpty()) {
            return IntentResolutionResult(
                uriString = uriString,
                scheme = null,
                host = null,
                path = null,
                isResolvable = false,
                targetPackage = null,
                targetActivity = null,
                isInternalTarget = false,
                linkTypeDescription = "Invalid URI"
            )
        }

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

        val targetPkg = resolveInfo?.activityInfo?.packageName
        val targetActivity = resolveInfo?.activityInfo?.name
        val isInternal = targetPkg == context.packageName

        val isVerifiedAppLink = uri.scheme in listOf("http", "https") &&
            (uri.host in listOf("www.example.com", "example.com"))
        val linkType = when {
            uri.scheme == "myapp" -> "Phase 2 Custom Scheme Deep Link"
            isVerifiedAppLink -> "Phase 3 Verified Android App Link"
            uri.scheme in listOf("http", "https") -> "Standard External Web Link"
            else -> "Other Custom Protocol (${uri.scheme}://)"
        }

        return IntentResolutionResult(
            uriString = uriString,
            scheme = uri.scheme,
            host = uri.host,
            path = uri.path,
            isResolvable = resolveInfo != null,
            targetPackage = targetPkg,
            targetActivity = targetActivity,
            isInternalTarget = isInternal,
            linkTypeDescription = linkType
        )
    }

    /**
     * Safely executes an implicit ACTION_VIEW intent.
     */
    fun launchImplicitUri(context: Context, uriString: String): Result<String> {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.success("Successfully launched: $uriString")
        } catch (e: ActivityNotFoundException) {
            Result.failure(Exception("No application found to handle URI: $uriString"))
        } catch (e: SecurityException) {
            Result.failure(Exception("Permission denied launching URI: ${e.localizedMessage}"))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to launch URI: ${e.localizedMessage}"))
        }
    }

    /**
     * Explicitly launches the DeepLinkActivity from the :deeplink feature module.
     */
    fun launchDeepLinkModule(context: Context) {
        val intent = Intent(context, DeepLinkActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Explicitly launches the AppLinkActivity from the :applink feature module.
     */
    fun launchAppLinkModule(context: Context) {
        val intent = Intent(context, AppLinkActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
