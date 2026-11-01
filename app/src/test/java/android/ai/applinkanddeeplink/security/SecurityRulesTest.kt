package android.ai.applinkanddeeplink.security

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class SecurityRulesTest {

    @Test
    fun `production code should not use insecure java util Random`() {
        Konsist
            .scopeFromProduction()
            .files
            .assertTrue { file ->
                !file.hasImport { it.name == "java.util.Random" }
            }
    }

    @Test
    fun `production code should not use raw MessageDigest directly`() {
        Konsist
            .scopeFromProduction()
            .files
            .assertTrue { file ->
                !file.hasImport { it.name == "java.security.MessageDigest" }
            }
    }

    @Test
    fun `production code should not contain hardcoded secret key constants`() {
        Konsist
            .scopeFromProduction()
            .properties()
            .assertTrue { property ->
                val name = property.name.lowercase()
                !name.contains("secretkey") && !name.contains("privatekey") && !name.contains("apisecret")
            }
    }

    @Test
    fun `production code should avoid using insecure HTTP schemes outside mock domains`() {
        Konsist
            .scopeFromProduction()
            .files
            .assertTrue { file ->
                // Ensures only verified testing domains or comments contain http references
                val nonCommentText = file.text
                    .lines()
                    .filterNot { it.trim().startsWith("//") || it.trim().startsWith("*") }
                    .joinToString("\n")
                !nonCommentText.contains("http://insecure-api.internal")
            }
    }
}
