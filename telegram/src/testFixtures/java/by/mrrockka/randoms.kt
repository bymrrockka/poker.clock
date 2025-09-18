package by.mrrockka

import com.github.javafaker.Faker
import java.util.Random

class TelegramRandoms(
        override val random: Random = telegramRandoms.random,
        override val faker: Faker = Faker(random),
        override val seed: String? = null,
) : CoreRandoms(random, faker, seed) {
    private var messageId: Long = 0L

    fun updateid(): Int = faker.number().numberBetween(1, 100)
    fun messageid(): Long = messageId++
    fun chatid(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)
    fun userid(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)

    override fun reset() {
        super.reset()
        messageId = 0
    }

    companion object {
        @JvmStatic
        val telegramRandoms = telegramRandoms()

        fun telegramRandoms(seed: String? = null): TelegramRandoms {
            val random = Random(seed.hashCode().toLong())
            return TelegramRandoms(random)
        }
    }
}