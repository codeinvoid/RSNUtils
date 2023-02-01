package org.pio.rsn.utils

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.pio.rsn.model.Banned
import org.pio.rsn.model.Integration
import org.pio.rsn.model.Valid
import org.pio.rsn.model.Whitelist

private val client = OkHttpClient()
private val gson = Gson()
const val success = 200

fun matchUUID(uuid: String): Any {
    val request = Request.Builder()
        .url("https://sessionserver.mojang.com/session/minecraft/profile/${uuid}")
        .build()

    client.newCall(request).execute().use { response ->
        if (response.code == 200)
            return true
    }
    return false
}

fun requestBanned(uuid: String): Banned? {
    val request = Request.Builder()
        .url("https://api.p-io.org/v1/players/${uuid}/banned")
        .build()
    client.newCall(request).execute().use { response ->
        if (response.code == success) {
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
        if (response.code == success) {
            return gson.fromJson(response.body?.string() ?: String(), Whitelist::class.java)
        }
        return null
    }
}

fun requestCard(uuid: String): Integration? {
    val request = Request.Builder()
        .url("https://api.p-io.org/v1/players/${uuid}/integration")
        .build()
    client.newCall(request).execute().use { response ->
        if (response.code == success) {
            return gson.fromJson(response.body?.string() ?: String(), Integration::class.java)
        }
        return null
    }
}

fun contrastBanned(uuid: String): Boolean? {
    val request = Request.Builder()
        .url("https://api.p-io.org/v1/players/${uuid}/banned")
        .build()
    client.newCall(request).execute().use { response ->
        if (response.code == success) {
            return !gson.fromJson(response.body?.string() ?: String(), Banned::class.java).active
        }
        return null
    }
}

fun putBanned(uuid: String,reason: String,from: String, active: Boolean): Boolean {
    var jsonString = gson.toJson(Banned(active,0,reason,from,""))
    val mediaType = "application/json; charset=utf-8".toMediaType()
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

fun matchCode(uuid: String, code:Int): Boolean {
    val jsonString = gson.toJson(Valid(code))
    val mediaType = "application/json; charset=utf-8".toMediaType()
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

fun sendMessage(context: Any) {
    println(context)
}
