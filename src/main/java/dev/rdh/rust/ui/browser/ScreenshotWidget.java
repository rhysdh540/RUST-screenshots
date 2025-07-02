package dev.rdh.rust.ui.browser;

import com.mojang.blaze3d.platform.NativeImage;

import dev.rdh.rust.util.gui.AbstractContainerWidget;
import dev.rdh.rust.util.gui.ImageWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotWidget extends AbstractContainerWidget {
	private final ImageWidget image;

	public ScreenshotWidget(int x, int y, int width, int height, Path path) {
		super(x, y, width, height);

		NativeImage nImage;
		try {
			nImage = NativeImage.read(Files.newInputStream(path));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read screenshot image from " + path, e);
		}

		int imageWidth, imageHeight;
		if (nImage.getWidth() > nImage.getHeight()) {
			double ratio = (double) width / nImage.getWidth();
			imageWidth = width;
			imageHeight = (int) (nImage.getHeight() * ratio);
		} else {
			double ratio = (double) height / nImage.getHeight();
			imageWidth = (int) (nImage.getWidth() * ratio);
			imageHeight = height;
		}

		this.image = addChild(new ImageWidget(x, y, imageWidth, imageHeight, nImage));
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.fill(this.getX(), this.getY(),
				this.getX() + this.getWidth(), this.getY() + this.getHeight(),
				0xFF000000
		);

		image.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

	}
}
