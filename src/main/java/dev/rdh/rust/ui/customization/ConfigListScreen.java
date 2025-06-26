package dev.rdh.rust.ui.customization;

import dev.rdh.rust.customization.ScreenshotConfig;
import dev.rdh.rust.ui.customization.ConfigListWidget.ConfigListEntry;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigListScreen extends Screen {
	private final Screen parent;

	private final ConfigEditorHelper editor;
	private ConfigListWidget list;

	public ConfigListScreen(Screen parent) {
		super(Component.literal("Screenshot Configurations"));
		this.parent = parent;
		this.editor = new ConfigEditorHelper(this);
	}

	@Override
	public void init() {
		super.init();

		this.addRenderableOnly(new StringWidget(
				0, 10,
				this.width / 2, 15,
				this.title, this.font
		).alignCenter());

		this.list = new ConfigListWidget(
				this.minecraft,
				this,
				this.width / 2,
				this.height - 36 * 2,
				30,
				font.lineHeight * 3
		);

		this.addRenderableWidget(this.list);

		this.addRenderableWidget(
				Button.builder(CommonComponents.GUI_DONE,
								button -> this.minecraft.setScreen(this.parent))
						.size(200, 20)
						.pos(width / 2 - 100, height - 32)
						.build()
		);

		this.editor.init(this::addRenderableWidget, this.font);
	}

	#if MC < "21.0"
	@Override
	public void render(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(graphics);
		super.render(graphics, mouseX, mouseY, delta);
	}
	#endif

	public void updateConfigDetails(ScreenshotConfig config) {
		this.editor.setConfig(config);
	}

	public ScreenshotConfig removeSelected() {
		ConfigListEntry entry = this.list.getSelected();
		if(entry == null) return null;

		int index = this.list.children().indexOf(entry);
		this.list.children().remove(entry);
		ConfigListEntry newSelection = this.list.children().get(Math.min(index, this.list.children().size() - 1));
		this.list.setSelected(newSelection);
		#if MC >= "21.5"
		this.refreshScrollAmount();
		#elif MC >= "21.0"
		this.list.clampScrollAmount();
		#else
		this.setScrollAmount(this.getScrollAmount());
		#endif

		return entry.config;
	}
}
