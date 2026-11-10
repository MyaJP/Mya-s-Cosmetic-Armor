/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** クライアント側で持っている、各プレイヤーの見た目の防具 */
public final class ClientCosArmor {
    private ClientCosArmor() {}

    private static final Map<UUID, CosArmorData> DATA = new HashMap<>();

    public static CosArmorData get(UUID uuid) {
        return DATA.computeIfAbsent(uuid, u -> new CosArmorData());
    }

    public static CosArmorData getIfPresent(UUID uuid) {
        return DATA.get(uuid);
    }

    public static void clear() {
        DATA.clear();
    }
}
