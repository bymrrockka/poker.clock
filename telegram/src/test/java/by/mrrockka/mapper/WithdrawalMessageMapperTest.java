package by.mrrockka.mapper;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.Person;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
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

class WithdrawalMessageMapperTest {

  public static final String BOT_NAME = "pokerbot";
  private final TelegramPersonMapper personMapper = Mappers.getMapper(TelegramPersonMapper.class);
  private final WithdrawalMessageMapper withdrawalMessageMapper = new WithdrawalMessageMapper(personMapper);

  @BeforeEach
  void setup() {
    withdrawalMessageMapper.setBotName(BOT_NAME);
  }

  @Builder
  private record WithdrawalArgument(MessageMetadata metadata, Set<String> nicknames, BigDecimal amount) {}

  private static Stream<Arguments> withdrawalWithMentionsMessage() {
    return Stream.of(
      Arguments.of(
        WithdrawalArgument.builder()
          .metadata(MessageMetadataCreator.domain(metadata -> metadata.command("/withdrawal @kinger 60")
            .entities(List.of(MessageEntityCreator.domainMention("@kinger")))))
          .nicknames(Set.of("kinger"))
          .amount(BigDecimal.valueOf(60))
          .build()),
      Arguments.of(
        WithdrawalArgument.builder()
          .metadata(MessageMetadataCreator.domain(metadata -> metadata.command("/withdrawal @%s 60".formatted(BOT_NAME))
            .entities(List.of(MessageEntityCreator.domainMention("@%s".formatted(BOT_NAME))))))
          .nicknames(Set.of())
          .amount(BigDecimal.valueOf(60))
          .build()),
      Arguments.of(
        WithdrawalArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(
              metadata -> metadata.command("/withdrawal @kinger @asadf @asdfasdf @koomko 30")
                .entities(List.of(
                  MessageEntityCreator.domainMention("@kinger"),
                  MessageEntityCreator.domainMention("@asadf"),
                  MessageEntityCreator.domainMention("@asdfasdf"),
                  MessageEntityCreator.domainMention("@koomko")
                ))))
          .nicknames(Set.of("kinger", "asadf", "asdfasdf", "koomko"))
          .amount(BigDecimal.valueOf(30))
          .build()),
      Arguments.of(
        WithdrawalArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(
              metadata -> metadata.command("/withdrawal @kinger @asadf @asdfasdf @koomko @%s 45".formatted(BOT_NAME))
                .entities(List.of(
                  MessageEntityCreator.domainMention("@kinger"),
                  MessageEntityCreator.domainMention("@asadf"),
                  MessageEntityCreator.domainMention("@asdfasdf"),
                  MessageEntityCreator.domainMention("@koomko"),
                  MessageEntityCreator.domainMention("@%s".formatted(BOT_NAME))
                ))))
          .nicknames(Set.of("kinger", "asadf", "asdfasdf", "koomko"))
          .amount(BigDecimal.valueOf(45))
          .build())
    );
  }

  @ParameterizedTest
  @MethodSource("withdrawalWithMentionsMessage")
  void givenEntryMessageWithMentions_whenMapAttempt_shouldParseToPair(final WithdrawalArgument argument) {
    final var actual = withdrawalMessageMapper.map(argument.metadata());

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
  void givenInvalidEntryMessageWithMentions_whenMapAttempt_shouldThrowException(final String message) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.command(message));

    assertThatThrownBy(() -> withdrawalMessageMapper.map(metadata))
      .isInstanceOf(InvalidMessageFormatException.class);
  }

}