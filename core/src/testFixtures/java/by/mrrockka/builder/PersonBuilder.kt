package by.mrrockka.builder

import by.mrrockka.Randoms
import by.mrrockka.Randoms.Companion.sharedRandoms
import by.mrrockka.domain.BasicPerson
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

    fun build(): BasicPerson {
        val firstname = firstname ?: randoms.firstname()
        val lastname = lastname ?: randoms.lastname()
        return BasicPerson(
                id = id ?: randoms.uuid(),
                firstname = firstname,
                lastname = lastname,
                nickname = username ?: "${firstname.lowercase()}_${lastname.lowercase()}",
        )
    }
}

fun person(builder: PersonBuilder.() -> Unit = {}): BasicPerson {
    return PersonBuilder(builder).build()
}

fun person(randoms: Randoms): BasicPerson {
    return PersonBuilder { this.randoms = randoms }.build()
}