package dev.rdh.rust.ui.browser.list;

import dev.rdh.rust.ui.browser.AbstractScreenshotBrowserScreen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;

import java.nio.file.Path;

public class ScreenshotGridScreen extends AbstractScreenshotBrowserScreen {

	ScreenshotListWidget list;
	private ScreenshotDetailsWidget details;

	@Override
	public void init() {
		super.init();
		this.list = this.addRenderableWidget(new ScreenshotListWidget(
				this.minecraft,
				this,
				200,
				this.height - 32 * 2,
				20,
				36,
				allScreenshots
		));

		this.addRenderableOnly(new StringWidget(
				0, 7,
				this.width / 2, 10,
				this.title, this.font
		).alignCenter());

		int padding = 2;

		Button openFolder = this.addRenderableWidget(
				openFolderButton()
						.size(20, 20)
						.pos(200 - 20, height - 32)
						.build()
		);

		Button config = this.addRenderableWidget(
				openConfigButton()
						.size(20, 20)
						.pos(openFolder.getX() - padding - 20, height - 32)
						.build()
		);

		this.addRenderableWidget(
				doneButton()
						.size(config.getX() - 2 * padding, 20)
						.pos(padding, height - 32)
						.build()
		);

		this.details = this.addRenderableWidget(new ScreenshotDetailsWidget(
				this.list,
				this.width / 2, 0,
				this.width / 2, this.height
		));
	}

	public void updateSelected(Path path) {
		if (this.details != null) {
			this.details.update(path);
		}
	}
}
