package dev.rdh.rust.customization;

import com.mojang.blaze3d.platform.InputConstants;

public final class CustomScreenshotConfig extends ScreenshotConfig {
	public static final String TYPE = "custom";

    private int width;
    private int height;

    public CustomScreenshotConfig(String name, InputConstants.Key key, int width, int height) {
        super(name == null ? defaultName(width, height) : name, key);
        setWidth(width);
		setHeight(height);
    }

	private static String defaultName(int width, int height) {
		return "Screenshot (" + width + "x" + height + ")";
	}

    @Override
    public String type() {
        return "custom";
    }

    @Override
    public String description() {
        return width + "x" + height;
    }

    @Override
    public int getWidth(int originalWidth) {
        return width;
    }

    @Override
    public int getHeight(int originalHeight) {
        return height;
    }

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String getDefaultName() {
		return defaultName(width, height);
	}

    public void setWidth(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }
        this.width = width;
    }

    public void setHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than 0");
        }
        this.height = height;
    }
}
