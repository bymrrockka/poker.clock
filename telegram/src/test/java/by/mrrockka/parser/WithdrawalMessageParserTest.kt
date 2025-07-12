package by.mrrockka.parser;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.Person;
import by.mrrockka.mapper.TelegramPersonMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WithdrawalMessageParserTest {

  public static final String BOT_NAME = "pokerbot";
  private final TelegramPersonMapper personMapper = Mappers.getMapper(TelegramPersonMapper.class);
  private final WithdrawalMessageParser withdrawalMessageParser = new WithdrawalMessageParser(personMapper);

  @Builder
  private record WithdrawalArgument(MessageMetadata metadata, Set<String> nicknames, BigDecimal amount) {}

  private static Stream<Arguments> withdrawalWithMentionsMessage() {
    return Stream.of(
      Arguments.of(
        WithdrawalArgument.builder()
          .metadata(MessageMetadataCreator.domain(metadata -> metadata.text("/withdrawal @kinger 60")
            .entities(List.of(MessageEntityCreator.domainMention("@kinger")))))
          .nicknames(Set.of("kinger"))
          .amount(BigDecimal.valueOf(60))
          .build()),
      Arguments.of(
        WithdrawalArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(
              metadata -> metadata.text("/withdrawal @kinger @asadf @asdfasdf @koomko 30")
                .entities(List.of(
                  MessageEntityCreator.domainMention("@kinger"),
                  MessageEntityCreator.domainMention("@asadf"),
                  MessageEntityCreator.domainMention("@asdfasdf"),
                  MessageEntityCreator.domainMention("@koomko")
                ))))
          .nicknames(Set.of("kinger", "asadf", "asdfasdf", "koomko"))
          .amount(BigDecimal.valueOf(30))
          .build())
    );
  }

  @ParameterizedTest
  @MethodSource("withdrawalWithMentionsMessage")
  void givenEntryMessageWithMentions_whenParseAttempt_shouldParseToPair(final WithdrawalArgument argument) {
    final var actual = withdrawalMessageParser.parse(argument.metadata());

    final var actualNicknames = actual.keySet().stream()
      .map(Person::getNickname)
      .collect(Collectors.toSet());

    assertThat(actualNicknames).isEqualTo(argument.nicknames());

    actual.values().forEach(actualValue -> {
      assertThat(actualValue).isEqualTo(argument.amount());
    });
  }

  private static Stream<Arguments> invalidEntryWithMentionsMessage() {
    return Stream.of(
      Arguments.of("/withdrawal 60 @kinger"),
      Arguments.of("/withdrawal @kinger"),
      Arguments.of("/withdrawal@kinger"),
      Arguments.of("/withdrawal@%s".formatted(BOT_NAME)),
      Arguments.of("/withdrawal"),
      Arguments.of("@kinger/withdrawal")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidEntryWithMentionsMessage")
  void givenInvalidEntryMessageWithMentions_whenParseAttempt_shouldThrowException(final String message) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(message));

    assertThatThrownBy(() -> withdrawalMessageParser.parse(metadata))
      .isInstanceOf(InvalidMessageFormatException.class);
  }

}