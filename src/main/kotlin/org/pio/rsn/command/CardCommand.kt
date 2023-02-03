package org.pio.rsn.command

import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.pio.rsn.model.Integration
import org.pio.rsn.temp.cardFeedback
import org.pio.rsn.utils.Types

class CardCommand {
    fun init(source: ServerCommandSource) {
        val integration = source.player?.let { Types().getIntegration(it.uuidAsString) }
        if (integration != null && integration.active) {
            source.sendFeedback(cardFeedback(integration), false)
        } else {
            report(integration, source)
        }
    }

    private fun report(integration : Integration?, source: ServerCommandSource) {
        if (integration == null) {
            source.sendFeedback(Text.literal("API错误，请向服主或管理员寻求帮助。"), false)
        }
        source.sendFeedback(Text.literal("你还没开通玩家一卡通服务！"), false)
    }
}
