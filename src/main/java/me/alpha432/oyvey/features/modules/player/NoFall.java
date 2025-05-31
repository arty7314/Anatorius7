package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Disables all knockback", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if( !mc.player.isOnGround() && OyVey.positionManager.getFallDistance() > 3 )
        {
            boolean bl = mc.player.horizontalCollision;
            PlayerMoveC2SPacket.Full pakcet = new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY() + 0.000000001, mc.player.getZ(),
                    mc.player.getYaw(), mc.player.getPitch(), false, bl );
            mc.player.networkHandler.sendPacket(pakcet);

        }
    }

    @Override
    public void onRender(MatrixStack matrices, float tickDelta) {

    }

    @Override
    public void onRenderWorldLast(MatrixStack matrices, float tickDelta) {

    }

    @Override
    public void onRender3D(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float tickDelta) {

    }




    @Override
    public void onRender3D(float partialTicks) {

    }

    @Override
    public void onRender3D(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {

    }

    @Override
    public void onRender3D(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, float tickDelta) {

    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {

    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {

    }

    @Override
    public void onRender() {

    }
}
