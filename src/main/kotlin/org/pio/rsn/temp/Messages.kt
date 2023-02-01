package org.pio.rsn.temp

import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.pio.rsn.model.Banned
import org.pio.rsn.model.Integration
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun bannedMessage(banned: Banned) : MutableText {
    return Text
        .literal("你已被封禁\n\n")
        .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true))
        .append(
            Text.literal("原因 ❯❯ ")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false)))
        .append(
            Text.literal(banned.reason+"\n")
                .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(false)))
        .append(
            Text.literal("封禁编号 ❯❯ ")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false)))
        .append(
            Text.literal(banned.nanoid+"\n")
                .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(false)))
        .append(
            Text.literal("操作时间 ❯❯ ")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false)))
        .append(
            Text.literal(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(banned.time)+ "\n")
                .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(false)))
        .append(
            Text.literal("申诉 ❯❯ ")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false)))
        .append(Text.literal("如有疑问发送邮件至 issue@p-io.org").setStyle(Style.EMPTY.withBold(false)))
}

val whitelistTitle : Text = Text.literal("输入 ").setStyle(Style.EMPTY.withColor(Formatting.GOLD))
    .append(Text.literal("/verify ").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
    .append(Text.literal("<验证码>").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))

fun cardFeedback(card: Integration): Text {
    val count = card.count.toDouble() / 100
    val countPoint = DecimalFormat("0.00")
    return Text.literal("玩家一卡通\n")
    .append(Text.literal("卡号 ➤ " + "${card.nanoid}\n"))
    .append(Text.literal("余额 ➤ ∅${countPoint.format(count)}\n"))
    .append(Text.literal("开通时间 ➤ " + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(card.time)))
}