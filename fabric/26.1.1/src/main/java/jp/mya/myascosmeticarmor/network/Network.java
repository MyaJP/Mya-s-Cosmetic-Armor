/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.network;

import jp.mya.myascosmeticarmor.ClientCosArmor;
import jp.mya.myascosmeticarmor.CosArmorMenu;
import jp.mya.myascosmeticarmor.CosArmorStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** 通信の登録と処理（ローダーごとの違いはここに集める） */
public final class Network {
    private Network() {}

    public static final Component TITLE = Component.translatable("container.myas_cosmetic_armor");

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(SyncPayload.TYPE, SyncPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(OpenPayload.TYPE, OpenPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(FlagPayload.TYPE, FlagPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenPayload.TYPE, (p, ctx) -> onOpen(ctx.player()));
        ServerPlayNetworking.registerGlobalReceiver(FlagPayload.TYPE, (p, ctx) -> onFlag(ctx.player(), p));
    }

    public static void sendToPlayer(ServerPlayer player, SyncPayload payload) {
        if (ServerPlayNetworking.canSend(player, SyncPayload.TYPE)) ServerPlayNetworking.send(player, payload);
    }

    private static void onOpen(ServerPlayer player) {
        if (player.isSpectator()) return;
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new CosArmorMenu(id, inv, CosArmorStorage.get(player)), TITLE));
    }

    private static void onFlag(ServerPlayer player, FlagPayload payload) {
        CosArmorStorage.get(player).setFlag(payload.kind(), payload.slot(), payload.value());
    }

    /** クライアント側で受け取ったとき */
    public static void onSync(SyncPayload p) {
        ClientCosArmor.get(p.player()).applySync(p.slot(), p.stack(), p.hidden(), p.shown(), p.allOff());
    }
}
