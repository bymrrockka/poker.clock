package by.mrrockka.builder

import by.mrrockka.Randoms
import by.mrrockka.domain.Person
import by.mrrockka.sharedRandoms
import java.util.*


class PersonBuilder(
        init: PersonBuilder.() -> Unit
) {
    var randoms = sharedRandoms
    var id: UUID? = null
    var firstname: String? = null
    var lastname: String? = null
    var username: String? = null

    init {
        init()
    }

    fun build(): Person = Person(
            id = id ?: randoms.uuid(),
            firstname = firstname ?: randoms.firstname(),
            lastname = lastname ?: randoms.lastname(),
            nickname = username ?: randoms.username()
    )

}

fun person(builder: PersonBuilder.() -> Unit = {}): Person {
    return PersonBuilder(builder).build()
}

fun person(randoms: Randoms): Person {
    return PersonBuilder { this.randoms = randoms }.build()
}