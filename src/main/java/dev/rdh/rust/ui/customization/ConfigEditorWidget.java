package dev.rdh.rust.ui.customization;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

import dev.rdh.rust.customization.CustomScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.customization.ScreenshotManager;
import dev.rdh.rust.customization.VanillaScreenshotConfig;
import dev.rdh.rust.util.gui.AbstractContainerWidget;
import dev.rdh.rust.util.gui.StretchingLabeledWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ConfigEditorWidget extends AbstractContainerWidget {

	private ScreenshotConfig config;

	private final Button enabledButton;
	private final Button deleteButton;
	private final EditBox nameEditor;

	private final StretchingLabeledWidget<Button> keybindButton;
	private boolean keybindSelected;

	private final StretchingLabeledWidget<EditBox> widthEditor;
	private final StretchingLabeledWidget<EditBox> heightEditor;

	private final List<AbstractWidget> children;

	public ConfigEditorWidget(ConfigListScreen screen, int x, int y, int width, int height) {
		super(x, y, width, height, CommonComponents.EMPTY);

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
				.pos(x + 5, 55)
				.size(width - 10, 20)
				.build();

		Predicate<String> intFilter = s -> {
			if (s.isEmpty()) return true;
			if(!s.chars().allMatch(Character::isDigit)) return false;

			int value = Integer.parseInt(s);
			return value >= 1 && value <= 1 << 16;
		};

		widthEditor = StretchingLabeledWidget.containing(new EditBox(font, 0, 0, 75, 20, CommonComponents.EMPTY))
				.label(Component.literal("Width"), font)
				.pos(x + 5, keybindButton.getY() + keybindButton.getHeight() + 5)
				.size(width - 10, 20)
				.build();
		widthEditor.widget.setFilter(intFilter);

		heightEditor = StretchingLabeledWidget.containing(new EditBox(font, 0, 0, 75, 20, CommonComponents.EMPTY))
				.label(Component.literal("Height"), font)
				.pos(x + 5, widthEditor.getY() + widthEditor.getHeight() + 5)
				.size(width - 10, 20)
				.build();
		heightEditor.widget.setFilter(intFilter);

		deleteButton = Button.builder(Component.translatable("selectServer.delete"), b -> {
					ScreenshotManager.ALL_CONFIGS.remove(screen.removeSelected());
		})
				.size(width, 20)
				.pos(x, y + height - 20)
				.build();

		try {
			List<AbstractWidget> children = new ArrayList<>();
			for (Field field : ConfigEditorWidget.class.getDeclaredFields()) {
				if (AbstractWidget.class.isAssignableFrom(field.getType())) {
					AbstractWidget widget = (AbstractWidget) field.get(this);
					if (widget != null) {
						children.add(widget);
					}
				}
			}

			this.children = Collections.unmodifiableList(children);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to access widget fields", t);
		}

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
			widthEditor.widget.setResponder(null);
			widthEditor.widget.setEditable(false);
			widthEditor.widget.active = false;
		} else {
			widthEditor.widget.setResponder(str -> {
				if (!str.isEmpty()) {
					c.setWidth(Integer.parseInt(str));
				}
			});
			widthEditor.widget.setEditable(true);
			widthEditor.widget.active = true;
		}
		widthEditor.widget.setValue(String.valueOf(config.getWidth(w.getWidth())));

		if (vanilla) {
			heightEditor.widget.setResponder(null);
			heightEditor.widget.setEditable(false);
			heightEditor.widget.active = false;
		} else {
			heightEditor.widget.setResponder(str -> {
				if (!str.isEmpty()) {
					c.setHeight(Integer.parseInt(str));
				}
			});
			heightEditor.widget.setEditable(true);
			heightEditor.widget.active = true;
		}
		heightEditor.widget.setValue(String.valueOf(config.getHeight(w.getHeight())));

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

	@Override
	public List<AbstractWidget> children() {
		return children;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		for (AbstractWidget child : children()) {
			child.render(graphics, mouseX, mouseY, partialTick);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		for (AbstractWidget child : children()) {
			child.updateNarration(narrationElementOutput);
		}
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
