package dev.rdh.rust.ui.customization;

import dev.rdh.rust.customization.CustomScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotManager;
import dev.rdh.rust.customization.VanillaScreenshotConfig;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ConfigEditorHelper {
	private final ConfigListScreen screen;
	private ScreenshotConfig config;

	private EditBox nameEditor;
	private Button enabled;
	private Button delete;

	public ConfigEditorHelper(ConfigListScreen screen) {
		this.screen = screen;
	}

	public void init(Consumer<AbstractWidget> addRenderableWidget, Font font) {
		nameEditor = new EditBox(
				font,
				(screen.width / 2 + 5), 30,
				(screen.width / 2 - 5 - 20 - 5 - 4), 20,
				Component.literal("Name")
		);

		enabled = Button.builder(Component.empty(), b -> {
			config.toggleEnabled();
			enabled.setMessage(Component.literal(config.enabled() ? "O" : "X"));
		})
				.size(20, 20)
				.pos(screen.width - 20 - 5, 30)
				.tooltip(Tooltip.create(Component.literal(config.enabled() ? "Enabled" : "Disabled")))
				.build();

		delete = Button.builder(Component.translatable("selectServer.delete"), b -> {
			ScreenshotManager.ALL_CONFIGS.remove(screen.removeSelected());
		})
				.size(screen.width / 2 - 20, 20)
				.pos(screen.width / 2 + 10, screen.height - 32 * 2)
				.build();

		addRenderableWidget.accept(nameEditor);
		addRenderableWidget.accept(enabled);
		addRenderableWidget.accept(delete);

		setConfig(VanillaScreenshotConfig.INSTANCE);
	}

	public void setConfig(ScreenshotConfig config) {
		this.config = config;

		if (nameEditor != null) {
			if (config instanceof CustomScreenshotConfig c) {
				nameEditor.setResponder(c::setName);
				nameEditor.setEditable(true);
				nameEditor.active = true;
				nameEditor.setHint(
						Component.literal(CustomScreenshotConfig.defaultName(
								config.getWidth(0),
								config.getHeight(0)
						)));
			} else {
				nameEditor.setResponder(null);
				nameEditor.setEditable(false);
				nameEditor.active = false;
			}
			nameEditor.setValue(config.getName());
		}

		if (enabled != null) {
			enabled.setMessage(Component.literal(config.enabled() ? "O" : "X"));
			enabled.setTooltip(Tooltip.create(Component.literal(config.enabled() ? "Enabled" : "Disabled")));
		}

		if (delete != null) {
			delete.active = config instanceof CustomScreenshotConfig;
		}
	}
}
