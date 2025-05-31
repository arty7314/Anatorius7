package me.alpha432.oyvey.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class BlockUtil {

    private static World world = null;
    private static PlayerEntity player;

    public static void setWorld(World w) {
        world = w;
    }

    // Sphere olarak çevre blokları döndür
    public static Iterable<BlockPos> getSphere(BlockPos center, int radius, int height, boolean hollow, boolean sphere, int plusY) {
        java.util.List<BlockPos> blocks = new java.util.ArrayList<>();
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = sphere ? cy - radius : cy; y < (sphere ? cy + radius : cy + height); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
                        blocks.add(new BlockPos(x, y + plusY, z));
                    }
                }
            }
        }
        return blocks;
    }

    // Blok pozisyonunda kristal koyulabilir mi kontrol et
    public static boolean canPlaceCrystal(BlockPos pos) {
        if (world == null) return false;

        BlockState blockState = world.getBlockState(pos);
        BlockState blockStateUp = world.getBlockState(pos.up());

        // Üst ve üst üst bloklar boş olmalı
        if (!blockState.isAir()) return false;
        if (!blockStateUp.isAir()) return false;

        // Alt blok obsidyen veya bedrock olmalı
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        if (blockBelow != Blocks.OBSIDIAN && blockBelow != Blocks.BEDROCK) return false;

        // Üstte ve üst üstte entity olmamalı (örneğin EndCrystal, oyuncu vs.)
        Box box = new Box(pos);
        for (Entity entity : world.getOtherEntities(null, box)) {
            if (!(entity instanceof EndCrystalEntity)) {
                return false;
            }
        }

        return true;
    }

    // Player pozisyonu alma (özellikle yeri tam alt blok)
    public static BlockPos getPlayerPos(PlayerEntity player) {
        BlockUtil.player = player;
        BlockPos blockPos = new BlockPos((int)Math.floor(player.getX()), (int)Math.floor(player.getY()), (int)Math.floor(player.getZ()));
        return blockPos;
    }

    // Pozisyonu ortalama vektöre çevir (dönüş için)
    public static net.minecraft.util.math.Vec3d toCenterPos(BlockPos pos) {
        return new net.minecraft.util.math.Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
    }

    // Yönü oyuncunun gözüne göre ayarla (silent rotate için)
    public static void faceVectorPacket(net.minecraft.util.math.Vec3d vec, boolean silent) {
        // Minecraft 1.21.5 paketi ile player döndürme (basit örnek)
        // silent true ise sadece paket gönder, false ise oyuncuyu da döndür
        if (silent) {
            // Paket gönderme kodu burada olmalı (Minecraft client eklentisine göre değişir)
            // Örnek placeholder:
            // mc.getNetworkHandler().sendPacket(new PlayerLookPacket(vec));
        } else {
            // Oyuncuyu dönüştür
            // mc.player.setYaw(...);
            // mc.player.setPitch(...);
        }
    }

    public static PlayerEntity getPlayer() {
        return player;
    }

    public static BlockHitResult getRayTraceResult(BlockPos placePos) {
        return null;
    }
}
