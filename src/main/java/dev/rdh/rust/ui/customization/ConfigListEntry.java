package dev.rdh.rust.ui.customization;

import dev.rdh.rust.customization.ScreenshotConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

public class ConfigListEntry extends ObjectSelectionList.Entry<ConfigListEntry> {
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
