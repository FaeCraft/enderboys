package io.github.faecraft.enderboys.client.renderer;

import io.github.faecraft.enderboys.client.model.EnderboyEntityModel;
import io.github.faecraft.enderboys.entity.EnderboyEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EndermanEntity;

@Environment(EnvType.CLIENT)
public class EnderboyBlockFeatureRenderer extends FeatureRenderer<EnderboyEntity, EnderboyEntityModel<EnderboyEntity>> {
    public EnderboyBlockFeatureRenderer(FeatureRendererContext<EnderboyEntity, EnderboyEntityModel<EnderboyEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                       int light, EnderboyEntity entity,
                       float limbAngle,
                       float limbDistance,
                       float tickDelta,
                       float animationProgress,
                       float headYaw,
                       float headPitch) {

        BlockState blockState = EnderboyEntity.getCarriedBlock();
        if (blockState != null) {
            matrices.push();
            matrices.translate(0.0D, 0.6875D, -0.75D);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(20.0F));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(45.0F));
            matrices.translate(0.25D, 0.1875D, 0.25D);
            float m = 0.5F;
            matrices.scale(-0.5F, -0.5F, 0.5F);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }


    }
}