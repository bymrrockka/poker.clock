package by.mrrockka

import by.mrrockka.CoreRandoms.Companion.coreRandoms
import by.mrrockka.domain.Payout
import by.mrrockka.domain.Player
import by.mrrockka.domain.total
import by.mrrockka.extension.JsonApproverExtension
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import java.math.BigDecimal

@ExtendWith(JsonApproverExtension::class)
abstract class AbstractTest {

    @AfterEach
    fun afterEach() {
        coreRandoms.reset()
    }

    val objectMapper = ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun Any?.toJsonString() = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

    data class SimplePayout(
            val creditor: String,
            val entries: BigDecimal,
            val total: BigDecimal,
            val debtors: List<SimpleDebtor>,
    )

    data class SimpleDebtor(
            val debtor: String,
            val debt: BigDecimal,
            val entries: BigDecimal,
    )

    internal fun List<Payout>.simplify(players: List<Player>): List<SimplePayout> {
        val personMap = players.associateBy { it.person }
        return map { payout ->
            SimplePayout(
                    creditor = payout.creditor.nickname ?: fail("No creditor nickname found"),
                    entries = personMap[payout.creditor]!!.entries.total(),
                    total = payout.total,
                    debtors = payout.debtors.map { debtor ->
                        SimpleDebtor(
                                debtor = debtor.person.nickname ?: fail("No debtor nickname found"),
                                debt = debtor.debt,
                                entries = personMap[debtor.person]!!.entries.total(),
                        )
                    },
            )
        }
    }
}