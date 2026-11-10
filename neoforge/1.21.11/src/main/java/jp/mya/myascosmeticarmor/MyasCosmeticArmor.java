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
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlags;
import jp.mya.myascosmeticarmor.client.ClientSetup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 見た目だけの防具を着られるようにする */
@Mod(MyasCosmeticArmor.MOD_ID)
public class MyasCosmeticArmor {
    public static final String MOD_ID = "myas_cosmetic_armor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MOD_ID);
    private static final DeferredHolder<MenuType<?>, MenuType<CosArmorMenu>> MENU = MENUS.register("cosmetic_armor",
            () -> MenuTypeInvoker.myas_cosmetic_armor$create(CosArmorMenu::createClient, FeatureFlags.VANILLA_SET));

    public static MenuType<CosArmorMenu> menuType() {
        return MENU.get();
    }

    public MyasCosmeticArmor(IEventBus modBus, Dist dist) {
        MENUS.register(modBus);
        if (dist.isClient()) ClientSetup.init(modBus);
        modBus.addListener(Network::register);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer p) CosArmorStorage.onJoin(p);
        });
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer p) CosArmorStorage.onLeave(p);
        });
    }
}
