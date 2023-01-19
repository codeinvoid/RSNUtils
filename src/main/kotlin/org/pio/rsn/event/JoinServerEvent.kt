package org.pio.rsn.event

import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.world.GameMode
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.event.PlayerEvents
import okhttp3.OkHttpClient
import okhttp3.Request
import org.pio.rsn.model.Banned
import org.pio.rsn.model.Whitelist
import java.text.SimpleDateFormat
import java.util.*


class JoinServerEvent {
    @OptIn(ExperimentalSilkApi::class, DelicateCoroutinesApi::class)
    fun joinEvent() {
        PlayerEvents.postLogin.listen { event ->
                val uuid = event.player.uuidAsString.replace("-", "")
                val banned = requestBanned(uuid)
                val whitelist = requestWhitelist(uuid)
                if (banned != null && whitelist != null) {
                    if (!banned.active) {
                        if (!whitelist.active) {
                            GlobalScope.launch() {
                                whitelistProcessor(whitelist, event)
                            }
                        } else {
                            return@listen
                        }
                    } else {
                        GlobalScope.launch() {
                            event.player.onSpawn().run {
                                event.player.networkHandler.disconnect(textTemp(banned))
                            }
                        }
                    }
                } else {
                    event.player.networkHandler.disconnect(Text.literal("API连接失败 请重试"))
                }
            }
    }
    private val client = OkHttpClient()
    private val gson = Gson()
    fun requestBanned(uuid: String): Banned? {
        val request = Request.Builder()
            .url("https://api.p-io.org/v1/players/${uuid}/banned")
            .build()
        client.newCall(request).execute().use { response ->
            if (response.code == 200) {
                return gson.fromJson(response.body?.string() ?: String(), Banned::class.java)
            }
            return null
        }
    }

    fun requestWhitelist(uuid: String): Whitelist? {
        val request = Request.Builder()
            .url("https://api.p-io.org/v1/players/${uuid}/whitelist")
            .build()
        client.newCall(request).execute().use { response ->
            if (response.code == 200) {
                return gson.fromJson(response.body?.string() ?: String(), Whitelist::class.java)
            }
            return null
        }
    }

    val title : Text = Text.literal("输入 ").setStyle(Style.EMPTY.withColor(Formatting.GOLD))
        .append(Text.literal("/verify ").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
        .append(Text.literal("<验证码>").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
    @OptIn(ExperimentalSilkApi::class)
    private fun whitelistProcessor(whitelist: Whitelist?, event: PlayerEvents.PlayerEvent<ServerPlayerEntity>) {
        if (whitelist != null) {
                event.player.networkHandler.sendPacket(TitleFadeS2CPacket(300,24000,0))
                event.player.networkHandler.sendPacket(TitleS2CPacket(title))
                event.player.changeGameMode(GameMode.ADVENTURE)
                event.player.networkHandler.sendPacket(SubtitleS2CPacket(Text.of("以完成验证")))
        } else {
            event.player.networkHandler.disconnect(Text.literal("请先申请白名单后进入"))
        }
    }

    fun textTemp(banned: Banned) : Text {
        return Text
            .literal("你已被封禁\n\n")
            .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true))
            .append(Text.literal("原因 ❯❯ ")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false)))
            .append(Text.literal(banned.reason+"\n")
                .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(false)))
            .append(Text.literal("封禁编号 ❯❯ ")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false)))
            .append(Text.literal(banned.nanoid+"\n")
                .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(false)))
            .append(Text.literal("操作时间 ❯❯ ")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false)))
            .append(Text.literal(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(banned.time)+ "\n")
                .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(false)))
            .append(Text.literal("申诉 ❯❯ ")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false)))
            .append(Text.literal("如有疑问发送邮件至 issue@p-io.org").setStyle(Style.EMPTY.withBold(false)))
    }
}