package android.ai.applinkanddeeplink.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ArchitectureRulesTest {

    @Test
    fun `classes in model package should be data classes`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .withPackage("..model..")
            .assertTrue { it.hasDataModifier }
    }

    @Test
    fun `navigation package should be decoupled from feature module activities`() {
        Konsist
            .scopeFromProduction()
            .files
            .withPackage("..navigation..")
            .assertTrue {
                !it.text.contains("DeepLinkActivity") && !it.text.contains("AppLinkActivity")
            }
    }

    @Test
    fun `test classes should end with Test suffix`() {
        Konsist
            .scopeFromTest()
            .classes()
            .assertTrue { it.name.endsWith("Test") }
    }

    @Test
    fun `classes in data packages should be data classes`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .withPackage("..data..")
            .assertTrue { it.hasDataModifier }
    }
}
