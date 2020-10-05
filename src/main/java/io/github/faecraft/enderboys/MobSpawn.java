package io.github.faecraft.enderboys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;

public class MobSpawn {

    public static void addSpawnEntries() {
        for (Biome biome : BuiltinRegistries.BIOME) {
            if (biome.getCategory().equals(Biome.Category.THEEND)) {
                addMobSpawnToBiome(biome, SpawnGroup.MONSTER,
                        new SpawnSettings.SpawnEntry(Enderboy.ENDERBOY, 10, 1, 4));

            }
        }
    }

    public static void addMobSpawnToBiome(Biome biome, SpawnGroup classification, SpawnSettings.SpawnEntry... spawnInfos) {
        List<SpawnSettings.SpawnEntry> spawnersList = new ArrayList<>(
                biome.getSpawnSettings().spawners.get(classification));
                spawnersList.addAll(Arrays.asList(spawnInfos));
                biome.getSpawnSettings().spawners.put(classification, spawnersList);
    }


    public static void SpawnRestriction() {
        SpawnRestriction.register(Enderboy.ENDERBOY, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
    }
}