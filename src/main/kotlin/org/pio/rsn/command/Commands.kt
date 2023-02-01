package org.pio.rsn.command

import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.GameProfileArgumentType
import net.minecraft.command.argument.MessageArgumentType
import net.silkmc.silk.commands.command

class Commands {

    val ban = command("ban") {
        requires { source ->  Permissions.check(source, "rsn.admin.command.ban", 4) }
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
        requires { source ->  Permissions.check(source, "rsn.admin.command.unban", 4) }
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
            requires { source ->  Permissions.check(source, "rsn.admin.command.card.check", 4) }
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
}
