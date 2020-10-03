package io.github.faecraft.enderboys.client.renderer;

import io.github.faecraft.enderboys.client.model.EnderboyEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EnderboyEyesFeatureRenderer<T extends LivingEntity> extends EyesFeatureRenderer<T, EnderboyEntityModel<T>> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(new Identifier("enderboys", "textures/entity/enderboy_eyes.png"));

    public EnderboyEyesFeatureRenderer(FeatureRendererContext<T, EnderboyEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}
