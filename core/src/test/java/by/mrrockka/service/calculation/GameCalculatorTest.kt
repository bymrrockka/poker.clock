package by.mrrockka.service.calculation

import by.mrrockka.AbstractTest
import by.mrrockka.builder.game
import by.mrrockka.builder.player
import by.mrrockka.domain.*
import com.oneeyedmen.okeydoke.Approver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

class GameCalculatorTest : AbstractTest() {
    private val calculator: GameCalculator = GameCalculator()

    @ParameterizedTest
    @MethodSource("playerSize")
    fun `given players entry equally when one prize place should calculate payouts`(size: Int) {
        val buyin = BigDecimal("10")
        val players = player { this.buyin = buyin }.tournamentBatch(size)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(PositionPrize(1, BigDecimal("100")))
            this.finalePlaces = listOf(FinalPlace(1, players[0]))
        }.tournament()

        val actual = calculator.calculate(game)
        val expect = listOf(Payout(
                creditor = players[0],
                debtors = players
                        .filterNot { it == players[0] }
                        .map { Debtor(it, buyin) }
                        .reversed(),
                total = BigDecimal("10") * (players.size - 1).toBigDecimal()
        ))

        assertThat(actual).isEqualTo(expect)
    }

    @Test
    fun `given players entry with different amounts when one prize place should calculate payouts`(approver: Approver) {
        val size = 10
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(size, buyin) + tournamentPlayer(buyin, 3) + tournamentPlayer(buyin, 4)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(PositionPrize(1, BigDecimal("100")))
            this.finalePlaces = listOf(FinalPlace(1, players[0]))
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given players with equal entries when there are more then one prize positions should calculate payouts`(approver: Approver) {
        val size = 10
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(size, buyin)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0]),
                    FinalPlace(2, players[1]),
            )
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given players with not equal entries when there are more then one prize positions should calculate payouts`(approver: Approver) {
        val size = 10
        val buyin = BigDecimal("10")
        val players = tournamentPlayers(size, buyin) + tournamentPlayer(buyin, 3) + tournamentPlayer(buyin, 4)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0]),
                    FinalPlace(2, players[1]),
            )
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }

    @Test
    fun `given winners has more than one entry should calculate prize pool including entries`(approver: Approver) {
        val size = 10
        val buyin = BigDecimal("10")
        val players = listOf(tournamentPlayer(buyin, 3), tournamentPlayer(buyin, 4)) + tournamentPlayers(size, buyin)

        val game = game {
            this.buyIn = buyin
            this.players = players
            this.prizePool = listOf(
                    PositionPrize(1, BigDecimal("70")),
                    PositionPrize(2, BigDecimal("30")),
            )
            this.finalePlaces = listOf(
                    FinalPlace(1, players[0]),
                    FinalPlace(2, players[1]),
            )
        }.tournament()

        approver.assertApproved(calculator.calculate(game).simplify().toJsonString())
    }


    /*

                @Test
                fun givenPlayerBuyInNotEquallyAndPrizePoolHasMultiplePositionsAndPrizePositionStillHasDebt_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
                    val size = 10
                    val entries = ArrayList<PersonEntries?>(EntriesCreator.entriesList(size, BUY_IN))

                    val firstPlace = entry(List.of<BigDecimal?>(BUY_IN, BUY_IN, BUY_IN))
                    val secondPlace = entry(List.of<BigDecimal?>(BUY_IN, BUY_IN))
                    val thirdPlace = entry(List.of<BigDecimal?>(BUY_IN, BUY_IN, BUY_IN))

                    entries.set(0, firstPlace)
                    entries.set(1, secondPlace)
                    entries.set(2, thirdPlace)

                    val game =
                            GameCreator.tournament(Consumer { builder: by.mrrockka.domain.game.TournamentGame.TournamentGameBuilder? ->
                                builder
                                        .entries(entries)
                                        .finaleSummary(FinaleSummary(finaleSummaries(entries)))
                            }
                            )

                    val actual = strategy.calculate(game)
                    val expect = List.of<Payout?>(
                            payout(
                                    entries.get(0)!!, List.of<Payer?>(
                                    payer(entries.get(2)!!, BigDecimal.valueOf(30)),
                                    payer(entries.get(3)!!, BUY_IN),
                                    payer(entries.get(4)!!, BUY_IN),
                                    payer(entries.get(5)!!, BUY_IN),
                                    payer(entries.get(6)!!, BUY_IN),
                                    payer(entries.get(7)!!, BigDecimal.valueOf(10))
                            )
                            ),
                            payout(
                                    entries.get(1)!!, List.of<Payer?>(
                                    payer(entries.get(8)!!, BUY_IN),
                                    payer(entries.get(9)!!, BUY_IN),
                                    payer(entries.get(7)!!, BigDecimal.valueOf(10))
                            )
                            )
                    )

                    Assertions.assertThat<Payout?>(actual).containsExactlyInAnyOrderElementsOf(expect)
                }

                @Test
                fun givenPlayerBuyEquallyAndPrizePoolAmountHasDecimalPoints_thenShouldCreateListOfDebtorsOrderedByTheAmount() {
                    val size = 17
                    val entries = ArrayList<PersonEntries?>(EntriesCreator.entriesList(size, BUY_IN))

                    val prizePool = PrizePool(
                            List.of<PositionPrize?>(
                                    PositionPrize(1, BigDecimal.valueOf(63)),
                                    PositionPrize(2, BigDecimal.valueOf(26)),
                                    PositionPrize(3, BigDecimal.valueOf(11))
                            )
                    )

                    val finalePlaces = FinalePlaces(
                            List.of<FinalPlace?>(
                                    FinalPlace(1, entries.get(0)!!.person),
                                    FinalPlace(2, entries.get(1)!!.person),
                                    FinalPlace(3, entries.get(2)!!.person)
                            )
                    )

                    val total = entries.stream()
                            .map<BigDecimal> { obj: PersonEntries? -> obj!!.total() }
                            .reduce { obj: BigDecimal?, augend: BigDecimal? -> obj!!.add(augend) }
                            .orElse(BigDecimal.ZERO)

                    val finaleSummary = FinaleSummary.of(prizePool, finalePlaces, total)
                    val game =
                            GameCreator.tournament(Consumer { builder: by.mrrockka.domain.game.TournamentGame.TournamentGameBuilder? ->
                                builder
                                        .entries(entries)
                                        .finaleSummary(finaleSummary)
                            }
                            )

                    val actual = strategy.calculate(game)

                    Assertions.assertThat(actual.get(0)!!.total()).isEqualTo(
                            prizePool.calculatePrizeAmountFor(1, total).subtract(
                                    BUY_IN
                            )
                    )
                    Assertions.assertThat(actual.get(1)!!.total()).isEqualTo(
                            prizePool.calculatePrizeAmountFor(2, total).subtract(
                                    BUY_IN
                            )
                    )

                    val lastPositionAmount = total
                            .subtract(prizePool.calculatePrizeAmountFor(1, total))
                            .subtract(prizePool.calculatePrizeAmountFor(2, total))
                            .subtract(BUY_IN)

                    Assertions.assertThat(actual.get(2)!!.total()).isEqualTo(lastPositionAmount)
                }

                private fun entry(entries: List<BigDecimal?>): PersonEntries? {
                    return EntriesCreator.entries(Consumer { builder: by.mrrockka.domain.collection.PersonEntries.PersonEntriesBuilder? ->
                        builder.entries(
                                entries
                        )
                    })
                }

                private fun finaleSummary(person: Person, amount: BigDecimal, position: Int): FinalePlaceSummary? {
                    return FinalePlaceSummary.builder()
                            .person(person)
                            .position(position)
                            .amount(amount)
                            .build()
                }

                private fun payouts(
                        creditorEntries: PersonEntries,
                        debtorsEntries: List<PersonEntries?>
                ): List<Payout?> {
                    val debts = debtorsEntries.stream()
                            .filter { entries: PersonEntries? -> entries != creditorEntries }
                            .map<Payer?> { debtEntries: PersonEntries? ->
                                Payer.builder()
                                        .personEntries(debtEntries)
                                        .amount(debtEntries!!.total())
                                        .build()
                            }
                            .toList()

                    return List.of<Payout?>(payout(creditorEntries, debts))
                }

                private fun payout(creditorEntries: PersonEntries, payers: List<Payer?>?): Payout? {
                    return Payout.builder()
                            .personEntries(creditorEntries)
                            .payers(payers)
                            .build()
                }

                private fun payer(personEntries: PersonEntries, amount: BigDecimal?): Payer? {
                    return Payer.builder()
                            .personEntries(personEntries)
                            .amount(amount)
                            .build()
                }

                private fun totalEntriesAmount(entriesList: List<PersonEntries?>): BigDecimal {
                    return entriesList.stream()
                            .map<BigDecimal> { obj: PersonEntries? -> obj!!.total() }
                            .reduce { obj: BigDecimal?, augend: BigDecimal? -> obj!!.add(augend) }
                            .orElseThrow()
                }

                private fun calculatePrizeAmount(total: BigDecimal, percentage: BigDecimal?): BigDecimal {
                    return total.multiply(percentage).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                }

                private fun finaleSummaries(entries: List<PersonEntries?>): List<FinalePlaceSummary> {
                    val totalEntriesAmount = totalEntriesAmount(entries)
                    return listOf(
                            finaleSummary(entries.get(0)!!.person, calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(60)), 1),
                            finaleSummary(entries.get(1)!!.person, calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(30)), 2),
                            finaleSummary(entries.get(2)!!.person, calculatePrizeAmount(totalEntriesAmount, BigDecimal.valueOf(10)), 3)
                    )
                }*/

    companion object {
        private val BUY_IN: BigDecimal = BigDecimal.valueOf(20)

        @JvmStatic
        private fun playerSize(): Stream<Arguments?> {
            return Stream.of(
                    Arguments.of(2),
                    Arguments.of(4),
                    Arguments.of(10),
                    Arguments.of(20),
                    Arguments.of(100)
            )
        }
    }
}

fun tournamentPlayers(size: Int, buyin: BigDecimal = BigDecimal("10")): List<Player> =
        (0..<size)
                .asSequence()
                .map { player { this.buyin = buyin }.tournament() }
                .toList()

fun tournamentPlayer(buyin: BigDecimal = BigDecimal("10"), entries: Int = 1): Player = player {
    this.buyin = buyin
    this.size = entries
}.tournament()

data class SimplePayout(
        val creditor: String,
        val entries: BigDecimal,
        val won: BigDecimal,
        val debtors: List<SimpleDebtor>,
)

data class SimpleDebtor(
        val debtor: String,
        val debt: BigDecimal,
        val entries: BigDecimal
)

internal fun List<Payout>.simplify(): List<SimplePayout> = map { payout ->
    SimplePayout(
            creditor = payout.creditor.person.nickname ?: fail("No creditor nickname found"),
            entries = payout.creditor.entries.total(),
            won = payout.total,
            debtors = payout.debtors.map { debtor ->
                SimpleDebtor(
                        debtor = debtor.player.person.nickname ?: fail("No debtor nickname found"),
                        debt = debtor.debt,
                        entries = debtor.player.entries.total()
                )
            }
    )
}