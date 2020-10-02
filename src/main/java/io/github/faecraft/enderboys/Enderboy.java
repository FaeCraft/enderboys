package io.github.faecraft.enderboys;

import io.github.faecraft.enderboys.entity.EnderboyEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Enderboy implements ModInitializer {

    public static final EntityType<EnderboyEntity> ENDERBOY = net.minecraft.util.registry.Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("enderboy"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EnderboyEntity::new).dimensions(EntityDimensions.fixed(0.75f,0.75f)).build()

    );

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(ENDERBOY, EnderboyEntity.createMobAttributes());

    }
}
