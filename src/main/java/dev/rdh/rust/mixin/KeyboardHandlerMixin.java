package dev.rdh.rust.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.rdh.rust.customization.ScreenshotManager;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.ui.customization.ConfigListScreen;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	#if forge
	@org.spongepowered.asm.mixin.injection.Redirect
	#else
	@com.llamalad7.mixinextras.injector.ModifyExpressionValue
	#endif
	(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z", ordinal = 1))

	private boolean disableVanillaScreenshot #if forge (net.minecraft.client.KeyMapping instance, int keysym, int scancode)
	#else (boolean original)
	#endif {
		return false;
	}

	@Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(II)Z", ordinal = 1), cancellable = true)
	private void onKeyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.keyScreenshot.matches(key, scanCode) && Screen.hasShiftDown() && !(mc.screen instanceof ConfigListScreen)) {
			mc.setScreen(new ConfigListScreen(mc.screen));
			return;
		}

		for(ScreenshotConfig config : ScreenshotManager.ALL_CONFIGS) {
			if(config.key().matches(key, scanCode) && config.enabled()) {
				ScreenshotManager.performScreenshot(config);
				ci.cancel();
				return;
			}
		}
	}
}
