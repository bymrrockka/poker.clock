package by.mrrockka.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class PokerClockBotKeyboard extends InlineKeyboardMarkup {


  private static List<List<InlineKeyboardButton>> KEYBOARD = List.of(
    List.of(
      InlineKeyboardButton.builder()
        .text("Something")
        .callbackData("some callback data")
        .build()
    )
  );

  public PokerClockBotKeyboard() {
    super(KEYBOARD);
  }

}
