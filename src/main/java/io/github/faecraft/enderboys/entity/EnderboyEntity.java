package io.github.faecraft.enderboys.entity;

import io.github.faecraft.enderboys.Enderboy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class EnderboyEntity extends EndermanEntity implements Angerable {

    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST;
    private static final TrackedData<Optional<BlockState>> CARRIED_BLOCK;
    private static final TrackedData<Boolean> ANGRY;
    private static final TrackedData<Boolean> PROVOKED;
    private static final Predicate<LivingEntity> PLAYER_ENDERMITE_PREDICATE;
    private int lastAngrySoundAge = -2147483648;
    private int ageWhenTargetSet;
    private static final IntRange ANGER_TIME_RANGE;
    private int angerTime;
    private UUID targetUuid;

    public EnderboyEntity(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1.0F;
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EnderboyEntity.ChasePlayerGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D, 0.0F));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(10, new EnderboyEntity.PlaceBlockGoal(this));
        this.goalSelector.add(11, new EnderboyEntity.PickUpBlockGoal(this));
        this.targetSelector.add(1, new EnderboyEntity.TeleportTowardsPlayerGoal(this, this::shouldAngerAt));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new FollowTargetGoal(this, EndermiteEntity.class, 10, true, false, PLAYER_ENDERMITE_PREDICATE));
        this.targetSelector.add(3, new EnderboyEntity.FollowEntityGoal(this));
        this.targetSelector.add(4, new UniversalAngerGoal(this, false));
    }

    public static DefaultAttributeContainer.Builder createEnderboyAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.5D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D);
    }

    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.dataTracker.set(ANGRY, false);
            this.dataTracker.set(PROVOKED, false);
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            this.ageWhenTargetSet = this.age;
            this.dataTracker.set(ANGRY, true);
            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
        }

    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CARRIED_BLOCK, Optional.empty());
        this.dataTracker.startTracking(ANGRY, false);
        this.dataTracker.startTracking(PROVOKED, false);
    }

    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.choose(this.random));
    }

    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    public void setAngryAt(@Nullable UUID uuid) {
        this.targetUuid = uuid;
    }

    public UUID getAngryAt() {
        return this.targetUuid;
    }

    public void playAngrySound() {
        if (this.age >= this.lastAngrySoundAge + 400) {
            this.lastAngrySoundAge = this.age;
            if (!this.isSilent()) {
                this.world.playSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
            }
        }

    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (ANGRY.equals(data) && this.isProvoked() && this.world.isClient) {
            this.playAngrySound();
        }

        super.onTrackedDataSet(data);
    }

    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        BlockState blockState = this.getCarriedBlock();
        if (blockState != null) {
            tag.put("carriedBlockState", NbtHelper.fromBlockState(blockState));
        }

        this.angerToTag(tag);
    }

    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        BlockState blockState = null;
        if (tag.contains("carriedBlockState", 10)) {
            blockState = NbtHelper.toBlockState(tag.getCompound("carriedBlockState"));
            if (blockState.isAir()) {
                blockState = null;
            }
        }

        this.setCarriedBlock(blockState);
        this.angerFromTag((ServerWorld) this.world, tag);
    }

    private boolean isPlayerStaring(PlayerEntity player) {
        ItemStack itemStack = (ItemStack) player.inventory.armor.get(3);
        if (itemStack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            return false;
        } else {
            Vec3d vec3d = player.getRotationVec(1.0F).normalize();
            Vec3d vec3d2 = new Vec3d(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
            double d = vec3d2.length();
            vec3d2 = vec3d2.normalize();
            double e = vec3d.dotProduct(vec3d2);
            return e > 1.0D - 0.025D / d ? player.canSee(this) : false;
        }
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.55F;
    }

    public void tickMovement() {
        if (this.world.isClient) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.PORTAL, this.getParticleX(0.5D), this.getRandomBodyY() - 0.25D, this.getParticleZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        this.jumping = false;
        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld) this.world, true);
        }

        super.tickMovement();
    }

    public boolean hurtByWater() {
        return true;
    }

    protected void mobTick() {
        if (this.world.isDay() && this.age >= this.ageWhenTargetSet + 600) {
            float f = this.getBrightnessAtEyes();
            if (f > 0.5F && this.world.isSkyVisible(this.getBlockPos()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setTarget((LivingEntity) null);
                this.teleportRandomly();
            }
        }

        super.mobTick();
    }

    protected boolean teleportRandomly() {
        if (!this.world.isClient() && this.isAlive()) {
            double d = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double e = this.getY() + (double) (this.random.nextInt(64) - 32);
            double f = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            return this.teleportTo(d, e, f);
        } else {
            return false;
        }
    }

    private boolean teleportTo(Entity entity) {
        Vec3d vec3d = new Vec3d(this.getX() - entity.getX(), this.getBodyY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3d = vec3d.normalize();
        double d = 16.0D;
        double e = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double f = this.getY() + (double) (this.random.nextInt(16) - 8) - vec3d.y * 16.0D;
        double g = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
        return this.teleportTo(e, f, g);
    }

    private boolean teleportTo(double x, double y, double z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while (mutable.getY() > 0 && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.world.getBlockState(mutable);
        boolean bl = blockState.getMaterial().blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2) {
            boolean bl3 = this.teleport(x, y, z, true);
            if (bl3 && !this.isSilent()) {
                this.world.playSound((PlayerEntity) null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return bl3;
        } else {
            return false;
        }
    }

    protected SoundEvent getAmbientSound() {
        return this.isAngry() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }

    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        BlockState blockState = this.getCarriedBlock();
        if (blockState != null) {
            this.dropItem(blockState.getBlock());
        }

    }

    public void setCarriedBlock(@Nullable BlockState state) {
        this.dataTracker.set(CARRIED_BLOCK, Optional.ofNullable(state));
    }

    @Nullable
    public BlockState getCarriedBlock() {
        return (BlockState) ((Optional) this.dataTracker.get(CARRIED_BLOCK)).orElse((Object) null);
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source instanceof ProjectileDamageSource) {
            for (int i = 0; i < 64; ++i) {
                if (this.teleportRandomly()) {
                    return true;
                }
            }

            return false;
        } else {
            boolean bl = super.damage(source, amount);
            if (!this.world.isClient() && !(source.getAttacker() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
                this.teleportRandomly();
            }

            return bl;
        }
    }

    public boolean isAngry() {
        return (Boolean) this.dataTracker.get(ANGRY);
    }

    public boolean isProvoked() {
        return (Boolean) this.dataTracker.get(PROVOKED);
    }

    public void setProvoked() {
        this.dataTracker.set(PROVOKED, true);
    }

    public boolean cannotDespawn() {
        return super.cannotDespawn() || this.getCarriedBlock() != null;
    }

    static {
        ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.125D, EntityAttributeModifier.Operation.ADDITION);
        CARRIED_BLOCK = DataTracker.registerData(EnderboyEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
        ANGRY = DataTracker.registerData(EnderboyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PROVOKED = DataTracker.registerData(EnderboyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PLAYER_ENDERMITE_PREDICATE = (livingEntity) -> {
            return livingEntity instanceof EndermiteEntity && ((EndermiteEntity) livingEntity).isPlayerSpawned();
        };
        ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
    }

    static class PickUpBlockGoal extends Goal {
        private final EnderboyEntity enderboy;

        public PickUpBlockGoal(EnderboyEntity enderboy) {
            this.enderboy = enderboy;
        }

        public boolean canStart() {
            if (this.enderboy.getCarriedBlock() != null) {
                return false;
            } else if (!this.enderboy.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return false;
            } else {
                return this.enderboy.getRandom().nextInt(20) == 0;
            }
        }

        public void tick() {
            Random random = this.enderboy.getRandom();
            World world = this.enderboy.world;
            int i = MathHelper.floor(this.enderboy.getX() - 2.0D + random.nextDouble() * 4.0D);
            int j = MathHelper.floor(this.enderboy.getY() + random.nextDouble() * 3.0D);
            int k = MathHelper.floor(this.enderboy.getZ() - 2.0D + random.nextDouble() * 4.0D);
            BlockPos blockPos = new BlockPos(i, j, k);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            Vec3d vec3d = new Vec3d((double) MathHelper.floor(this.enderboy.getX()) + 0.5D, (double) j + 0.5D, (double) MathHelper.floor(this.enderboy.getZ()) + 0.5D);
            Vec3d vec3d2 = new Vec3d((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D);
            BlockHitResult blockHitResult = world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this.enderboy));
            boolean bl = blockHitResult.getBlockPos().equals(blockPos);
            if (block.isIn(BlockTags.ENDERMAN_HOLDABLE) && bl) {
                world.removeBlock(blockPos, false);
                this.enderboy.setCarriedBlock(blockState.getBlock().getDefaultState());
            }

        }
    }

    static class PlaceBlockGoal extends Goal {
        private final EnderboyEntity enderboy;

        public PlaceBlockGoal(EnderboyEntity enderboy) {
            this.enderboy = enderboy;
        }

        public boolean canStart() {
            if (this.enderboy.getCarriedBlock() == null) {
                return false;
            } else if (!this.enderboy.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return false;
            } else {
                return this.enderboy.getRandom().nextInt(2000) == 0;
            }
        }

        public void tick() {
            Random random = this.enderboy.getRandom();
            World world = this.enderboy.world;
            int i = MathHelper.floor(this.enderboy.getX() - 1.0D + random.nextDouble() * 2.0D);
            int j = MathHelper.floor(this.enderboy.getY() + random.nextDouble() * 2.0D);
            int k = MathHelper.floor(this.enderboy.getZ() - 1.0D + random.nextDouble() * 2.0D);
            BlockPos blockPos = new BlockPos(i, j, k);
            BlockState blockState = world.getBlockState(blockPos);
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState2 = world.getBlockState(blockPos2);
            BlockState blockState3 = this.enderboy.getCarriedBlock();
            if (blockState3 != null) {
                blockState3 = Block.postProcessState(blockState3, this.enderboy.world, blockPos);
                if (this.canPlaceOn(world, blockPos, blockState3, blockState, blockState2, blockPos2)) {
                    world.setBlockState(blockPos, blockState3, 3);
                    this.enderboy.setCarriedBlock((BlockState) null);
                }

            }
        }

        private boolean canPlaceOn(World world, BlockPos posAbove, BlockState carriedState, BlockState stateAbove, BlockState state, BlockPos pos) {
            return stateAbove.isAir() && !state.isAir() && !state.isOf(Blocks.BEDROCK) && state.isFullCube(world, pos) && carriedState.canPlaceAt(world, posAbove) && world.getOtherEntities(this.enderboy, Box.method_29968(Vec3d.of(posAbove))).isEmpty();
        }
    }

    static class ChasePlayerGoal extends Goal {
        private final EnderboyEntity enderboy;
        private LivingEntity target;

        public ChasePlayerGoal(EnderboyEntity enderboy) {
            this.enderboy = enderboy;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            this.target = this.enderboy.getTarget();
            if (!(this.target instanceof PlayerEntity)) {
                return false;
            } else {
                double d = this.target.squaredDistanceTo(this.enderboy);
                return d > 256.0D ? false : this.enderboy.isPlayerStaring((PlayerEntity) this.target);
            }
        }

        public void start() {
            this.enderboy.getNavigation().stop();
        }

        public void tick() {
            this.enderboy.getLookControl().lookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    static class TeleportTowardsPlayerGoal extends FollowTargetGoal<PlayerEntity> {
        private final EnderboyEntity enderboy;
        private PlayerEntity targetPlayer;
        private int lookAtPlayerWarmup;
        private int ticksSinceUnseenTeleport;
        private final TargetPredicate staringPlayerPredicate;
        private final TargetPredicate validTargetPredicate = (new TargetPredicate()).includeHidden();

        public TeleportTowardsPlayerGoal(EnderboyEntity enderboy, @Nullable Predicate<LivingEntity> predicate) {
            super(enderboy, PlayerEntity.class, 10, false, false, predicate);
            this.enderboy = enderboy;
            this.staringPlayerPredicate = (new TargetPredicate()).setBaseMaxDistance(this.getFollowRange()).setPredicate((playerEntity) -> {
                return enderboy.isPlayerStaring((PlayerEntity) playerEntity);
            });
        }

        public boolean canStart() {
            this.targetPlayer = this.enderboy.world.getClosestPlayer(this.staringPlayerPredicate, this.enderboy);
            return this.targetPlayer != null;
        }

        public void start() {
            this.lookAtPlayerWarmup = 5;
            this.ticksSinceUnseenTeleport = 0;
            this.enderboy.setProvoked();
        }

        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }

        public boolean shouldContinue() {
            if (this.targetPlayer != null) {
                if (!this.enderboy.isPlayerStaring(this.targetPlayer)) {
                    return false;
                } else {
                    this.enderboy.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.targetEntity != null && this.validTargetPredicate.test(this.enderboy, this.targetEntity) ? true : super.shouldContinue();
            }
        }

        public void tick() {
            if (this.enderboy.getTarget() == null) {
                super.setTargetEntity((LivingEntity) null);
            }

            if (this.targetPlayer != null) {
                if (--this.lookAtPlayerWarmup <= 0) {
                    this.targetEntity = this.targetPlayer;
                    this.targetPlayer = null;
                    super.start();
                }
            } else {
                if (this.targetEntity != null && !this.enderboy.hasVehicle()) {
                    if (this.enderboy.isPlayerStaring((PlayerEntity) this.targetEntity)) {
                        if (this.targetEntity.squaredDistanceTo(this.enderboy) < 16.0D) {
                            this.enderboy.teleportRandomly();
                        }

                        this.ticksSinceUnseenTeleport = 0;
                    } else if (this.targetEntity.squaredDistanceTo(this.enderboy) > 256.0D && this.ticksSinceUnseenTeleport++ >= 30 && this.enderboy.teleportTo(this.targetEntity)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }

                super.tick();
            }

        }
    }

    static class FollowEntityGoal extends FollowTargetGoal<EndermanEntity> {
        public FollowEntityGoal(EnderboyEntity enderboy) {
            super(enderboy, EndermanEntity.class, 0, true, true, LivingEntity::isMobOrPlayer);
        }

        public void start() {
            super.start();
            this.mob.setDespawnCounter(0);
        }
    }

}
