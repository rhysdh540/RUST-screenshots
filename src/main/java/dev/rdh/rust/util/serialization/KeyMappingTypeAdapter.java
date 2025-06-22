package dev.rdh.rust.util.serialization;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import java.io.IOException;

public class KeyMappingTypeAdapter extends TypeAdapter<KeyMapping> {
	@Override
	public void write(JsonWriter out, KeyMapping value) throws IOException {
		out.beginObject();
		out.name("name").value(value.getName());
		out.name("key").value(value.saveString());
		out.endObject();
	}

	@Override
	public KeyMapping read(JsonReader in) throws IOException {
		in.beginObject();

		String keyName = null;
		InputConstants.Key key = null;
		while (in.hasNext()) {
			String name = in.nextName();
			if (name.equals("name")) {
				keyName = in.nextString();
			} else if (name.equals("key")) {
				key = InputConstants.getKey(in.nextString());
			} else {
				throw new JsonSyntaxException("Unknown key '" + name + "'");
			}
		}

		in.endObject();

		if (keyName == null) {
			throw new JsonSyntaxException("Key name is missing");
		}

		if (key == null) {
			throw new JsonSyntaxException("Key is missing");
		}

		KeyMapping result = new KeyMapping(keyName, 0, "key.categories.rust");
		result.setKey(key);
		return result;
	}
}
