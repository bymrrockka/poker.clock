package by.mrrockka.mapper;

import by.mrrockka.domain.mesageentity.MessageEntity;
import by.mrrockka.domain.mesageentity.MessageEntityType;
import org.mapstruct.Mapper;

import java.util.Arrays;

@Mapper
@Deprecated(forRemoval = true)
public interface MessageEntityMapper {

  MessageEntity map(org.telegram.telegrambots.meta.api.objects.MessageEntity messageEntity);

  default MessageEntityType mapType(final String type) {
    return Arrays.stream(MessageEntityType.values())
      .filter(entityType -> entityType.value().equals(type))
      .findFirst()
      .orElse(MessageEntityType.OTHER);
  }

}
