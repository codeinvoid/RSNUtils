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
import org.pio.rsn.temp.bannedMessage
import org.pio.rsn.utils.putBanned
import org.pio.rsn.utils.requestBanned

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
            println(uuid)
            if (putBanned(uuid, reason.toString(), source.name, true)) {
                val banned = requestBanned(uuid)
                if (banned != null) {
                    source.server.broadcastText(
                        Text.literal("玩家 ${gameProfile.name} 因为 $reason 而被封禁!")
                            .setStyle(Style.EMPTY.withColor(Formatting.YELLOW))
                    )
                    val serverPlayerEntity: ServerPlayerEntity =
                        source.server.playerManager.getPlayer(gameProfile.id) ?: continue
                    serverPlayerEntity.networkHandler.disconnect(bannedMessage(banned))
                }
            }
        }
    }
}
