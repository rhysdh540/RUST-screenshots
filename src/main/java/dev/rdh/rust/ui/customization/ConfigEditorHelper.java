package dev.rdh.rust.ui.customization;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;

import dev.rdh.rust.customization.CustomScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotManager;
import dev.rdh.rust.customization.VanillaScreenshotConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
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

	private Button keybindButton;
	private boolean keybindSelected;

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

		keybindButton = Button.builder(config.key().getTranslatedKeyMessage(), b -> {
					keybindSelected = true;
					KeyMapping.resetMapping();
					refreshKeybindButton();
				})
				.size(screen.width / 2 - 20, 20)
				.pos(screen.width / 2 + 10, 50)
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
		addRenderableWidget.accept(keybindButton);

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

		keybindSelected = false;
		refreshKeybindButton();
	}

	private void refreshKeybindButton() {
		if(keybindButton == null) return;

		Component c = config.key().getTranslatedKeyMessage();
		if (keybindSelected) {
			c = Component.literal("> ")
					.append(c.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
					.append(Component.literal(" <"))
					.withStyle(ChatFormatting.YELLOW);
		}

		keybindButton.setMessage(c);
	}

	public boolean onMouseClick(int button) {
		if (keybindSelected) {
			config.key().setKey(Type.MOUSE.getOrCreate(button));
			keybindSelected = false;
			refreshKeybindButton();
			return true;
		}
		return false;
	}

	public boolean onKeyPress(int keyCode, int scanCode) {
		if (keybindSelected) {
			if (keyCode == InputConstants.KEY_ESCAPE) {
				config.key().setKey(InputConstants.UNKNOWN);
			} else {
				config.key().setKey(InputConstants.getKey(keyCode, scanCode));
			}

			keybindSelected = false;
			refreshKeybindButton();
			return true;
		}
		return false;
	}
}
