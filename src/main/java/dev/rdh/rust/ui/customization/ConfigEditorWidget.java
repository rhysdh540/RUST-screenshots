package dev.rdh.rust.ui.customization;

import com.mojang.blaze3d.platform.InputConstants;

import dev.rdh.rust.customization.CustomScreenshotConfig;
import dev.rdh.rust.customization.ScaledScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ConfigManager;
import dev.rdh.rust.util.gui.RustContainerWidget;
import dev.rdh.rust.util.gui.StretchingLabeledWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public class ConfigEditorWidget extends RustContainerWidget {

	private ScreenshotConfig config;

	private final Button enabledButton;
	private final EditBox nameEditor;

	private final StretchingLabeledWidget<Button> keybindButton;
	private boolean keybindSelected;

	private final StringWidget xWidget;
	private final EditBox widthEditor;
	private final EditBox heightEditor;
	private final StretchingLabeledWidget<EditBox> scaleEditor;

	public ConfigEditorWidget(ConfigListScreen screen, int x, int y, int width, int height) {
		super(x, y, width, height);

		this.config = screen.getSelected();

		Font font = screen.getFont();

		this.nameEditor = new EditBox(font, x, y, width - 20 - 5, 20, CommonComponents.EMPTY);

		enabledButton = Button.builder(CommonComponents.EMPTY, b -> {
			config.toggleEnabled();
			refreshEnabledButton();
		})
				.size(20, 20)
				.pos(x + width - 20, y)
				.build();

		Button button = Button.builder(CommonComponents.EMPTY, b -> {
					keybindSelected = true;
					KeyMapping.resetMapping();
					refreshKeybindButton();
		})
				.size(75, 20)
				.build();

		keybindButton = StretchingLabeledWidget.containing(button)
				.label(Component.literal("Keybind:"), font)
				.pos(x + 2, 55)
				.size(width - 2, 20)
				.build();

		Predicate<String> intFilter = s -> {
			if (s.isEmpty()) return true;
			if(!s.codePoints().allMatch(Character::isDigit)) return false;

			int value = Integer.parseInt(s);
			return value >= 1 && value <= 1 << 16;
		};

		int secondRowY = keybindButton.getY() + keybindButton.getHeight() + 5;
		Component xText = Component.literal(" x ");
		xWidget = new StringWidget(x - 1, secondRowY + 5, width, font.lineHeight, xText, font);

		int middleX = x + (width / 2);
		int middleXBack = middleX - (font.width(xText) / 2);
		int middleXFront = middleX + (font.width(xText) / 2);

		widthEditor = new EditBox(font, x, secondRowY, middleXBack - this.getX(), 20, CommonComponents.EMPTY);
		widthEditor.setFilter(intFilter);
		widthEditor.setResponder(str -> {
			if (!str.isEmpty()) {
				CustomScreenshotConfig c = (CustomScreenshotConfig) config;
				c.setWidth(Integer.parseInt(str));
			}
		});

		heightEditor = new EditBox(font, middleXFront, secondRowY, x + width - middleXFront, 20, CommonComponents.EMPTY);
		heightEditor.setFilter(intFilter);
		heightEditor.setResponder(str -> {
			if (!str.isEmpty()) {
				CustomScreenshotConfig c = (CustomScreenshotConfig) config;
				c.setHeight(Integer.parseInt(str));
			}
		});

		scaleEditor = StretchingLabeledWidget.containing(new EditBox(font, 0, 0, 75, 20, CommonComponents.EMPTY))
				.label(Component.literal("Scale:"), font)
				.pos(x + 2, secondRowY)
				.size(width - 2, 20)
				.build();
		scaleEditor.widget.setFilter(str -> {
			if (str.isEmpty()) return true;
			return str.matches("\\d*\\.?\\d{0,2}") && Float.parseFloat(str) <= 10.0;
		});
		scaleEditor.widget.setResponder(str -> {
			if (!str.isEmpty()) {
				ScaledScreenshotConfig s = (ScaledScreenshotConfig) config;
				s.setScale(Float.parseFloat(str));
			}
		});

		this.addChild(
				Button.builder(Component.translatable("selectServer.delete"),
								b -> ConfigManager.ALL_CONFIGS.remove(screen.removeSelected()))
						.size(width, 20)
						.pos(x, y + height - 20)
						.build()
		);

		addChildrenFromFields();

		setConfig(this.config);
	}

	public void setConfig(ScreenshotConfig config) {
		this.config = config;

		if (config == null) {
			this.visible = false;
			return;
		} else {
			this.visible = true;
		}

		nameEditor.setResponder(config::setName);
		nameEditor.setEditable(true);
		nameEditor.active = true;
		nameEditor.setHint(Component.literal(config.getDefaultName()));

		nameEditor.setValue(config.getName());

		refreshEnabledButton();

		if (config instanceof CustomScreenshotConfig c) {
			xWidget.visible = true;
			widthEditor.setValue(String.valueOf(c.getWidth()));
			widthEditor.visible = true;
			heightEditor.setValue(String.valueOf(c.getHeight()));
			heightEditor.visible = true;
		} else {
			xWidget.visible = false;
			widthEditor.visible = false;
			heightEditor.visible = false;
		}

		if (config instanceof ScaledScreenshotConfig s) {
			scaleEditor.widget.setValue(String.valueOf(s.getScale()));
			scaleEditor.visible = true;
		} else {
			scaleEditor.visible = false;
		}

		keybindSelected = false;
		refreshKeybindButton();

		this.setFocused(null);
	}

	private void refreshEnabledButton() {
		enabledButton.setMessage(Component.literal(config.enabled() ? "O" : "X"));
		enabledButton.setTooltip(Tooltip.create(Component.literal(config.enabled() ? "Enabled" : "Disabled")));
	}

	private void refreshKeybindButton() {
		Component c = config.key().getTranslatedKeyMessage();
		if (keybindSelected) {
			c = Component.literal("> ")
					.append(c.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
					.append(Component.literal(" <"))
					.withStyle(ChatFormatting.YELLOW);
		}

		keybindButton.widget.setMessage(c);
	}

	public boolean onMouseClick(int button) {
		if (keybindSelected) {
			config.key().setKey(InputConstants.Type.MOUSE.getOrCreate(button));
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
