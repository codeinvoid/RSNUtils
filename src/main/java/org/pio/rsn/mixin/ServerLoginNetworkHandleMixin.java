package org.pio.rsn.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
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
    @Shadow @Nullable GameProfile profile;
    @Shadow
    public String getConnectionInfo() {
        return null;
    }

    private static final AtomicInteger NEXT_LOGGING_THREAD_ID = new AtomicInteger(0);

    /**
     * @author HoiGe
     * @reason 设置封禁
     */
    @Overwrite
    private void addToServer(ServerPlayerEntity player) {
        Thread thread = new Thread(() ->
                requestBannedList(player), "Logging thread #"+ NEXT_LOGGING_THREAD_ID.incrementAndGet());
        thread.setUncaughtExceptionHandler((threadx, throwable) ->
                LOGGER.error("Uncaught exception in server thread", throwable));
        if (Runtime.getRuntime().availableProcessors() > 4) {
            thread.setPriority(6);
        }
        thread.start();
    }

    private void requestBannedList(ServerPlayerEntity player) {
        if (StateKt.contrastBanned(player.getUuidAsString()) != null) {
            addPlayerConnect(player);
        } else {
            Text text = Text.literal("错误 ").append(Text.literal("API请求失败!"));
            this.connection.send(new DisconnectS2CPacket(text));
            this.connection.disconnect(text);
        }
    }

    private void addPlayerConnect(ServerPlayerEntity player) {
        Boolean uUid = StateKt.contrastBanned(player.getUuidAsString());
        if (Boolean.TRUE.equals(uUid)) {
            this.server.getPlayerManager().onPlayerConnect(this.connection, player);
        } else {
            Text text = bannedMessage(Objects.requireNonNull(requestBanned(player.getUuidAsString())));
            this.connection.send(new DisconnectS2CPacket(text));
            this.connection.disconnect(text);
        }
    }

    /**
     * @author HoiGe
     * @reason 修改断开连接通知 眼不见心不烦
     */
    @Overwrite
    public void onDisconnected(Text reason) {
        Thread thread = new Thread(() ->
                sendDisconnectedMessages(reason), "Logging thread #"+ NEXT_LOGGING_THREAD_ID.incrementAndGet());
        thread.setUncaughtExceptionHandler((threadx, throwable) ->
                LOGGER.error("Uncaught exception in server thread", throwable));
        if (Runtime.getRuntime().availableProcessors() > 4) {
            thread.setPriority(6);
        }
        thread.start();
    }

    private void sendDisconnectedMessages(Text reason) {
        try {
            assert this.profile != null;
            if (Boolean.TRUE.equals(StateKt.contrastBanned(this.profile.getId().toString()))) {
                LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getString());
            }
        } catch (Exception exception) {
            LOGGER.error("Couldn't get player data.");
            MutableText text2 = Text.translatable("multiplayer.disconnect.invalid_player_data");
            this.connection.send(new DisconnectS2CPacket(text2));
            this.connection.disconnect(text2);
        }
    }
}
