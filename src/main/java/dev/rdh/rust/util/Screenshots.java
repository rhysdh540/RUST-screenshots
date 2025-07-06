package dev.rdh.rust.util;

import com.mojang.blaze3d.platform.Window;

import dev.rdh.rust.RUST;
import dev.rdh.rust.customization.ScreenshotConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;

import java.nio.file.Path;

public final class Screenshots {
	public static final Path DIRECTORY = Minecraft.getInstance().gameDirectory.toPath().resolve("screenshots");

	public static void grab(ScreenshotConfig config) {
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
