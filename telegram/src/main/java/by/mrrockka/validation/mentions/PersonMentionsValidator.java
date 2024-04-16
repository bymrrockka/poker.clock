package by.mrrockka.validation.mentions;

import by.mrrockka.domain.MessageEntity;
import by.mrrockka.domain.MessageEntityType;
import by.mrrockka.domain.MessageMetadata;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonMentionsValidator {

  @Value("${telegrambots.name}")
  @Setter
  private String botName;

  public void validateMessageMentions(final MessageMetadata messageMetadata) {
    validateMessageHasUserTextMention(messageMetadata);
    validateMessageHasInsufficientAmountOfMentions(messageMetadata);
  }

  public void validateMessageHasUserTextMention(final MessageMetadata messageMetadata) {
    messageMetadata.entities().stream()
      .filter(entity -> entity.type().equals(MessageEntityType.TEXT_MENTION))
      .findAny()
      .ifPresent(textMention -> {
        throw new PlayerHasNoNicknameException(textMention.text());
      });
  }

  public void validateMessageHasInsufficientAmountOfMentions(final MessageMetadata messageMetadata) {
    final var playersMentions = filterUserMentions(messageMetadata);

    validateMessageHasNoMentions(messageMetadata);

    if (playersMentions.size() == 1) {
      throw new OnlyOnePlayerSpecifiedException();
    }
  }

  public void validateMessageHasNoMentions(final MessageMetadata messageMetadata) {
    final var playersMentions = filterUserMentions(messageMetadata);

    if (playersMentions.isEmpty()) {
      throw new NoPlayersException();
    }
  }

  private List<MessageEntity> filterUserMentions(final MessageMetadata messageMetadata) {
    //    todo: verify if there is a case to add filter to message metadata (at least partially)
    return messageMetadata.entities().stream()
      .filter(entity -> entity.type().equals(MessageEntityType.MENTION))
      .filter(entity -> !entity.text().contains(botName))
      .toList();
  }
}
