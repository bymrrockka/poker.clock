package by.mrrockka.builder

import by.mrrockka.CoreRandoms
import by.mrrockka.CoreRandoms.Companion.coreRandoms
import by.mrrockka.domain.BasicPerson
import java.util.*


class PersonBuilder(
        init: PersonBuilder.() -> Unit,
) : AbstractBuilder<CoreRandoms>(coreRandoms) {
    var id: UUID? = null
    var nickname: String? = null

    init {
        init()
    }

    fun build(): BasicPerson {
        val firstname = randoms.firstname()
        val lastname = randoms.lastname()
        return BasicPerson(
                id = id ?: randoms.uuid(),
                nickname = nickname ?: "${firstname.lowercase()}_${lastname.lowercase()}",
        )
    }
}

fun person(builder: PersonBuilder.() -> Unit = {}): BasicPerson = PersonBuilder(builder).build()
fun person(randoms: CoreRandoms, builder: PersonBuilder.() -> Unit = {}): BasicPerson = PersonBuilder { randoms(randoms) }
        .apply(builder)
        .build()
