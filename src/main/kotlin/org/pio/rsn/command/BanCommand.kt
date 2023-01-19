package org.pio.rsn.command

import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.silkmc.silk.core.text.broadcastText
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.pio.rsn.event.JoinServerEvent
import org.pio.rsn.model.Banned
import org.pio.rsn.model.Player

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
                if (requestBanned(uuid,reason,source.name,true)) {
                    val banned = JoinServerEvent().requestBanned(uuid)
                    source.server.broadcastText(
                        Text.literal("玩家 $player 因为 $reason 而被封禁!")
                            .setStyle(Style.EMPTY.withColor(Formatting.RED))
                    )
                    source.player?.networkHandler?.disconnect(banned?.let { JoinServerEvent().textTemp(it) })
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun unbanHandle(
        source: ServerCommandSource,
        player: String
    ) {
        GlobalScope.launch {
            if (findUUID(player) != null) {
                if (requestBanned(findUUID(player)?.id.toString(),"",source.name,false)){
                    source.server.broadcastText(Text.literal("玩家 $player 被 ${source.name} 赦免了!")
                        .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)))
                }
            }
        }
    }

    private val client = OkHttpClient()
    private val gson = Gson()
    private fun requestBanned(uuid: String,reason: String,from: String, active: Boolean): Boolean {
        var jsonString = gson.toJson(Banned(active,0,reason,from,""))
        val mediaType = "application/json; charset=utf-8".toMediaType();
        val body: RequestBody = jsonString.trimIndent().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://api.p-io.org/v1/players/${uuid}/banned")
            .addHeader("Authorization","Bearer "+"Ybt6mVHCEYXdmqgUFttSX4pLqR6mGjAkmVyy55QRpU5xfU9dBRwLmUbLausg4462")
            .put(body)
            .build()
        client.newCall(request).execute().use { response ->
            if (response.code == 202) {
                return true
            }
            return false
        }
    }

    private fun findUUID(player: String): Player? {
        val request = Request.Builder()
            .url("https://api.mojang.com/users/profiles/minecraft/${player}")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.code == 200)
                return gson.fromJson(response.body?.string() ?: String(), Player::class.java)
        }
        return null
    }
}