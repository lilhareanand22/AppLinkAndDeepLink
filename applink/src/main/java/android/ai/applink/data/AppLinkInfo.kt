package android.ai.applink.data

import android.net.Uri

/**
 * Data model representing the components extracted from an incoming Android App Link URI.
 *
 * Example App Link URI: https://www.example.com/details/456?discount=30&ref=campaign
 * - Scheme: "https" (or "http")
 * - Host: "www.example.com"
 * - Path: "/details/456"
 * - Path Segments: ["details", "456"]
 * - Query Parameters: {"discount": "30", "ref": "campaign"}
 *
 * App Link vs Deep Link Characteristics:
 * - Scheme must be standard web scheme (HTTP or HTTPS).
 * - Host must match verified domain registered in assetlinks.json.
 * - Opens seamlessly in app without user prompt if domain verification succeeds.
 */
data class AppLinkInfo(
    val rawUri: String,
    val scheme: String?,
    val host: String?,
    val path: String?,
    val pathSegments: List<String> = emptyList(),
    val queryParams: Map<String, String> = emptyMap(),
    val targetScreen: String = "Unknown",
    val isVerifiedScheme: Boolean = false,
    val isApplinkCandidate: Boolean = false
) {
    companion object {
        fun fromUri(uri: Uri?): AppLinkInfo? {
            if (uri == null) return null

            val queryMap = mutableMapOf<String, String>()
            try {
                uri.queryParameterNames?.forEach { key ->
                    uri.getQueryParameter(key)?.let { value ->
                        queryMap[key] = value
                    }
                }
            } catch (e: Exception) {
                // Ignore malformed query params
            }

            val scheme = uri.scheme?.lowercase()
            val host = uri.host?.lowercase()
            val pathSegments = uri.pathSegments ?: emptyList()
            val firstSegment = pathSegments.firstOrNull()

            val target = when (firstSegment) {
                "details" -> "Item Details"
                "user" -> "User Profile"
                "order" -> "Order Tracking"
                "guide" -> "AssetLinks Guide"
                "inspector" -> "App Link Inspector"
                else -> "App Link Home"
            }

            val isWebScheme = scheme == "http" || scheme == "https"
            val isKnownHost = host == "www.example.com" || host == "example.com"

            return AppLinkInfo(
                rawUri = uri.toString(),
                scheme = uri.scheme,
                host = uri.host,
                path = uri.path,
                pathSegments = pathSegments,
                queryParams = queryMap,
                targetScreen = target,
                isVerifiedScheme = isWebScheme,
                isApplinkCandidate = isWebScheme && isKnownHost
            )
        }
    }
}
