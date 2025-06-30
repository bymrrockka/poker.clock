package by.mrrockka.service;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.TelegramPersonCreator;
import by.mrrockka.domain.BasicPerson;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.TelegramPersonMapper;
import by.mrrockka.repo.person.TelegramPersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramPersonServiceTest {

  @Mock
  private TelegramPersonMapper telegramPersonMapper;
  @Mock
  private PersonService personService;
  @Mock
  private TelegramPersonRepository telegramPersonRepository;
  @InjectMocks
  private TelegramPersonService telegramPersonService;

  @Test
  void givenMetadataWithMentions_whenStoreMissedCalled_thenShouldReturnTelegramPersonsList() {
    final var existent = List.of("nickname1", "nickname2", "nickname3");
    final var newNicknames = List.of("newperson1", "newperson2", "newperson3");
    final var notExistentInChat = List.of("notexistent1", "notexistent2", "notexistent3");
    final var allNicknames = Stream.of(existent, newNicknames, notExistentInChat)
      .flatMap(List::stream).toList();

    final ArgumentCaptor<List<BasicPerson>> argument = ArgumentCaptor.forClass(List.class);

    final var metadata = MessageMetadataCreator.domain(meta -> meta.entities(
      allNicknames.stream()
        .map(MessageEntityCreator::domainMention)
        .toList()
    ));

    final var notExistentInChatIds = notExistentInChat.stream().map(nick -> UUID.randomUUID()).toList();

    when(personService.getNewNicknames(allNicknames)).thenReturn(newNicknames);
    when(telegramPersonRepository.findNotExistentInChat(allNicknames, metadata.getChatId()))
      .thenReturn(notExistentInChatIds);

    final var expectedEntities = allNicknames.stream().map(TelegramPersonCreator::entity).toList();
    final var expected = allNicknames.stream().map(TelegramPersonCreator::domain).toList();
    when(telegramPersonRepository.findAllByChatIdAndNicknames(allNicknames, metadata.getChatId()))
      .thenReturn(expectedEntities);
    when(telegramPersonMapper.mapToTelegramPersons(expectedEntities))
      .thenReturn(expected);

    final var actual = telegramPersonService.storeMissed(metadata);
    assertThat(actual).hasSize(allNicknames.size());
    assertThat(actual.stream().map(TelegramPerson::getNickname).toList()).containsAll(allNicknames);

    verify(personService).storeAll(argument.capture());
    final var argValue = argument.getValue();
    final var newPersons = argValue.stream().map(BasicPerson::getNickname).toList();
    assertThat(newPersons).hasSize(newNicknames.size());
    assertThat(newPersons).containsAll(newNicknames);

    verify(telegramPersonRepository).saveAll(
      Stream.of(
          argValue.stream().map(BasicPerson::getId).toList(),
          notExistentInChatIds
        ).flatMap(List::stream)
        .toList()
      , metadata.getChatId()
    );
  }

}