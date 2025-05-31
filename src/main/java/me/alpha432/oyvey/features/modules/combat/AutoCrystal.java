package me.alpha432.oyvey.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.render.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;

public class AutoCrystal extends Module {

    private final Setting<Float> placeRange = this.register(new Setting<>("PlaceRange", 6.0f, 0.0f, 6.0f));
    private final Setting<Float> attackRange = this.register(new Setting<>("AttackRange", 6.0f, 0.0f, 6.0f));
    private final Setting<Float> minDamage = this.register(new Setting<>("MinDamage", 6.0f, 0.0f, 20.0f));
    private final Setting<Float> maxSelfDamage = this.register(new Setting<>("MaxLocalDamage", 10.0f, 0.0f, 36.0f));
    private final Setting<Boolean> renderDebug = this.register(new Setting<>("RenderDebug", true));
    private final Setting<Integer> red = this.register(new Setting<>("DebugRed", 200, 0, 255, v -> renderDebug.getValue()));
    private final Setting<Integer> green = this.register(new Setting<>("DebugGreen", 10, 0, 255, v -> renderDebug.getValue()));
    private final Setting<Integer> blue = this.register(new Setting<>("DebugBlue", 230, 0, 255, v -> renderDebug.getValue()));
    private final Setting<Integer> breakSpeed = this.register(new Setting<>("BreakSpeed", 0, 0, 20));
    private final Setting<Integer> placeSpeed = this.register(new Setting<>("PlaceSpeed", 0, 0, 20));
    private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", false));

    private BlockPos lastPlacedCrystalPos = null;
    private long lastBreakTime = 0;
    private long lastPlaceTime = 0;

    public AutoCrystal() {
        super("AutoCrystal", "Disables all knockback", Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        Command.sendMessage(this.getName() + " was toggled " + Formatting.GREEN + "on" + Formatting.GRAY + ".");
    }

    @Override
    public void onDisable() {
        Command.sendMessage(this.getName() + " was toggled " + Formatting.RED + "off" + Formatting.GRAY + ".");
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        long currentTime = System.currentTimeMillis();

        // Break logic - always active as if inhibit enabled, using breakSpeed setting
        if (currentTime - lastBreakTime >= breakSpeed.getValue()) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof EndCrystalEntity && entity.squaredDistanceTo(mc.player) <= attackRange.getValue() * attackRange.getValue()) {
                    if (mc.interactionManager != null) {
                        if (rotate.getValue()) {
                            rotateToEntity(entity);
                        }
                        mc.interactionManager.attackEntity(mc.player, entity);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        lastBreakTime = currentTime;
                        break;
                    }
                }
            }
        }

        if (currentTime - lastPlaceTime >= placeSpeed.getValue()) {
            Hand crystalHand = null;
            if (mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL) {
                crystalHand = Hand.OFF_HAND;
            } else if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL) {
                crystalHand = Hand.MAIN_HAND;
            } else return;

            PlayerEntity target = null;
            double closestDist = placeRange.getValue() * placeRange.getValue();
            for (Entity e : mc.world.getEntities()) {
                if (e instanceof PlayerEntity && e != mc.player && !e.isInvisible() && e.isAlive()) {
                    double dist = e.squaredDistanceTo(mc.player);
                    if (dist < closestDist) {
                        closestDist = dist;
                        target = (PlayerEntity) e;
                    }
                }
            }
            if (target == null) return;

            BlockPos targetPos = target.getBlockPos();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos basePos = targetPos.add(x, y, z);
                        BlockPos up1 = basePos.up();
                        BlockPos up2 = basePos.up(2);

                        if ((mc.world.getBlockState(basePos).isOf(Blocks.OBSIDIAN) || mc.world.getBlockState(basePos).isOf(Blocks.BEDROCK))
                                && mc.world.getBlockState(up1).isAir() && mc.world.getBlockState(up2).isAir()) {

                            Vec3d crystalPos = new Vec3d(basePos.getX() + 0.5, basePos.getY() + 1, basePos.getZ() + 0.5);
                            if (crystalPos.distanceTo(mc.player.getPos()) <= placeRange.getValue()) {

                                float damageToTarget = calculateDamage(target, crystalPos);
                                float selfDamage = calculateDamage(mc.player, crystalPos);

                                if (damageToTarget >= minDamage.getValue() && selfDamage <= maxSelfDamage.getValue()) {
                                    BlockHitResult hit = new BlockHitResult(crystalPos, Direction.UP, basePos, false);
                                    if (mc.interactionManager != null) {
                                        if (rotate.getValue()) {
                                            rotateToPos(crystalPos);
                                        }
                                        mc.interactionManager.interactBlock(mc.player, crystalHand, hit);
                                        mc.player.swingHand(crystalHand);
                                        lastPlacedCrystalPos = basePos;
                                        lastPlaceTime = currentTime;
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private float calculateDamage(LivingEntity entity, Vec3d crystalPos) {
        double distance = entity.getPos().distanceTo(crystalPos);
        return Math.max(0, 12.0f - (float) distance * 2.0f);
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (renderDebug.getValue()) {
            if (lastPlacedCrystalPos == null) return;

            VoxelShape shape = mc.world.getBlockState(lastPlacedCrystalPos).getOutlineShape(mc.world, lastPlacedCrystalPos);
            if (shape.isEmpty()) return;
            RenderUtil.drawBox(event.getMatrix(), shape.getBoundingBox().offset(lastPlacedCrystalPos), new Color(red.getValue(), green.getValue(), blue.getValue(), 255), 2f);
            RenderUtil.drawBoxFilled(event.getMatrix(), shape.getBoundingBox().offset(lastPlacedCrystalPos), new Color(red.getValue(), green.getValue(), blue.getValue(), 20));
        }
    }

    @Override
    public void onRender(MatrixStack matrices, float tickDelta) {}

    @Override
    public void onRenderWorldLast(MatrixStack matrices, float tickDelta) {}

    @Override
    public void onRender3D(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, float tickDelta) {}

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

    private void rotateToEntity(Entity entity) {
        Vec3d eyesPos = mc.player.getEyePos();
        Vec3d targetPos = entity.getPos().add(0, entity.getHeight() / 2.0, 0);
        double diffX = targetPos.x - eyesPos.x;
        double diffY = targetPos.y - eyesPos.y;
        double diffZ = targetPos.z - eyesPos.z;
        double distXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, distXZ));
        mc.player.setYaw(updateRotation(mc.player.getYaw(), yaw, 360f));
        mc.player.setPitch(updateRotation(mc.player.getPitch(), pitch, 360f));
    }

    private void rotateToPos(Vec3d pos) {
        Vec3d eyesPos = mc.player.getEyePos();
        double diffX = pos.x - eyesPos.x;
        double diffY = pos.y - eyesPos.y;
        double diffZ = pos.z - eyesPos.z;
        double distXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, distXZ));
        mc.player.setYaw(updateRotation(mc.player.getYaw(), yaw, 360f));
        mc.player.setPitch(updateRotation(mc.player.getPitch(), pitch, 360f));
    }

    private float updateRotation(float current, float intended, float maxIncrease) {
        float f = MathHelper.wrapDegrees(intended - current);
        if (f > maxIncrease) f = maxIncrease;
        if (f < -maxIncrease) f = -maxIncrease;
        return current + f;
    }
}
