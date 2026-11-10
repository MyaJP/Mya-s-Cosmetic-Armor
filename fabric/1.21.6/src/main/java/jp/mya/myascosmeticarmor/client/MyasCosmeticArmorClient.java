/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.client;

import jp.mya.myascosmeticarmor.ClientCosArmor;
import jp.mya.myascosmeticarmor.MyasCosmeticArmor;
import jp.mya.myascosmeticarmor.mixin.MenuScreensInvoker;
import jp.mya.myascosmeticarmor.network.Network;
import jp.mya.myascosmeticarmor.network.SyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;

/** Fabric版のクライアント側の入り口 */
public class MyasCosmeticArmorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreensInvoker.myas_cosmetic_armor$register(MyasCosmeticArmor.menuType(), CosArmorScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(SyncPayload.TYPE, (payload, context) -> Network.onSync(payload));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientCosArmor.clear());
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!ScreenHooks.onInit(screen, Screens.getButtons(screen)::add)) return;
            ScreenEvents.beforeRender(screen).register((s, g, mx, my, d) -> ScreenHooks.beforeRender(s));
        });
    }
}
