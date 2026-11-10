/*
 * Mya's Cosmetic Armor - a fork of CosmeticArmorReworked by zlainsama (LainMI)
 * https://github.com/zlainsama/CosmeticArmorReworked
 * Licensed under the Minecraft Mod Public License (MMPL) 1.0.1. See LICENSE.txt.
 */
package jp.mya.myascosmeticarmor.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.BooleanSupplier;

/** テクスチャの一部を絵にした小さいボタン。alt が true のとき（またはマウスが乗っているとき）は横にずらした絵を使う */
public class TexButton extends AbstractWidget {
    private final ResourceLocation texture;
    private final int u, v;
    private final BooleanSupplier alt;
    private final Runnable action;
    private boolean hoverAlt;

    public TexButton(ResourceLocation texture, int x, int y, int w, int h, int u, int v, BooleanSupplier alt, Runnable action, Component message) {
        super(x, y, w, h, message);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.alt = alt;
        this.action = action;
    }

    /** マウスが乗っている間だけ横の絵にする（見た目の防具ボタン用） */
    public TexButton hoverAlt() {
        this.hoverAlt = true;
        return this;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        action.run();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean useAlt = (alt != null && alt.getAsBoolean()) || (hoverAlt && isHovered());
        int uu = u + (useAlt ? width : 0);
        blit(graphics, texture, getX(), getY(), uu, v, width, height, 256, 256);
    }

    /** テクスチャの一部を描く */
    public static void blit(GuiGraphics graphics, ResourceLocation tex, int x, int y, int u, int v, int w, int h, int texW, int texH) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, w, h, texW, texH);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
