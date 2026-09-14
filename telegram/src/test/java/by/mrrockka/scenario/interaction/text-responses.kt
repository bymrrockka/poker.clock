package by.mrrockka.scenario.interaction

import eu.vendeli.tgbot.types.common.PollAnswer
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.msg.Message
import mockwebserver3.RecordedRequest
import org.junit.jupiter.api.Assertions.assertSame
import tools.jackson.core.JsonPointer

private val replyMarkupPath = JsonPointer.compile("/reply_markup/keyboard")

internal fun Update.toText(): String =
        when {
            message != null -> message!!.toText()
            pollAnswer != null -> pollAnswer!!.toText()

            else -> "Unknown update type"
        }

internal fun Message.replyToText(): String =
        if (replyToMessage != null) """
        |>[${replyToMessage!!.text ?: "reply to"}](#message-${replyToMessage!!.messageId})
        """.trimMargin() else ""

internal fun Message.toText(): String =
        """|${replyToText()}
           |### Message ${messageId}
           |&rarr; Message from <ins>@${from!!.username}</ins>
           |```
           |${text} 
           |```
           |___
           """.trimMargin()

internal fun PollAnswer.toText(): String =
        """
           |### Poll answer
           |&rarr; Message from <ins>@${user!!.username}</ins>
           |```
           |${optionIds.joinToString("\n") { " - $it" }}
           |```
        """.trimMargin()


internal fun RecordedRequest.toText(interaction: Interaction<*>): String {
    val json = toJson()
    return when (interaction) {
        is Interaction.Bot -> {
            val replyMarkup = json.at(replyMarkupPath).findValues("text").map { "(${it.asString()})" }.joinToString(" | ")
            val text = json.findPath("text").asString() + if (replyMarkup.isNotEmpty()) "\n$replyMarkup" else ""
            val replyTo = json.findPath("reply_to_message_id").takeIf { it.values().isNotEmpty() }?.asLong()
                    .let { if (it != null) ">[reply to message id $it](#message-$it)\n" else "" }
            """|$replyTo
               |### Message ${interaction.index}
               |&larr; Message from <ins>Bot</ins>
               |```
               |$text
               |```
               |___
                """.trimMargin()
        }

        is Interaction.Poll -> {
            val question = json.findPath("question").asString()
            val options = json.findPath("options")
                    .mapIndexed { index, option -> "${index + 1}. '${option.findPath("text").asString()}'" }
                    .joinToString("\n")

            """
                |### Message ${interaction.index}
                |&larr; Poll from <ins>Bot</ins>
                |``` 
                |$question
                |$options
                |``` 
                |___
            """.trimMargin()
        }

        is Interaction.Pin -> {
            val id = json.findPath("message_id")
            checkNotNull(id) { "Message id is not found." }
            assertSame(id.asLong(), interaction.data)
            """|
               |### Pin 
               |&larr; Pin from <ins>Bot</ins>
               |[Pinned message ${id.asLong()}](#message-${id.asLong()})
            """.trimMargin()
        }

        is Interaction.Unpin -> {
            val id = json.findPath("message_id")
            checkNotNull(id) { "Message id is not found." }
            assertSame(id.asLong(), interaction.data)
            """|
               |### Unpin 
               |&larr; Unpin from <ins>Bot</ins>
               |[Unpinned message ${id.asLong()}](#message-${id.asLong()})
            """.trimMargin()
        }

        is Interaction.Delete -> {
            val ids = json.findPath("message_ids")
            check(ids != null && ids.values().isNotEmpty()) { "Delete ids are not found." }
            val values = ids.values()
                    .map { it.asLong() }
                    .distinct()
                    .sorted()
            val inMessage = values.containsAll(interaction.data)
            """|
               |### Delete 
               |&larr; Delete from <ins>Bot</ins>
               |>Deleted messages: 
               |${
                if (inMessage)
                    interaction.data.map { " - [message id $it](#message-$it)" }.joinToString("\n")
                else "Delete command does not contain message ids"
            }""".trimMargin()
        }

        else -> error("Interaction type is not recognized")
    }
}