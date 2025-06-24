package dev.rdh.rust.util.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.platform.InputConstants;

import dev.rdh.rust.customization.CustomScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotManager;
import dev.rdh.rust.customization.VanillaScreenshotConfig;

import net.minecraft.client.KeyMapping;

import java.io.IOException;

public class ScreenshotConfigTypeAdapter extends TypeAdapter<ScreenshotConfig> {
	@Override
	public void write(JsonWriter out, ScreenshotConfig value) throws IOException {
		out.beginObject();
		out.name("type");
		out.value(value.type());

		if (value != VanillaScreenshotConfig.INSTANCE) {
			out.name("name").value(value.getName());
			out.name("width").value(value.getWidth(0));
			out.name("height").value(value.getHeight(0));

			out.name("key").value(value.key().saveString());
		}

		out.name("enabled").value(value.enabled());
		out.endObject();
	}

	@Override
	public ScreenshotConfig read(JsonReader in) throws IOException {
		in.beginObject();
		String type = null;
		String name = null;
		int width = 0;
		int height = 0;
		InputConstants.Key key = null;
		boolean enabled = true;

		while (in.hasNext()) {
			String fieldName = in.nextName();
			switch (fieldName) {
				case "type" -> type = in.nextString();
				case "name" -> name = in.nextString();
				case "width" -> width = in.nextInt();
				case "height" -> height = in.nextInt();
				case "key" -> key = InputConstants.getKey(in.nextString());
				case "enabled" -> enabled = in.nextBoolean();
			}
		}

		in.endObject();

		if (type == null) {
			throw new IOException("Missing type in screenshot config");
		}

		ScreenshotConfig config;
		if ("vanilla".equals(type)) {
			if (name != null) {
				throw new IOException("Name is not applicable for vanilla screenshot config");
			}

			if (width != 0 || height != 0) {
				throw new IOException("Width and height are not applicable for vanilla screenshot config");
			}

			if (key != null) {
				throw new IOException("Key is not applicable for vanilla screenshot config");
			}

			config = VanillaScreenshotConfig.INSTANCE;
		} else if ("custom".equals(type)) {
			if (key == null) {
				throw new IOException("Missing key in custom screenshot config");
			}

			if (width <= 0 || height <= 0) {
				throw new IOException("Invalid dimensions in custom screenshot config: " + width + "x" + height);
			}

			if (name == null || name.isBlank()) {
				name = CustomScreenshotConfig.defaultName(width, height);
			}

			config = new CustomScreenshotConfig(name, key, width, height);
		} else {
			throw new IOException("Unknown screenshot config type: " + type);
		}

		if (!enabled) {
			config.toggleEnabled();
		}

		return config;
	}
}
