package dev.rdh.rust.ui.browser;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import dev.rdh.rust.util.gui.ScreenWithParent;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ScreenshotBrowserScreen extends ScreenWithParent {

	public ScreenshotBrowserScreen(Screen parent) {
		super(parent, Component.literal("Screenshots"));
	}

	@Override
	public void init() {
		Path folder = this.minecraft.gameDirectory.toPath().resolve("screenshots");
		List<Path> allScreenshots = new ReferenceArrayList<>();

		try {
			Files.walk(folder)
					.filter(Files::isRegularFile)
					.filter(f -> f.getFileName().endsWith(".png"))
					.forEach(allScreenshots::add);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}
}
