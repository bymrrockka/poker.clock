package by.mrrockka.mapper;

import by.mrrockka.bot.properties.TelegramBotsProperties;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.mesageentity.MessageEntity;
import by.mrrockka.domain.mesageentity.MessageEntityType;
import by.mrrockka.repo.game.TelegramGameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(imports = {Instant.class, MeMentionMapper.class, Collections.class})
public abstract class MessageMetadataMapper {
  @Autowired
  private MessageEntityMapper messageEntityMapper;
  @Autowired
  private TelegramBotsProperties telegramBotsProperties;

  @Mapping(source = "chat.id", target = "chatId")
  @Mapping(target = "createdAt", expression = "java(Instant.ofEpochSecond(message.getDate()))")
  @Mapping(source = "messageId", target = "id")
  @Mapping(target = "command", expression = "java(MeMentionMapper.replaceMeMention(message))")
  @Mapping(target = "replyTo", conditionQualifiedByName = "replyToMessage", expression = "java(this.map(message.getReplyToMessage()))")
  @Mapping(target = "entities", source = "message", qualifiedByName = "mapEntities")
  @Mapping(target = "fromNickname", source = "message.from.userName")
  public abstract MessageMetadata map(Message message);

  @Mapping(source = "createdAt", target = "createdAt")
  @Mapping(source = "chatId", target = "chatId")
  @Mapping(source = "messageId", target = "id")
  @Mapping(target = "replyTo", ignore = true)
  @Mapping(target = "command", ignore = true)
  @Mapping(target = "fromNickname", ignore = true)
  @Mapping(target = "entities", expression = "java(Collections.emptyList())")
  public abstract MessageMetadata map(TelegramGameEntity entity);

  @Named("mapEntities")
  public List<MessageEntity> mapEntities(final Message message) {
    final var entities = message.getEntities().stream()
      .distinct()
      .filter(this::isBotMention)
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

  private boolean isBotMention(org.telegram.telegrambots.meta.api.objects.MessageEntity entity) {
    return !(entity.getText().contains(telegramBotsProperties.getNickname()) && entity.getType().equals(
      MessageEntityType.MENTION.value()));
  }
}
