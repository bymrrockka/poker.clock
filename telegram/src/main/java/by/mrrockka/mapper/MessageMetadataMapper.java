package by.mrrockka.mapper;

import by.mrrockka.domain.MessageMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Instant;

@Mapper(imports = Instant.class)
public interface MessageMetadataMapper {

  @Mapping(source = "chat.id", target = "chatId")
  @Mapping(target = "createdAt", expression = "java(Instant.ofEpochSecond(message.getDate()))")
  @Mapping(source = "messageId", target = "messageId")
  @Mapping(source = "text", target = "command")
  @Mapping(target = "replyTo", conditionQualifiedByName = "replyToMessage", expression = "java(this.map(message.getReplyToMessage()))")
  MessageMetadata map(Message message);
}
