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

		this.setSelected(this.getEntry(0));

		this.setX(0);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int #if MC < "21.5" getScrollbarPosition #else scrollBarX #endif() {
		return this.getRight() - SCROLLBAR_WIDTH;
	}

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

	@Override
	public void setSelected(ConfigListEntry entry) {
		super.setSelected(entry);
		parent.updateConfigDetails(entry.config);
	}

	public ConfigListEntry removeSelected() {
		ConfigListEntry entry = this.getSelected();
		if (entry == null) return null;

		int index = this.children().indexOf(entry);

		this.children().remove(entry);

		ConfigListEntry newSelection = this.getEntry(Math.min(index, this.children().size() - 1));
		this.setSelected(newSelection);

		#if MC < "21.5"
		this.clampScrollAmount();
		#else
		this.refreshScrollAmount();
		#endif

		return entry;
	}
}
