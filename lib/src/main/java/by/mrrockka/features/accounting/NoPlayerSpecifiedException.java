package by.mrrockka.features.accounting;

class NoPlayerSpecifiedException extends RuntimeException {

  NoPlayerSpecifiedException() {
    super("No players specified");
  }
}
