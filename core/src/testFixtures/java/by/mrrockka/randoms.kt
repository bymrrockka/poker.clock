package by.mrrockka

import com.github.javafaker.Faker
import java.math.BigDecimal
import java.time.Instant
import java.util.*

interface Randoms {
    val random: Random
    val faker: Faker
    val seed: String?
}

open class CoreRandoms(
        override val random: Random = coreRandoms.random,
        override val faker: Faker = Faker(random),
        override val seed: String? = null,
) : Randoms {

    fun instant(): Instant = Instant.now()
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
        val coreRandoms = randoms()

        fun randoms(seed: String? = null): CoreRandoms {
            val random = Random(seed.hashCode().toLong())
            return CoreRandoms(random)
        }
    }
}

fun Randoms.reset() = random.setSeed(seed.hashCode().toLong())
