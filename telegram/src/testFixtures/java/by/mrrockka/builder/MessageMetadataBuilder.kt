package by.mrrockka.builder

import by.mrrockka.Randoms
import by.mrrockka.sharedRandoms

//TODO: refactor
@Suppress("UNCHECKED_CAST")
class MessageMetadataBuilder(init: (MessageMetadataBuilder.() -> Unit) = {}) {

    var randoms = sharedRandoms
    var messageid: Int? = null

    init {
        init()
    }

//    fun build(): MessageMetadata {
//        return MessageMetadata(
//
//        )
//    }

}

fun message(init: (MessageMetadataBuilder.() -> Unit) = {}) = MessageMetadataBuilder(init)
fun message(randoms: Randoms, init: (MessageMetadataBuilder.() -> Unit) = {}) = message().apply { this.randoms = randoms }.also(init)
