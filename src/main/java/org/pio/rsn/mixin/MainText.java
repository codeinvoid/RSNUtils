package org.pio.rsn.mixin;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.Main.class)
public abstract class MainText {
    @Shadow @Final private static Logger LOGGER;

    @Shadow
    public static void main(String[] args) {
    }

    @Inject(method = "main", at = @At("HEAD"))
    private static void main(String[] args, CallbackInfo ci) {
        LOGGER.info("""
                     
                     ___           ___           ___    \s
                    /\\  \\         /\\  \\         /\\__\\   \s
                   /::\\  \\       /::\\  \\       /::|  |  \s
                  /:/\\:\\  \\     /:/\\ \\  \\     /:|:|  |  \s
                 /::\\~\\:\\  \\   _\\:\\~\\ \\  \\   /:/|:|  |__\s
                /:/\\:\\ \\:\\__\\ /\\ \\:\\ \\ \\__\\ /:/ |:| /\\__\\
                \\/_|::\\/:/  / \\:\\ \\:\\ \\/__/ \\/__|:|/:/  /
                   |:|::/  /   \\:\\ \\:\\__\\       |:/:/  /\s
                   |:|\\/__/     \\:\\/:/  /       |::/  / \s
                   |:|  |        \\::/  /        /:/  /  \s
                    \\|__|         \\/__/         \\/__/   \s""");
        LOGGER.info("RSN SERVER 1.19.3");
    }
}
