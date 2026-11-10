/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 1人分の見た目の防具。
 * スロット 0～3 = 足・脚・胴・頭（プレイヤーのインベントリの 36～39 と同じ順番）。
 *
 * 全切替（allOff）がオフのとき：見た目の防具を表示する。部位ごとのスイッチ（hidden）はその部位を隠す。
 * 全切替（allOff）がオンのとき：本物の防具を表示する。部位ごとのスイッチ（shown）はその部位だけ見た目の防具にする。
 * 全切替を切り替えると、部位ごとのスイッチは全部オフに戻る。
 */
public class CosArmorData extends SimpleContainer {
    public static final int SIZE = 4;
    public static final EquipmentSlot[] SLOTS = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    /** C2S で変える値の種類 */
    public static final int FLAG_HIDDEN = 0, FLAG_SHOWN = 1, FLAG_ALL_OFF = 2;

    /** 保存用 */
    public record Saved(List<ItemStack> items, List<Boolean> hidden, List<Boolean> shown, boolean allOff) {
        public static final Codec<Saved> CODEC = RecordCodecBuilder.create(i -> i.group(
                ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(Saved::items),
                Codec.BOOL.listOf().optionalFieldOf("hidden", List.of()).forGetter(Saved::hidden),
                Codec.BOOL.listOf().optionalFieldOf("shown", List.of()).forGetter(Saved::shown),
                Codec.BOOL.optionalFieldOf("all_off", false).forGetter(Saved::allOff)
        ).apply(i, Saved::new));
    }

    private final boolean[] hidden = new boolean[SIZE];
    private final boolean[] shown = new boolean[SIZE];
    private boolean allOff;
    /** サーバー側だけ。中身が変わったら呼ばれる */
    private Runnable onChange;

    public CosArmorData() {
        super(SIZE);
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public boolean isAllOff() {
        return allOff;
    }

    /** 部位ごとのスイッチの状態（今のモードで意味が変わる） */
    public boolean getSlotSwitch(int slot) {
        if (slot < 0 || slot >= SIZE) return false;
        return allOff ? shown[slot] : hidden[slot];
    }

    /** この部位に見た目の防具を描くか */
    public boolean showsCosmetic(int slot) {
        if (slot < 0 || slot >= SIZE || getItem(slot).isEmpty()) return false;
        return allOff ? shown[slot] : !hidden[slot];
    }

    /** この部位の防具を何も描かないか（全切替オフのときの「隠す」） */
    public boolean hidesArmor(int slot) {
        return !allOff && slot >= 0 && slot < SIZE && hidden[slot];
    }

    /** 値を変える。kind は FLAG_* */
    public void setFlag(int kind, int slot, boolean value) {
        if (kind == FLAG_ALL_OFF) {
            if (allOff == value) return;
            allOff = value;
            // 全切替を切り替えたら、部位ごとのスイッチは全部オフからやり直す（前の状態は覚えておかない）
            java.util.Arrays.fill(hidden, false);
            java.util.Arrays.fill(shown, false);
        } else {
            if (slot < 0 || slot >= SIZE) return;
            boolean[] arr = kind == FLAG_SHOWN ? shown : hidden;
            if (arr[slot] == value) return;
            arr[slot] = value;
        }
        setChanged();
    }

    public boolean getFlag(int kind, int slot) {
        if (kind == FLAG_ALL_OFF) return allOff;
        if (slot < 0 || slot >= SIZE) return false;
        return kind == FLAG_SHOWN ? shown[slot] : hidden[slot];
    }

    /** 同期パケットで受け取った値を、変更通知なしで入れる（クライアント側） */
    public void applySync(int slot, ItemStack stack, boolean hide, boolean show, boolean off) {
        if (slot < 0 || slot >= SIZE) return;
        Runnable saved = onChange;
        onChange = null;
        setItem(slot, stack);
        hidden[slot] = hide;
        shown[slot] = show;
        allOff = off;
        onChange = saved;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (onChange != null) onChange.run();
    }

    public Saved save() {
        List<ItemStack> items = new ArrayList<>();
        List<Boolean> h = new ArrayList<>();
        List<Boolean> s = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            items.add(getItem(i).copy());
            h.add(hidden[i]);
            s.add(shown[i]);
        }
        return new Saved(items, h, s, allOff);
    }

    public void load(Saved saved) {
        Runnable keep = onChange;
        onChange = null;
        for (int i = 0; i < SIZE; i++) {
            setItem(i, i < saved.items().size() ? saved.items().get(i) : ItemStack.EMPTY);
            hidden[i] = i < saved.hidden().size() && saved.hidden().get(i);
            shown[i] = i < saved.shown().size() && saved.shown().get(i);
        }
        allOff = saved.allOff();
        onChange = keep;
    }
}
