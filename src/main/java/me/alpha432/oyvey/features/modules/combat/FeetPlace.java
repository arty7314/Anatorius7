package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class FeetPlace extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final Setting<Integer> delay = this.register(new Setting<>("Delay", 3, 0, 20));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<>("BPT", 4, 1, 4));
    private final Setting<Boolean> silentSwitch = this.register(new Setting<>("SilentSwitch", true));

    private int tickTimer = 0;
    private int blocksPlacedThisTick = 0;

    public FeetPlace() {
        super("FeetPlace", "Places obsidian around your feet with silent rotate, delay and silent switch", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        tickTimer++;
        if (tickTimer < delay.getValue()) return;
        tickTimer = 0;

        blocksPlacedThisTick = 0;

        int obsidianSlot = InventoryUtil.findHotbarBlock(net.minecraft.block.Blocks.OBSIDIAN);
        if (obsidianSlot == -1) return;

        int oldSlot = mc.player.getInventory().getSelectedSlot();
        if (!silentSwitch.getValue()) {
            mc.player.getInventory().setSelectedSlot(obsidianSlot);
        }

        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos[] surroundPositions = new BlockPos[]{
                playerPos.north(),
                playerPos.east(),
                playerPos.south(),
                playerPos.west()
        };

        for (BlockPos pos : surroundPositions) {
            if (blocksPlacedThisTick >= blocksPerTick.getValue()) break;

            if (mc.world.getBlockState(pos).isAir() && canPlaceBlock(pos)) {
                // Silent rotate ve blok yerleştir
                placeBlockWithSilentRotate(pos, obsidianSlot);
                blocksPlacedThisTick++;
            }
        }

        if (!silentSwitch.getValue()) {
            mc.player.getInventory().setSelectedSlot(oldSlot);
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

    private boolean canPlaceBlock(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            if (!mc.world.getBlockState(pos).isSolidBlock(mc.world, pos))
            {
                return true;
            }
        }
        return false;
    }

    private void placeBlockWithSilentRotate(BlockPos pos, int obsidianSlot) {
        int currentSlot = mc.player.getInventory().getSelectedSlot();

        if (silentSwitch.getValue() && currentSlot != obsidianSlot) {
            mc.player.getInventory().setSelectedSlot(obsidianSlot);
            mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }

        // Hangi yöne bakacağımızı hesapla
        Vec3d eyesPos = mc.player.getEyePos();
        Vec3d blockCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vec3d diff = blockCenter.subtract(eyesPos);
        float[] rotations = calcAngle(diff);

        // Silent rotate için direkt pozisyon + bakış yönü paketi yolla (yani sadece server'a bildiriyoruz, client kamera sabit)
        sendSilentRotationPacket(rotations[0], rotations[1]);

        // Raycast hit sonucu oluştur
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            if (!mc.world.getBlockState(pos).isSolidBlock(mc.world, pos)) {
                Vec3d hitVec = new Vec3d(neighbor.getX() + 0.5, neighbor.getY() + 0.5, neighbor.getZ() + 0.5)
                        .add(new Vec3d(dir.getVector()).multiply(0.5));

                BlockHitResult hitResult = new BlockHitResult(hitVec, dir.getOpposite(), neighbor, false);

                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
                mc.player.swingHand(Hand.MAIN_HAND);
                break;
            }
        }

        // Silent switch ise slotu eski haline çevir
        if (silentSwitch.getValue() && currentSlot != obsidianSlot) {
            mc.player.getInventory().setSelectedSlot(currentSlot);
        }
    }

    private void sendSilentRotationPacket(float yaw, float pitch) {
        boolean onGround = mc.player.isOnGround();
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround, true));
    }

    private float[] calcAngle(Vec3d vec) {
        double diffX = vec.x;
        double diffY = vec.y;
        double diffZ = vec.z;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, dist)));

        return new float[]{yaw, pitch};
    }
}
