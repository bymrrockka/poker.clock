package by.mrrockka.parser

import by.mrrockka.creator.MessageMetadataCreator
import by.mrrockka.domain.PollTask
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.scheduling.support.CronExpression

class PollMessageParserTest {

    private val parser = PollMessageParser()

    private var text = """
        /create_poll
        cron: 10 00 10 1-6 FEB MON-FRI    
        message:    Poker Night on Friday 19:30. Are you going?    
        options:    
        1. Yes - participant 
        2 No   
        3   Maybe - participant
        
        """

    @Test
    fun `when metadata text contains cron expresssion then should extract it`() {
        val metadata = MessageMetadataCreator.domain {
            it.text(text)
        }

        val expected = CronExpression.parse("10 00 10 1-6 FEB MON-FRI")
        assertThat(parser.parseCron(metadata))
                .isEqualTo(expected)
    }

    @Test
    fun `when metadata text contains message text then should extract it`() {
        val metadata = MessageMetadataCreator.domain {
            it.text(text)
        }

        val expected = "Poker Night on Friday 19:30. Are you going?"
        assertThat(parser.parseMessageText(metadata))
                .isEqualTo(expected)
    }

    @Test
    fun `when metadata text contains poll options then should extract it`() {
        val metadata = MessageMetadataCreator.domain {
            it.text(text)
        }

        val expected = listOf(
                PollTask.Option("Yes", true),
                PollTask.Option("No"),
                PollTask.Option("Maybe", true),
        )
        assertThat(parser.parseOptions(metadata))
                .isEqualTo(expected)
    }


}