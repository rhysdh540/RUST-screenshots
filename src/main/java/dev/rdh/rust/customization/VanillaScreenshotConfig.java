package dev.rdh.rust.customization;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public enum VanillaScreenshotConfig implements ScreenshotConfig {
	INSTANCE;

	@Override
	public KeyMapping key() {
		return Minecraft.getInstance().options.keyScreenshot;
	}

	@Override
	public int getWidth(int originalWidth) {
		return originalWidth;
	}

	@Override
	public int getHeight(int originalHeight) {
		return originalHeight;
	}
}
