package by.mrrockka.commands

import by.mrrockka.domain.GameType
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PositionPrize
import eu.vendeli.tgbot.implementations.MapStateManager
import java.math.BigDecimal

class BigDecimalState : MapStateManager<BigDecimal>()
class GameTypeState : MapStateManager<GameType>()
class MessageMetadataState : MapStateManager<MessageMetadata>()
class PositionPrizeState : MapStateManager<List<PositionPrize>>()
class PositionMentionState : MapStateManager<Map<Int, String>>()
