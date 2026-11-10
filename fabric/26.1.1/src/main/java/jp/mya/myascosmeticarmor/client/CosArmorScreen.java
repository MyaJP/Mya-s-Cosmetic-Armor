/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.client;

import jp.mya.myascosmeticarmor.CosArmorMenu;
import jp.mya.myascosmeticarmor.MyasCosmeticArmor;
import jp.mya.myascosmeticarmor.CosArmorData;
import jp.mya.myascosmeticarmor.network.FlagPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** 見た目の防具の画面 */
public class CosArmorScreen extends AbstractContainerScreen<CosArmorMenu> {
    public static final Identifier TEXTURE = MyasCosmeticArmor.id("textures/gui/cosmetic_armor.png");
    private static final Component CRAFTING = Component.translatable("container.crafting");

    public CosArmorScreen(CosArmorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.titleLabelX = 97;
        this.titleLabelY = 6; // 普通のインベントリの「クラフト」と同じ位置
    }

    @Override
    protected void init() {
        super.init();
        // 普通のインベントリに戻るボタン
        addRenderableWidget(new TexButton(TEXTURE, leftPos + ScreenHooks.BUTTON_X, topPos + ScreenHooks.BUTTON_Y, 10, 10, 0, 166, null,
                CosArmorScreen::backToInventory, Component.translatable("myas_cosmetic_armor.button.back")).hoverAlt());
        // 自分の見た目の防具の全切替（白＝全部オフ）
        addRenderableWidget(ScreenHooks.toggleAllButton(leftPos + ScreenHooks.TOGGLE_X, topPos + ScreenHooks.TOGGLE_Y));
        // 見た目の防具の各スロットのスイッチ
        // 全切替がオフのとき：その部位を隠す / 全切替がオン（白）のとき：その部位だけ見た目の防具にする
        for (int i = 0; i < 4; i++) {
            final int slot = 3 - i;
            addRenderableWidget(new TexButton(TEXTURE, leftPos + 97 + 18 * i, topPos + 61, 5, 5, 0, 176,
                    () -> menu.data.getSlotSwitch(slot),
                    () -> {
                        int kind = menu.data.isAllOff() ? CosArmorData.FLAG_SHOWN : CosArmorData.FLAG_HIDDEN;
                        boolean next = !menu.data.getFlag(kind, slot);
                        menu.data.setFlag(kind, slot, next); // 先にこちらの表示を変えておく（サーバーからも同じ値が届く）
                        ClientNet.send(new FlagPayload(kind, slot, next));
                    },
                    Component.translatable("myas_cosmetic_armor.button.slot")));
        }
    }

    private static void backToInventory() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        // closeContainer() を使うと一度画面が無くなってマウスが中央に戻るので、
        // 閉じる合図だけ送って、そのまま普通のインベントリに切り替える
        player.connection.send(new ServerboundContainerClosePacket(player.containerMenu.containerId));
        player.containerMenu = player.inventoryMenu;
        open(new InventoryScreen(player));
    }

    /** 画面を切り替える（26.2 から場所が変わった） */
    static void open(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    private void drawBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        TexButton.blit(graphics, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        if (minecraft != null && minecraft.player != null) {
            InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, leftPos + 26, topPos + 8, leftPos + 75, topPos + 78, 30, 0.0625F, mouseX, mouseY, minecraft.player);
        }
    }

    // 普通のインベントリと同じく「クラフト」だけ描く（「インベントリ」の文字は防具の欄に重なるので出さない）
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, CRAFTING, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        drawBackground(graphics, mouseX, mouseY);
    }
}
