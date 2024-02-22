package by.mrrockka.mapper;

import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.person.NoPlayersException;
import by.mrrockka.mapper.person.PersonMessageMapper;
import lombok.Builder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonMessageMapperTest {

  private static final Long CHAT_ID = 123L;

  private final PersonMessageMapper personMessageMapper = new PersonMessageMapper();

  @Builder
  private record PersonsMessageArgument(String message, List<TelegramPerson> persons) {}

  private static Stream<Arguments> personsMessage() {
    return Stream.of(
      Arguments.of(
        PersonsMessageArgument.builder()
          .message("""
                     /tournament
                     buy-in: 30  
                     stack: 30k 
                     players: 
                       @mrrockka
                     @me   
                             """)
          .persons(List.of(
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("me")
              .chatId(CHAT_ID)
              .build()
          ))
          .build()),
      Arguments.of(
        PersonsMessageArgument.builder()
          .message("""
                     /tournament 
                     buyin:    15zl    
                     stack: 1.5k
                       @mrrockka
                     @ivan 
                      @andrei 
                     @ivan 
                      @andrei 
                     @mrrockka
                     @me   
                             """)
          .persons(List.of(
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("ivan")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("me")
              .chatId(CHAT_ID)
              .build()))
          .build()),
      Arguments.of(
        PersonsMessageArgument.builder()
          .message("""
                     /tournament 
                     buyin:    15zl    
                     stack: 1.5k
                     @mrrockka @ivan @andrei @ivan @andrei @mrrockka @me   
                             """)
          .persons(List.of(
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("ivan")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("me")
              .chatId(CHAT_ID)
              .build()))
          .build()),
      Arguments.of(
        PersonsMessageArgument.builder()
          .message("""
                     /tournament 
                     buyin:    15zl    
                     stack: 1.5k
                     @mrrockka, @ivan, @andrei, @ivan, @andrei, @mrrockka, @me
                             """)
          .persons(List.of(
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("ivan")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.builder()
              .id(UUID.randomUUID())
              .telegram("me")
              .chatId(CHAT_ID)
              .build()))
          .build())
    );
  }

  @ParameterizedTest
  @MethodSource("personsMessage")
  void givenMessage_whenMapExecuted_shouldReturnPersonsList(PersonsMessageArgument argument) {

    assertThat(personMessageMapper.map(argument.message(), CHAT_ID))
      .usingRecursiveComparison()
      .ignoringFields("id")
      .isEqualTo(argument.persons());
  }

  private static Stream<Arguments> noPlayersMessages() {
    return Stream.of(
      Arguments.of(
        """
            /tournament
            buyin:      100  
            stack:50000
          """),
      Arguments.of(
        """
            /tournament
            buyin:      100  
            stack:50000 
            players: 
              @mrrockka
          """)
    );
  }

  @ParameterizedTest
  @MethodSource("noPlayersMessages")
  void givenMessage_whenNoPlayers_thenThrowException(String message) {
    assertThatThrownBy(() -> personMessageMapper.map(message, CHAT_ID))
      .isInstanceOf(NoPlayersException.class);
  }

}
