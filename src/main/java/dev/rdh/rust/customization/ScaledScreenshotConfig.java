package dev.rdh.rust.customization;

import com.mojang.blaze3d.platform.InputConstants;

/**
 * Screenshot config that scales the original width/height by a given factor.
 */
public class ScaledScreenshotConfig extends ScreenshotConfig {
	public static final String TYPE = "scaled";

    private float scale;

    public ScaledScreenshotConfig(String name, InputConstants.Key key, float scale) {
        super(name == null ? defaultName(scale) : name, key);
        setScale(scale);
    }

	private static String defaultName(float scale) {
		return "Scaled Screenshot (" + scale + "x)";
	}

	@Override
    public String type() {
        return "scaled";
    }

    @Override
    public String description() {
        return scale + "x screen resolution";
    }

    @Override
    public int getWidth(int originalWidth) {
        return Math.round(originalWidth * scale);
    }

    @Override
    public int getHeight(int originalHeight) {
        return Math.round(originalHeight * scale);
    }

	@Override
	public String getDefaultName() {
		return defaultName(scale);
	}

    public float getScale() {
        return scale;
    }

	public void setScale(float scale) {
		if (scale <= 0) throw new IllegalArgumentException("Scale must be > 0");
		this.scale = scale;
	}
}

