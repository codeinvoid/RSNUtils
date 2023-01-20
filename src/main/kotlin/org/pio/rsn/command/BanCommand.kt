package org.pio.rsn.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.silkmc.silk.core.text.broadcastText
import org.pio.rsn.temp.textTemp
import org.pio.rsn.utils.findUUID
import org.pio.rsn.utils.putBanned
import org.pio.rsn.utils.requestBanned

class BanCommand {
    @OptIn(DelicateCoroutinesApi::class)
    fun banHandle(
        source: ServerCommandSource,
        player: String,
        reason: String
    ) {
        GlobalScope.launch {
            if (findUUID(player) != null) {
                val uuid = findUUID(player)?.id.toString()
                if (putBanned(uuid,reason,source.name,true)) {
                    val banned = requestBanned(uuid)
                    source.server.broadcastText(
                        Text.literal("玩家 $player 因为 $reason 而被封禁!")
                            .setStyle(Style.EMPTY.withColor(Formatting.RED))
                    )
                    source.player?.networkHandler?.disconnect(banned?.let { textTemp(it) })
                }
            }
        }
    }
}
