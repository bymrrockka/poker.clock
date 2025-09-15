package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Component
import java.util.*
import kotlin.text.RegexOption.MULTILINE

@Component
class PollMessageParser : MessageParser<PollTask> {

    private val cronRegex = "^cron:( *)(.*)$".toRegex(MULTILINE);
    private val textRegex = "^message:( *)([\\w \\d:_\\-.,?]+)$".toRegex(MULTILINE);
    private val optionsText = "options:";
    private val optionRegex = "^[\\d .]*(?<text>[\\w \\d_.,?!@#$%^&*()<>/|\"\';+={}\\[\\]~`]+)( - (?<participant>participant)|)( *)$".toRegex(MULTILINE);

    override fun parse(metadata: MessageMetadata): PollTask {
        return PollTask(
                id = UUID.randomUUID(),
                chatId = metadata.chatId,
                messageId = metadata.id,
                cron = parseCron(metadata),
                message = parseMessageText(metadata),
                options = parseOptions(metadata),
                createdAt = metadata.createdAt,
        )
    }

    private fun parseCron(messageMetadata: MessageMetadata): CronExpression {
        val match = cronRegex.find(messageMetadata.text.trimIndent())
        check(match != null) { "Schedule cron should be populated" }
        val (_, cron) = match.destructured
        return CronExpression.parse(cron.trim())
    }

    private fun parseMessageText(messageMetadata: MessageMetadata): String {
        val match = textRegex.find(messageMetadata.text.trimIndent())
        check(match != null) { "Message should be populated" }
        val (_, text) = match.destructured
        return text.trim()
    }

    private fun parseOptions(metadata: MessageMetadata): List<PollTask.Option> {
        check(metadata.text.contains(optionsText)) { "Options block should be specified" }
        return metadata.text.trimIndent()
                .run { substringAfterLast(optionsText).lines() }
                .filter { it.isNotBlank() }
                .mapNotNull {
                    val match = optionRegex.find(it)
                    if (match != null) {
                        PollTask.Option(
                                text = match.groups["text"]!!.value.trim(),
                                participant = match.groups["participant"]?.value != null,
                        )
                    } else null
                }
    }
}