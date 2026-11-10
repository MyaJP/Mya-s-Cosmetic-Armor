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
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** 通信の登録と処理（ローダーごとの違いはここに集める） */
public final class Network {
    private Network() {}

    public static final Component TITLE = Component.translatable("container.myas_cosmetic_armor");

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar r = event.registrar("1").optional();
        r.playToClient(SyncPayload.TYPE, SyncPayload.CODEC, (p, ctx) -> ctx.enqueueWork(() -> onSync(p)));
        r.playToServer(OpenPayload.TYPE, OpenPayload.CODEC, (p, ctx) -> ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer sp) onOpen(sp);
        }));
        r.playToServer(FlagPayload.TYPE, FlagPayload.CODEC, (p, ctx) -> ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer sp) onFlag(sp, p);
        }));
    }

    public static void sendToPlayer(ServerPlayer player, SyncPayload payload) {
        if (player.connection != null && player.connection.hasChannel(SyncPayload.TYPE)) PacketDistributor.sendToPlayer(player, payload);
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
