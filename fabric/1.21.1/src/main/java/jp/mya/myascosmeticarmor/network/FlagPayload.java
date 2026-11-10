/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.network;

import jp.mya.myascosmeticarmor.MyasCosmeticArmor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** クライアント → サーバー：スイッチを切り替えた（kind は CosArmorData.FLAG_*） */
public record FlagPayload(int kind, int slot, boolean value) implements CustomPacketPayload {
    public static final Type<FlagPayload> TYPE = new Type<>(MyasCosmeticArmor.id("flag"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FlagPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FlagPayload::kind,
            ByteBufCodecs.VAR_INT, FlagPayload::slot,
            ByteBufCodecs.BOOL, FlagPayload::value,
            FlagPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
