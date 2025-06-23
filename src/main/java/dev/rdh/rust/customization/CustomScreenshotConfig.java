package dev.rdh.rust.customization;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import java.util.Objects;

public final class CustomScreenshotConfig implements ScreenshotConfig {

	private String name;
	private final KeyMapping key;
	private final int width;
	private final int height;
	private boolean enabled = true;

	public static String defaultName(int width, int height) {
		return "Screenshot (" + width + "x" + height + ")";
	}

	public CustomScreenshotConfig(String name, InputConstants.Key key, int width, int height) {
		if (name == null || name.isBlank()) {
			name = defaultName(width, height);
		}

		if (key == null) {
			throw new IllegalArgumentException("KeyMapping cannot be null");
		}

		this.key = new KeyMapping("key.rust.screenshot." + name.hashCode(), key.getValue(), "key.categories.rust_generated");
		this.width = width;
		this.height = height;
		this.name = name;
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

	public void setName(String name) {
		if (name == null || name.isBlank()) {
			name = defaultName(width, height);
		}
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, key, width, height, enabled);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CustomScreenshotConfig other)) return false;
		return name.equals(other.name) &&
				key.equals(other.key) &&
				width == other.width &&
				height == other.height &&
				enabled == other.enabled;
	}
}
