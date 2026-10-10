package com.gtohjs.client.renderer;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import net.minecraft.client.gui.GuiGraphics;

/** The 16x16 adaptive_net_B icon from the approved icon sheet. */
public final class AdaptiveNetFrequencyIcon implements IGuiTexture {
    public static final AdaptiveNetFrequencyIcon INSTANCE = new AdaptiveNetFrequencyIcon();
    private static final int BLUE_BORDER = 0xff1b5e7c;
    private static final int BLUE_CENTER = 0xff4fc3f7;
    private static final int CABLE = 0xffa0a0a0;
    private static final int CABLE_DARK = 0xff5a5a5a;

    private AdaptiveNetFrequencyIcon() {}

    @Override
    public void draw(GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
        int left = (int) x + (width - 16) / 2;
        int top = (int) y + (height - 16) / 2;
        fill(graphics, left, top, 2, 5, 7, 10, BLUE_BORDER);
        fill(graphics, left, top, 3, 6, 6, 9, BLUE_CENTER);
        fill(graphics, left, top, 9, 2, 10, 13, CABLE);
        fill(graphics, left, top, 10, 2, 12, 3, CABLE);
        fill(graphics, left, top, 10, 7, 12, 8, CABLE);
        fill(graphics, left, top, 10, 12, 12, 13, CABLE);
        fill(graphics, left, top, 12, 0, 14, 4, CABLE_DARK);
        fill(graphics, left, top, 12, 5, 14, 9, CABLE_DARK);
        fill(graphics, left, top, 12, 10, 14, 14, CABLE_DARK);
    }

    private static void fill(GuiGraphics graphics, int left, int top,
                             int x1, int y1, int x2, int y2, int color) {
        graphics.fill(left + x1, top + y1, left + x2, top + y2, color);
    }
}
