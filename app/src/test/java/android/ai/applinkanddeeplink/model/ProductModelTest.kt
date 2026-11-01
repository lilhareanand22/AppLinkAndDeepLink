package android.ai.applinkanddeeplink.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ProductModelTest {

    @Test
    fun product_defaultValues_areAssignedCorrectly() {
        val product = Product(id = "101", name = "Android Plushie", price = 25.0)
        assertEquals("101", product.id)
        assertEquals("Android Plushie", product.name)
        assertEquals(25.0, product.price, 0.001)
        assertEquals("General", product.category)
        assertEquals(0, product.discountPercent)
        assertEquals(25.0, product.discountedPrice, 0.001)
    }

    @Test
    fun product_discountedPrice_computesCorrectDiscount() {
        val product = Product(
            id = "202",
            name = "Compose Guide",
            price = 100.0,
            category = "Books",
            discountPercent = 20
        )
        assertEquals(80.0, product.discountedPrice, 0.001)
    }

    @Test
    fun product_equality_worksAsExpected() {
        val p1 = Product(id = "1", name = "Test", price = 10.0)
        val p2 = Product(id = "1", name = "Test", price = 10.0)
        assertEquals(p1, p2)
        assertEquals(p1.hashCode(), p2.hashCode())
    }

    @Test
    fun product_inequality_detectsDifferentIds() {
        val p1 = Product(id = "1", name = "Item", price = 15.0)
        val p2 = Product(id = "2", name = "Item", price = 15.0)
        assertNotEquals(p1, p2)
    }

    @Test
    fun product_copy_updatesSpecifiedFieldsOnly() {
        val original = Product(id = "55", name = "Original", price = 50.0, discountPercent = 10)
        val copied = original.copy(price = 40.0)
        assertEquals("55", copied.id)
        assertEquals("Original", copied.name)
        assertEquals(40.0, copied.price, 0.001)
        assertEquals(10, copied.discountPercent)
    }
}
