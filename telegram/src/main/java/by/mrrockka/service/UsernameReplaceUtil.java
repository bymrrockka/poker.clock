package by.mrrockka.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
class UsernameReplaceUtil {

  String replaceUsername(Update update) {
    return update.getMessage().getText()
      .replaceFirst("@me(\b|$)", "@" + update.getMessage().getFrom().getUserName());
  }
}
