package by.mrrockka.mapper;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BountyMessageMapperTest {

  private final TelegramPersonMapper personMapper = Mappers.getMapper(TelegramPersonMapper.class);
  private final BountyMessageMapper bountyMessageMapper = new BountyMessageMapper(personMapper);

  @BeforeEach
  void setup() {
    bountyMessageMapper.setBotName("pokerbot");
  }

  private static Stream<Arguments> bountyMessage() {
    return Stream.of(
      Arguments.of("/bounty @kinger kicked @queen", "queen", "kinger"),
      Arguments.of("/bounty     @kinger          kicked      @queen", "queen", "kinger"),
      Arguments.of("/bounty @queen kicked @jackas", "jackas", "queen")
    );
  }

  @ParameterizedTest
  @MethodSource("bountyMessage")
  void givenBountyMessage_whenMapAttempt_shouldParseToPair(final String command, final String left,
                                                           final String right) {
    final var actual = bountyMessageMapper.map(command);
    assertThat(actual.getLeft()).isEqualTo(left);
    assertThat(actual.getRight()).isEqualTo(right);
  }

  @Test
  void givenInvalidBountyMessage_whenMapAttempt_shouldThrowException() {
    final var message = "/bounty @kinger kicked";
    assertThatThrownBy(() -> bountyMessageMapper.map(message))
      .isInstanceOf(InvalidMessageFormatException.class);
  }

  @ParameterizedTest
  @MethodSource("bountyMessage")
  void givenBountyMessageMetadata_whenMapAttempt_shouldParseToPair(final String command, final String left,
                                                                   final String right) {
    final var messageMetadata = MessageMetadataCreator.domain(builder -> builder
      .text(command)
      .entities(List.of(
        MessageEntityCreator.domainMention("@%s".formatted(left)),
        MessageEntityCreator.domainMention("@%s".formatted(right))
      ))
    );

    final var actual = bountyMessageMapper.map(messageMetadata);
    assertThat(actual.getLeft().getNickname()).isEqualTo(left);
    assertThat(actual.getRight().getNickname()).isEqualTo(right);
  }

  @Test
  void givenInvalidBountyMessageMetadata_whenMapAttempt_shouldThrowException() {
    final var message = "/bounty @kinger kicked";
    final var messageMetadata = MessageMetadataCreator.domain(builder -> builder
      .text(message)
      .entities(List.of(
        MessageEntityCreator.domainMention("@kinger")
      ))
    );

    assertThatThrownBy(() -> bountyMessageMapper.map(messageMetadata))
      .isInstanceOf(InvalidMessageFormatException.class);
  }
}