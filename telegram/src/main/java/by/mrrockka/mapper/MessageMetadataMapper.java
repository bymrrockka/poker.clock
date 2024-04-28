package by.mrrockka.mapper;

import by.mrrockka.domain.MessageEntity;
import by.mrrockka.domain.MessageEntityType;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.repo.game.TelegramGameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(imports = {Instant.class, MeMentionMapper.class, Collections.class})
public interface MessageMetadataMapper {

  @Mapping(source = "chat.id", target = "chatId")
  @Mapping(target = "createdAt", expression = "java(Instant.ofEpochSecond(message.getDate()))")
  @Mapping(source = "messageId", target = "id")
  @Mapping(target = "command", expression = "java(MeMentionMapper.replaceMeMention(message))")
  @Mapping(target = "replyTo", conditionQualifiedByName = "replyToMessage", expression = "java(this.map(message.getReplyToMessage()))")
  @Mapping(target = "entities", source = "message", qualifiedByName = "mapEntities")
  @Mapping(target = "fromNickname", source = "message.from.userName")
  MessageMetadata map(Message message);

  @Mapping(source = "createdAt", target = "createdAt")
  @Mapping(source = "chatId", target = "chatId")
  @Mapping(source = "messageId", target = "id")
  @Mapping(target = "replyTo", ignore = true)
  @Mapping(target = "command", ignore = true)
  @Mapping(target = "fromNickname", ignore = true)
  @Mapping(target = "entities", expression = "java(Collections.emptyList())")
  MessageMetadata map(TelegramGameEntity entity);


  @Named("mapEntities")
  default List<MessageEntity> mapEntities(final Message message) {
    final var messageEntityMapper = Mappers.getMapper(MessageEntityMapper.class);

    final var entities = message.getEntities().stream()
      .distinct()
      .map(messageEntityMapper::map)
      .collect(Collectors.toList());

    if (MeMentionMapper.hasMeMention(message)) {
      entities.add(MessageEntity.builder()
                     .text("@%s".formatted(message.getFrom().getUserName()))
                     .type(MessageEntityType.MENTION)
                     .build());
    }

    return entities;
  }
}
