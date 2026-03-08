package by.mrrockka.domain

import eu.vendeli.tgbot.types.User
import java.util.*

fun User.toPerson(): BasicPerson {
    return BasicPerson(
            id = UUID.randomUUID(),
            nickname = username,
    )
}