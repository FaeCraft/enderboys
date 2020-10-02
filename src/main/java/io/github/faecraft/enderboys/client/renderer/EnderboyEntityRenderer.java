package io.github.faecraft.enderboys.client.renderer;

import io.github.faecraft.enderboys.client.model.EnderboyEntityModel;
import io.github.faecraft.enderboys.entity.EnderboyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class EnderboyEntityRenderer extends MobEntityRenderer<EnderboyEntity, EnderboyEntityModel<EnderboyEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/enderboy.png");
    private final Random random = new Random();

    public EnderboyEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EnderboyEntityModel(0.0F), 0.5F);
        this.addFeature(new EndermanEyesFeatureRenderer(this));
        this.addFeature(new EndermanBlockFeatureRenderer(this));
    }

    public void render(EnderboyEntity EnderboyEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BlockState blockState = EnderboyEntity.getCarriedBlock();
        EnderboyEntityModel<EnderboyEntity> EnderboyEntityModel = (EnderboyEntityModel)this.getModel();
        EnderboyEntityModel.carryingBlock = blockState != null;
        EnderboyEntityModel.angry = EnderboyEntity.isAngry();
        super.render(EnderboyEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Vec3d getPositionOffset(EnderboyEntity EnderboyEntity, float f) {
        if (EnderboyEntity.isAngry()) {
            double d = 0.02D;
            return new Vec3d(this.random.nextGaussian() * 0.02D, 0.0D, this.random.nextGaussian() * 0.02D);
        } else {
            return super.getPositionOffset(EnderboyEntity, f);
        }
    }

    public Identifier getTexture(EnderboyEntity EnderboyEntity) {
        return TEXTURE;
    }
}