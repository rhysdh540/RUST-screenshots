package dev.rdh.rust.ui.browser.list;

import com.mojang.blaze3d.platform.NativeImage;

import dev.rdh.rust.RUST;
import dev.rdh.rust.ui.browser.list.ScreenshotListWidget.ScreenshotEntry;
import dev.rdh.rust.util.gui.ImageWidget;
import dev.rdh.rust.util.gui.RustSelectionList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.io.Closeable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ScreenshotListWidget extends RustSelectionList<ScreenshotEntry> {

	private final ScreenshotGridScreen parent;

	public ScreenshotListWidget(
			Minecraft mc,
			ScreenshotGridScreen parent,
			int width,
			int height,
			int y,
			int itemHeight,
			List<Path> screenshots
	) {
		super(mc, width, height, y, itemHeight);
		this.parent = parent;

		for (Path path : screenshots) {
			try {
				this.addEntry(new ScreenshotEntry(path, NativeImage.read(Files.newInputStream(path))));
			} catch (Exception e) {
				RUST.LOGGER.error("Failed to load screenshot: " + path, e);
			}
		}

		if (!screenshots.isEmpty()) {
			this.setSelected(this.getEntry(0));
		}
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.renderWidget(graphics, mouseX, mouseY, partialTick);
		if (this.children().isEmpty()) {
			Font font = Minecraft.getInstance().font;
			String noScreenshots = "No screenshots found";
			int textWidth = font.width(noScreenshots);
			int x = (this.width - textWidth) / 2;
			int y = this.getY() + (this.height - font.lineHeight) / 2;
			graphics.drawString(font, noScreenshots, x, y, 0xFFFFFF);
		}
	}

	@Override
	public void setSelected(ScreenshotEntry entry) {
		super.setSelected(entry);
		if (entry == null) {
			this.parent.updateSelected(null);
		} else {
			this.parent.updateSelected(entry.path);
		}
	}

	public static class ScreenshotEntry extends RustSelectionList.Entry<ScreenshotEntry> implements Closeable {
		public final Path path;
		public final ImageWidget thumbnail;

		public ScreenshotEntry(Path path, NativeImage thumbnail) {
			this.path = path;
			this.thumbnail = new ImageWidget(0, 0, 0, 0, thumbnail);
		}

		@Override
		public Component getNarration() {
			return Component.literal(path.getFileName().toString());
		}

		@Override
		public void close() {
			thumbnail.close();
		}

		@Override
		public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
			left += 2;
			top += 2;
			thumbnail.setPosition(left, top);
			thumbnail.setWidth(height - 4);
			thumbnail.setHeight(height - 4);
			thumbnail.shrinkToAspectRatio(true);
			thumbnail.render(graphics, mouseX, mouseY, partialTick);

			left += thumbnail.getWidth() + 2;

			Font font = Minecraft.getInstance().font;

			String line1 = path.getFileName().toString();
			String line2 = thumbnail.getImageWidth() + "x" + thumbnail.getImageHeight();

			graphics.drawString(font, line1, left, top, 0xFFFFFF);
			top += font.lineHeight + 2;
			graphics.drawString(font, line2, left, top, 0xAAAAAA);
		}
	}
}
