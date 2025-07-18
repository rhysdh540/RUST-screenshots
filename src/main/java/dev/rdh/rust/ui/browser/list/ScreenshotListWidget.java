package dev.rdh.rust.ui.browser.list;

import dev.rdh.rust.ui.browser.list.ScreenshotListWidget.ScreenshotEntry;
import dev.rdh.rust.util.ImageCache;
import dev.rdh.rust.util.gui.DynamicImageWidget;
import dev.rdh.rust.util.gui.RustSelectionList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.io.Closeable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ScreenshotListWidget extends RustSelectionList<ScreenshotEntry> {

	private final ScreenshotListScreen parent;

	public ScreenshotListWidget(
			Minecraft mc,
			ScreenshotListScreen parent,
			int width,
			int height,
			int y,
			int itemHeight,
			Set<Path> screenshots
	) {
		super(mc, width, height, y, itemHeight);
		this.parent = parent;

		screenshots.forEach(this::addEntry);
		ImageCache.onAdded(this, this::addEntry);

		if (!this.children().isEmpty()) {
			this.setSelected(this.getEntry(0));
		}
	}

	private void addEntry(Path path) {
		if (!Files.exists(path)) return;
		ScreenshotEntry entry = new ScreenshotEntry(path);
		this.addEntry(entry);

		ImageCache.onRemoved(path, () -> {
			if (this.children().contains(entry)) {
				this.removeEntry(entry);
				entry.close();
			}
		});
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

	@Override
	public void close() {
		super.close();
		ImageCache.removeAddedCallback(this);
	}

	public static class ScreenshotEntry extends RustSelectionList.Entry<ScreenshotEntry> implements Closeable {
		public final Path path;
		public final DynamicImageWidget thumbnail;

		public ScreenshotEntry(Path path) {
			this.path = path;
			this.thumbnail = new DynamicImageWidget(0, 0, 0, 0, path);
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
