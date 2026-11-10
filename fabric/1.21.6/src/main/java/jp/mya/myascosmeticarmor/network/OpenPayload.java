/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.network;

import jp.mya.myascosmeticarmor.MyasCosmeticArmor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** クライアント → サーバー：見た目の防具の画面を開いて */
public record OpenPayload() implements CustomPacketPayload {
    public static final OpenPayload INSTANCE = new OpenPayload();
    public static final Type<OpenPayload> TYPE = new Type<>(MyasCosmeticArmor.id("open"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
