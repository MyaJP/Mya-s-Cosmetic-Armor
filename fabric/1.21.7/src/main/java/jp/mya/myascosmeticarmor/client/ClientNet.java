/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.client;

import jp.mya.myascosmeticarmor.network.OpenPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** クライアント → サーバーの送信 */
public final class ClientNet {
    private ClientNet() {}

    /** サーバーにもこのMODが入っているか */
    public static boolean canSend() {
        return ClientPlayNetworking.canSend(OpenPayload.TYPE);
    }

    public static void send(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
