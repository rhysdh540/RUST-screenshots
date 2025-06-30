package dev.rdh.rust.util.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ScreenWithParent extends Screen {
	protected final Screen parent;

	public ScreenWithParent(Screen parent, Component title) {
		super(title);
		this.parent = parent;
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}
}
