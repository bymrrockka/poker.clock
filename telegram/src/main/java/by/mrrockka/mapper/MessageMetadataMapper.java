package by.mrrockka.mapper;

import by.mrrockka.bot.TelegramBotsProperties;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.MetadataEntity;
import by.mrrockka.domain.mesageentity.MessageEntityType;
import by.mrrockka.repo.game.TelegramGameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(imports = {Instant.class, MeMentionUtil.class, Collections.class})
@Deprecated(forRemoval = true)
public abstract class MessageMetadataMapper {
  @Autowired
  private MessageEntityMapper messageEntityMapper;
  @Autowired
  private TelegramBotsProperties telegramBotsProperties;

  @Mapping(source = "chat.id", target = "chatId")
  @Mapping(target = "createdAt", expression = "java(Instant.ofEpochSecond(message.getDate()))")
  @Mapping(source = "messageId", target = "id")
  @Mapping(target = "text", source = "message", qualifiedByName = "filterText")
  @Mapping(target = "replyTo", expression = "java(this.map(message.getReplyToMessage()))")
  @Mapping(target = "metadataEntities", source = "message", qualifiedByName = "mapMessageEntities")
  @Mapping(target = "fromNickname", source = "message.from.userName")
  @Mapping(target = "entities", expression = "java(Collections.emptyList())")
  public abstract MessageMetadata map(Message message);

  @Mapping(source = "createdAt", target = "createdAt")
  @Mapping(source = "chatId", target = "chatId")
  @Mapping(source = "messageId", target = "id")
  @Mapping(target = "replyTo", ignore = true)
  @Mapping(target = "text", ignore = true)
  @Mapping(target = "fromNickname", ignore = true)
  @Mapping(target = "entities", expression = "java(Collections.emptyList())")
  public abstract MessageMetadata map(TelegramGameEntity entity);

  @Named("mapMessageEntities")
  public List<MetadataEntity> mapMessageEntities(final Message message) {
    final var messageEntities = message.getEntities().stream()
      .distinct()
      .filter(this::isBotNotMention)
      .toList();
    final var entities = new ArrayList<MetadataEntity>();
    for (final org.telegram.telegrambots.meta.api.objects.MessageEntity messageEntity : messageEntities) {
      if (MessageEntityType.BOT_COMMAND.value().equals(messageEntity.getType())) {
        entities.add(MetadataEntity.builder()
                       .text(removeBotNicknameFromCommand(messageEntity.getText()))
//                       .type(MessageEntityType.BOT_COMMAND)
                       .build());
        continue;
      }

      entities.add(messageEntityMapper.map(messageEntity));
    }


    if (MeMentionUtil.hasMeMention(message)) {
      entities.add(MetadataEntity.builder()
                     .text("@%s".formatted(message.getFrom().getUserName()))
//                     .type(MessageEntityType.MENTION)
                     .build());
    }


    return entities;
  }

  private boolean isBotNotMention(org.telegram.telegrambots.meta.api.objects.MessageEntity entity) {
    return !(entity.getText().contains(telegramBotsProperties.getNickname())
      && MessageEntityType.MENTION.value().equals(entity.getType()));
  }

  @Named("filterText")
  public String filterText(final Message message) {
    return removeBotNicknameFromCommand(MeMentionUtil.replaceMeMention(message));
  }

  private String removeBotNicknameFromCommand(final String text) {
    return text.replaceAll("([/\\w]+)@%s".formatted(telegramBotsProperties.getNickname()), "$1");
  }
}
