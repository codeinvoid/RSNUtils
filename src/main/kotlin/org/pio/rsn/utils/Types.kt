package org.pio.rsn.utils

import org.pio.rsn.Server
import org.pio.rsn.config.Config
import org.pio.rsn.config.ConfigOperator
import org.pio.rsn.model.Banned
import org.pio.rsn.model.Integration
import org.pio.rsn.model.Valid
import org.pio.rsn.model.Whitelist

class Types {
    /**
     * Config
     */
    fun readConfig() = ConfigOperator(Server.configFile()).read(Config::class.java)
    fun writeConfig(content: Config) = ConfigOperator(Server.configFile()).write(Config::class.java, content)

    /**
     * API
     */
    private val playerAPI = readConfig().api["serverAPI"].toString()

    /**
     * GET
     */
    fun getBanned(uuid : String) = State()
        .request(Banned::class.java, "banned", uuid, playerAPI)

    fun getWhitelist(uuid : String) = State()
        .request(Whitelist::class.java, "whitelist", uuid, playerAPI)

    fun getIntegration(uuid: String) = State()
        .request(Integration::class.java, "integration", uuid, playerAPI)

    /**
     * PUT
     */
    fun putBanned(banned: Banned, uuid: String) = State()
        .put(banned, "banned", uuid, playerAPI)

    /**
     * POST
     */
    fun postValid(valid: Valid, uuid: String) = State()
        .post(valid, "code", uuid, playerAPI)

}
