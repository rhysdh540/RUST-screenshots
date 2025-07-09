package dev.rdh.rust.util.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.platform.InputConstants;

import dev.rdh.rust.customization.ConfigManager;
import dev.rdh.rust.customization.CustomScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ScaledScreenshotConfig;

import java.io.IOException;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class ScreenshotConfigTypeAdapter extends TypeAdapter<ScreenshotConfig> {
	@Override
	public void write(JsonWriter out, ScreenshotConfig value) throws IOException {
		out.beginObject();
		out.name("type");
		out.value(value.type());

		out.name("name").value(value.getName());
		out.name("key").value(value.key().saveString());

		if (value instanceof CustomScreenshotConfig c) {
			out.name("width").value(c.getWidth(0));
			out.name("height").value(c.getHeight(0));
		} else if (value instanceof ScaledScreenshotConfig s) {
			out.name("scale").value(s.getScale());
		} else {
			throw new IllegalArgumentException("Unknown screenshot config type: " + value.getClass().getName());
		}

		out.name("enabled").value(value.enabled);
		out.endObject();
	}

	@Override
	public ScreenshotConfig read(JsonReader in) throws IOException {
		in.beginObject();
		String type = null;
		String name = null;
		OptionalInt width = OptionalInt.empty();
		OptionalInt height = OptionalInt.empty();
		InputConstants.Key key = null;
		boolean enabled = true;
		OptionalDouble scale = OptionalDouble.empty();

		while (in.hasNext()) {
			String fieldName = in.nextName();
			switch (fieldName) {
				case "type" -> type = in.nextString();
				case "name" -> name = in.nextString();
				case "width" -> width = OptionalInt.of(in.nextInt());
				case "height" -> height = OptionalInt.of(in.nextInt());
				case "key" -> key = InputConstants.getKey(in.nextString());
				case "enabled" -> enabled = in.nextBoolean();
				case "scale" -> scale = OptionalDouble.of(in.nextDouble());
			}
		}
		in.endObject();

		if (type == null) {
			throw new IllegalArgumentException("Screenshot config type is required");
		}

		if (!"vanilla".equals(type)) {
			if (name == null) {
				throw new IllegalArgumentException("Screenshot config name is required");
			}
			if (key == null) {
				throw new IllegalArgumentException("Screenshot config key is required");
			}
		}

		ScreenshotConfig config = switch(type) {
			case CustomScreenshotConfig.TYPE -> {
				if(scale.isPresent()) {
					throw new IllegalArgumentException("Custom screenshot config cannot have a scale");
				}

				if(width.isEmpty() || height.isEmpty()) {
					throw new IllegalArgumentException("Custom screenshot config must have width and height");
				}

				yield new CustomScreenshotConfig(name, key, width.getAsInt(), height.getAsInt());
			}
			case ScaledScreenshotConfig.TYPE -> {
				if(width.isPresent() || height.isPresent()) {
					throw new IllegalArgumentException("Scaled screenshot config cannot have width or height");
				}

				if(scale.isEmpty()) {
					throw new IllegalArgumentException("Scaled screenshot config must have a scale");
				}

				yield new ScaledScreenshotConfig(name, key, (float) scale.getAsDouble());
			}
			case "vanilla" ->  ConfigManager.VANILLA_CONFIG;
			default -> throw new IllegalArgumentException("Unknown screenshot config type: " + type);
		};

		config.enabled = enabled;

		return config;
	}
}
