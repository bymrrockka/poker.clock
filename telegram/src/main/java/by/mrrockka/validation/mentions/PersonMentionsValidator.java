package by.mrrockka.validation.mentions;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.mesageentity.MetadataEntity;
import eu.vendeli.tgbot.types.msg.EntityType;
import org.springframework.stereotype.Component;

import java.util.List;

//todo: refactor to kotlin
@Component
@Deprecated
public class PersonMentionsValidator {

  public void validateMessageMentions(final MessageMetadata messageMetadata, final int mentionsSize) {
    validateMessageHasNoUserTextMention(messageMetadata);
    validateMessageHasMentionsNotLessThen(messageMetadata, mentionsSize);
  }

  public void validateMessageHasNoUserTextMention(final MessageMetadata messageMetadata) {
    messageMetadata.getEntities().stream()
      .filter(entity -> entity.getType().equals(EntityType.TextMention))
      .findAny()
      .ifPresent(textMention -> {
//        todo: find another way to do that
        throw new PlayerHasNoNicknameException("");
      });
  }

  public void validateMessageHasMentionsNotLessThen(final MessageMetadata messageMetadata, final int size) {
    final var playersMentions = filterUserMentions(messageMetadata);

    if (playersMentions.isEmpty() || playersMentions.size() < size) {
      throw new InsufficientMentionsSizeSpecifiedException(playersMentions.size(), size);
    }
  }

  private List<MetadataEntity> filterUserMentions(final MessageMetadata messageMetadata) {
    return messageMetadata.getMentions().stream().toList();
  }
}
