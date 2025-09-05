package by.mrrockka.mapper;

import by.mrrockka.domain.MetadataEntity;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.repo.person.TelegramPersonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(imports = UUID.class, uses = PersonMapper.class)
@Deprecated(forRemoval = true)
public interface TelegramPersonMapper {

  TelegramPerson mapToTelegramPerson(TelegramPersonEntity entity);

  List<TelegramPerson> mapToTelegramPersons(List<TelegramPersonEntity> entities);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "chatId", source = "chatId")
  @Mapping(target = "nickname", expression = "java(entity.getText().replaceAll(\"@\", \"\"))")
  @Mapping(target = "lastname", ignore = true)
  @Mapping(target = "firstname", ignore = true)
  TelegramPerson mapMessageToTelegramPerson(MetadataEntity entity, long chatId);

  default List<TelegramPerson> mapMessageToTelegramPersons(final List<MetadataEntity> entities, final long chatId) {
    return entities.stream()
      .map(entity -> mapMessageToTelegramPerson(entity, chatId))
      .toList();
  }
}
