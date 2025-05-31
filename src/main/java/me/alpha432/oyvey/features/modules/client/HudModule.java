package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class HudModule extends Module {
    public HudModule() {
        super("Hud", "Disables all knockback", Category.CLIENT, true, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int color = getSpaceColor(System.currentTimeMillis());
        event.getContext().drawTextWithShadow(
                mc.textRenderer,
                "Anatorius 0.6",
                3,
                1,
                color
        );
    }

    // Uzay temalı yumuşak RGB geçişli renk hesaplama
    private int getSpaceColor(long time) {
        float speed = 0.002f; // Renk geçiş hızı (ayarlanabilir)
        float hue = (time * speed) % 1.0f;
        // HSV renk paletinden geçişli renk (doygunluk ve parlaklık sabit)
        return Color.HSBtoRGB(hue, 0.7f, 1.0f);
    }

    @Override public void onRender(MatrixStack matrices, float tickDelta) {}
    @Override public void onRenderWorldLast(MatrixStack matrices, float tickDelta) {}
    @Override public void onRender3D(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float tickDelta) {}
    @Override public void onRender3D(float partialTicks) {}
    @Override public void onRender3D(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {}
    @Override public void onRender3D(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, float tickDelta) {}

    @Override
    public void onPacketSend(PacketEvent.Send event) {}

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {

    }

    @Override
    public void onRender() {

    }
}
