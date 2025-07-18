package by.mrrockka.domain

import java.util.*

interface Person {
    val id: UUID
    val firstname: String?
    val lastname: String?
    val nickname: String?
}

//todo refactor telegram wrapper
data class BasicPerson(
        override val id: UUID,
        override val firstname: String? = null,
        override val lastname: String? = null,
        override val nickname: String? = null,
) : Person {

    //    todo: remove
    companion object {
        @JvmStatic
        fun personBuilder(): PersonBuilder = PersonBuilder()
    }

    @Deprecated(message = "Use constructor instead.")
    class PersonBuilder {
        lateinit var id: UUID
        var firstname: String? = null
        var lastname: String? = null
        var nickname: String? = null

        fun id(id: UUID): PersonBuilder {
            this.id = id; return this
        }

        fun firstname(firstname: String): PersonBuilder {
            this.firstname = firstname; return this
        }

        fun lastname(lastname: String): PersonBuilder {
            this.lastname = lastname; return this
        }

        fun nickname(nickname: String): PersonBuilder {
            this.nickname = nickname; return this
        }

        fun build(): BasicPerson = BasicPerson(id, firstname, lastname, nickname)
    }
}
