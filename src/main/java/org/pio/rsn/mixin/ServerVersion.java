package org.pio.rsn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(net.minecraft.MinecraftVersion.class)
public abstract class ServerVersion {
    /**
     * @author HoiGe
     * @reason 修改获取版本类型
     */
    @Overwrite
    public String getName() {
        return "RSNCore 1.19.3";
    }
}
