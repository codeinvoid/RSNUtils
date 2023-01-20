package org.pio.rsn

import net.fabricmc.api.ModInitializer
import net.minecraft.server.command.CommandManager.*
import org.pio.rsn.command.Commands
import org.pio.rsn.event.JoinEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("UNUSED")
object Server: ModInitializer {
    val LOGGER: Logger = LoggerFactory.getLogger("RSN-Server")
    private const val server = "rsn_server"
    override fun onInitialize() {
        LOGGER.info("RSN-Server Is Loaded!")
        Commands()
        JoinEvent().joinEvents()
    }
}