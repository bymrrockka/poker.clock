package by.mrrockka.mapper;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.exception.BusinessException;
import by.mrrockka.mapper.person.PersonMessageMapper;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import by.mrrockka.validation.mentions.NoPlayersException;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonMessageMapperTest {

  private static final Long CHAT_ID = 123L;

  private final TelegramPersonMapper personMapper = Mappers.getMapper(TelegramPersonMapper.class);
  private final PersonMessageMapper personMessageMapper = new PersonMessageMapper(personMapper);

  @Builder
  private record PersonsMessageArgument(String message, MessageMetadata metadata, List<TelegramPerson> persons,
                                        Class<? extends BusinessException> exception) {}

  @BeforeEach
  void setup() {
    personMessageMapper.setBotName("pokerbot");
  }


  @Deprecated(since = "1.1.0", forRemoval = true)
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
                     @miscusi   
                             """)
          .persons(List.of(
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("miscusi")
              .chatId(CHAT_ID)
              .build()
          ))
          .build()),
      Arguments.of(
        PersonsMessageArgument.builder()
          .message("""
                     /tournament@pokerbot 
                     buyin:    15zl    
                     stack: 1.5k
                       @mrrockka
                     @ivano 
                      @andrei 
                     @ivano 
                      @andrei 
                     @mrrockka
                     @miscusi   
                             """)
          .persons(List.of(
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("ivano")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("miscusi")
              .chatId(CHAT_ID)
              .build()))
          .build()),
      Arguments.of(
        PersonsMessageArgument.builder()
          .message("""
                     /tournament 
                     buyin:    15zl    
                     stack: 1.5k
                     @mrrockka @ivano @andrei @ivano @andrei @mrrockka @miscusi @pokerbot   
                             """)
          .persons(List.of(
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("ivano")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("miscusi")
              .chatId(CHAT_ID)
              .build()))
          .build()),
      Arguments.of(
        PersonsMessageArgument.builder()
          .message("""
                     /tournament 
                     buyin:    15zl    
                     stack: 1.5k
                     @mrrockka, @ivano, @andrei, @ivano, @andrei, @mrrockka, @miscusi
                             """)
          .persons(List.of(
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("ivano")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("miscusi")
              .chatId(CHAT_ID)
              .build()))
          .build())
    );
  }

  @Deprecated(since = "1.1.0", forRemoval = true)
  @ParameterizedTest
  @MethodSource("personsMessage")
  void givenMessage_whenMapExecuted_shouldReturnPersonsList(final PersonsMessageArgument argument) {
    assertThat(personMessageMapper.map(argument.message(), CHAT_ID))
      .usingRecursiveComparison()
      .ignoringFields("id")
      .isEqualTo(argument.persons());
  }


  @Deprecated(since = "1.1.0", forRemoval = true)
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

  @Deprecated(since = "1.1.0", forRemoval = true)
  @ParameterizedTest
  @MethodSource("noPlayersMessages")
  void givenMessage_whenNoPlayers_thenThrowException(final String message) {
    assertThatThrownBy(() -> personMessageMapper.map(message, CHAT_ID))
      .isInstanceOf(NoPlayersException.class);
  }

  private static Stream<Arguments> updateMessage() {
    return Stream.of(
      Arguments.of(
        PersonsMessageArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(builder -> builder
              .chatId(CHAT_ID)
              .command("""
                         /tournament
                         buy-in: 30  
                         stack: 30k 
                         players: 
                           @mrrockka
                         @miscusi   
                                 """)
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@miscusi")
              ))))
          .persons(List.of(
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("miscusi")
              .chatId(CHAT_ID)
              .build()
          )).build()),
      Arguments.of(
        PersonsMessageArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(builder -> builder
              .chatId(CHAT_ID)
              .command("""
                         /tournament@pokerbot 
                         buyin:    15zl    
                         stack: 1.5k
                           @mrrockka
                         @ivano 
                          @andrei 
                         @ivano 
                          @andrei 
                         @mrrockka
                         @miscusi   
                                 """)
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ivano"),
                MessageEntityCreator.domainMention("@andrei"),
                MessageEntityCreator.domainMention("@miscusi")
              ))))
          .persons(List.of(
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("ivano")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("miscusi")
              .chatId(CHAT_ID)
              .build())).build()),

      Arguments.of(
        PersonsMessageArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(builder -> builder
              .chatId(CHAT_ID)
              .command("""
                         /tournament 
                         buyin:    15zl    
                         stack: 1.5k
                         @mrrockka @ivano @andrei @ivano @andrei @mrrockka @miscusi @pokerbot   
                                 """)
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ivano"),
                MessageEntityCreator.domainMention("@andrei"),
                MessageEntityCreator.domainMention("@miscusi"),
                MessageEntityCreator.domainMention("@pokerbot")
              ))))
          .persons(List.of(
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("ivano")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("miscusi")
              .chatId(CHAT_ID)
              .build())).build()),

      Arguments.of(
        PersonsMessageArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(builder -> builder
              .chatId(CHAT_ID)
              .command("""
                         /tournament 
                         buyin:    15zl    
                         stack: 1.5k
                         @mrrockka, @ivano, @andrei, @ivano, @andrei, @mrrockka, @miscusi
                                 """)
              .entities(List.of(
                MessageEntityCreator.domainMention("@mrrockka"),
                MessageEntityCreator.domainMention("@ivano"),
                MessageEntityCreator.domainMention("@andrei"),
                MessageEntityCreator.domainMention("@miscusi")
              ))))
          .persons(List.of(
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("mrrockka")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("ivano")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("andrei")
              .chatId(CHAT_ID)
              .build(),
            TelegramPerson.telegramPersonBuilder()
              .id(UUID.randomUUID())
              .nickname("miscusi")
              .chatId(CHAT_ID)
              .build())).build())
    );
  }

  @ParameterizedTest
  @MethodSource("updateMessage")
  void givenMessageMeta_whenMapExecuted_shouldReturnPersonsList(final PersonsMessageArgument argument) {
    assertThat(personMessageMapper.map(argument.metadata()))
      .usingRecursiveComparison()
      .ignoringFields("id")
      .isEqualTo(argument.persons());
  }

}
