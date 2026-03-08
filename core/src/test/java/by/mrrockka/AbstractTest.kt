package by.mrrockka

import by.mrrockka.CoreRandoms.Companion.coreRandoms
import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.Debtor
import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import by.mrrockka.domain.ServiceFee
import by.mrrockka.domain.TournamentGame
import by.mrrockka.extension.TextApproverExtension
import by.mrrockka.service.BountyTournamentPlayerSummary
import by.mrrockka.service.toTournamentSummary
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(TextApproverExtension::class)
abstract class AbstractTest {

    @AfterEach
    fun afterEach() {
        coreRandoms.reset()
    }

    fun List<Payout>.text(): String = joinToString("\n") { payout ->
        when (payout.creditor) {
            is BasicPerson -> """
               |Payout to: ${payout.creditor.nickname} 
               |Total: ${payout.total}
               |Debtors:
               |${payout.debtors.toText()}
               |${"_".repeat(30)}
            """.trimMargin()

            is ServiceFee -> """
               |Description: ${payout.creditor.description}
               |URL: ${payout.creditor.url}
               |Total: ${payout.total}
               |Debtors:
               |${payout.debtors.toText()}
               |${"_".repeat(30)}
            """.trimMargin()

            else -> error("Unknown payout type: ${payout.creditor::class.simpleName}")
        }
    }

    fun List<Debtor>.toText(): String = joinToString("\n") { debtor ->
        when (debtor.person) {
            is BasicPerson -> """
                |  - Debtor: ${debtor.person.nickname}
                |    Debt: ${debtor.debt}
            """.trimMargin()

            else -> error("Debtor type is not supported")
        }
    }

    fun Game.text(): String = "Game Summary\n" + when (this) {
        is TournamentGame -> toTournamentSummary()
                .filter { it.position != null }
                .sortedBy { it.position }
                .joinToString("\n") {
                    """
                    |Position ${it.position}
                    |  Prize ${it.prize}
                    |  Buyins: -${it.entries()}
                    |  Nickname ${it.person.nickname}
                """.trimMargin()
                }

        is BountyTournamentGame -> toTournamentSummary()
                .map { it as BountyTournamentPlayerSummary }
                .filter { it.position != null || it.total() > BigDecimal.ZERO }
                .sortedBy { it.position }
                .joinToString("\n") {
                    if (it.position != null) {
                        """
                            |Position ${it.position}
                            |  Prize ${it.prize}
                            |  Buyins: -${it.entries()}
                            |  Bounties: ${it.bounty.total} (taken ${it.bounty.taken} - given ${it.bounty.given})
                            |  Nickname ${it.person.nickname}
                        """.trimMargin()
                    } else {
                        """
                            |Bounty winner
                            |  Buyins: -${it.entries()}
                            |  Bounties: ${it.bounty.total} (taken ${it.bounty.taken} - given ${it.bounty.given})
                            |  Nickname ${it.person.nickname}
                        """.trimMargin()
                    }
                }

        else -> error("Unknown game type!")
    }


}