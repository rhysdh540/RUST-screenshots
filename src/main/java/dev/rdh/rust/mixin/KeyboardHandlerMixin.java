package dev.rdh.rust.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.rdh.rust.ScreenshotManager;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ui.ScreenshotBrowser;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	@ModifyExpressionValue(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z", ordinal = 1))
	private boolean disableVanillaScreenshot(boolean original) {
		return false;
	}

	@Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z", ordinal = 1), cancellable = true)
	private void onKeyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
		if (Minecraft.getInstance().options.keyScreenshot.matches(key, scanCode) && Screen.hasShiftDown()) {
			Minecraft.getInstance().setScreen(new ScreenshotBrowser(Minecraft.getInstance().screen));
			return;
		}

		for(ScreenshotConfig config : ScreenshotManager.ALL_SCREENSHOT_KEYS) {
			if(config.key().matches(key, scanCode)) {
				ScreenshotManager.performScreenshot(config);
				ci.cancel();
				return;
			}
		}
	}
}
