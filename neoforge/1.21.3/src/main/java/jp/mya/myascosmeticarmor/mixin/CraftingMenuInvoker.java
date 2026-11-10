/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.server.level.ServerLevel;

/** バニラのクラフト結果の計算をそのまま使う */
@Mixin(CraftingMenu.class)
public interface CraftingMenuInvoker {
    @Invoker("slotChangedCraftingGrid")
    static void myas_cosmetic_armor$slotChanged(AbstractContainerMenu menu, ServerLevel level, Player player, CraftingContainer grid, ResultContainer result, RecipeHolder<CraftingRecipe> recipe) {
        throw new AssertionError();
    }
}
