package dev.rdh.rust.customization.ui;

import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.io.File;

public class ScreenshotBrowser extends Screen {
	private final Screen parent;

	public ScreenshotBrowser(Screen parent) {
		super(Component.literal("Test"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		addRenderableWidget(
				Button.builder(Component.translatable("gui.done"), b -> minecraft.setScreen(parent))
						.size(200, 20)
						.pos(width / 2 - 100, height - 22)
						.build()
		);

		addRenderableWidget(
				Button.builder(CommonComponents.EMPTY,
								b -> Util.getPlatform().openFile(new File(minecraft.gameDirectory, "screenshots")))
						.size(20, 20)
						.pos(width - 22, height - 22)
						.build()
		);
	}


}
