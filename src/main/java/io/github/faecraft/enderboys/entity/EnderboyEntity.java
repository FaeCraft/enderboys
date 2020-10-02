package io.github.faecraft.enderboys.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.world.World;

public class EnderboyEntity extends EndermanEntity {
    public EnderboyEntity(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType,world);
    }
}
