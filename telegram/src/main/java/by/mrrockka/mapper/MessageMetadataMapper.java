package by.mrrockka.mapper;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.repo.game.TelegramGameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Instant;
import java.util.Collections;

@Mapper(imports = {Instant.class, UsernameReplacer.class, Collections.class}, uses = MessageEntityMapper.class)
public interface MessageMetadataMapper {

  @Mapping(source = "chat.id", target = "chatId")
  @Mapping(target = "createdAt", expression = "java(Instant.ofEpochSecond(message.getDate()))")
  @Mapping(source = "messageId", target = "id")
  @Mapping(target = "command", expression = "java(UsernameReplacer.replaceUsername(message))")
  @Mapping(target = "replyTo", conditionQualifiedByName = "replyToMessage", expression = "java(this.map(message.getReplyToMessage()))")
  @Mapping(target = "entities", source = "entities")
  MessageMetadata map(Message message);

  @Mapping(source = "createdAt", target = "createdAt")
  @Mapping(source = "chatId", target = "chatId")
  @Mapping(source = "messageId", target = "id")
  @Mapping(target = "replyTo", ignore = true)
  @Mapping(target = "command", ignore = true)
  @Mapping(target = "entities", expression = "java(Collections.emptyList())")
  MessageMetadata map(TelegramGameEntity entity);

}
