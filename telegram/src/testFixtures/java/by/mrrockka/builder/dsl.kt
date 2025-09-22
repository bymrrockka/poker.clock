package by.mrrockka.builder

@DslMarker
@Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.TYPE,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.TYPEALIAS,
)
annotation class BddDsl

