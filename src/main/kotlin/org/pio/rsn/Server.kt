package org.pio.rsn

import io.github.prismwork.prismconfig.api.PrismConfig
import io.github.prismwork.prismconfig.api.config.DefaultDeserializers
import net.fabricmc.api.ModInitializer
import org.pio.rsn.command.Commands
import org.pio.rsn.config.Config
import org.pio.rsn.event.JoinEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


@Suppress("UNUSED")
object Server: ModInitializer {
    val LOGGER: Logger = LoggerFactory.getLogger("RSNUtils")
    val configFile = File("./config/RSNUtils.toml")
    override fun onInitialize() {
        Commands()
        JoinEvent().joinEvents()
        LOGGER.info("This server is mixed!")

        if (!configFile.exists()) {
            val content = Config("")
            PrismConfig.getInstance().deserializeAndWrite(
                Config::class.java,
                content,
                DefaultDeserializers.getInstance().toml(Config::class.java),
                configFile
            )
        }
        LOGGER.info("Config is loaded!")
    }
}
