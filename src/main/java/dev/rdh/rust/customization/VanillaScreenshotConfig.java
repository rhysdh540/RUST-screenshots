package dev.rdh.rust.customization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public enum VanillaScreenshotConfig implements ScreenshotConfig {
	INSTANCE;

	public boolean enabled = true;

	@Override
	public String getName() {
		return "Vanilla Screenshot";
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

	@Override
	public JsonElement toJson(Gson gson) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "vanilla");
		json.addProperty("enabled", enabled);
		return json;
	}
}
