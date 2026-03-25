package by.mrrockka.builder

import by.mrrockka.TelegramRandoms
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.chat.ChatMember

class MemberBuilder(init: (MemberBuilder.() -> Unit) = {}) : AbstractBuilder<TelegramRandoms>(telegramRandoms) {
    var user: User? = null

    fun user(user: User) {
        this.user = user
    }

    init {
        randoms(telegramRandoms)
        init()
    }

    inline fun <reified T : ChatMember> build(): T {
        val klass = T::class
        return when (klass) {
            ChatMember.Administrator::class -> ChatMember.Administrator(
                    user = user ?: error("No user specified"),
                    canBeEdited = false,
                    isAnonymous = false,
                    canManageChat = false,
                    canDeleteMessages = false,
                    canRestrictMembers = false,
                    canPromoteMembers = false,
                    canChangeInfo = false,
                    canInviteUsers = false,
                    canManageVideoChats = false,
                    canPostStories = false,
                    canEditStories = false,
                    canDeleteStories = false,
            )

            ChatMember.Member::class -> ChatMember.Member(
                    user = user ?: error("No user specified"),
            )

            ChatMember.Owner::class -> ChatMember.Owner(
                    user = user ?: error("No user specified"),
                    isAnonymous = false,
            )

            else -> error("Unknown member type")
        } as T
    }
}

inline fun <reified T : ChatMember> member(noinline init: (MemberBuilder.() -> Unit) = {}) = MemberBuilder(init).build<T>()
fun member() = MemberBuilder { user(user()) }.build<ChatMember.Member>()
