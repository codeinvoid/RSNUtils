package org.pio.rsn.mixin.whitelist;

import com.google.common.collect.Lists;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.pio.rsn.model.Whitelist;
import org.pio.rsn.utils.StateKt;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class WhitelistChecker {
    @Shadow @Final private static Logger LOGGER;

    private static final AtomicInteger NEXT_WHITELIST_THREAD_ID = new AtomicInteger(0);

    /**
     * @author HoiGe
     * @reason REMIX Whitelist
     */
    @Overwrite
    public void kickNonWhitelistedPlayers(ServerCommandSource source) {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        List<ServerPlayerEntity> list = Lists.newArrayList(playerManager.getPlayerList());
        for (ServerPlayerEntity serverPlayerEntity : list) {
            Thread thread = new Thread(() ->
                   checkWhitelistValue(serverPlayerEntity), "Whitelist thread #"+ NEXT_WHITELIST_THREAD_ID.incrementAndGet());
            thread.setUncaughtExceptionHandler((threadx, throwable) ->
                    LOGGER.error("Uncaught exception in server thread", throwable));
            if (Runtime.getRuntime().availableProcessors() > 4) {
                thread.setPriority(8);
            }
            thread.start();
        }
    }

    public void checkWhitelistValue(ServerPlayerEntity serverPlayerEntity) {
        Whitelist isWhitelistActive = StateKt.requestWhitelist(serverPlayerEntity.getUuidAsString());
        if (isWhitelistActive == null) {
            serverPlayerEntity.networkHandler.disconnect(Text.literal("multiplayer.disconnect.not_whitelisted"));
        } else {
            serverPlayerEntity.sendMessage(Text.literal("✔ ").setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                    .append("白名单验证通过"));
        }
    }
}
