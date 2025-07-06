package dev.rdh.rust.customization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;

import dev.rdh.rust.RUST;
import dev.rdh.rust.util.serialization.ScreenshotConfigTypeAdapter;

import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Set;

public final class ConfigManager {
	public static final Set<ScreenshotConfig> ALL_CONFIGS = Collections.newSetFromMap(new Object2BooleanLinkedOpenHashMap<>());
	private static final Type SCREENSHOT_CONFIG_SET_TYPE = TypeToken.getParameterized(Set.class, ScreenshotConfig.class).getType();

	private static final Path path = RUST.CONFIG_PATH.resolve("screenshots.json");
	private static final Gson GSON = new GsonBuilder()
			#if MC < 21.5
			.setLenient()
			#else
			.setStrictness(com.google.gson.Strictness.LENIENT)
			#endif
			.setPrettyPrinting()
			.registerTypeHierarchyAdapter(ScreenshotConfig.class, new ScreenshotConfigTypeAdapter())
			.create();

	static {
		ALL_CONFIGS.add(VanillaScreenshotConfig.INSTANCE);
		try {
			if(Files.exists(path)) {
				ALL_CONFIGS.addAll(
						GSON.fromJson(Files.newBufferedReader(path, StandardCharsets.UTF_8),
								SCREENSHOT_CONFIG_SET_TYPE)
				);
			}

		} catch (Throwable t) {
			RUST.LOGGER.error("Failed to load screenshot configs", t);
		}

		if (RUST.IS_DEV_ENV && ALL_CONFIGS.size() == 1) {
			for (int i = 1; i <= 50; i++) {
				ALL_CONFIGS.add(new CustomScreenshotConfig("Test Config " + i, InputConstants.UNKNOWN, i * 20, i * 10));
			}
		}

		RUST.LOGGER.info("Loaded {} screenshot configs", ALL_CONFIGS.size());

		Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::saveConfigs));
	}

	private static void saveConfigs() {
		try(Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			GSON.toJson(ALL_CONFIGS, Set.class, w);
			w.flush();

			RUST.LOGGER.info("Saved {} screenshot configs", ALL_CONFIGS.size());
		} catch (Throwable t) {
			RUST.LOGGER.error("Failed to write screenshot configs", t);
		}
	}
}
