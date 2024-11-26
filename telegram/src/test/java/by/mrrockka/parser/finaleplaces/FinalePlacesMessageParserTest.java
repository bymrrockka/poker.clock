package by.mrrockka.parser.finaleplaces;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.TelegramPersonCreator;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.exception.BusinessException;
import by.mrrockka.mapper.TelegramPersonMapper;
import by.mrrockka.parser.InvalidMessageFormatException;
import by.mrrockka.service.exception.FinalPlaceContainsNicknameOfNonExistingPlayerException;
import lombok.Builder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinalePlacesMessageParserTest {

  private final TelegramPersonMapper personMapper = Mappers.getMapper(TelegramPersonMapper.class);
  private final FinalePlacesMessageParser finalePlacesMessageParser = new FinalePlacesMessageParser(personMapper);

  @Builder
  private record FinalePlacesArgument(MessageMetadata metadata, Map<Integer, TelegramPerson> result,
                                      Class<? extends BusinessException> exception) {}

  private static Stream<Arguments> finalePlacesMessageWithMentions() {
    return Stream.of(
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("""
                      /finaleplaces
                      1 @mrrockka
                      2 @ararat
                      3 @andrei
                      """)
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@andrei")
              ))
            )).result(Map.of(
            1, TelegramPersonCreator.domain("mrrockka"),
            2, TelegramPersonCreator.domain("ararat"),
            3, TelegramPersonCreator.domain("andrei")
          )).build()
      ),
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("""
                      /finaleplaces
                      1 - @mrrockka
                      2 -@ararat
                      3-@andrei
                      """)
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@andrei")
              ))
            )).result(Map.of(
            1, TelegramPersonCreator.domain("mrrockka"),
            2, TelegramPersonCreator.domain("ararat"),
            3, TelegramPersonCreator.domain("andrei")
          )).build()
      ),
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("""
                      /finaleplaces
                      1 @mrrockka, 2. @ararat,3- @andrei
                      """)
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@andrei")
              ))
            )).result(Map.of(
            1, TelegramPersonCreator.domain("mrrockka"),
            2, TelegramPersonCreator.domain("ararat"),
            3, TelegramPersonCreator.domain("andrei")
          )).build()
      ),
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("""
                      /finaleplaces
                      1= @mrrockka, 2. @ararat,
                      3: @andrei
                      """)
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@andrei")
              ))
            )).result(Map.of(
            1, TelegramPersonCreator.domain("mrrockka"),
            2, TelegramPersonCreator.domain("ararat"),
            3, TelegramPersonCreator.domain("andrei")
          )).build()
      ),
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("/finaleplaces 1 @mrrockka, 2. @ararat,3- @andrei")
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@andrei")
              ))
            )).result(Map.of(
            1, TelegramPersonCreator.domain("mrrockka"),
            2, TelegramPersonCreator.domain("ararat"),
            3, TelegramPersonCreator.domain("andrei")
          )).build()
      ),
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("/finaleplaces 1 @mrrockka, 2. @ararat,3- @AnDreI")
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@AnDreI")
              ))
            )).result(Map.of(
            1, TelegramPersonCreator.domain("mrrockka"),
            2, TelegramPersonCreator.domain("ararat"),
            3, TelegramPersonCreator.domain("AnDreI")
          )).build()
      )
    );
  }

  @ParameterizedTest
  @MethodSource("finalePlacesMessageWithMentions")
  void givenFinalePlacesMetadata_whenAttemptToParse_shouldReturnPositionAndTelegramPerson(
    final FinalePlacesArgument argument) {
    assertThat(finalePlacesMessageParser.parse(argument.metadata()))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .ignoringFieldsMatchingRegexes(".*id", ".*chatId")
      .isEqualTo(argument.result());
  }

  private static Stream<Arguments> invalidMessageWithMentions() {
    return Stream.of(
//      case 1 - finale places text does not meet regex places
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("/finaleplaces 1@mrrockka, 2@ararat,3@andrei")
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@andrei")
              ))
            )).exception(InvalidMessageFormatException.class)
          .build()),
//      case 2 - finale places text has no positions
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("/finaleplaces\n@mrrockka @ararat")
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat")
              ))
            )).exception(InvalidMessageFormatException.class)
          .build()),
//      case 3 - finale places text is ok but mentions size is less than positions
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("/finaleplaces 1 @mrrockka, 2 @ararat, 3 @andrei")
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat")
              ))
            )).exception(FinalePlacesDoNotMatchMentionsSizeException.class)
          .build()),
//      case 4 - finale places text is ok but mentions size is more than positions
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("/finaleplaces 1 @mrrockka, 2 @ararat, 3 @andrei")
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@andrei"),
                MessageEntityCreator.domainMention("@okmasdfoakmsdf")
              ))
            )).exception(FinalePlacesDoNotMatchMentionsSizeException.class)
          .build()),
//      case 5 - finale places text is ok and mentions size is same but mentions contain different nickname
      Arguments.of(
        FinalePlacesArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(metadata -> metadata
              .text("/finaleplaces 1 @mrrockka, 2 @ararat, 3 @andrei")
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ararat"),
                MessageEntityCreator.domainMention("@okmasdfoakmsdf")
              ))
            )).exception(FinalPlaceContainsNicknameOfNonExistingPlayerException.class)
          .build())
    );
  }

  @ParameterizedTest
  @MethodSource("invalidMessageWithMentions")
  void givenInvalidFinalePlacesMetadata_whenParseAttempt_shouldThrowException(final FinalePlacesArgument argument) {
    assertThatThrownBy(() -> finalePlacesMessageParser.parse(argument.metadata()))
      .isInstanceOf(argument.exception());
  }
}