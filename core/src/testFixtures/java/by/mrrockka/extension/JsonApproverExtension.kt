package by.mrrockka.extension

import com.oneeyedmen.okeydoke.*
import com.oneeyedmen.okeydoke.internal.IO
import com.oneeyedmen.okeydoke.sources.FileSystemSourceOfApproval
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ExtensionContext.Store
import org.junit.jupiter.api.fail
import java.io.File
import java.lang.reflect.Method

class JsonApproverExtension : BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {
    private val STORE_KEY = "json.approver"
    private val ENABLED_KEY = "json.approver.enabled"
    private val testNamer = TestNamer()
    private val sourceRoot = File("src/test/resources")

    override fun beforeTestExecution(context: ExtensionContext) {
        store(context).put(
                STORE_KEY,
                JsonApprover(
                        testNamer.nameFor(context.requiredTestClass, context.requiredTestMethod),
                        JsonSourceOfApproval(File(sourceRoot, Sources.pathForPackage(context.requiredTestClass.`package`))),
                )
        )
    }

    override fun afterTestExecution(context: ExtensionContext) {
        val enabled = store(context)[ENABLED_KEY].let { (it ?: false) as Boolean }
        if (!context.executionException.isPresent && enabled) {
            val approver = store(context).get(STORE_KEY) as JsonApprover
            if (!approver.satisfactionChecked()) {
                approver.assertSatisfied()
            }
        }
    }

    override fun supportsParameter(parameterContext: ParameterContext, extenstionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type.isAssignableFrom(JsonApprover::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? {
        return if (parameterContext.parameter.type.isAssignableFrom(JsonApprover::class.java)) {
            store(extensionContext).put(ENABLED_KEY, true)
            store(extensionContext)[STORE_KEY]

        } else null
    }

    private fun store(context: ExtensionContext): Store {
        return context.getStore(Namespace.create(context.requiredTestClass.name, context.requiredTestMethod.name))
    }

    private class TestNamer {
        fun nameFor(testClass: Class<*>, testMethod: Method): String {
            return nameFromClass(testClass) + "." + nameFromMethod(testMethod)
        }

        private fun nameFromMethod(testMethod: Method): String {
            val nameAnnotation = testMethod.getAnnotation(Name::class.java)
            return nameAnnotation?.value ?: testMethod.name
        }

        private fun nameFromClass(testClass: Class<*>): String {
            val nameAnnotation = testClass.getAnnotation(Name::class.java)
            return nameAnnotation?.value ?: testClass.simpleName
        }
    }
}

class JsonApprover(testName: String, sourceOfApproval: SourceOfApproval) : Approver(testName, sourceOfApproval)
class JsonSourceOfApproval(dir: File) : FileSystemSourceOfApproval(dir, dir, ".json", Reporters.fileSystemReporter()) {
    override fun <T : Any?> checkActualAgainstApproved(testName: String, serializer: Serializer<T>, checker: Checker<T>) {
        (IO.readResource(approvedFor(testName), serializer) as String?)?.let { approved ->
            assertThat(approved).isEqualTo(IO.readResource(actualFor(testName), serializer) as String)
        } ?: fail("No approved file found")
    }
}

inline fun <reified A> A.jsonApprover(name: String) = JsonApprover(
        "${A::class.java.simpleName}.${name}",
        JsonSourceOfApproval(File(File("src/test/resources"), Sources.pathForPackage(A::class.java.`package`)))
)
