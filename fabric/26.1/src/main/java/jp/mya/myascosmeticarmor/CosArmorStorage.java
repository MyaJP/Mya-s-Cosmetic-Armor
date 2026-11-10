/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor;

import com.mojang.serialization.DynamicOps;
import jp.mya.myascosmeticarmor.network.Network;
import jp.mya.myascosmeticarmor.network.SyncPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * サーバー側の保存と同期。
 * ワールドの playerdata フォルダに「UUID.mya_cosarmor」として1人1ファイルで保存する。
 */
public final class CosArmorStorage {
    private CosArmorStorage() {}

    private static final Map<UUID, CosArmorData> LOADED = new HashMap<>();

    public static CosArmorData get(ServerPlayer player) {
        return LOADED.computeIfAbsent(player.getUUID(), u -> load(player));
    }

    private static MinecraftServer server(ServerPlayer player) {
        return player.level().getServer();
    }

    private static Path file(MinecraftServer server, UUID uuid) {
        return server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve(uuid + ".mya_cosarmor");
    }

    private static DynamicOps<Tag> ops(MinecraftServer server) {
        return server.registryAccess().createSerializationContext(NbtOps.INSTANCE);
    }

    private static CosArmorData load(ServerPlayer player) {
        MinecraftServer server = server(player);
        UUID uuid = player.getUUID();
        CosArmorData data = new CosArmorData();
        try {
            Path path = file(server, uuid);
            if (Files.exists(path)) {
                CompoundTag tag = NbtIo.read(path);
                if (tag != null) {
                    CosArmorData.Saved.CODEC.parse(ops(server), tag).resultOrPartial(err ->
                            MyasCosmeticArmor.LOGGER.warn("Failed to read cosmetic armor of {}: {}", uuid, err)).ifPresent(data::load);
                }
            }
        } catch (Exception e) {
            MyasCosmeticArmor.LOGGER.error("Failed to load cosmetic armor of {}", uuid, e);
        }
        data.setOnChange(() -> {
            save(server, uuid, data);
            broadcast(server, uuid, data);
        });
        return data;
    }

    private static void save(MinecraftServer server, UUID uuid, CosArmorData data) {
        try {
            Tag tag = CosArmorData.Saved.CODEC.encodeStart(ops(server), data.save()).getOrThrow();
            if (tag instanceof CompoundTag compound) {
                Path path = file(server, uuid);
                Files.createDirectories(path.getParent());
                NbtIo.write(compound, path);
            }
        } catch (Exception e) {
            MyasCosmeticArmor.LOGGER.error("Failed to save cosmetic armor of {}", uuid, e);
        }
    }

    /** 1人分の見た目の防具を、サーバーにいる全員に送る */
    private static void broadcast(MinecraftServer server, UUID uuid, CosArmorData data) {
        for (ServerPlayer target : server.getPlayerList().getPlayers()) sendAll(target, uuid, data);
    }

    private static void sendAll(ServerPlayer target, UUID uuid, CosArmorData data) {
        for (int i = 0; i < CosArmorData.SIZE; i++) {
            Network.sendToPlayer(target, new SyncPayload(uuid, i, data.getItem(i).copy(), data.getFlag(CosArmorData.FLAG_HIDDEN, i), data.getFlag(CosArmorData.FLAG_SHOWN, i), data.isAllOff()));
        }
    }

    public static void onJoin(ServerPlayer player) {
        MinecraftServer server = server(player);
        LOADED.remove(player.getUUID());
        CosArmorData mine = get(player);
        for (ServerPlayer other : server.getPlayerList().getPlayers()) {
            sendAll(other, player.getUUID(), mine); // 入ってきた人の分を全員へ
            if (other != player) sendAll(player, other.getUUID(), get(other)); // ほかの人の分を入ってきた人へ
        }
    }

    public static void onLeave(ServerPlayer player) {
        CosArmorData data = LOADED.remove(player.getUUID());
        if (data != null) save(server(player), player.getUUID(), data);
    }
}
