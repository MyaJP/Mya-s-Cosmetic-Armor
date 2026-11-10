/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.client;

import jp.mya.myascosmeticarmor.ClientCosArmor;
import jp.mya.myascosmeticarmor.MyasCosmeticArmor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/** NeoForge版のクライアント側の登録（クライアントのときだけ呼ばれる） */
public final class ClientSetup {
    private ClientSetup() {}

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::onMenuScreens);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onInit);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onRender);
        NeoForge.EVENT_BUS.addListener(ClientSetup::onLogout);
    }

    private static void onMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MyasCosmeticArmor.menuType(), CosArmorScreen::new);
    }

    private static void onInit(ScreenEvent.Init.Post event) {
        ScreenHooks.onInit(event.getScreen(), event::addListener);
    }

    private static void onRender(ScreenEvent.Render.Pre event) {
        ScreenHooks.beforeRender(event.getScreen());
    }

    private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientCosArmor.clear();
    }
}
