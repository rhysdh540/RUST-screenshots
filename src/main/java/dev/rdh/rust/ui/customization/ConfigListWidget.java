package dev.rdh.rust.ui.customization;

import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ConfigManager;
import dev.rdh.rust.ui.customization.ConfigListWidget.ConfigListEntry;
import dev.rdh.rust.util.gui.RustSelectionList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ConfigListWidget extends RustSelectionList<ConfigListEntry> {
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

		for (ScreenshotConfig config : ConfigManager.ALL_CONFIGS) {
			this.addEntry(new ConfigListEntry(config));
		}

		this.setSelected(this.getEntry(0));
	}

	@Override
	public void setSelected(ConfigListEntry entry) {
		super.setSelected(entry);
		parent.updateConfigDetails(entry.config);
	}

	public void add(ScreenshotConfig config) {
		ConfigManager.ALL_CONFIGS.add(config);
		ConfigListEntry entry = new ConfigListEntry(config);
		this.addEntry(entry);
		this.setSelected(entry);
		this.centerScrollOn(entry);
	}

	public static class ConfigListEntry extends RustSelectionList.Entry<ConfigListEntry> {
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
	}
}
