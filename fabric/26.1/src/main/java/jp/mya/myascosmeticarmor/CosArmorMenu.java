/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import jp.mya.myascosmeticarmor.mixin.CraftingMenuInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 見た目の防具の画面。並びは元の Cosmetic Armor Reworked と同じ（2x2 のクラフト欄つき、レシピ本は無し）。
 * 0～3: 見た目の防具（頭・胴・脚・足） 4～7: 本物の防具（頭・胴・脚・足） 8: オフハンド
 * 9～35: インベントリ 36～44: ホットバー 45: クラフトの完成品 46～49: クラフト欄
 */
public class CosArmorMenu extends AbstractContainerMenu {
    /** 頭・胴・脚・足の順 */
    private static final EquipmentSlot[] TOP_DOWN = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final Identifier[] ICONS = {InventoryMenu.EMPTY_ARMOR_SLOT_HELMET, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS};

    public static final int COS_START = 0, ARMOR_START = 4, OFFHAND = 8, INV_START = 9, INV_END = 45, RESULT = 45, GRID_START = 46, GRID_END = 50;

    private final Player player;
    public final CosArmorData data;
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 2, 2);
    private final ResultContainer resultSlots = new ResultContainer();

    /** クライアント側（サーバーから「開いて」と言われたとき） */
    public static CosArmorMenu createClient(int id, Inventory inv) {
        return new CosArmorMenu(id, inv, ClientCosArmor.get(inv.player.getUUID()));
    }

    public CosArmorMenu(int id, Inventory inv, CosArmorData data) {
        super(MyasCosmeticArmor.menuType(), id);
        this.player = inv.player;
        this.data = data;

        // 見た目の防具（右下の横1列）
        for (int i = 0; i < 4; i++) {
            addSlot(new ArmorLikeSlot(data, 3 - i, 98 + i * 18, 62, TOP_DOWN[i], ICONS[i], false));
        }
        // 本物の防具（左の縦1列）
        for (int i = 0; i < 4; i++) {
            addSlot(new ArmorLikeSlot(inv, 39 - i, 8, 8 + i * 18, TOP_DOWN[i], ICONS[i], true));
        }
        // オフハンド
        addSlot(new Slot(inv, 40, 77, 62) {
            @Override
            public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
                player.onEquipItem(EquipmentSlot.OFFHAND, oldStack, newStack);
                super.setByPlayer(newStack, oldStack);
            }

            @Override
            public Identifier getNoItemIcon() {
                return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
            }
        });
        // インベントリとホットバー
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
        // クラフト（完成品と 2x2）
        addSlot(new ResultSlot(player, craftSlots, resultSlots, 0, 154, 28));
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                addSlot(new Slot(craftSlots, col + row * 2, 98 + col * 18, 18 + row * 18));
            }
        }
    }

    @Override
    public void slotsChanged(Container container) {
        if (container == craftSlots && player.level() instanceof ServerLevel level) {
            CraftingMenuInvoker.myas_cosmetic_armor$slotChanged(this, level, player, craftSlots, resultSlots, null);
        }
    }

    @Override
    public void removed(Player p) {
        super.removed(p);
        resultSlots.clearContent();
        if (p instanceof ServerPlayer) clearContainer(p, craftSlots); // クラフト欄に残った物はプレイヤーに返す
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    private class ArmorLikeSlot extends Slot {
        private final EquipmentSlot equipmentSlot;
        private final Identifier icon;
        private final boolean real;

        ArmorLikeSlot(Container container, int index, int x, int y, EquipmentSlot equipmentSlot, Identifier icon, boolean real) {
            super(container, index, x, y);
            this.equipmentSlot = equipmentSlot;
            this.icon = icon;
            this.real = real;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return player.getEquipmentSlotForItem(stack) == equipmentSlot;
        }

        @Override
        public boolean mayPickup(Player p) {
            ItemStack stack = getItem();
            // 本物の防具は、束縛の呪いが付いていたら外せない（クリエイティブは別）
            if (real && !stack.isEmpty() && !p.isCreative() && EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) return false;
            return super.mayPickup(p);
        }

        @Override
        public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
            if (real) player.onEquipItem(equipmentSlot, oldStack, newStack);
            super.setByPlayer(newStack, oldStack);
        }

        @Override
        public Identifier getNoItemIcon() {
            return icon;
        }
    }

    @Override
    public boolean stillValid(Player p) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player p, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index == RESULT) {
            // 完成品 → インベントリへ（Shift+クリックでまとめて作る）
            if (!moveItemStackTo(stack, INV_START, INV_END, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, original);
        } else if (index < INV_START || index >= GRID_START) {
            // 防具・見た目・オフハンド・クラフト欄 → インベントリへ
            if (!moveItemStackTo(stack, INV_START, INV_END, false)) return ItemStack.EMPTY;
        } else {
            EquipmentSlot target = p.getEquipmentSlotForItem(stack);
            int armorIndex = -1;
            for (int i = 0; i < 4; i++) if (TOP_DOWN[i] == target) armorIndex = i;
            boolean moved = false;
            if (armorIndex >= 0) {
                // まず本物の防具の欄、空いていなければ見た目の欄へ
                if (!slots.get(ARMOR_START + armorIndex).hasItem()) moved = moveItemStackTo(stack, ARMOR_START + armorIndex, ARMOR_START + armorIndex + 1, false);
                if (!moved && !slots.get(COS_START + armorIndex).hasItem()) moved = moveItemStackTo(stack, COS_START + armorIndex, COS_START + armorIndex + 1, false);
            }
            if (!moved) {
                if (index < 36) {
                    if (!moveItemStackTo(stack, 36, INV_END, false)) return ItemStack.EMPTY; // インベントリ → ホットバー
                } else if (!moveItemStackTo(stack, INV_START, 36, false)) {
                    return ItemStack.EMPTY; // ホットバー → インベントリ
                }
            }
        }

        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY, original);
        else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(p, stack);
        return original;
    }
}
