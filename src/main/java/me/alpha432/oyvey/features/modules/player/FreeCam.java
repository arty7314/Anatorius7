package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.event.impl.PacketEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;

public class FreeCam extends Module {

    private Vec3d originalPos;
    private float originalYaw, originalPitch;
    private OtherClientPlayerEntity fakePlayer;

    public FreeCam() {
        super("FreeCam", "Disables all knockback", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        originalPos = mc.player.getPos();
        originalYaw = mc.player.getYaw();
        originalPitch = mc.player.getPitch();

        // FakePlayer oluştur
        fakePlayer = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
        fakePlayer.copyPositionAndRotation(mc.player);
        mc.world.addEntity(fakePlayer);

        mc.player.noClip = true;
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        mc.player.setPos(originalPos.x, originalPos.y, originalPos.z);
        mc.player.setYaw(originalYaw);
        mc.player.setPitch(originalPitch);
        mc.player.setVelocity(Vec3d.ZERO);
        mc.player.noClip = false;

        if (fakePlayer != null) {
            mc.world.removeEntity(fakePlayer.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }
    }

    @Override
    public void onUpdate() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        mc.player.setVelocity(Vec3d.ZERO);
        mc.player.noClip = true;

        double speed = 1.5;
        float yaw = mc.player.getYaw();
        Vec3d forward = new Vec3d(
                -MathHelper.sin(yaw * 0.017453292F) * speed,
                0,
                MathHelper.cos(yaw * 0.017453292F) * speed
        );

        double dx = 0, dy = 0, dz = 0;

        if (mc.options.forwardKey.isPressed()) {
            dx += forward.x;
            dz += forward.z;
        }
        if (mc.options.backKey.isPressed()) {
            dx -= forward.x;
            dz -= forward.z;
        }
        if (mc.options.leftKey.isPressed()) {
            dx += forward.z;
            dz -= forward.x;
        }
        if (mc.options.rightKey.isPressed()) {
            dx -= forward.z;
            dz += forward.x;
        }
        if (mc.options.jumpKey.isPressed()) {
            dy += speed;
        }
        if (mc.options.sneakKey.isPressed()) {
            dy -= speed;
        }

        mc.player.updatePosition(mc.player.getX() + dx * 0.05, mc.player.getY() + dy * 0.05, mc.player.getZ() + dz * 0.05);
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
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.cancel(); // Sunucuya hareket paketlerini gönderme
        }
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {

    }

    @Override
    public void onRender() {

    }
}
