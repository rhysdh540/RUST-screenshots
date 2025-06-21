package dev.rdh.rust;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import org.lwjgl.glfw.GLFW;

import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfigWithDimensions;
import dev.rdh.rust.customization.VanillaScreenshotConfig;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;

import java.util.Collections;
import java.util.Set;

public final class ScreenshotManager {
	public static final Set<ScreenshotConfig> ALL_SCREENSHOT_KEYS = Collections.newSetFromMap(new Object2BooleanLinkedOpenHashMap<>());

	static {
		ALL_SCREENSHOT_KEYS.add(VanillaScreenshotConfig.INSTANCE);
		ALL_SCREENSHOT_KEYS.add(new ScreenshotConfigWithDimensions(
				new KeyMapping("key.rust.screenshot.5kx5k", GLFW.GLFW_KEY_F9, "key.categories.rust"),
				5000, 5000
		));
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
