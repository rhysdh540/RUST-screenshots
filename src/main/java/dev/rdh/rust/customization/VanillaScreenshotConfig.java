package dev.rdh.rust.customization;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public enum VanillaScreenshotConfig implements ScreenshotConfig {
	INSTANCE;

	public boolean enabled = true;

	@Override
	public String type() {
		return "vanilla";
	}

	@Override
	public String getName() {
		return "Vanilla Screenshot";
	}

	@Override
	public String description() {
		return "1x screen resolution";
	}

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

	@Override
	public boolean enabled() {
		return enabled;
	}

	@Override
	public void toggleEnabled() {
		enabled = !enabled;
	}
}

