package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PositionPrize
import by.mrrockka.parser.PrizePoolMessageParser
import by.mrrockka.repo.PrizePoolRepo
import org.springframework.stereotype.Service

interface PrizePoolTelegramService {
    fun store(messageMetadata: MessageMetadata): List<PositionPrize>
}

@Service
open class PrizePoolTelegramServiceImpl(
        private val prizePoolRepo: PrizePoolRepo,
        private val prizePoolMessageParser: PrizePoolMessageParser,
        private val gameService: GameTelegramService,
) : PrizePoolTelegramService {

    override fun store(messageMetadata: MessageMetadata): List<PositionPrize> {
        val prizePool = prizePoolMessageParser.parse(messageMetadata)

        val game = gameService.findGame(messageMetadata)
        check(game !is CashGame) { "Prize pool is not allowed for cash game" }
        prizePoolRepo.store(game.id, prizePool)

        return prizePool
    }
}
