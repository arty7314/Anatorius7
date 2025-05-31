package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class Aura extends Module {

    private final float range = 6.0f;

    public Aura() {
        super("Aura", "Disables all knockback", Category.COMBAT, true, false, false);
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

        // Sadece kılıç varsa çalış
        Item item = mc.player.getMainHandStack().getItem();
        if (!(item == Items.NETHERITE_SWORD || item == Items.DIAMOND_SWORD ||
                item == Items.IRON_SWORD || item == Items.STONE_SWORD ||
                item == Items.WOODEN_SWORD || item == Items.GOLDEN_SWORD)) return;

        // Vurma cooldown'ı kontrol et
        if (mc.player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        // En yakın canlı hedefi bul
        LivingEntity target = null;
        double closestDistance = range;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == mc.player || !entity.isAlive()) continue;

            double dist = mc.player.distanceTo(entity);
            if (dist <= closestDistance) {
                closestDistance = dist;
                target = (LivingEntity) entity;
            }
        }

        // Hedef bulunduysa saldır
        if (target != null) {
            attack(target);
        }
    }

    @Override
    public void onRender(MatrixStack matrices, float tickDelta) {

    }

    private void attack(LivingEntity entity) {
        if (mc.interactionManager != null && mc.player != null) {
            mc.interactionManager.attackEntity(mc.player, entity);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    // Gerekli ama boş render methodları
    @Override
    public void onRenderWorldLast(MatrixStack matrices, float tickDelta) {}

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
