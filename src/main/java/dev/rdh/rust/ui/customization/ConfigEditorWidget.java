package dev.rdh.rust.ui.customization;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import dev.rdh.rust.customization.CustomScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ConfigManager;
import dev.rdh.rust.customization.VanillaScreenshotConfig;
import dev.rdh.rust.util.gui.RustContainerWidget;
import dev.rdh.rust.util.gui.StretchingLabeledWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
	private final Button deleteButton;
	private final EditBox nameEditor;

	private final StretchingLabeledWidget<Button> keybindButton;
	private boolean keybindSelected;

	private final EditBox widthEditor;
	private final EditBox heightEditor;

	public ConfigEditorWidget(ConfigListScreen screen, int x, int y, int width, int height) {
		super(x, y, width, height);

		Font font = screen.getFont();

		this.config = VanillaScreenshotConfig.INSTANCE;

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
				.pos(x, 55)
				.size(width, 20)
				.build();

		Predicate<String> intFilter = s -> {
			if (s.isEmpty()) return true;
			if(!s.codePoints().allMatch(Character::isDigit)) return false;

			int value = Integer.parseInt(s);
			return value >= 1 && value <= 1 << 16;
		};

		int secondRowY = keybindButton.getY() + keybindButton.getHeight() + 5;
		Component xText = Component.literal(" x ");
		this.addChild(new StringWidget(x - 1, secondRowY + 5, width, font.lineHeight, xText, font));

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

		deleteButton = Button.builder(Component.translatable("selectServer.delete"), b -> {
					ConfigManager.ALL_CONFIGS.remove(screen.removeSelected());
		})
				.size(width, 20)
				.pos(x, y + height - 20)
				.build();

		addChildrenFromFields();

		setConfig(this.config);
	}

	public void setConfig(ScreenshotConfig config) {
		this.config = config;

		boolean vanilla = config == VanillaScreenshotConfig.INSTANCE;
		CustomScreenshotConfig c = vanilla ? null : (CustomScreenshotConfig) config;

		if(vanilla) {
			nameEditor.setResponder(null);
			nameEditor.setEditable(false);
			nameEditor.active = false;
		} else {
			nameEditor.setResponder(c::setName);
			nameEditor.setEditable(true);
			nameEditor.active = true;
			nameEditor.setHint(
					Component.literal(CustomScreenshotConfig.defaultName(
							config.getWidth(0),
							config.getHeight(0)
					)));
		}

		nameEditor.setValue(config.getName());

		refreshEnabledButton();

		Window w = Minecraft.getInstance().getWindow();

		if (vanilla) {
			widthEditor.setResponder(null);
			widthEditor.setEditable(false);
			widthEditor.active = false;
		} else {
			widthEditor.setResponder(str -> {
				if (!str.isEmpty()) {
					c.setWidth(Integer.parseInt(str));
				}
			});
			widthEditor.setEditable(true);
			widthEditor.active = true;
		}
		widthEditor.setValue(String.valueOf(config.getWidth(w.getWidth())));

		if (vanilla) {
			heightEditor.setResponder(null);
			heightEditor.setEditable(false);
			heightEditor.active = false;
		} else {
			heightEditor.setResponder(str -> {
				if (!str.isEmpty()) {
					c.setHeight(Integer.parseInt(str));
				}
			});
			heightEditor.setEditable(true);
			heightEditor.active = true;
		}
		heightEditor.setValue(String.valueOf(config.getHeight(w.getHeight())));

		deleteButton.active = !vanilla;

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
