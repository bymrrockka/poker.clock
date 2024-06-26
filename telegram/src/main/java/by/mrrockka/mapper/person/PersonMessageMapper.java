package by.mrrockka.mapper.person;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PersonMessageMapper {

  private final TelegramPersonMapper personMapper;

  public List<TelegramPerson> map(final MessageMetadata messageMetadata) {
    return personMapper.mapMessageToTelegramPersons(messageMetadata.mentions().toList(), messageMetadata.chatId());
  }

}
