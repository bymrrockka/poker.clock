package by.mrrockka

import com.github.javafaker.Faker
import java.util.Random

class TelegramRandoms(
        override val random: Random = telegramRandoms.random,
        override val faker: Faker = Faker(random),
) : CoreRandoms(random, faker) {
    fun updateid(): Int = faker.number().numberBetween(1, 100)
    fun messageid(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)
    fun chatid(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)
    fun userid(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)

    companion object {
        @JvmStatic
        val telegramRandoms = telegramRandoms()

        fun telegramRandoms(seed: String? = null): TelegramRandoms {
            val random = Random(seed.hashCode().toLong())
            return TelegramRandoms(random)
        }

    }
}