package android.ai.applinkanddeeplink.deeplink

import android.ai.deeplink.data.DeepLinkInfo
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DeepLinkInfoTest {

    @Test
    fun deepLinkInfo_nullUri_returnsNull() {
        val result = DeepLinkInfo.fromUri(null)
        assertNull(result)
    }

    @Test
    fun deepLinkInfo_productUri_resolvesProductDetailTarget() {
        val uri = Uri.parse("myapp://deeplink/product/101?discount=20&ref=catalog")
        val info = DeepLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertEquals("myapp", info?.scheme)
        assertEquals("deeplink", info?.host)
        assertEquals("/product/101", info?.path)
        assertEquals("Product Detail", info?.targetScreen)
        assertEquals("20", info?.queryParams?.get("discount"))
        assertEquals("catalog", info?.queryParams?.get("ref"))
        assertEquals(listOf("product", "101"), info?.pathSegments)
    }

    @Test
    fun deepLinkInfo_profileUri_resolvesUserProfileTarget() {
        val uri = Uri.parse("myapp://deeplink/profile/varsha_dev")
        val info = DeepLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertEquals("User Profile", info?.targetScreen)
        assertEquals(listOf("profile", "varsha_dev"), info?.pathSegments)
    }

    @Test
    fun deepLinkInfo_genericUri_resolvesDeepLinkHomeTarget() {
        val uri = Uri.parse("myapp://deeplink/unknown/path")
        val info = DeepLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertEquals("Deep Link Home / Inspector", info?.targetScreen)
    }

    @Test
    fun deepLinkInfo_queryParameters_emptyMapWhenNoQuery() {
        val uri = Uri.parse("myapp://deeplink/product/99")
        val info = DeepLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertTrue(info?.queryParams?.isEmpty() == true)
    }

    @Test
    fun deepLinkInfo_rawUri_preservesFullUriString() {
        val raw = "myapp://deeplink/product/123?promo=save50"
        val uri = Uri.parse(raw)
        val info = DeepLinkInfo.fromUri(uri)

        assertEquals(raw, info?.rawUri)
    }
}
