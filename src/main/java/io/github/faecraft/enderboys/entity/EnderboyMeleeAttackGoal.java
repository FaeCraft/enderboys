package io.github.faecraft.enderboys.entity;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PathAwareEntity;

public class EnderboyMeleeAttackGoal extends MeleeAttackGoal {
    public EnderboyMeleeAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
    }

    @Override
    public boolean canStart() {
        if (this.mob.getTarget() instanceof EndermanEntity) {
            return false;
        }

        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.getTarget() instanceof EndermanEntity) {
            return false;
        }

        return super.shouldContinue();
    }
}
