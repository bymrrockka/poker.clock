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
    private val optionsText = "options:\n";
    private val optionRegex = "^([\\d .])*(?<text>[\\w]+)(( - )(?<participant>participant)|)$".toRegex(MULTILINE);

    fun parseCron(messageMetadata: MessageMetadata): CronExpression {
        val (_, cron) = cronRegex.find(messageMetadata.text)!!.destructured
        return CronExpression.parse(cron)
    }

    fun parseMessageText(messageMetadata: MessageMetadata): String {
        val (_, text) = textRegex.find(messageMetadata.text)!!.destructured
        return text
    }

    fun parseOptions(messageMetadata: MessageMetadata): List<PollTask.Option> {
        return messageMetadata.text
                .run {
                    this.removeRange(0, this.indexOf(optionsText) + optionsText.length)
                }.run {
                    optionRegex.findAll(this)
                            .map {
                                PollTask.Option(it.groups["text"]!!.value,
                                        it.groups["participant"]?.value != null)
                            }
                            .toList()
                }
    }

}