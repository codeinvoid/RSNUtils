package org.pio.rsn.event

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.GameMode
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.event.PlayerEvents
import org.pio.rsn.model.Whitelist
import org.pio.rsn.temp.whitelistTitle
import org.pio.rsn.utils.Types

class JoinEvent {
    companion object {
        const val inout = 30
        const val stay = 24000
    }
    @OptIn(ExperimentalSilkApi::class)
    fun joinEvents() {
        PlayerEvents.preLogin.listen { event ->  
            val player = event.player
            val uuid = event.player.uuidAsString.replace("-", "")
            playerTest(player, uuid)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun playerTest(player: ServerPlayerEntity, uuid: String) {
        GlobalScope.launch {
            val whitelist = Types().getWhitelist(uuid)
            contrastWhitelist(whitelist, player)
        }
    }

    private fun contrastWhitelist(whitelist: Whitelist?, player: ServerPlayerEntity) {
        val channel = player.networkHandler
        player.networkHandler.sendPacket(ClearTitleS2CPacket(true))
        if (whitelist != null) {
            if (isActive(whitelist, player, channel)) {
                player.sendMessage(
                    Text.literal("✔ ").setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                        .append("白名单验证通过")
                )
            }
            return
        } else {
            channel.disconnect(Text.literal("请先在群内申请白名单后再进入"))
        }
    }

    private fun isActive(whitelist: Whitelist, player: ServerPlayerEntity, channel: ServerPlayNetworkHandler): Boolean {
        return if(!whitelist.active){
            player.sendMessage(Text.literal("✘ ").setStyle(Style.EMPTY.withColor(Formatting.RED))
                .append("白名单验证不通过"))
            channel.sendPacket(TitleFadeS2CPacket(inout, stay, inout))
            channel.sendPacket(TitleS2CPacket(whitelistTitle))
            player.changeGameMode(GameMode.ADVENTURE)
            channel.sendPacket(SubtitleS2CPacket(Text.of("以完成验证")))
            false
        } else {
            true
        }
    }
}
