package me.alpha432.oyvey.util;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;

public class InventoryUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static int findBlockInHotbar(Block block) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() == block) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int findHotbarBlock(Block obsidian) {
        return 0;
    }
}
