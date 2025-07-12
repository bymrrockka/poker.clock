package by.mrrockka.parser;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.mapper.TelegramPersonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BountyMessageParserTest {

  private final TelegramPersonMapper personMapper = Mappers.getMapper(TelegramPersonMapper.class);
  private final BountyMessageParser bountyMessageParser = new BountyMessageParser(personMapper);

  private static Stream<Arguments> bountyMessage() {
    return Stream.of(
      Arguments.of("/bounty @kinger kicked @queen", "queen", "kinger"),
      Arguments.of("/bounty     @kinger          kicked      @queen", "queen", "kinger"),
      Arguments.of("/bounty @queen kicked @jackas", "jackas", "queen")
    );
  }

  @ParameterizedTest
  @MethodSource("bountyMessage")
  void givenBountyMessageMetadata_whenParseAttempt_shouldParseToPair(final String command, final String left,
                                                                     final String right) {
    final var messageMetadata = MessageMetadataCreator.domain(builder -> builder
      .text(command)
      .entities(List.of(
        MessageEntityCreator.domainMention("@%s".formatted(left)),
        MessageEntityCreator.domainMention("@%s".formatted(right))
      ))
    );

    final var actual = bountyMessageParser.parse(messageMetadata);
    assertThat(actual.getLeft()).isEqualTo(left);
    assertThat(actual.getRight()).isEqualTo(right);
  }

  @Test
  void givenInvalidBountyMessageMetadata_whenParseAttempt_shouldThrowException() {
    final var message = "/bounty @kinger kicked";
    final var messageMetadata = MessageMetadataCreator.domain(builder -> builder
      .text(message)
      .entities(List.of(
        MessageEntityCreator.domainMention("@kinger")
      ))
    );

    assertThatThrownBy(() -> bountyMessageParser.parse(messageMetadata))
      .isInstanceOf(InvalidMessageFormatException.class);
  }
}