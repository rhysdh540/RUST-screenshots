package dev.rdh.rust.customization;

import net.minecraft.client.KeyMapping;

public interface ScreenshotConfig {

	String getName();
	KeyMapping key();

	int getWidth(int originalWidth);
	int getHeight(int originalHeight);

	boolean enabled();
	void toggleEnabled();
}
