package by.mrrockka.parser.finaleplaces

import by.mrrockka.creator.MessageEntityCreator
import by.mrrockka.creator.MessageMetadataCreator
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.FinalePlacesMessageParser
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ThrowableAssert
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class FinalePlacesMessageParserTest {
    private val finalePlacesMessageParser = FinalePlacesMessageParser()

    data class FinalePlacesArgument(
            val metadata: MessageMetadata,
            val result: Map<Int, String>? = emptyMap(),
            val exception: Class<out RuntimeException>? = null
    )

    @ParameterizedTest
    @MethodSource("finalePlacesMessageWithMentions")
    fun `should parse valid finale places message`(argument: FinalePlacesArgument) {
        assertThat(finalePlacesMessageParser.parse(argument.metadata))
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .ignoringFieldsMatchingRegexes(".*id", ".*chatId")
                .isEqualTo(argument.result)
    }

    @ParameterizedTest
    @MethodSource("invalidMessageWithMentions")
    fun `should throw exceptions with invalid message`(argument: FinalePlacesArgument) {
        Assertions.assertThatThrownBy(ThrowableAssert.ThrowingCallable { finalePlacesMessageParser.parse(argument.metadata) })
                .isInstanceOf(argument.exception)
    }

    companion object {
        @JvmStatic
        fun finalePlacesMessageWithMentions(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text(
                                        """
                                      /finaleplaces
                                      1 @mrrockka
                                      2 @ararat
                                      3 @andrei
                                      
                                      """.trimIndent()
                                )
                                it.metadataEntities(listOf(
                                        MessageEntityCreator.domainMention("@mrrockka"),
                                        MessageEntityCreator.domainMention("@ararat"),
                                        MessageEntityCreator.domainMention("@andrei")
                                ))
                            },
                                    result = mapOf(1 to "mrrockka", 2 to "ararat", 3 to "andrei")
                            )
                    ),

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text(
                                        """
                                     /finaleplaces
                                     1 @mrrockka, 2. @ararat,3 @andrei
                                      
                                     """.trimIndent()
                                )
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                                MessageEntityCreator.domainMention("@andrei")
                                        ))
                            },
                                    result = mapOf(1 to "mrrockka", 2 to "ararat", 3 to "andrei")
                            )
                    ),

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text(
                                        """
                                      /finaleplaces
                                      1 @mrrockka, 2. @ararat,
                                      3. @andrei
                                     
                                     """.trimIndent()
                                )
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                                MessageEntityCreator.domainMention("@andrei")
                                        ))
                            },
                                    result = mapOf(1 to "mrrockka", 2 to "ararat", 3 to "andrei")
                            )
                    ),

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text("/finaleplaces 1 @mrrockka, 2. @ararat,3 @andrei")
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                                MessageEntityCreator.domainMention("@andrei")
                                        ))
                            },
                                    result = mapOf(1 to "mrrockka", 2 to "ararat", 3 to "andrei")
                            )
                    ),

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text("/finaleplaces 1 @mrrockka, 2. @ararat,3 @AnDreI")
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                                MessageEntityCreator.domainMention("@andrei")
                                        ))
                            },
                                    result = mapOf(1 to "mrrockka", 2 to "ararat", 3 to "AnDreI")
                            )
                    ),

                    )
        }

        @JvmStatic
        fun invalidMessageWithMentions(): Stream<Arguments> {
            return Stream.of(

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text("/finaleplaces 1@mrrockka, 2@ararat,3@andrei")
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                                MessageEntityCreator.domainMention("@andrei")
                                        ))
                            },
                                    exception = IllegalStateException::class.java
                            )
                    ),

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text("/finaleplaces\n@mrrockka @ararat")
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                                MessageEntityCreator.domainMention("@andrei")
                                        ))
                            },
                                    exception = IllegalStateException::class.java)
                    ),

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text("/finaleplaces 1-@mrrockka, 2:@ararat, 3=@andrei")
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                                MessageEntityCreator.domainMention("@andrei")
                                        ))
                            },
                                    exception = IllegalStateException::class.java
                            )
                    ),

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text("/finaleplaces 1 @mrrockka, 2 @ararat, 3 @andrei")
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                        ))
                            },
                                    exception = IllegalStateException::class.java
                            )
                    ),

                    Arguments.of(
                            FinalePlacesArgument(metadata = MessageMetadataCreator.domain {
                                it.text("/finaleplaces 1 @ mrrockka, 2 @ ararat, 3 @ andrei")
                                it.metadataEntities(
                                        listOf(
                                                MessageEntityCreator.domainMention("@mrrockka"),
                                                MessageEntityCreator.domainMention("@ararat"),
                                                MessageEntityCreator.domainMention("@andrei")
                                        ))
                            },
                                    exception = IllegalStateException::class.java
                            )
                    ),

                    )
        }
    }
}