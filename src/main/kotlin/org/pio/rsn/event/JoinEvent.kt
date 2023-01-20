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
import net.silkmc.silk.core.text.broadcastText
import org.pio.rsn.model.Whitelist
import org.pio.rsn.temp.textTemp
import org.pio.rsn.temp.whitelistTitle
import org.pio.rsn.utils.contrastBanned
import org.pio.rsn.utils.requestBanned
import org.pio.rsn.utils.requestWhitelist

class JoinEvent {
    @OptIn(ExperimentalSilkApi::class)
    fun joinEvents() {
        PlayerEvents.postLogin.listen { event ->
            val player = event.player
            val name = event.player.name
            val uuid = event.player.uuidAsString.replace("-", "")
            playerHandle(player, name, uuid)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun playerHandle(player: ServerPlayerEntity, name: Text, uuid: String) {
        GlobalScope.launch {
            val banned = requestBanned(uuid)
            val whitelist = requestWhitelist(uuid)
            when (contrastBanned(uuid)) {
                null -> {
                    player.networkHandler.disconnect(Text.literal("API连接失败 请重试"))
                }

                true -> {
                    contrastWhitelist(whitelist, player)
                }

                false -> {
                    player.networkHandler.disconnect(banned?.let { textTemp(it) })
                    player.server.broadcastText(
                        Text.literal("玩家 $name 试图进入服务器，但是他已被被封禁!")
                            .setStyle(Style.EMPTY.withColor(Formatting.RED))
                    )
                }
            }
        }
    }

    private fun contrastWhitelist(whitelist: Whitelist?, player: ServerPlayerEntity) {
        val channel = player.networkHandler

        if (whitelist != null) {
            if(!whitelist.active){
                channel.sendPacket(TitleFadeS2CPacket(300, 24000, 0))
                channel.sendPacket(TitleS2CPacket(whitelistTitle))
                player.changeGameMode(GameMode.ADVENTURE)
                channel.sendPacket(SubtitleS2CPacket(Text.of("以完成验证")))
            }
            return
        } else {
            channel.disconnect(Text.literal("请先在群内申请白名单后再进入"))
        }
    }
}
