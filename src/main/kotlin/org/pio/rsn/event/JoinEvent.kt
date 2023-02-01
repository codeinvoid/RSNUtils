package org.pio.rsn.event

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.GameMode
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.event.PlayerEvents
import org.pio.rsn.model.Whitelist
import org.pio.rsn.temp.whitelistTitle
import org.pio.rsn.utils.requestWhitelist

class JoinEvent {
    @OptIn(ExperimentalSilkApi::class)
    fun joinEvents() {
        PlayerEvents.preLogin.listen { event ->  
            val player = event.player
            val name = event.player.name.copy()
            val uuid = event.player.uuidAsString.replace("-", "")
            playerTest(player, name, uuid)
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun playerTest(player: ServerPlayerEntity, name: Text, uuid: String) {
        GlobalScope.launch {
            val whitelist = requestWhitelist(uuid)
            contrastWhitelist(whitelist, player)
        }
    }

    private fun contrastWhitelist(whitelist: Whitelist?, player: ServerPlayerEntity) {
        val channel = player.networkHandler

        if (whitelist != null) {
            if(!whitelist.active){
                player.sendMessage(Text.literal("✘ ").setStyle(Style.EMPTY.withColor(Formatting.RED))
                    .append("白名单验证不通过"))
                channel.sendPacket(TitleFadeS2CPacket(30, 24000, 30))
                channel.sendPacket(TitleS2CPacket(whitelistTitle))
                player.changeGameMode(GameMode.ADVENTURE)
                channel.sendPacket(SubtitleS2CPacket(Text.of("以完成验证")))
                return
            }
            player.sendMessage(Text.literal("✔ ").setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                .append("白名单验证通过"))
        } else {
            channel.disconnect(Text.literal("请先在群内申请白名单后再进入"))
        }
    }
}
