package by.mrrockka.creator;

import by.mrrockka.domain.MessageEntity;
import by.mrrockka.domain.MessageEntityType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.EntityType;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageEntityCreator {

  public static MessageEntity domainMention(final String text) {
    return domainEntity(builder -> builder.type(MessageEntityType.MENTION).text(text));
  }

  public static MessageEntity domainEntity() {
    return domainEntity(null);
  }

  public static MessageEntity domainEntity(final Consumer<MessageEntity.MessageEntityBuilder> domainBuilderConsumer) {
    final var domainBuilder = MessageEntity.builder()
      .type(MessageEntityType.BOT_COMMAND)
      .text("");

    if (nonNull(domainBuilderConsumer)) {
      domainBuilderConsumer.accept(domainBuilder);
    }

    return domainBuilder.build();
  }

  public static org.telegram.telegrambots.meta.api.objects.MessageEntity apiEntity() {
    return apiEntity(null);
  }

  public static org.telegram.telegrambots.meta.api.objects.MessageEntity apiEntity(
    final Consumer<org.telegram.telegrambots.meta.api.objects.MessageEntity.MessageEntityBuilder> apiBuilderConsumer) {
    final var apiBuilder = org.telegram.telegrambots.meta.api.objects.MessageEntity.builder()
      .offset(0)
      .length(8)
      .type(EntityType.BOTCOMMAND)
      .text("/command");

    if (nonNull(apiBuilderConsumer)) {
      apiBuilderConsumer.accept(apiBuilder);
    }

    return apiBuilder.build();
  }

}
