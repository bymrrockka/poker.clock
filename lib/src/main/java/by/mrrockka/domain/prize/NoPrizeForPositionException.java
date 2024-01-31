package by.mrrockka.domain.prize;

public class NoPrizeForPositionException extends RuntimeException{
  public NoPrizeForPositionException(int position) {
    super("No prize found for %d position".formatted(position));
  }
}
