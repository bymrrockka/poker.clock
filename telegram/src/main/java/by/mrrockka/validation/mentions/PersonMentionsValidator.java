package by.mrrockka.validation.mentions;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.mesageentity.MessageEntity;
import by.mrrockka.domain.mesageentity.MessageEntityType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonMentionsValidator {

  public void validateMessageMentions(final MessageMetadata messageMetadata, final int mentionsSize) {
    validateMessageHasNoUserTextMention(messageMetadata);
    validateMessageHasMentionsNotLessThen(messageMetadata, mentionsSize);
  }

  public void validateMessageHasNoUserTextMention(final MessageMetadata messageMetadata) {
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
