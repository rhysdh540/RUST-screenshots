package dev.rdh.rust.ui.browser;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import dev.rdh.rust.ui.customization.ConfigListScreen;
import dev.rdh.rust.util.gui.RustScreen;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractScreenshotBrowserScreen extends RustScreen {
	protected final List<Path> allScreenshots;
	protected final Path screenshotsDir = Minecraft.getInstance().gameDirectory.toPath().resolve("screenshots");

	protected AbstractScreenshotBrowserScreen() {
		super(Component.literal("Screenshots"));

		allScreenshots = new ReferenceArrayList<>();

		if(!Files.exists(screenshotsDir)) {
			Files.createDirectories(screenshotsDir);
		}

		Files.walk(screenshotsDir)
				.sorted(Comparator.comparingLong(path -> -path.toFile().lastModified()))
				.filter(Files::isRegularFile)
				.filter(path -> path.toString().endsWith(".png"))
				.forEach(allScreenshots::add);


	}

	protected final Button.Builder openFolderButton() {
		return Button.builder(CommonComponents.EMPTY, b -> Util.getPlatform().openFile(screenshotsDir.toFile()));
	}

	protected final Button.Builder openConfigButton() {
		return Button.builder(CommonComponents.EMPTY, b -> Minecraft.getInstance().setScreen(new ConfigListScreen()));
	}
}
