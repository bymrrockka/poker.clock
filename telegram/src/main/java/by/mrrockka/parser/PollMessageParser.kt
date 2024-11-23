package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Component
import kotlin.text.RegexOption.MULTILINE

@Component
class PollMessageParser {

    private val cronRegex = "^cron:( *)(.*)$".toRegex(MULTILINE);
    private val textRegex = "^message:( *)([\\w \\d:_\\-.,?]+)$".toRegex(MULTILINE);
    private val optionsText = "options:([\n \r\t]*)";
    private val optionRegex = "^([\\d .])*(?<text>[\\w]+)(( - )(?<participant>participant)|)( *)$".toRegex(MULTILINE);

    fun parseCron(messageMetadata: MessageMetadata): CronExpression {
        val (_, cron) = cronRegex.find(messageMetadata.text.trimIndent())!!.destructured
        return CronExpression.parse(cron.trim())
    }

    fun parseMessageText(messageMetadata: MessageMetadata): String {
        val (_, text) = textRegex.find(messageMetadata.text.trimIndent())!!.destructured
        return text.trim()
    }

    fun parseOptions(messageMetadata: MessageMetadata): List<PollTask.Option> {
        return messageMetadata.text.trimIndent()
                .run {
                    this.removeRange(0, this.indexOf(optionsText) + optionsText.length)
                }.run {
                    optionRegex.findAll(this)
                            .map {
                                PollTask.Option(it.groups["text"]!!.value.trim(),
                                        it.groups["participant"]?.value != null)
                            }
                            .toList()
                }
    }

}