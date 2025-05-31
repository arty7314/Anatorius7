package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class Velocity extends Module {

    public Velocity() {
        super("Velocity", "Completely disables knockback", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
            EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.getPacket();
            if (packet.getEntityId() == mc.player.getId()) {
                event.cancel(); // Paketi iptal et, knockback gelmesin
            }
        }

        if (event.getPacket() instanceof ExplosionS2CPacket) {
            event.cancel(); // Patlama knockback'ini iptal et
        }
    }

    @Override
    public void onRender() {

    }

    // Geri kalan override metodlar boş bırakılabilir
    @Override
    public void onRender(MatrixStack matrices, float tickDelta) {}

    @Override
    public void onRenderWorldLast(MatrixStack matrices, float tickDelta) {}

    @Override
    public void onRender3D(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float tickDelta) {}

    @Override
    public void onRender3D(float partialTicks) {}

    @Override
    public void onRender3D(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {}

    @Override
    public void onRender3D(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, float tickDelta) {}

    @Override
    public void onPacketSend(PacketEvent.Send event) {}
}
