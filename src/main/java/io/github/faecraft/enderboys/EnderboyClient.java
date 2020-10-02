package io.github.faecraft.enderboys;

import io.github.faecraft.enderboys.client.renderer.EnderboyEntityRenderer;
import io.github.faecraft.enderboys.entity.EnderboyEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class EnderboyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.INSTANCE.register(Enderboy.ENDERBOY, ((entityRenderDispatcher, context) -> {
            return new EnderboyEntityRenderer(entityRenderDispatcher);
        }));
    }
}
