package dev.rdh.rust.ui.browser;

import dev.rdh.rust.ui.browser.ScreenshotListWidget.ScreenshotEntry;
import dev.rdh.rust.ui.customization.ConfigListScreen;
import dev.rdh.rust.util.gui.ImageWidget;
import dev.rdh.rust.util.gui.SelectionList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.List;

public class ScreenshotListWidget extends SelectionList<ScreenshotEntry> {

	public ScreenshotListWidget(
			Minecraft mc,
			int width,
			int height,
			int y,
			int itemHeight,
			List<Path> screenshots
	) {
		super(mc, width, height, y, #if MC <= 20.1 y + height, #endif itemHeight);

		for (Path path : screenshots) {
			this.addEntry(new ScreenshotEntry(path));
		}

		this.setRenderHeader(true, 12);

		this.setSelected(this.getEntry(0));
	}

	@Override
	public void renderHeader(GuiGraphics graphics, int x, int y) {
		Font font = minecraft.font;
		graphics.drawString(font, "Screenshots", x + 2, y + 2, 0xFFFFFF);
	}

	public #if MC >= 21.0 static #endif class ScreenshotEntry extends SelectionList.Entry<ScreenshotEntry> {
		public final Path path;
		public final ImageWidget thumbnail;

		public ScreenshotEntry(Path path) {
			this.path = path;
			try {
				this.thumbnail = new ImageWidget(0, 0, 0, 0, path);
			} catch (Exception e) {
				throw new RuntimeException("Failed to load screenshot thumbnail: " + path, e);
			}
		}

		@Override
		public Component getNarration() {
			return Component.literal(path.getFileName().toString());
		}

		@Override
		public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
			thumbnail.setX(left + 2);
			thumbnail.setY(top + 2);
			thumbnail.setWidth(height - 4);
			thumbnail.setHeight(height - 4);
			thumbnail.shrinkToAspectRatio();
			thumbnail.render(graphics, mouseX, mouseY, partialTick);

			Font font = Minecraft.getInstance().font;

			graphics.drawString(font, path.getFileName().toString(), left + 2 + thumbnail.getWidth() + 2, top + 2, 0xFFFFFF);
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
