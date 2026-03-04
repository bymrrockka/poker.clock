package by.mrrockka

import com.github.javafaker.Faker
import java.util.*
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
class TelegramRandoms(
        override val random: Random = telegramRandoms.random,
        override val faker: Faker = Faker(random),
        override val seed: String? = null,
) : CoreRandoms(random, faker, seed) {
    @OptIn(ExperimentalAtomicApi::class)
    @Volatile
    private var messageId = AtomicLong(0L)

    fun updateid(): Int = faker.number().numberBetween(1, 100)
    fun messageid(): Long = messageId.incrementAndFetch()
    fun chatid(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)
    fun userid(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)
    fun pollid(): String = faker.numerify("#".repeat(20))

    override fun reset() {
        super.reset()
        messageId = AtomicLong(0L)
    }

    companion object {
        @JvmStatic
        var telegramRandoms = telegramRandoms()

        fun telegramRandoms(seed: String? = null): TelegramRandoms {
            val random = Random(seed.hashCode().toLong())
            telegramRandoms = TelegramRandoms(random)
            return telegramRandoms
        }
    }
}