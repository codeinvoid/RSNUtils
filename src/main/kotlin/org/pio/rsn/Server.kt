package org.pio.rsn

import net.fabricmc.api.ModInitializer
import org.pio.rsn.command.Commands
import org.pio.rsn.config.Config
import org.pio.rsn.config.ConfigOperator
import org.pio.rsn.event.JoinEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


@Suppress("UNUSED")
object Server: ModInitializer {
    val LOGGER: Logger = LoggerFactory.getLogger("RSNUtils")
    fun configFile() = File("./config/RSNUtils.toml")

    override fun onInitialize() {
        if (!configFile().exists()) {
            configFile().createNewFile()
            val content = Config("", mutableMapOf("serverAPI" to ""))
            ConfigOperator(configFile()).write(Config::class.java, content)
        }
        LOGGER.info("Config is loaded!")

        Commands()
        JoinEvent().joinEvents()
        LOGGER.info("This server is mixed!")
    }
}
