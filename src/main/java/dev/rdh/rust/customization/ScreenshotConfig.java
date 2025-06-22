package dev.rdh.rust.customization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.client.KeyMapping;

public sealed interface ScreenshotConfig
		permits CustomScreenshotConfig, VanillaScreenshotConfig {

	String getName();
	JsonElement toJson(Gson gson);
	KeyMapping key();

	int getWidth(int originalWidth);
	int getHeight(int originalHeight);

	boolean enabled();
	void toggleEnabled();
}
