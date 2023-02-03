package org.pio.rsn.config

import io.github.prismwork.prismconfig.api.PrismConfig
import io.github.prismwork.prismconfig.api.config.DefaultDeserializers
import io.github.prismwork.prismconfig.api.config.DefaultSerializers
import org.pio.rsn.api.ConfigOperator
import java.io.File

class ConfigOperator(file: File) : ConfigOperator {
    private val configFile = file

    override fun <T> read(config: Class<T>): T {
        return PrismConfig.getInstance().serialize(
            config,
            configFile,
            DefaultSerializers.getInstance().toml(config)
        )
    }

    override fun <T> write(config: Class<T>, content: T) {
        PrismConfig.getInstance().deserializeAndWrite(
            config,
            content,
            DefaultDeserializers.getInstance().toml(config),
            configFile
        )
    }
}
