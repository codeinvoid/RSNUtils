package org.pio.rsn.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pio.rsn.utils.StateKt;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.pio.rsn.temp.MessagesKt.bannedMessage;
import static org.pio.rsn.utils.StateKt.requestBanned;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandleMixin {
    @Shadow @Final public ClientConnection connection;
    @Shadow @Final static Logger LOGGER;
    @Shadow @Final MinecraftServer server;

    private static final AtomicInteger NEXT_BANNED_THREAD_ID = new AtomicInteger(0);

    /**
     * @author HoiGe
     * @reason SetBan
     */
    @Overwrite
    private void addToServer(ServerPlayerEntity player) {
        Thread thread = new Thread(() ->
                requestBannedList(player), "Banned thread #"+ NEXT_BANNED_THREAD_ID.incrementAndGet());
        thread.setUncaughtExceptionHandler((threadx, throwable) ->
                LOGGER.error("Uncaught exception in server thread", throwable));
        if (Runtime.getRuntime().availableProcessors() > 4) {
            thread.setPriority(8);
        }
        thread.start();
    }

    private void requestBannedList(ServerPlayerEntity player) {
        if (StateKt.contrastBanned(player.getUuidAsString()) != null) {
            if (Boolean.TRUE.equals(StateKt.contrastBanned(player.getUuidAsString()))) {
                this.server.getPlayerManager().onPlayerConnect(this.connection, player);
            } else {
                Text text = bannedMessage(Objects.requireNonNull(requestBanned(player.getUuidAsString())));
                this.connection.send(new DisconnectS2CPacket(text));
                this.connection.disconnect(text);
            }
        } else {
            Text text = Text.literal("错误 ").append(Text.literal("API请求失败!"));
            this.connection.send(new DisconnectS2CPacket(text));
            this.connection.disconnect(text);
        }
    }
}
