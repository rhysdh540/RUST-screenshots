package dev.rdh.rust.customization;

import net.minecraft.client.KeyMapping;

public interface ScreenshotConfig {

	String type();
	String getName();
	String description();
	KeyMapping key();

	int getWidth(int originalWidth);
	int getHeight(int originalHeight);

	boolean enabled();
	void toggleEnabled();
}
