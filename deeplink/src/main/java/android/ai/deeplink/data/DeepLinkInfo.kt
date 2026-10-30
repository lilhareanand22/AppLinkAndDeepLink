package android.ai.deeplink.data

import android.net.Uri

/**
 * Data model representing the components extracted from an incoming Deep Link URI.
 *
 * Example URI: myapp://deeplink/product/123?ref=android_weekly&discount=20
 * - Scheme: "myapp"
 * - Host: "deeplink"
 * - Path: "/product/123"
 * - Path Segments: ["product", "123"]
 * - Query Parameters: {"ref": "android_weekly", "discount": "20"}
 */
data class DeepLinkInfo(
    val rawUri: String,
    val scheme: String?,
    val host: String?,
    val path: String?,
    val pathSegments: List<String> = emptyList(),
    val queryParams: Map<String, String> = emptyMap(),
    val targetScreen: String = "Unknown"
) {
    companion object {
        fun fromUri(uri: Uri?): DeepLinkInfo? {
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

            val pathSegments = uri.pathSegments ?: emptyList()
            val target = when {
                pathSegments.firstOrNull() == "product" -> "Product Detail"
                pathSegments.firstOrNull() == "profile" -> "User Profile"
                else -> "Deep Link Home / Inspector"
            }

            return DeepLinkInfo(
                rawUri = uri.toString(),
                scheme = uri.scheme,
                host = uri.host,
                path = uri.path,
                pathSegments = pathSegments,
                queryParams = queryMap,
                targetScreen = target
            )
        }
    }
}
