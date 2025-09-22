package by.mrrockka.builder

import by.mrrockka.CoreRandoms
import by.mrrockka.CoreRandoms.Companion.coreRandoms
import by.mrrockka.domain.BasicPerson
import java.util.*


class PersonBuilder(
        init: PersonBuilder.() -> Unit,
) : AbstractBuilder<CoreRandoms>(coreRandoms) {
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

fun person(builder: PersonBuilder.() -> Unit = {}): BasicPerson = PersonBuilder(builder).build()