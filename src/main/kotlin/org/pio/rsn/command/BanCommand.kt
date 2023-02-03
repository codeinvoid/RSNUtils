package org.pio.rsn.command

import com.mojang.authlib.GameProfile
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.silkmc.silk.core.text.broadcastText
import org.pio.rsn.model.Banned
import org.pio.rsn.temp.bannedMessage
import org.pio.rsn.utils.Types

class BanCommand {
    @OptIn(DelicateCoroutinesApi::class)
    fun banHandle(
        source: ServerCommandSource,
        player: MutableCollection<GameProfile>,
        reason: String?
    ) {
        GlobalScope.launch {
            if (player.size > 1) {
                source.sendFeedback(Text.literal("操作涉及人数过多: 每次只能操作1人")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)), true)
            } else {
                banExecute(player, source, reason)
            }
        }
    }

    private fun banExecute(player: MutableCollection<GameProfile>, source: ServerCommandSource, reason: String?) {
        for (gameProfile in player) {
            val uuid = gameProfile.id.toString()
            val oldBanned = Types().getBanned(uuid)
            val putBanned = Types().putBanned(
                Banned(true,0, reason.toString(), source.name, ""),
                uuid
            )
            val banned = Types().getBanned(uuid)
            if (oldBanned != null) {
                if (putBanned && banned != null && !oldBanned.active) {
                    source.server.broadcastText(
                        Text.literal("玩家 ${gameProfile.name} 因为 $reason 而被封禁!")
                            .setStyle(Style.EMPTY.withColor(Formatting.YELLOW))
                    )
                    val serverPlayerEntity: ServerPlayerEntity =
                        source.server.playerManager.getPlayer(gameProfile.id) ?: continue
                    serverPlayerEntity.networkHandler.disconnect(bannedMessage(banned))
                } else {
                    source.sendMessage(Text.literal("处理出错或该玩家已被封禁."))
                }
            }
        }
    }
}
