package by.mrrockka;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FakerProvider {
  private static final Faker FAKER = new Faker();

  public static Faker faker() {
    return FAKER;
  }
}
