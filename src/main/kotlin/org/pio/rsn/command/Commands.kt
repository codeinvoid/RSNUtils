package org.pio.rsn.command

import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.silkmc.silk.commands.command
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Commands {
    private val logger: Logger = LoggerFactory.getLogger("RSN-I")
    val awa = command("verify") {
        argument<String>("code") { code ->
            runs {
                if (source.isExecutedByPlayer) Link().ver(code().toString(), source) else playerOnly(source)
            }
        }
    }

    val ban = command("aban") {
        argument<String>("player") { player ->
            argument<String>("reason") { reason ->
                runs {
                    if (source.hasPermissionLevel(4)) println()
                }
            }
        }
    }

    val unban = command("unban") {
        argument<String>("code") { code ->
            runs {
                if (source.isExecutedByPlayer) Link().ver(code().toString(), source) else playerOnly(source)
            }
        }
    }

    private fun playerOnly(source: ServerCommandSource){
        source.sendError(Text.literal("只有玩家才能使用这个指令"))
    }
}
