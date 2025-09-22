package by.mrrockka.builder

import by.mrrockka.Randoms

@BuilderDsl
abstract class AbstractBuilder<Ran : Randoms>(protected var randoms: Ran) {
    fun randoms(randoms: Ran) {
        this.randoms = randoms
    }
}

@DslMarker
@Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.TYPE,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.TYPEALIAS,
)
annotation class BuilderDsl
