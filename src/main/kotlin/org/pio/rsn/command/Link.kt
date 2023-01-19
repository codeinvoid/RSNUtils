package org.pio.rsn.command


import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.minecraft.world.GameMode
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.pio.rsn.event.JoinServerEvent
import org.pio.rsn.model.Valid


class Link {
    @OptIn(DelicateCoroutinesApi::class)
    fun ver(code: Int, source: ServerCommandSource){
        val uuid = source.player?.uuidAsString?.replace("-", "").toString()
        if (JoinServerEvent().requestWhitelist(uuid)?.active == false) {
            GlobalScope.launch() {
                if (source.player?.let { matchUUID(uuid) } == true) {
                    if (!matchCode(uuid, code)) {
                        source.sendFeedback(Text.literal("验证失败,请检查账号或验证码。"), false)
                    } else {
                        source.sendFeedback(Text.literal("验证成功!"), false)
                        source.player?.networkHandler
                            ?.sendPacket(ClearTitleS2CPacket(true))
                        source.player?.changeGameMode(GameMode.SURVIVAL)
                    }
                } else {
                    source.sendFeedback(Text.literal("API错误,验证失败。"), true)
                }
            }
        } else {
            source.sendFeedback(Text.literal("你已经验证过了。"), false)
        }
    }

    private val client = OkHttpClient()
    private fun matchUUID(uuid: String): Any {
        val request = Request.Builder()
            .url("https://sessionserver.mojang.com/session/minecraft/profile/${uuid}")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code == 200)
                return true
        }
        return false
    }

    private fun matchCode(uuid: String, code:Int): Boolean {
        val gson = Gson()
        val jsonString = gson.toJson(Valid(code))
        val mediaType = "application/json; charset=utf-8".toMediaType();
        val body: RequestBody = jsonString.trimIndent().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://api.p-io.org/v1/players/${uuid}/code")
            .addHeader("Authorization","Bearer "+"Ybt6mVHCEYXdmqgUFttSX4pLqR6mGjAkmVyy55QRpU5xfU9dBRwLmUbLausg4462")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            return response.code == 200
        }
    }
}

