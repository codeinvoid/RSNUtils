package org.pio.rsn.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.net.SocketAddress;

@Mixin(net.minecraft.server.PlayerManager.class)
public abstract class PlayerManager {
    /**
     * @author HoiGe
     * @reason 修改返回
     */
    @Overwrite
    @Nullable
    public Text checkCanJoin(SocketAddress address, GameProfile profile) {
        return null;
    }
}
