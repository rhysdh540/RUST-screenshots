package dev.rdh.rust.customization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;

import dev.rdh.rust.RUST;
import dev.rdh.rust.util.serialization.ScreenshotConfigTypeAdapter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;

import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Set;

public final class ScreenshotManager {
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

		Runtime.getRuntime().addShutdownHook(new Thread(ScreenshotManager::saveConfigs));
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

			#if MC < 21.5
			mc.getMainRenderTarget().bindWrite(true);
			com.mojang.blaze3d.systems.RenderSystem.enableCull();
			#endif
			#if forge
			net.minecraftforge.event.ForgeEventFactory.onRenderTickStart(0);
			#elif neoforge
			net.neoforged.neoforge.client.ClientHooks.fireRenderFramePre(net.minecraft.client.DeltaTracker.ZERO);
			#endif
			mc.gameRenderer.render(
					#if MC > 20.1 net.minecraft.client.DeltaTracker.ZERO,
					#else 0, 0,
					#endif
					true);
			#if forge
			net.minecraftforge.event.ForgeEventFactory.onRenderTickEnd(0);
			#elif neoforge
			net.neoforged.neoforge.client.ClientHooks.fireRenderFramePost(net.minecraft.client.DeltaTracker.ZERO);
			#endif

			#if MC < 21.5
			mc.getMainRenderTarget().unbindWrite();
			#endif
		}

		Screenshot.grab(mc.gameDirectory, mc.getMainRenderTarget(), component -> mc.execute(() -> mc.gui.getChat().addMessage(component)));

		if (needsResize) {
			window.setWidth(originalWidth);
			window.setHeight(originalHeight);
			mc.resizeDisplay();
		}
	}
}
