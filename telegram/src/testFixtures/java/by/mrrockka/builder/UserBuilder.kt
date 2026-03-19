package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.domain.BasicPerson
import eu.vendeli.tgbot.types.User

class UserBuilder(init: (UserBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    internal var id: Long? = null
    internal var username: String? = null

    fun id(id: Long?) {
        this.id = id
    }

    fun username(username: String) {
        this.username = username
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    fun build(): User {
        val firstname = randoms.firstname()
        val lastname = randoms.lastname()
        return User(
                id = id ?: randoms.userid(),
                isBot = false,
                firstName = firstname,
                lastName = lastname,
                username = username ?: "${firstname.lowercase()}_${lastname.lowercase()}",
        )
    }
}

fun user(init: (UserBuilder.() -> Unit) = {}) = UserBuilder(init).build()
fun user(randoms: TelegramRandoms) = UserBuilder { randoms(randoms) }.build()
fun BasicPerson.toUser(): User = user {
    username(this@toUser.nickname ?: "")
}