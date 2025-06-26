package dev.rdh.rust.ui.customization;

import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotManager;
import dev.rdh.rust.ui.customization.ConfigListWidget.ConfigListEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

public class ConfigListWidget extends ObjectSelectionList<ConfigListEntry> {
	private final ConfigListScreen parent;

	public ConfigListWidget(
			Minecraft mc,
			ConfigListScreen parent,
			int width,
			int height,
			int y,
			int itemHeight
	) {
		super(mc, width, height, y, #if MC <= "20.1" y + height, #endif itemHeight);
		this.parent = parent;

		for (ScreenshotConfig config : ScreenshotManager.ALL_CONFIGS) {
			this.addEntry(new ConfigListEntry(config));
		}

		this.setSelected(this.getEntry(0));
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int #if MC < "21.5" getScrollbarPosition #else scrollBarX #endif() {
		#if MC >= "21.0"
		return this.getRight() - SCROLLBAR_WIDTH;
		#else
		return this.x1 - 6;
		#endif
	}

	#if MC >= "21.0"
	@Override
	protected void renderSelection(GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
		if (this.scrollbarVisible()) {
			int left = this.getRowLeft() - 2;
			int right = this.getRight() - SCROLLBAR_WIDTH - 1;
			graphics.fill(left, top - 2, right, top + height + 2, outerColor);
			graphics.fill(left + 1, top - 1, right - 1, top + height + 1, innerColor);
		} else {
			super.renderSelection(graphics, top, width, height, outerColor, innerColor);
		}
	}
	#endif

	@Override
	public void setSelected(ConfigListEntry entry) {
		super.setSelected(entry);
		parent.updateConfigDetails(entry.config);
	}

	public void add(ScreenshotConfig config) {
		ScreenshotManager.ALL_CONFIGS.add(config);
		ConfigListEntry entry = new ConfigListEntry(config);
		this.addEntry(entry);
		this.setSelected(entry);
		this.centerScrollOn(entry);
	}

	public #if MC >= "21.0" static #endif class ConfigListEntry extends ObjectSelectionList.Entry<ConfigListEntry> {
		public final ScreenshotConfig config;

		public ConfigListEntry(ScreenshotConfig config) {
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

		#if MC < "21.0"
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0) {
				ConfigListWidget.this.setSelected(this);
				return true;
			}
			return false;
		}
		#endif
	}
}
