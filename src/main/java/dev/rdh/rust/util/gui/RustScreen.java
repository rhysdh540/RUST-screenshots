package dev.rdh.rust.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.Closeable;

public abstract class RustScreen extends Screen {
	protected final Screen parent;

	public RustScreen(Component title) {
		this(Minecraft.getInstance().screen, title);
	}

	public RustScreen(Screen parent, Component title) {
		super(title);
		this.parent = parent;
	}

	#if MC < 21.0
	@Override
	public void render(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(graphics);
		super.render(graphics, mouseX, mouseY, delta);
	}
	#endif

	@Override
	public void onClose() {
		for (var child : this.children()) {
			if (child instanceof Closeable c) {
				c.close();
			}
		}
		this.minecraft.setScreen(this.parent);
	}
}
