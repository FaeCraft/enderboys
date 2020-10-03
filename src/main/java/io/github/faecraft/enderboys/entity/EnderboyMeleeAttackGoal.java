package io.github.faecraft.enderboys.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PathAwareEntity;

public class EnderboyMeleeAttackGoal extends MeleeAttackGoal {
    public EnderboyMeleeAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        if (this.mob.getTarget() instanceof EndermanEntity) {
            return;
        }

        super.attack(target, squaredDistance);
    }
}
