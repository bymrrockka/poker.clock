package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata

interface MessageParser<T> {
    fun parse(metadata: MessageMetadata): T
}