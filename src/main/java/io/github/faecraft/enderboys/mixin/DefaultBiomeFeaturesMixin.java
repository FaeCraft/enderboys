package io.github.faecraft.enderboys.mixin;

import io.github.faecraft.enderboys.Enderboy;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultBiomeFeatures.class)
public class DefaultBiomeFeaturesMixin {
    @Inject(at = @At("TAIL"), method = "addMonsters")
    private static void addMonsters(SpawnSettings.Builder builder, int zombieWeight, int zombieVillagerWeight, int skeletonWeight, CallbackInfo ci) {
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(Enderboy.ENDERBOY, 10, 1, 4));
    }

    @Inject(at = @At("TAIL"), method = "addEndMobs")
    private static void addEndMobs(SpawnSettings.Builder builder, CallbackInfo ci) {
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(Enderboy.ENDERBOY, 10, 1, 4));
    }
}
