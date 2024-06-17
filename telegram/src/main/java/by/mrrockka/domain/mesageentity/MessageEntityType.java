package by.mrrockka.domain.mesageentity;

import org.apache.commons.lang3.StringUtils;

public enum MessageEntityType {
  MENTION("mention"),
  BOT_COMMAND("bot_command"),

  TEXT_MENTION("text_mention"),
  OTHER(StringUtils.EMPTY);

  private final String value;

  MessageEntityType(final String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
