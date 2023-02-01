package org.pio.rsn.command

import com.mojang.authlib.GameProfile
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.server.command.ServerCommandSource
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
        player: Collection<GameProfile>,
        reason: String?
    ) {
        GlobalScope.launch {
            for (item in player) {
                val uuid = item.id.toString()
                if (putBanned(uuid, reason.toString(), source.name, true)) {
                    val banned = requestBanned(uuid)
                    source.server.broadcastText(
                        Text.literal("玩家 ${item.name} 因为 $reason 而被封禁!")
                            .setStyle(Style.EMPTY.withColor(Formatting.RED))
                    )
                    source.player?.networkHandler?.disconnect(banned?.let { bannedMessage(it) })
                }
            }
        }
    }
}
