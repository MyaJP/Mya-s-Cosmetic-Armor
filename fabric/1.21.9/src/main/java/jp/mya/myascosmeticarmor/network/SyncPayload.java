/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.network;

import jp.mya.myascosmeticarmor.MyasCosmeticArmor;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/** サーバー → クライアント：あるプレイヤーの見た目の防具1スロット分 */
public record SyncPayload(UUID player, int slot, ItemStack stack, boolean hidden, boolean shown, boolean allOff) implements CustomPacketPayload {
    public static final Type<SyncPayload> TYPE = new Type<>(MyasCosmeticArmor.id("sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, SyncPayload::player,
            ByteBufCodecs.VAR_INT, SyncPayload::slot,
            ItemStack.OPTIONAL_STREAM_CODEC, SyncPayload::stack,
            ByteBufCodecs.BOOL, SyncPayload::hidden,
            ByteBufCodecs.BOOL, SyncPayload::shown,
            ByteBufCodecs.BOOL, SyncPayload::allOff,
            SyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
