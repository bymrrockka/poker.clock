package by.mrrockka.mapper.person;

import by.mrrockka.domain.MessageEntity;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.PersonMapper;
import by.mrrockka.repo.person.TelegramPersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(imports = UUID.class, uses = PersonMapper.class)
public interface TelegramPersonMapper {

  Person mapToPerson(TelegramPerson telegramPerson);

  List<Person> mapToPersons(List<TelegramPerson> telegramPerson);

  TelegramPerson mapToTelegram(TelegramPersonEntity entity);

  List<TelegramPerson> mapToTelegrams(List<TelegramPersonEntity> entities);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "chatId", source = "chatId")
  @Mapping(target = "nickname", expression = "java(entity.text().replaceAll(\"@\", \"\"))")
  @Mapping(target = "lastname", ignore = true)
  @Mapping(target = "firstname", ignore = true)
  TelegramPerson mapMessageToTelegram(MessageEntity entity, long chatId);

  default List<TelegramPerson> mapMessageToTelegrams(final List<MessageEntity> entities, final long chatId) {
    return entities.stream()
      .map(entity -> mapMessageToTelegram(entity, chatId))
      .toList();
  }
}
