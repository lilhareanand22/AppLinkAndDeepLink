package android.ai.applinkanddeeplink.applink

import android.ai.applink.data.AppLinkInfo
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppLinkInfoTest {

    @Test
    fun appLinkInfo_nullUri_returnsNull() {
        val result = AppLinkInfo.fromUri(null)
        assertNull(result)
    }

    @Test
    fun appLinkInfo_detailsPath_resolvesItemDetailsTarget() {
        val uri = Uri.parse("https://www.example.com/details/456?discount=30&ref=campaign")
        val info = AppLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertEquals("https", info?.scheme)
        assertEquals("www.example.com", info?.host)
        assertEquals("/details/456", info?.path)
        assertEquals("Item Details", info?.targetScreen)
        assertTrue(info?.isVerifiedScheme == true)
        assertTrue(info?.isApplinkCandidate == true)
        assertEquals("30", info?.queryParams?.get("discount"))
        assertEquals(listOf("details", "456"), info?.pathSegments)
    }

    @Test
    fun appLinkInfo_userPath_resolvesUserProfileTarget() {
        val uri = Uri.parse("https://example.com/user/alex_coder?role=Engineer")
        val info = AppLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertEquals("User Profile", info?.targetScreen)
        assertTrue(info?.isApplinkCandidate == true)
        assertEquals("Engineer", info?.queryParams?.get("role"))
    }

    @Test
    fun appLinkInfo_orderPath_resolvesOrderTrackingTarget() {
        val uri = Uri.parse("https://www.example.com/order/ORD-99124?status=in_transit")
        val info = AppLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertEquals("Order Tracking", info?.targetScreen)
        assertEquals("in_transit", info?.queryParams?.get("status"))
    }

    @Test
    fun appLinkInfo_guidePath_resolvesAssetLinksGuideTarget() {
        val uri = Uri.parse("https://www.example.com/guide")
        val info = AppLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertEquals("AssetLinks Guide", info?.targetScreen)
    }

    @Test
    fun appLinkInfo_unverifiedDomain_isNotApplinkCandidate() {
        val uri = Uri.parse("https://www.otherdomain.com/details/999")
        val info = AppLinkInfo.fromUri(uri)

        assertNotNull(info)
        assertTrue(info?.isVerifiedScheme == true)
        assertFalse(info?.isApplinkCandidate == true)
    }
}
