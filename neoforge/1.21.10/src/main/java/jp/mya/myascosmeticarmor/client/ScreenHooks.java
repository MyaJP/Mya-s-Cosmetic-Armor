/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.client;

import jp.mya.myascosmeticarmor.mixin.ScreenPosAccessor;
import jp.mya.myascosmeticarmor.ClientCosArmor;
import jp.mya.myascosmeticarmor.CosArmorData;
import jp.mya.myascosmeticarmor.network.FlagPayload;
import jp.mya.myascosmeticarmor.network.OpenPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/** 普通のインベントリ画面とクリエイティブのインベントリに「見た目の防具」を開くボタンを付ける */
public final class ScreenHooks {
    private ScreenHooks() {}

    /** 見た目の防具ボタン（10x10）と、そのすぐ左の全部オン・オフのスイッチ（5x5、縦は真ん中）の位置 */
    public static final int BUTTON_X = 65, BUTTON_Y = 67, TOGGLE_X = 59, TOGGLE_Y = 71;
    /** クリエイティブのインベントリでの位置（元の Cosmetic Armor Reworked と同じ） */
    private static final int CREATIVE_BUTTON_X = 95, CREATIVE_BUTTON_Y = 38;

    private static Screen current;
    private static TexButton button;
    private static TexButton toggleAll;

    /** 自分の見た目の防具を全部オン・オフするスイッチ（白＝全部オフ。ほかの人にも同じように見える） */
    public static TexButton toggleAllButton(int x, int y) {
        return new TexButton(CosArmorScreen.TEXTURE, x, y, 5, 5, 0, 176, () -> myData() != null && myData().isAllOff(),
                () -> {
                    CosArmorData data = myData();
                    if (data == null) return;
                    boolean next = !data.isAllOff();
                    data.setFlag(CosArmorData.FLAG_ALL_OFF, 0, next); // 先にこちらの表示を変えておく（サーバーからも同じ値が届く）
                    ClientNet.send(new FlagPayload(CosArmorData.FLAG_ALL_OFF, 0, next));
                }, Component.translatable("myas_cosmetic_armor.button.toggle_all"));
    }

    /** 自分の見た目の防具のデータ */
    static CosArmorData myData() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player == null ? null : ClientCosArmor.get(mc.player.getUUID());
    }

    /** 画面の初期化後に呼ぶ。ボタンを付けたら true */
    public static boolean onInit(Screen screen, Consumer<AbstractWidget> add) {
        if (!(screen instanceof InventoryScreen) && !(screen instanceof CreativeModeInventoryScreen)) return false;
        if (!ClientNet.canSend()) return false; // サーバーにこのMODが無いときは出さない
        button = new TexButton(CosArmorScreen.TEXTURE, 0, 0, 10, 10, 0, 166, null,
                () -> ClientNet.send(OpenPayload.INSTANCE), Component.translatable("myas_cosmetic_armor.button.open")).hoverAlt();
        toggleAll = toggleAllButton(0, 0);
        current = screen;
        beforeRender(screen);
        add.accept(button);
        add.accept(toggleAll);
        return true;
    }

    /** 毎フレーム描く前に呼ぶ。レシピ本の開閉で画面がずれても追従させる */
    public static void beforeRender(Screen screen) {
        if (screen != current || button == null || toggleAll == null || !(screen instanceof ScreenPosAccessor acc)) return;
        int left = acc.myas_cosmetic_armor$getLeftPos();
        int top = acc.myas_cosmetic_armor$getTopPos();
        int bx = BUTTON_X, by = BUTTON_Y;
        boolean visible = true;
        if (screen instanceof CreativeModeInventoryScreen creative) {
            bx = CREATIVE_BUTTON_X;
            by = CREATIVE_BUTTON_Y;
            visible = creative.isInventoryOpen(); // 「インベントリ」のタブを開いているときだけ出す
        }
        button.setX(left + bx);
        button.setY(top + by);
        toggleAll.setX(left + bx - (BUTTON_X - TOGGLE_X));
        toggleAll.setY(top + by + (TOGGLE_Y - BUTTON_Y));
        button.visible = visible;
        toggleAll.visible = visible;
    }
}
