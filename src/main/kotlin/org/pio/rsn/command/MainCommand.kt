package org.pio.rsn.command

import io.github.prismwork.prismconfig.api.PrismConfig
import io.github.prismwork.prismconfig.api.config.DefaultDeserializers
import io.github.prismwork.prismconfig.api.config.DefaultSerializers
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.pio.rsn.Server
import org.pio.rsn.config.Config

class MainCommand {
    fun settingToken(token: String, source: ServerCommandSource) {
        val content = Config(token)
        PrismConfig.getInstance().deserializeAndWrite(
            Config::class.java,
            content,
            DefaultDeserializers.getInstance().toml(Config::class.java),
            Server.configFile
        )
        source.sendFeedback(Text.literal("设置成功！\n").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false)
        Server.LOGGER.info("现有token为 $token")
    }

    fun lookupToken(source: ServerCommandSource) {
        val config: Config = PrismConfig.getInstance().serialize(
            Config::class.java,
            Server.configFile,
            DefaultSerializers.getInstance().toml(Config::class.java)
        )
        Server.LOGGER.info("现有token为 ${config.token}")
        if (source.isExecutedByPlayer) {
            source.sendMessage(Text.literal("不允许在游戏内获取TOKEN，请去控制台查看")
                .setStyle(Style.EMPTY.withColor(Formatting.RED)))
        }
    }
}
