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

val testNamer = TestNamer()
val sourceRoot = File("src/test/resources")

class TestNamer {
    fun nameFor(testMethod: Method): String {
        return nameFromMethod(testMethod)
    }

    private fun nameFromMethod(testMethod: Method): String {
        val nameAnnotation = testMethod.getAnnotation(Name::class.java)
        return nameAnnotation?.value ?: testMethod.name
    }
}

fun ExtensionContext.dir() =
        File(
                File(sourceRoot, Sources.pathForPackage(this.requiredTestClass.`package`)),
                this.requiredTestClass.simpleName,
        )

class CustomSourceOfApproval(dir: File, fileExtension: String) : FileSystemSourceOfApproval(dir, dir, fileExtension, Reporters.fileSystemReporter()) {

    constructor(context: ExtensionContext, fileExtension: String) : this(context.dir(), fileExtension)

    override fun <T : Any?> checkActualAgainstApproved(testName: String, serializer: Serializer<T>, checker: Checker<T>) {
        (IO.readResource(approvedFor(testName), serializer) as String?)?.let { approved ->
            assertThat(approved).isEqualTo(IO.readResource(actualFor(testName), serializer) as String)
        } ?: fail("No approved file found")
    }
}

abstract class AbstractApproverExtension(
        open val storeKey: String,
        open val enabledKey: String,
        open val fileExtension: String,
) : BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {
    private val testNamer = TestNamer()

    override fun beforeTestExecution(context: ExtensionContext) {
        store(context).put(storeKey, Approver(testNamer.nameFor(context.requiredTestMethod), CustomSourceOfApproval(context, fileExtension)))
    }

    override fun afterTestExecution(context: ExtensionContext) {
        val enabled = store(context)[enabledKey].let { (it ?: false) as Boolean }
        if (!context.executionException.isPresent && enabled) {
            val approver = store(context).get(storeKey) as Approver
            if (!approver.satisfactionChecked()) {
                approver.assertSatisfied()
            }
        }
    }

    override fun supportsParameter(parameterContext: ParameterContext, extenstionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type.isAssignableFrom(Approver::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? {
        return if (parameterContext.parameter.type.isAssignableFrom(Approver::class.java)) {
            store(extensionContext).put(enabledKey, true)
            store(extensionContext)[storeKey]

        } else null
    }

    private fun store(context: ExtensionContext): Store {
        return context.getStore(Namespace.create(context.requiredTestClass.name, context.requiredTestMethod.name))
    }
}


class TextApproverExtension(
        storeKey: String = "text.approver",
        enabledKey: String = "text.approver.enabled",
        fileExtension: String = ".txt",
) : AbstractApproverExtension(storeKey, enabledKey, fileExtension)

inline fun <reified A> A.textApprover(name: String) = Approver(
        "${A::class.java.simpleName}.${name}",
        CustomSourceOfApproval(
                File(
                        File(File("src/test/resources"), Sources.pathForPackage(A::class.java.`package`)),
                        A::class.java.simpleName,
                ),
                ".txt",
        ),
)

class JsonApproverExtension(
        storeKey: String = "json.approver",
        enabledKey: String = "json.approver.enabled",
        fileExtension: String = ".json",
) : AbstractApproverExtension(storeKey, enabledKey, fileExtension)


inline fun <reified A> A.jsonApprover(name: String) = Approver(
        "${A::class.java.simpleName}.${name}",
        CustomSourceOfApproval(
                File(
                        File(File("src/test/resources"), Sources.pathForPackage(A::class.java.`package`)),
                        A::class.java.simpleName,
                ),
                ".json",
        ),
)
