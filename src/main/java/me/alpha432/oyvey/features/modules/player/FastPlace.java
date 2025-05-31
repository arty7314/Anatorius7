package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", "Removes item use cooldown for blocks and items", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;

        if (mc.player == null) return;

        // Elinde tuttuğu eşya
        Item heldItem = mc.player.getMainHandStack().getItem();

        // Eğer elindeki eşya BlockItem ise (yani blok) veya EXP şişesi veya başka hızlı kullanmak istediğin itemler
        if (heldItem instanceof BlockItem || heldItem == Items.EXPERIENCE_BOTTLE) {
            mc.itemUseCooldown = 0;
        }
    }

    // nullCheck fonksiyonun varsa kullan, yoksa aşağıdaki gibi basit bir kontrol yazabilirsin
    public static boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }

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

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {}

    @Override
    public void onRender() {

    }
}
