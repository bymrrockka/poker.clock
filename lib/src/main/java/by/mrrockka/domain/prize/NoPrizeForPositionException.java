package by.mrrockka.domain.prize;

class NoPrizeForPositionException extends RuntimeException {
  NoPrizeForPositionException(int position) {
    super("No percentage found for %d position".formatted(position));
  }
}
