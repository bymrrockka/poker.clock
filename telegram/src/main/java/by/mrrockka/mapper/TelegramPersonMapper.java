package by.mrrockka.mapper;

import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.mesageentity.MessageEntity;
import by.mrrockka.repo.person.TelegramPersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(imports = UUID.class, uses = PersonMapper.class)
public interface TelegramPersonMapper {

  TelegramPerson mapToTelegramPerson(TelegramPersonEntity entity);

  List<TelegramPerson> mapToTelegramPersons(List<TelegramPersonEntity> entities);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "chatId", source = "chatId")
  @Mapping(target = "nickname", expression = "java(entity.text().replaceAll(\"@\", \"\"))")
  @Mapping(target = "lastname", ignore = true)
  @Mapping(target = "firstname", ignore = true)
  TelegramPerson mapMessageToTelegramPerson(MessageEntity entity, long chatId);

  default List<TelegramPerson> mapMessageToTelegramPersons(final List<MessageEntity> entities, final long chatId) {
    return entities.stream()
      .map(entity -> mapMessageToTelegramPerson(entity, chatId))
      .toList();
  }
}
