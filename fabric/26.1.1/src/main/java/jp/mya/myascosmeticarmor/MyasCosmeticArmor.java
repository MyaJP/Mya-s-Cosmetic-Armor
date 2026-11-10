/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor;

import jp.mya.myascosmeticarmor.network.Network;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jp.mya.myascosmeticarmor.mixin.MenuTypeInvoker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;

/** 見た目だけの防具を着られるようにする */
public class MyasCosmeticArmor implements ModInitializer {
    public static final String MOD_ID = "myas_cosmetic_armor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static MenuType<CosArmorMenu> menuType;

    public static MenuType<CosArmorMenu> menuType() {
        return menuType;
    }

    @Override
    public void onInitialize() {
        menuType = Registry.register(BuiltInRegistries.MENU, id("cosmetic_armor"),
                MenuTypeInvoker.myas_cosmetic_armor$create(CosArmorMenu::createClient, FeatureFlags.VANILLA_SET));
        Network.register();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> CosArmorStorage.onJoin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> CosArmorStorage.onLeave(handler.getPlayer()));
    }
}
