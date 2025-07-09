package dev.rdh.rust.ui.customization;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import dev.rdh.rust.customization.CustomScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.ui.customization.ConfigListWidget.ConfigListEntry;
import dev.rdh.rust.util.gui.RustScreen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

public class ConfigListScreen extends RustScreen {

	private ConfigEditorWidget editor;
	private ConfigListWidget list;

	public ConfigListScreen() {
		super(Component.literal("Screenshot Configurations"));
	}

	@Override
	public void init() {
		this.addRenderableOnly(new StringWidget(
				0, 10,
				this.width / 2, 15,
				this.title, this.font
		).alignCenter());

		this.list = addRenderableWidget(new ConfigListWidget(
				this.minecraft,
				this,
				this.width / 2,
				this.height - 36 * 2,
				30,
				font.lineHeight * 3
		));

		this.addRenderableWidget(
				doneButton()
						.size(200, 20)
						.pos(width / 2 - 100, height - 32)
						.build()
		);

		this.addRenderableWidget(
				Button.builder(Component.literal("+"), b -> {
							Window w = this.minecraft.getWindow();
							ScreenshotConfig config = new CustomScreenshotConfig(null, InputConstants.UNKNOWN, w.getWidth(), w.getHeight());
							this.list.add(config);
						})
						.size(18, 18)
						.pos(width / 2 - 20 - 5, 8)
						.build()
		);

		this.editor = addRenderableWidget(new ConfigEditorWidget(
				this,
				this.width / 2 + 5,
				30,
				this.width / 2 - 10,
				this.height - 36 * 2
		));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.editor.onKeyPress(keyCode, scanCode)) {
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.editor.onMouseClick(button)) {
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	public void updateConfigDetails(ScreenshotConfig config) {
		if (editor != null) {
			editor.setConfig(config);
		}
	}

	public Font getFont() {
		return this.font;
	}

	public ScreenshotConfig removeSelected() {
		ConfigListEntry entry = this.list.removeSelected();
		if (entry != null) {
			return entry.config;
		} else {
			return null;
		}
	}

	public ScreenshotConfig getSelected() {
		ConfigListEntry e = this.list.getSelected();
		if (e != null) {
			return e.config;
		} else {
			return null;
		}
	}
}
