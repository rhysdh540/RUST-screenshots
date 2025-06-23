package dev.rdh.rust.customization;

import net.minecraft.client.KeyMapping;

public sealed interface ScreenshotConfig
		permits CustomScreenshotConfig, VanillaScreenshotConfig {

	String getName();
	KeyMapping key();

	int getWidth(int originalWidth);
	int getHeight(int originalHeight);

	boolean enabled();
	void toggleEnabled();
}
