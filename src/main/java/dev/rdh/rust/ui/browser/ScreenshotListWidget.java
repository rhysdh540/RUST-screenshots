package dev.rdh.rust.ui.browser;

import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.ui.browser.ScreenshotListWidget.ScreenshotEntry;
import dev.rdh.rust.ui.customization.ConfigListScreen;
import dev.rdh.rust.util.gui.SelectionList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

public class ScreenshotListWidget extends SelectionList<ScreenshotEntry> {
	private final ConfigListScreen parent;

	public ScreenshotListWidget(
			Minecraft mc,
			ConfigListScreen parent,
			int width,
			int height,
			int y,
			int itemHeight
	) {
		super(mc, width, height, y, #if MC <= 20.1 y + height, #endif itemHeight);
		this.parent = parent;

		this.setSelected(this.getEntry(0));
	}

	public #if MC >= 21.0 static #endif class ScreenshotEntry extends SelectionList.Entry<ScreenshotEntry> {
		public final ScreenshotConfig config;

		public ScreenshotEntry(ScreenshotConfig config) {
			this.config = config;
		}

		@Override
		public Component getNarration() {
			return Component.literal(config.getName());
		}

		@Override
		public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
			Font font = Minecraft.getInstance().font;

			int color = config.enabled() ? 0xFFFFFF : 0x808080;

			int y = top + 1;
			int x = left + 2;
			graphics.drawString(font, config.getName(), x, y, color);
			y += font.lineHeight + 2;
			graphics.drawString(font, config.description(), x, y, color);
		}

		#if MC < 21.0
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0) {
				ScreenshotListWidget.this.setSelected(this);
				return true;
			}
			return false;
		}
		#endif
	}
}
