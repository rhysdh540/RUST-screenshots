package dev.rdh.rust.ui.browser;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import dev.rdh.rust.util.gui.ScreenWithParent;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class ScreenshotBrowserScreen extends ScreenWithParent {

	private ScreenshotListWidget list;

	public ScreenshotBrowserScreen(Screen parent) {
		super(parent, Component.literal("Screenshots"));
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
				200,
				this.height - 36 * 2,
				30,
				36,
				allScreenshots
		));
	}
}
