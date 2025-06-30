package by.mrrockka

import com.github.javafaker.Faker
import java.util.Random

class TelegramRandoms(
        override val random: Random = sharedRandoms.random,
        override val faker: Faker = Faker(random)
) : Randoms(random, faker) {
    fun messageid(): Int = faker.number().numberBetween(0, 100)
    fun chatid(): Long = faker.number().numberBetween(0, 100).toLong()

}

fun randoms(seed: String? = null): Randoms {
    val random = Random(seed.hashCode().toLong())
    return Randoms(random)
}