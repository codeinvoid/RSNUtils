package org.pio.rsn.command

import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.MessageArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literal


class Commands {
    private val noPermissions: MutableText = Text.literal("你使用没有该命令的权限!")

    private fun playerOnly(source: ServerCommandSource) {
        source.sendError(Text.literal("只有玩家才能使用这个指令"))
    }

    val verify = command("verify") {
        argument<Int>("验证码") { code ->
            runsAsync {
                if (source.isExecutedByPlayer)
                    if (!Permissions.check(source, "rsn.command.verify")) {
                        source.sendError(noPermissions)
                    } else {
                        VerifyCommand().verify(code(), source)
                    }
                else playerOnly(source)
            }
        }
    }

    val ban = command("ban") {
        argument<String>("ID"){ player ->
            argument("Reason", MessageArgumentType.message()){ reason ->
                runsAsync {
                    if (!Permissions.check(source, "rsn.admin.command.ban", 4)) {
                        source.sendError(noPermissions)
                    }else{
                        BanCommand().banHandle(source, player(), reason().contents.literal.string)
                    }
                }
            }
        }
    }

    val unban = command("unban") {
        argument<String>("ID"){ player ->
            runsAsync {
                if (!Permissions.check(source, "rsn.admin.command.unban", 4)) {
                    source.sendError(noPermissions)
                } else {
                    UnbanCommand().unbanHandle(source, player())
                }
            }
        }
    }
}
