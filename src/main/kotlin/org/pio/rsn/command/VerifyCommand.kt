package org.pio.rsn.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.world.GameMode
import org.pio.rsn.model.Valid
import org.pio.rsn.utils.Types


class VerifyCommand {
    @OptIn(DelicateCoroutinesApi::class)
    fun verify(code: Int, source: ServerCommandSource){
        val uuid = source.player?.uuidAsString?.replace("-", "").toString()
        GlobalScope.launch {
            if (!checkWhitelist(uuid)) {
                matchPlayer(source, uuid, code)
            } else {
                source.sendFeedback(Text.literal("你已经验证过了!"), false)
            }
        }
    }

    private fun checkWhitelist(uuid: String): Boolean = Types().getWhitelist(uuid)?.active == true

    private fun matchPlayer(source: ServerCommandSource, uuid: String, code: Int) {
        if (!Types().postValid(Valid(code), uuid)) {
            source.sendFeedback(Text.literal("验证失败,请检查账号或验证码。"), false)
        } else {
            source.sendFeedback(Text.literal("验证成功!"), false)
            source.player?.networkHandler
                ?.sendPacket(ClearTitleS2CPacket(true))
            source.player?.changeGameMode(GameMode.SURVIVAL)
        }
    }
}
