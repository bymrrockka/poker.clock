package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PositionPrize
import by.mrrockka.parser.PrizePoolMessageParser
import by.mrrockka.repo.PrizePoolRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface PrizePoolTelegramService {
    fun store(messageMetadata: MessageMetadata): List<PositionPrize>
    fun store(metadata: MessageMetadata, prizePool: List<PositionPrize>): List<PositionPrize>
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class PrizePoolTelegramServiceImpl(
        private val prizePoolRepo: PrizePoolRepo,
        private val prizePoolMessageParser: PrizePoolMessageParser,
        private val gameService: GameTelegramService,
) : PrizePoolTelegramService {

    override fun store(messageMetadata: MessageMetadata): List<PositionPrize> =
            store(messageMetadata, prizePoolMessageParser.parse(messageMetadata))

    override fun store(metadata: MessageMetadata, prizePool: List<PositionPrize>): List<PositionPrize> {
        val game = gameService.findGame(metadata)
        check(game !is CashGame) { "Prize pool is not allowed for cash game" }
        prizePoolRepo.store(game.id, prizePool)

        return prizePool
    }
}
