package by.mrrockka.parser.finaleplaces;

import by.mrrockka.exception.BusinessException;

class FinalePlacesDoNotMatchMentionsSizeException extends BusinessException {
  FinalePlacesDoNotMatchMentionsSizeException() {
    super("Finale places do not match mentions size.");
  }
}
