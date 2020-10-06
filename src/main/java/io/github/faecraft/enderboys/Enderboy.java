package io.github.faecraft.enderboys;

import io.github.faecraft.enderboys.entity.EnderboyEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Enderboy implements ModInitializer {

    public static final EntityType<EnderboyEntity> ENDERBOY = net.minecraft.util.registry.Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("enderboys","enderboy"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EnderboyEntity::new).dimensions(EntityDimensions.fixed(0.55f,2.0f)).build()

    );

    public static final SpawnEggItem ENDERBOY_SPAWN_EGG = new SpawnEggItem(
            Enderboy.ENDERBOY,
            0x262626,
            0x000000,
            new FabricItemSettings().group(ItemGroup.MISC)
    );

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(ENDERBOY, EnderboyEntity.createMobAttributes());
        Registry.register(Registry.ITEM, new Identifier("enderboys","enderboy_spawn_egg"), ENDERBOY_SPAWN_EGG);
        }
}
