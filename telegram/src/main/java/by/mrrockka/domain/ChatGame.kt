package by.mrrockka.domain

import by.mrrockka.domain.MessageMetadata.MessageMetadataBuilder

data class ChatGame(
        val game: Game,
        val messageMetadata: MessageMetadata,
) {

    //todo: remove
    companion object {
        @JvmStatic
        fun builder(): MessageMetadataBuilder = MessageMetadataBuilder()
    }

    @Deprecated(message = "Use constructor instead.")
    class ChatGameBuilder {
        lateinit var game: Game
        lateinit var messageMetadata: MessageMetadata

        fun game(game: Game): ChatGameBuilder {
            this.game = game; return this
        }

        fun messageMetadata(messageMetadata: MessageMetadata): ChatGameBuilder {
            this.messageMetadata = messageMetadata; return this
        }
    }
}