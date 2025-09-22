package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata

interface MessageParser<T> {
    fun parse(metadata: MessageMetadata): T

    fun String.ifMe(metadata: MessageMetadata): String {
        check(metadata.from?.username != null) { "Only user can use @me mention" }
        return if (this == "me") metadata.from.username!! else this
    }
}