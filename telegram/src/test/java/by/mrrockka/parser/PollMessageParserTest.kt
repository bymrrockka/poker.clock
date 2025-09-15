package by.mrrockka.parser

import by.mrrockka.creator.MessageMetadataCreator
import by.mrrockka.domain.PollTask
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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

        val actual = parser.parse(metadata)
        assertAll(
                { assertThat(actual.cron).isEqualTo(CronExpression.parse("10 00 10 1-6 FEB MON-FRI")) },
                { assertThat(actual.message).isEqualTo("Poker Night on Friday 19:30. Are you going?") },
                {
                    assertThat(actual.options).isEqualTo(
                            listOf(
                                    PollTask.Option("Yes", true),
                                    PollTask.Option("No"),
                                    PollTask.Option("Maybe", true),
                            ),
                    )
                },
        )
    }
}