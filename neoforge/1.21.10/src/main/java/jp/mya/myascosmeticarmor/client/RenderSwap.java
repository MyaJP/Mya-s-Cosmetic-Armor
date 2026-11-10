/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.client;

import jp.mya.myascosmeticarmor.ClientCosArmor;
import jp.mya.myascosmeticarmor.CosArmorData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * プレイヤーを描く間だけ、防具の欄を見た目の防具に差し替える。描き終わったら元に戻す。
 * （元の Cosmetic Armor Reworked と同じやり方。インベントリの 36～39 番が防具）
 */
public final class RenderSwap {
    private RenderSwap() {}


    private record Saved(Inventory inv, ItemStack[] stacks) {}

    private static final Deque<Saved> STACK = new ArrayDeque<>();

    public static void before(Entity entity) {
        if (!(entity instanceof Player player)) {
            STACK.push(new Saved(null, null));
            return;
        }
        Inventory inv = player.getInventory();
        CosArmorData data = ClientCosArmor.getIfPresent(player.getUUID());
        if (data == null) {
            STACK.push(new Saved(null, null));
            return;
        }
        ItemStack[] saved = new ItemStack[CosArmorData.SIZE];
        for (int i = 0; i < CosArmorData.SIZE; i++) saved[i] = inv.getItem(36 + i);
        STACK.push(new Saved(inv, saved));
        for (int i = 0; i < CosArmorData.SIZE; i++) {
            ItemStack cos = data.getItem(i);
            if (data.hidesArmor(i)) inv.setItem(36 + i, ItemStack.EMPTY); // 隠すスイッチが入っていたら何も着ていないように見せる
            else if (data.showsCosmetic(i)) inv.setItem(36 + i, cos);
        }
    }

    public static void after() {
        Saved s = STACK.poll();
        if (s == null || s.inv() == null) return;
        for (int i = 0; i < CosArmorData.SIZE; i++) s.inv().setItem(36 + i, s.stacks()[i]);
    }
}
