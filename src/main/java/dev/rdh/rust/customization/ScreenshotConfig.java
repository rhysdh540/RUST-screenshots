package dev.rdh.rust.customization;

import net.minecraft.client.KeyMapping;

public sealed interface ScreenshotConfig
		permits ScreenshotConfigWithDimensions, VanillaScreenshotConfig {
	KeyMapping key();
	int getWidth(int originalWidth);
	int getHeight(int originalHeight);
}
