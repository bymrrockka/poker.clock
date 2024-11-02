package by.mrrockka.mapper;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.exception.BusinessException;
import lombok.Builder;
import org.junit.jupiter.params.provider.Arguments;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

class PersonMessageMapperTest {

  private static final Long CHAT_ID = 123L;

  private final TelegramPersonMapper personMapper = Mappers.getMapper(TelegramPersonMapper.class);

  @Builder
  private record PersonsMessageArgument(String message, MessageMetadata metadata, List<TelegramPerson> persons,
                                        Class<? extends BusinessException> exception) {}

  private static Stream<Arguments> updateMessage() {
    return Stream.of(
      Arguments.of(
        PersonsMessageArgument.builder()
          .metadata(
            MessageMetadataCreator.domain(builder -> builder
              .chatId(CHAT_ID)
              .text("""
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
              .text("""
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
              .text("""
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

}
