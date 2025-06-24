package dev.rdh.rust.ui.customization;

import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;

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
		super(mc, width, height, y, itemHeight);
		this.parent = parent;

		for (ScreenshotConfig config : ScreenshotManager.ALL_CONFIGS) {
			this.addEntry(new ConfigListEntry(config));
		}

		this.setX(0);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.getRight() - 6;
	}

	@Override
	protected void renderSelection(GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor) {
		if (this.scrollbarVisible()) {
			int left = this.getRowLeft() - 2;
			int right = this.getRight() - 6 - 1;
			guiGraphics.fill(left, top - 2, right, top + height + 2, outerColor);
			guiGraphics.fill(left + 1, top - 1, right - 1, top + height + 1, innerColor);
		} else {
			super.renderSelection(guiGraphics, top, width, height, outerColor, innerColor);
		}
	}

	@Override
	public void setSelected(ConfigListEntry entry) {
		super.setSelected(entry);
		parent.updateConfigDetails(entry.config);
	}
}
