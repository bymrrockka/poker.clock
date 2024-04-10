package by.mrrockka.mapper;

import by.mrrockka.domain.MessageEntity;
import by.mrrockka.domain.MessageEntityType;
import org.mapstruct.Mapper;

import java.util.Arrays;

@Mapper
public interface MessageEntityMapper {

  MessageEntity map(org.telegram.telegrambots.meta.api.objects.MessageEntity messageEntity);

  default MessageEntityType mapType(final String type) {
    return Arrays.stream(MessageEntityType.values())
      .filter(entityType -> entityType.value().equals(type))
      .findFirst()
      .orElse(MessageEntityType.OTHER);
  }

}
