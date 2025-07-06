package dev.rdh.rust.ui.browser;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import dev.rdh.rust.ui.customization.ConfigListScreen;
import dev.rdh.rust.util.Screenshots;
import dev.rdh.rust.util.gui.RustScreen;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;

public abstract class AbstractScreenshotBrowserScreen extends RustScreen {
	protected final Set<Path> allScreenshots;

	protected AbstractScreenshotBrowserScreen() {
		super(Component.literal("Screenshots"));
		allScreenshots = new ObjectLinkedOpenHashSet<>();
	}

	@Override
	public void init() {
		if(!Files.exists(Screenshots.DIRECTORY)) {
			Files.createDirectories(Screenshots.DIRECTORY);
		}

		Files.walk(Screenshots.DIRECTORY)
				.sorted(Comparator.comparingLong(path -> -path.toFile().lastModified()))
				.filter(Files::isRegularFile)
				.filter(path -> path.toString().endsWith(".png"))
				.forEach(allScreenshots::add);
	}

	protected final Button.Builder openFolderButton() {
		return Button.builder(CommonComponents.EMPTY, b -> Util.getPlatform().openFile(Screenshots.DIRECTORY.toFile()));
	}

	protected final Button.Builder openConfigButton() {
		return Button.builder(CommonComponents.EMPTY, b -> Minecraft.getInstance().setScreen(new ConfigListScreen()));
	}
}
