package dev.rdh.rust.ui.browser.list;

import dev.rdh.rust.util.gui.RustContainerWidget;
import dev.rdh.rust.util.gui.ImageWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotDetailsWidget extends RustContainerWidget {

	private Path screenshotPath;
	private ImageWidget image;
	private final StringWidget name;

  	private final Button editButton;
	private final Button deleteButton;

	public ScreenshotDetailsWidget(ScreenshotListWidget list, int x, int y, int width, int height) {
		super(x, y, width, height, CommonComponents.EMPTY);

		this.name = this.addChild(new StringWidget(
				x + 5, y + 5, width - 10, 20,
				CommonComponents.EMPTY,
				Minecraft.getInstance().font
		).alignCenter());

		this.editButton = this.addChild(Button.builder(Component.literal("Edit"), button -> {
			if (this.screenshotPath != null) {
				//Minecraft.getInstance().setScreen(new ScreenshotEditScreen(this.screenshotPath));
			}
		})
				.size(width - 10, 20)
				.pos(x + 5, y + height - 32)
				.build());

		this.deleteButton = this.addChild(Button.builder(Component.literal("Delete"), button -> {
			if (this.screenshotPath != null) {
				Files.deleteIfExists(this.screenshotPath);
				this.update(null);
				list.removeSelected();
			}
		})
				.size(width - 10, 20)
				.pos(x + 5, editButton.getY() - 20 - 2)
				.build()
		);

		if (list.getSelected() != null) {
			this.update(list.getSelected().path);
		} else {
			this.update(null);
		}
	}

	public void update(Path path) {
		this.screenshotPath = path;

		if (this.image != null) {
			this.children.remove(image);
		}

		if (path == null) {
			this.screenshotPath = null;
			this.image = null;
			this.name.setMessage(CommonComponents.EMPTY);
			this.editButton.visible = false;
			this.deleteButton.visible = false;
			return;
		}

		this.editButton.visible = true;
		this.deleteButton.visible = true;

		this.image = new ImageWidget(getX(), getY() + 5, 200, 200, path);
		this.image.shrinkToAspectRatio(false);
		this.image.setX(getX() + (getWidth() - this.image.getWidth()) / 2);

		this.addChild(this.image);

		this.name.setMessage(Component.literal(path.getFileName().toString()));
		this.name.setY(this.getY() + image.getHeight() + 5);
	}
}
