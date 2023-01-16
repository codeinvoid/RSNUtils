package org.pio.rsn.event

import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.event.PlayerEvents
import okhttp3.OkHttpClient
import okhttp3.Request
import org.pio.rsn.model.Banned
import java.text.SimpleDateFormat
import java.util.*


class JoinServerEvent {
    @OptIn(ExperimentalSilkApi::class, DelicateCoroutinesApi::class)
    fun joinEvent() {
        PlayerEvents.postLogin.listen { event ->
            GlobalScope.launch() {
                val uuid = event.player.uuidAsString.replace("-", "")
                val banned = requestBanned(uuid)
                if (banned != null) {
                    if (!banned.active) {
                        if (requestWhitelist(uuid)) {
                            println("awa")
                        }
                    } else {
                        event.player.onSpawn().run {
                            event.player.networkHandler.disconnect(textTemp(banned))
                            return@launch
                        }
                    }
                } else {
                    event.player.networkHandler.disconnect(Text.literal("API连接失败 请重试"))
                    return@launch
                }
            }
            return@listen
        }
    }
    private val client = OkHttpClient()
    private fun requestBanned(uuid: String): Banned? {
        val request = Request.Builder()
            .url("https://api.p-io.org/v1/players/${uuid}/banned")
            .addHeader("Authorization","Bearer "+"Ybt6mVHCEYXdmqgUFttSX4pLqR6mGjAkmVyy55QRpU5xfU9dBRwLmUbLausg4462")
            .build()
        client.newCall(request).execute().use { response ->
            if (response.code == 200) {
                var gson = Gson()
                var banned = gson.fromJson(response.body?.string() ?: String(), Banned::class.java)
                return banned
            }
            return null
        }
        return null
    }

    private fun requestWhitelist(uuid: String) : Boolean {
        val request = Request.Builder()
            .url("https://api.p-io.org/v1/players/${uuid}/banned")
            .addHeader("Authorization","Bearer"+"Ybt6mVHCEYXdmqgUFttSX4pLqR6mGjAkmVyy55QRpU5xfU9dBRwLmUbLausg4462")
            .build()
        client.newCall(request).execute().use { response ->
            if (response.code == 200)
                return true
        }
        return false
    }

    private fun textTemp(banned: Banned) : Text {
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
            .append(Text.literal("如有疑问请发送邮件至\n").setStyle(Style.EMPTY.withBold(false)))
            .append(Text.literal("issue@p-io.org").setStyle(Style.EMPTY.withBold(false)))
    }
}