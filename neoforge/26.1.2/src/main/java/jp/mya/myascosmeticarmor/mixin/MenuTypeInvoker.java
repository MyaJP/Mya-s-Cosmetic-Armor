/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.mixin;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** MenuType のコンストラクタは private なので、これで作る */
@Mixin(MenuType.class)
public interface MenuTypeInvoker {
    @Invoker("<init>")
    static <T extends AbstractContainerMenu> MenuType<T> myas_cosmetic_armor$create(MenuType.MenuSupplier<T> supplier, FeatureFlagSet flags) {
        throw new AssertionError();
    }
}
