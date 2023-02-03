package org.pio.rsn.command

import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.GameProfileArgumentType
import net.minecraft.command.argument.MessageArgumentType
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.silkmc.silk.commands.command

class Commands {
    companion object {
        const val maxLevel = 4

    }
    val ban = command("ban") {
        requires { source ->  Permissions.check(source, "rsn.admin.command.ban", maxLevel) }
        argument("targets", GameProfileArgumentType.gameProfile()) { player ->
            runsAsync {
                BanCommand().banHandle(source, player().getNames(source), null)
            }
            argument("reason", MessageArgumentType.message()){ reason ->
                runsAsync {
                    BanCommand().banHandle(source, player().getNames(source), reason().contents.toString())
                }
            }
        }
    }

    val unban = command("unban") {
        requires { source ->  Permissions.check(source, "rsn.admin.command.unban", maxLevel) }
        argument("targets", GameProfileArgumentType.gameProfile()) { player ->
            runsAsync {
                UnbanCommand().unbanHandle(source, player().getNames(source))
            }
        }
    }

    val card = command("card") {
        requires { source ->  Permissions.check(source, "rsn.command.card") }
        runsAsync {
            CardCommand().init(source)
        }

        literal("to") {
            argument("targets", GameProfileArgumentType.gameProfile()) {
                argument<Int>("count") {
                    runsAsync {

                    }
                }
            }
        }

        literal("check") {
            requires { source ->  Permissions.check(source, "rsn.admin.command.card.check", maxLevel) }
            runsAsync {

            }
        }
    }

    val verify = command("verify") {
        requires { source ->  Permissions.check(source, "rsn.command.verify") }
        requires { source -> source.isExecutedByPlayer }
        argument<Int>("code") { code ->
            runsAsync {
                VerifyCommand().verify(code(), source)
            }
        }
    }

    val main = command("rsn") {
        runsAsync {
            source.sendMessage(Text.literal("RSN Utils VER.1.0\n")
                .setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                .append(Text.literal("纮鸽 保留所有权利")
                    .setStyle(Style.EMPTY.withColor(Formatting.GOLD))))
        }

        literal("token") {
            requires { source ->  Permissions.check(source, "rsn.admin.command.config.token") }
            argument<String>("token") { token ->
                runsAsync {
                    MainCommand().settingToken(token(), source)
                }
            }
            runsAsync {
                MainCommand().lookupToken(source)
            }
        }
    }
}
