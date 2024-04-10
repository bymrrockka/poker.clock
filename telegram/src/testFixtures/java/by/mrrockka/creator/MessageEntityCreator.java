package by.mrrockka.creator;

import by.mrrockka.domain.MessageEntity;
import by.mrrockka.domain.MessageEntityType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

}
