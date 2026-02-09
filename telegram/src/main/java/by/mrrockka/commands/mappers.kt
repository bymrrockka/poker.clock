package by.mrrockka.commands

import by.mrrockka.domain.GameType
import by.mrrockka.domain.MessageMetadata
import eu.vendeli.tgbot.types.chain.UserChatReference
import eu.vendeli.tgbot.types.chain.WizardStateManager
import eu.vendeli.tgbot.types.chain.WizardStep
import java.math.BigDecimal
import kotlin.reflect.KClass

class BigDecimalState : WizardStateManager<BigDecimal> {
    val state = mutableMapOf<KClass<out WizardStep>, BigDecimal>()
    override suspend fun get(key: KClass<out WizardStep>, reference: UserChatReference): BigDecimal? = state[key]

    override suspend fun set(key: KClass<out WizardStep>, reference: UserChatReference, value: BigDecimal) {
        state[key] = value
    }

    override suspend fun del(key: KClass<out WizardStep>, reference: UserChatReference) {
        state.remove(key)
    }
}

class GameTypeState : WizardStateManager<GameType> {
    val state = mutableMapOf<KClass<out WizardStep>, GameType>()
    override suspend fun get(key: KClass<out WizardStep>, reference: UserChatReference): GameType? = state[key]

    override suspend fun set(key: KClass<out WizardStep>, reference: UserChatReference, value: GameType) {
        state[key] = value
    }

    override suspend fun del(key: KClass<out WizardStep>, reference: UserChatReference) {
        state.remove(key)
    }
}

class MessageMetadataState : WizardStateManager<MessageMetadata> {
    val state = mutableMapOf<KClass<out WizardStep>, MessageMetadata>()
    override suspend fun get(key: KClass<out WizardStep>, reference: UserChatReference): MessageMetadata? = state[key]

    override suspend fun set(key: KClass<out WizardStep>, reference: UserChatReference, value: MessageMetadata) {
        state[key] = value
    }

    override suspend fun del(key: KClass<out WizardStep>, reference: UserChatReference) {
        state.remove(key)
    }
}

