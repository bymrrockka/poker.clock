package by.mrrockka

import com.github.javafaker.Faker
import java.util.Random

class TelegramRandoms(
        override val random: Random = telegramRandoms.random,
        override val faker: Faker = Faker(random)
) : Randoms(random, faker) {
    fun messageid(from: Int = 10, to: Int = 100): Int = faker.number().numberBetween(from, to)
    fun chatid(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)

    companion object {
        @JvmStatic
        val telegramRandoms = telegramRandoms()

        fun telegramRandoms(seed: String? = null): TelegramRandoms {
            val random = Random(seed.hashCode().toLong())
            return TelegramRandoms(random)
        }

    }
}

fun resetRandom() = TelegramRandoms.telegramRandoms.random.setSeed(0)
