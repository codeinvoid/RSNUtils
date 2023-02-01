package org.pio.rsn.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.pio.rsn.MinecraftServerInterface;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerInterface{
    @Override
    public String getServerModName() {
        return "RSNCore";
    }
}
