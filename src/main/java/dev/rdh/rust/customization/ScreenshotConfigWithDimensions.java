package dev.rdh.rust.customization;

import net.minecraft.client.KeyMapping;

public final class ScreenshotConfigWithDimensions implements ScreenshotConfig {
	private final KeyMapping key;
	private final int width;
	private final int height;

	public ScreenshotConfigWithDimensions(KeyMapping key, int width, int height) {
		this.key = key;
		this.width = width;
		this.height = height;
	}

	@Override
	public KeyMapping key() {
		return key;
	}

	@Override
	public int getWidth(int originalWidth) {
		return width;
	}

	@Override
	public int getHeight(int originalHeight) {
		return height;
	}
}
