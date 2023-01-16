package org.pio.rsn.command


import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.silkmc.silk.core.text.sendText
import okhttp3.OkHttpClient
import okhttp3.Request
import org.pio.rsn.model.Valid


class Link {
    @OptIn(DelicateCoroutinesApi::class)
    fun ver(code: String, source: ServerCommandSource){
        GlobalScope.launch() {
            if (code.isNotBlank()) {
                var gson = Gson()
                var jsonString = gson.toJson(Valid(code.toInt()))
                source.player?.sendText(code.toString())
                if (source.player?.let { matchUUID(code.toString()) } == true)
                    source.sendFeedback(Text.literal("验证成功"), false)
                else source.sendFeedback(Text.literal("失败"), false)
            }
        }
    }

    private val client = OkHttpClient()
    private fun request(uuid: String) : Boolean {
        val request = Request.Builder()
            .url("https://api.p-io.org/v1/player/${uuid}")
            .addHeader("Authorization","Bearer"+"Ybt6mVHCEYXdmqgUFttSX4pLqR6mGjAkmVyy55QRpU5xfU9dBRwLmUbLausg4462")
            .build()
        client.newCall(request).execute().use { response ->
            if (response.code == 200)
                return true
        }
        return false
    }

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
}

