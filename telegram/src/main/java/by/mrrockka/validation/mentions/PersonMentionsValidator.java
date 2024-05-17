package by.mrrockka.validation.mentions;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.mesageentity.MessageEntity;
import by.mrrockka.domain.mesageentity.MessageEntityType;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonMentionsValidator {
  //todo: I see lots of usage of this field, probably worth creating model for options instead of it
  @Value("${telegrambots.nickname}")
  @Setter
  private String botName;

  public void validateMessageMentions(final MessageMetadata messageMetadata, final int mentionsSize) {
    validateMessageHasUserTextMention(messageMetadata);
    validateMessageHasMentionsNotLessThen(messageMetadata, mentionsSize);
  }

  public void validateMessageHasUserTextMention(final MessageMetadata messageMetadata) {
    messageMetadata.entities().stream()
      .filter(entity -> entity.type().equals(MessageEntityType.TEXT_MENTION))
      .findAny()
      .ifPresent(textMention -> {
//        todo: find another way to do that
        throw new PlayerHasNoNicknameException(textMention.text());
      });
  }

  public void validateMessageHasMentionsNotLessThen(final MessageMetadata messageMetadata, final int size) {
    final var playersMentions = filterUserMentions(messageMetadata);

    if (playersMentions.isEmpty() || playersMentions.size() < size) {
      throw new InsufficientMentionsSizeSpecifiedException(playersMentions.size(), size);
    }
  }

  private List<MessageEntity> filterUserMentions(final MessageMetadata messageMetadata) {
    return messageMetadata.mentions().toList();
  }
}
