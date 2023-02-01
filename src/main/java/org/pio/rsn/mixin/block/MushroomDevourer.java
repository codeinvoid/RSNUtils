package org.pio.rsn.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.block.MushroomPlantBlock.class)
public abstract class MushroomDevourer extends PlantBlock {

    public MushroomDevourer(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.getRegistryKey() == World.END) {
            world.removeBlock(pos,true);
        }
    }
}
