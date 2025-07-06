package dev.rdh.rust.util.gui;

import com.mojang.blaze3d.platform.NativeImage;

import dev.rdh.rust.util.ImageCache;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.io.Closeable;
import java.nio.file.Path;

public class ImageWidget extends AbstractWidget implements Closeable {
	private final ResourceLocation resource;

	private final int imageWidth;
	private final int imageHeight;

	public ImageWidget(int x, int y, int width, int height, Path path) {
		super(x, y, width, height, CommonComponents.EMPTY);

		this.resource = ImageCache.get(path);

		NativeImage image = ImageCache.getPixels(path);

		this.imageWidth = image.getWidth();
		this.imageHeight = image.getHeight();
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void shrinkToAspectRatio(boolean center) {
	    int origX = getX();
	    int origY = getY();
	    int origWidth = getWidth();
	    int origHeight = getHeight();

	    double aspectRatio = (double) getImageWidth() / getImageHeight();
		if (aspectRatio == 1.0) {
			return;
		}
	    int newWidth, newHeight;

	    if (origWidth / (double) origHeight > aspectRatio) {
	        // too wide, shrink width
	        newWidth = (int) (origHeight * aspectRatio);
	        newHeight = origHeight;
	    } else {
	        // too tall, shrink height
	        newWidth = origWidth;
	        newHeight = (int) (origWidth / aspectRatio);
	    }

	    int centerX = origX + origWidth / 2;
	    int centerY = origY + origHeight / 2;

	    // Set new position so the widget is centered
	    if (center) {
			setX(centerX - newWidth / 2);
			setY(centerY - newHeight / 2);
		}
	    setWidth(newWidth);
	    setHeight(newHeight);
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

		int x = this.getX();
		int y = this.getY();
		int w = this.getWidth();
		int h = this.getHeight();

		#if MC >= 21.5
		graphics.blit(
				net.minecraft.client.renderer.RenderType::guiTextured,
				this.resource,
				x, y,
				0, 0,
				w, h,
				w, h,
				w, h
		);
		#else
		graphics.blit(
				this.resource,
				x, y,
				0, 0,
				w, h,
				w, h
		);
		#endif
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
	}

	@Override
	public void close() {
		Minecraft.getInstance().getTextureManager().release(this.resource);
	}
}
