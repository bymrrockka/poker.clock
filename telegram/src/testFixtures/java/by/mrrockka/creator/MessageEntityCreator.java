package by.mrrockka.creator;

import by.mrrockka.domain.MetadataEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.EntityType;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated(forRemoval = true)
public final class MessageEntityCreator {

  public static MetadataEntity domainMention(final String text) {
    return domainEntity(builder -> builder.type(eu.vendeli.tgbot.types.msg.EntityType.Mention).text(text));
  }

  public static MetadataEntity domainCommand(final String text) {
    return domainEntity(builder -> builder.type(eu.vendeli.tgbot.types.msg.EntityType.BotCommand).text(text));
  }

  public static MetadataEntity domainEntity() {
    return domainEntity(null);
  }

  public static MetadataEntity domainEntity(final Consumer<MetadataEntity.MessageEntityBuilder> domainBuilderConsumer) {
    final var domainBuilder = MetadataEntity.builder()
      .type(eu.vendeli.tgbot.types.msg.EntityType.BotCommand)
      .text("");

    if (nonNull(domainBuilderConsumer)) {
      domainBuilderConsumer.accept(domainBuilder);
    }

    return domainBuilder.build();
  }

  public static org.telegram.telegrambots.meta.api.objects.MessageEntity apiMention(final String text,
                                                                                    final String mention) {
    return apiEntity(
      bulder -> bulder.offset(text.indexOf(mention)).length(mention.length()).type(EntityType.MENTION));
  }

  public static org.telegram.telegrambots.meta.api.objects.MessageEntity apiCommand(final String text,
                                                                                    final String command) {
    return apiEntity(
      bulder -> bulder.offset(text.indexOf(command)).length(command.length()));
  }

  public static org.telegram.telegrambots.meta.api.objects.MessageEntity apiEntity() {
    return apiEntity(null);
  }

  public static org.telegram.telegrambots.meta.api.objects.MessageEntity apiEntity(
    final Consumer<org.telegram.telegrambots.meta.api.objects.MessageEntity.MessageEntityBuilder> apiBuilderConsumer) {
    final var apiBuilder = org.telegram.telegrambots.meta.api.objects.MessageEntity.builder()
      .offset(0)
      .length(0)
      .type(EntityType.BOTCOMMAND);

    if (nonNull(apiBuilderConsumer)) {
      apiBuilderConsumer.accept(apiBuilder);
    }

    return apiBuilder.build();
  }

}
