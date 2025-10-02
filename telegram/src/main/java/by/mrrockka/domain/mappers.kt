package by.mrrockka.domain

import eu.vendeli.tgbot.types.User
import java.util.*

fun User.toPerson(): Person {
    return BasicPerson(
            id = UUID.randomUUID(),
            firstname = firstName,
            lastname = lastName,
            nickname = username,
    )
}