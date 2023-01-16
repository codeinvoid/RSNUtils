package org.pio.rsn

import kotlinx.coroutines.DelicateCoroutinesApi
import net.fabricmc.api.ModInitializer
import net.minecraft.server.command.CommandManager.*
import net.silkmc.silk.core.event.PlayerEvents
import org.pio.rsn.command.Commands
import org.pio.rsn.event.JoinServerEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("UNUSED")
object Server: ModInitializer {
    val LOGGER: Logger = LoggerFactory.getLogger("RSN-Server")
    private const val server = "rsn_server"
    @OptIn(DelicateCoroutinesApi::class)
    override fun onInitialize() {
        LOGGER.info("RSN-Server Is Loaded!");
        Commands()
        JoinServerEvent().joinEvent()
    }
}