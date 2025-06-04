package by.mrrockka

import com.github.javafaker.Faker
import java.math.BigDecimal
import java.util.*

val sharedRandoms = randoms()

class Randoms(
        val random: Random = sharedRandoms.random,
        val faker: Faker = Faker(random)
) {

    fun username(): String = faker.name().username().replace("\\.".toRegex(), "_")
    fun firstname(): String = faker.name().firstName()
    fun lastname(): String = faker.name().lastName()
    fun decimal(from: Int = 10, to: Int = 100): BigDecimal = BigDecimal.valueOf(faker.number().numberBetween(from, to).toLong())
    fun uuid(): UUID {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return UUID.nameUUIDFromBytes(bytes)
    }
}

fun randoms(seed: String? = null): Randoms {
    val random = Random(seed.hashCode().toLong())
    return Randoms(random)
}