package android.ai.applinkanddeeplink.model

/**
 * Product data model representing catalog items referenced in deep links and app links.
 * Example: myapp://deeplink/product/101?discount=20
 */
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String = "General",
    val discountPercent: Int = 0
) {
    val discountedPrice: Double
        get() = if (discountPercent > 0) price * (1.0 - discountPercent / 100.0) else price
}
