package dev.rdh.rust.ui.customization;

import dev.rdh.rust.customization.ScreenshotConfig;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigListScreen extends Screen {
	private final Screen parent;

	public ConfigListScreen(Screen parent) {
		super(Component.literal("Screenshot Configurations"));
		this.parent = parent;
	}

	@Override
	public void init() {
		super.init();

		this.addRenderableWidget(new ConfigListWidget(
				this.minecraft,
				this,
				this.width / 2 - 8,
				this.height - 36 * 2,
				30,
				font.lineHeight * 3
		));

		this.addRenderableWidget(
				Button.builder(Component.translatable("gui.done"),
								button -> this.minecraft.setScreen(this.parent))
						.size(200, 20)
						.pos(width / 2 - 100, height - 32)
						.build()
		);
	}

	@Override
	public void render(
		GuiGraphics graphics,
		int mouseX,
		int mouseY,
		float delta
	) {
		super.render(graphics, mouseX, mouseY, delta);

		graphics.drawCenteredString(
				this.font, this.title, this.width / 4, 15, 0xFFFFFF
		);
	}

	public void updateConfigDetails(ScreenshotConfig config) {

	}
}
