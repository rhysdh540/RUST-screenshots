package dev.rdh.rust.customization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.KeyMapping;

public final class CustomScreenshotConfig implements ScreenshotConfig {

	private String name;
	private final KeyMapping key;
	private final int width;
	private final int height;
	private boolean enabled = true;

	public CustomScreenshotConfig(String name, KeyMapping key, int width, int height) {
		this.key = key;
		this.width = width;
		this.height = height;

		this.name = name;
	}

	public CustomScreenshotConfig(KeyMapping key, int width, int height) {
		this("Screenshot (" + width + "x" + height + ")", key, width, height);
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

	@Override
	public boolean enabled() {
		return enabled;
	}

	@Override
	public void toggleEnabled() {
		enabled = !enabled;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public JsonElement toJson(Gson gson) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "custom");
		json.addProperty("name", name);
		json.addProperty("width", width);
		json.addProperty("height", height);
		json.addProperty("enabled", enabled);
		json.add("key", gson.toJsonTree(key));
		return json;
	}

	public static CustomScreenshotConfig fromJson(Gson gson, JsonObject json) {
		String name = json.getAsJsonPrimitive("name").getAsString();
		int width = json.getAsJsonPrimitive("width").getAsInt();
		int height = json.getAsJsonPrimitive("height").getAsInt();
		boolean enabled = json.getAsJsonPrimitive("enabled").getAsBoolean();
		KeyMapping key = gson.fromJson(json.get("key"), KeyMapping.class);
		CustomScreenshotConfig config = new CustomScreenshotConfig(name, key, width, height);
		config.enabled = enabled;

		return config;
	}
}
