package by.mrrockka.mapper;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.Person;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import lombok.Builder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntryMessageMapperTest {
  private final TelegramPersonMapper personMapper = Mappers.getMapper(TelegramPersonMapper.class);
  private final EntryMessageMapper entryMessageMapper = new EntryMessageMapper(personMapper);

  private static Stream<Arguments> entryMessage() {
    return Stream.of(
      Arguments.of("/entry @kinger 60", BigDecimal.valueOf(60)),
      Arguments.of("/entry @kinger", null)
    );
  }

  @ParameterizedTest
  @MethodSource("entryMessage")
  void givenEntryMessage_whenMapAttempt_shouldParseToPair(final String command, final BigDecimal amount) {
    final var actual = entryMessageMapper.map(command);
    assertThat(actual.getKey()).isEqualTo("kinger");
    if (nonNull(amount)) {
      assertThat(actual.getValue()).contains(amount);
    } else {
      assertThat(actual.getValue()).isEmpty();
    }
  }


  private static Stream<Arguments> invalidEntryMessage() {
    return Stream.of(
      Arguments.of("/entry 60 @kinger"),
      Arguments.of("/entry@kinger"),
      Arguments.of("/entry"),
      Arguments.of("@kinger/entry")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidEntryMessage")
  void givenInvalidEntryMessage_whenMapAttempt_shouldThrowException(final String message) {
    assertThatThrownBy(() -> entryMessageMapper.map(message))
      .isInstanceOf(InvalidMessageFormatException.class);
  }

  @Builder
  private record EntryArgument(MessageMetadata metadata, Set<String> nicknames, BigDecimal amount) {}

  private static Stream<Arguments> entryWithMentionsMessage() {
    return Stream.of(
      Arguments.of(
        EntryArgument.builder()
          .metadata(MessageMetadataCreator.domain(metadata -> metadata.command("/entry @kinger 60")
            .entities(List.of(MessageEntityCreator.domainMention("@kinger")))))
          .nicknames(Set.of("kinger"))
          .amount(BigDecimal.valueOf(60))
          .build()),
      Arguments.of(
        EntryArgument.builder()
          .metadata(MessageMetadataCreator.domain(metadata -> metadata.command("/entry @kinger")
            .entities(List.of(MessageEntityCreator.domainMention("@kinger")))))
          .nicknames(Set.of("kinger"))
          .amount(null)
          .build()),
      Arguments.of(
        EntryArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(
              metadata -> metadata.command("/entry @kinger @asadf @asdfasdf @koomko 60")
                .entities(List.of(
                  MessageEntityCreator.domainMention("@kinger"),
                  MessageEntityCreator.domainMention("@asadf"),
                  MessageEntityCreator.domainMention("@asdfasdf"),
                  MessageEntityCreator.domainMention("@koomko")
                ))))
          .nicknames(Set.of("kinger", "asadf", "asdfasdf", "koomko"))
          .amount(BigDecimal.valueOf(60))
          .build())
    );
  }

  @ParameterizedTest
  @MethodSource("entryWithMentionsMessage")
  void givenEntryMessageWithMentions_whenMapAttempt_shouldParseToPair(final EntryArgument argument) {
    final var actual = entryMessageMapper.map(argument.metadata());

    final var actualNicknames = actual.keySet().stream()
      .map(Person::getNickname)
      .collect(Collectors.toSet());

    assertThat(actualNicknames).isEqualTo(argument.nicknames());

    actual.values()
      .forEach(actualValue -> {
        if (nonNull(argument.amount())) {
          assertThat(actualValue).contains(argument.amount());
        } else {
          assertThat(actualValue).isEmpty();
        }
      });
  }


  private static Stream<Arguments> invalidEntryWithMentionsMessage() {
    return Stream.of(
      Arguments.of("/entry 60 @kinger"),
      Arguments.of("/entry@kinger"),
      Arguments.of("/entry"),
      Arguments.of("@kinger/entry")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidEntryWithMentionsMessage")
  void givenInvalidEntryMessageWithMentions_whenMapAttempt_shouldThrowException(final String message) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.command(message));

    assertThatThrownBy(() -> entryMessageMapper.map(metadata))
      .isInstanceOf(InvalidMessageFormatException.class);
  }
}