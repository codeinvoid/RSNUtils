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
import org.pio.rsn.utils.putBanned

class UnbanCommand {
    @OptIn(DelicateCoroutinesApi::class)
    fun unbanHandle(
        source: ServerCommandSource,
        player: Collection<GameProfile>
    ) {
        GlobalScope.launch {
            for (item in player) {
                val uuid = item.id.toString()
                if (putBanned(uuid, "", source.name, false)) {
                    source.server.broadcastText(
                        Text.literal("玩家 ${item.name} 被 ${source.name} 赦免了!")
                            .setStyle(Style.EMPTY.withColor(Formatting.YELLOW))
                    )
                }
            }
        }
    }
}
