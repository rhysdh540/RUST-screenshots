package dev.rdh.rust.customization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import org.lwjgl.glfw.GLFW;

import dev.rdh.rust.RUST;
import dev.rdh.rust.util.serialization.KeyMappingTypeAdapter;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;

import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Set;

public final class ScreenshotManager {
	public static final Set<ScreenshotConfig> ALL_CONFIGS = Collections.newSetFromMap(new Object2BooleanLinkedOpenHashMap<>());
	private static final Path path = RUST.CONFIG_PATH.resolve("screenshots.json");
	private static final Gson GSON = new GsonBuilder()
			.setLenient()
			.setPrettyPrinting()
			.registerTypeAdapter(KeyMapping.class, new KeyMappingTypeAdapter())
			.create();

	static {
		try {
			if(!Files.exists(path)) {
				JsonArray arr = new JsonArray();
				arr.add(VanillaScreenshotConfig.INSTANCE.toJson(GSON));
				Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
				GSON.toJson(arr, w);
				w.flush();
				w.close();
			}
			JsonArray json = GSON.fromJson(Files.newBufferedReader(path, StandardCharsets.UTF_8), JsonArray.class);

			for (JsonElement element : json) {
				if (element.isJsonObject()) {
					JsonObject object = element.getAsJsonObject();
					String type = object.getAsJsonPrimitive("type").getAsString();
					if (type.equals("vanilla")) {
						VanillaScreenshotConfig config = VanillaScreenshotConfig.INSTANCE;
						config.enabled = object.getAsJsonPrimitive("enabled").getAsBoolean();
						ALL_CONFIGS.add(config);
					} else if (type.equals("custom")) {
						ALL_CONFIGS.add(CustomScreenshotConfig.fromJson(GSON, object));
					} else {
						throw new RuntimeException("Unknown screenshot type: " + type);
					}
				}
			}
		} catch (Throwable t) {
			RUST.LOGGER.error("Failed to load screenshot configs", t);
			ALL_CONFIGS.add(VanillaScreenshotConfig.INSTANCE);
		}

		ALL_CONFIGS.add(new CustomScreenshotConfig(
				new KeyMapping("key.rust.screenshot.5kx5k", GLFW.GLFW_KEY_F9, "key.categories.rust"),
				5000, 5000
		));

		RUST.LOGGER.info("Loaded {} screenshots", ALL_CONFIGS.size());

		Runtime.getRuntime().addShutdownHook(new Thread(ScreenshotManager::saveConfigs));
	}

	private static void saveConfigs() {
		try {
			JsonArray arr = new JsonArray();
			for (ScreenshotConfig config : ALL_CONFIGS) {
				arr.add(config.toJson(GSON));
			}

			Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
			Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			gson.toJson(arr, JsonArray.class, w);
			w.flush();
			w.close();

			RUST.LOGGER.info("Saved {} screenshot configs", ALL_CONFIGS.size());
		} catch (Throwable t) {
			RUST.LOGGER.error("Failed to write screenshot configs", t);
		}
	}

	public static void performScreenshot(ScreenshotConfig config) {
		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();

		int originalWidth = window.getWidth();
		int originalHeight = window.getHeight();
		int newWidth = config.getWidth(originalWidth);
		int newHeight = config.getHeight(originalHeight);

		boolean needsResize = newWidth != originalWidth || newHeight != originalHeight;

		if (needsResize) {
			RUST.LOGGER.info("taking screenshot with custom resolution: {}x{}", newWidth, newHeight);
			window.setWidth(newWidth);
			window.setHeight(newHeight);
			mc.resizeDisplay();

			mc.getMainRenderTarget().bindWrite(true);
			RenderSystem.enableCull();
			#if forge
			net.minecraftforge.event.ForgeEventFactory.onRenderTickStart(0);
			#elif neoforge
			net.neoforged.neoforge.client.ClientHooks.fireRenderFramePre(net.minecraft.client.DeltaTracker.ZERO);
			#endif
			mc.gameRenderer.render(
					#if MC > "20.1" net.minecraft.client.DeltaTracker.ZERO,
					#else 0, 0,
					#endif
					true);
			#if forge
			net.minecraftforge.event.ForgeEventFactory.onRenderTickEnd(0);
			#elif neoforge
			net.neoforged.neoforge.client.ClientHooks.fireRenderFramePost(net.minecraft.client.DeltaTracker.ZERO);
			#endif
			mc.getMainRenderTarget().unbindWrite();
		}

		Screenshot.grab(mc.gameDirectory, mc.getMainRenderTarget(), component -> mc.execute(() -> mc.gui.getChat().addMessage(component)));

		if (needsResize) {
			window.setWidth(originalWidth);
			window.setHeight(originalHeight);
			mc.resizeDisplay();
		}
	}
}
