package dev.rdh.rust.ui.browser;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import dev.rdh.rust.ui.customization.ConfigListScreen;
import dev.rdh.rust.util.gui.RustScreen;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class ScreenshotBrowserScreen extends RustScreen {

	ScreenshotListWidget list;
	private ScreenshotDetailsWidget details;

	public ScreenshotBrowserScreen() {
		super(Component.literal("Screenshots"));
	}

	@Override
	public void init() {
		List<Path> allScreenshots = new ReferenceArrayList<>();

		Path screenshotsDir = minecraft.gameDirectory.toPath().resolve("screenshots");
		if (!Files.exists(screenshotsDir)) {
			Files.createDirectories(screenshotsDir);
		}

		Files.walk(screenshotsDir)
				.sorted(Comparator.comparingLong(path -> -path.toFile().lastModified()))
				.filter(Files::isRegularFile)
				.filter(path -> path.toString().endsWith(".png"))
				.forEach(allScreenshots::add);

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
				Button.builder(CommonComponents.EMPTY, b -> Util.getPlatform().openFile(screenshotsDir.toFile()))
						.size(20, 20)
						.pos(200 - 20, height - 32)
						.build()
		);

		Button config = this.addRenderableWidget(
				Button.builder(CommonComponents.EMPTY, b -> Minecraft.getInstance().setScreen(new ConfigListScreen()))
						.size(20, 20)
						.pos(openFolder.getX() - padding - 20, height - 32)
						.build()
		);

		this.addRenderableWidget(
				Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
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
