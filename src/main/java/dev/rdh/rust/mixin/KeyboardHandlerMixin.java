package dev.rdh.rust.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.rdh.rust.RUST;
import dev.rdh.rust.ScreenshotManager;
import dev.rdh.rust.customization.ScreenshotConfig;

import net.minecraft.client.KeyboardHandler;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	@ModifyExpressionValue(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z", ordinal = 1))
	private boolean disableVanillaScreenshot(boolean original) {
		return false;
	}

	@Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z", ordinal = 1), cancellable = true)
	private void onKeyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
		for(ScreenshotConfig config : ScreenshotManager.ALL_SCREENSHOT_KEYS) {
			if(config.key().matches(key, scanCode)) {
				RUST.LOGGER.info("Key pressed: {}", config.key());
				ScreenshotManager.performScreenshot(config);
				ci.cancel();
				return;
			}
		}
	}
}
