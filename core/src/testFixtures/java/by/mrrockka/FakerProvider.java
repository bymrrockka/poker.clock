package by.mrrockka;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FakerProvider {
  private static final Faker FAKER = new Faker(new Random(20));

  public static Faker faker() {
    return FAKER;
  }
}
