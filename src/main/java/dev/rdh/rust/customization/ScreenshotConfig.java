package dev.rdh.rust.customization;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import java.util.Objects;

public abstract class ScreenshotConfig {
    protected String name;
    protected KeyMapping key;
    public boolean enabled = true;
	public boolean hideUI = false;

	public static final String CATEGORY = "key.categories.rust_generated";

    protected ScreenshotConfig(String name, InputConstants.Key key) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null");
		}

		setName(name);

		String keyName = "key.rust.screenshot.";
		keyName += Long.toHexString(name.hashCode() >> Double.doubleToLongBits(Math.random()));
        this.key = new KeyMapping(keyName, key.getValue(), CATEGORY);
    }

    public abstract String type();
    public abstract String description();
    public abstract int getWidth(int originalWidth);
    public abstract int getHeight(int originalHeight);
	public abstract String getDefaultName();

    public String getName() {
        return name;
    }

	public void setName(String name) {
		if(name == null || name.isEmpty()) {
			name = getDefaultName();
		}
		this.name = name;
	}

    public KeyMapping key() {
        return key;
    }

    @Override
    public int hashCode() {
        int hash = 7;
		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + key.saveString().hashCode();
		return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ScreenshotConfig other)) return false;
        return Objects.equals(name, other.name) &&
                Objects.equals(key, other.key) &&
                enabled == other.enabled;
    }
}
