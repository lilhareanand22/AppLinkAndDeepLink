package android.ai.applinkanddeeplink.router

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppRouterTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun resolveIntent_invalidUri_returnsNonResolvableResult() {
        val result = AppRouter.resolveIntent(context, "")
        assertFalse(result.isResolvable)
        assertNull(result.scheme)
        assertNull(result.host)
        assertEquals("Invalid URI", result.linkTypeDescription)
    }

    @Test
    fun resolveIntent_customSchemeDeepLink_identifiesPhase2Link() {
        val uri = "myapp://deeplink/product/101"
        val result = AppRouter.resolveIntent(context, uri)
        assertEquals("myapp", result.scheme)
        assertEquals("deeplink", result.host)
        assertEquals("/product/101", result.path)
        assertEquals("Phase 2 Custom Scheme Deep Link", result.linkTypeDescription)
    }

    @Test
    fun resolveIntent_verifiedDomainAppLink_identifiesPhase3Link() {
        val uri = "https://www.example.com/details/456"
        val result = AppRouter.resolveIntent(context, uri)
        assertEquals("https", result.scheme)
        assertEquals("www.example.com", result.host)
        assertEquals("Phase 3 Verified Android App Link", result.linkTypeDescription)
    }

    @Test
    fun resolveIntent_standardExternalWebLink_identifiesExternalWeb() {
        val uri = "https://www.google.com/search?q=android"
        val result = AppRouter.resolveIntent(context, uri)
        assertEquals("https", result.scheme)
        assertEquals("www.google.com", result.host)
        assertEquals("Standard External Web Link", result.linkTypeDescription)
    }

    @Test
    fun resolveIntent_otherCustomProtocol_identifiesCustomScheme() {
        val uri = "customproto://test/resource"
        val result = AppRouter.resolveIntent(context, uri)
        assertEquals("customproto", result.scheme)
        assertEquals("Other Custom Protocol (customproto://)", result.linkTypeDescription)
    }

    @Test
    fun launchImplicitUri_withActionView_returnsResult() {
        val result = AppRouter.launchImplicitUri(context, "https://www.example.com/details/1")
        assertNotNull(result)
    }
}
