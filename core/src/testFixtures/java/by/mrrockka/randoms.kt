package by.mrrockka

import by.mrrockka.Randoms.Companion.sharedRandoms
import com.github.javafaker.Faker
import java.math.BigDecimal
import java.time.Instant
import java.util.*


open class Randoms(
        open val random: Random = sharedRandoms.random,
        open val faker: Faker = Faker(random)
) {

    fun instant(): Instant = Instant.ofEpochMilli(random.nextLong())
    fun firstname(): String = faker.name().firstName()
    fun lastname(): String = faker.name().lastName()
    fun decimal(from: Int = 10, to: Int = 100): BigDecimal = BigDecimal.valueOf(faker.number().numberBetween(from, to).toLong())
    fun int(from: Int = 10, to: Int = 100): Int = faker.number().numberBetween(from, to)
    fun long(from: Long = 10, to: Long = 100): Long = faker.number().numberBetween(from, to)
    fun uuid(): UUID {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return UUID.nameUUIDFromBytes(bytes)
    }

    companion object {
        @JvmStatic
        val sharedRandoms = randoms()

        fun randoms(): Randoms {
            val random = Random(0)
            return Randoms(random)
        }

    }
}

fun resetRandom() = sharedRandoms.random.setSeed(0)
