package dev.rdh.rust.util;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import dev.rdh.rust.RUST;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public final class ImageCache {
	private static final Map<Path, ResourceLocation> cache = new Object2ObjectLinkedOpenHashMap<>();

	static {
		Thread thread = new Thread(ImageCache::threadRun, "RUST Image Loader");
		thread.setDaemon(true);
		thread.start();
	}

	public static ResourceLocation get(Path path) {
		return cache.computeIfAbsent(path, ImageCache::createResource);
	}

	public static NativeImage getPixels(Path path) {
		ResourceLocation location = get(path);

		AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(location);
		if (texture instanceof DynamicTexture dynamicTexture) {
			return dynamicTexture.getPixels();
		} else {
			// idk why this happens but this fixes it
			cache.remove(path);
			return getPixels(path);
		}
	}

	private static final Map<Path, List<Runnable>> removalCallbacks = new Object2ObjectLinkedOpenHashMap<>();

	public static void onRemoved(Path path, Runnable callback) {
		if (path == null || !Files.exists(path)) {
			throw new IllegalArgumentException("File does not exist: " + path);
		}

		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}

		removalCallbacks.computeIfAbsent(path, k -> new ObjectArrayList<>()).add(callback);
	}

	private static final List<Consumer<Path>> addCallbacks = new ObjectArrayList<>();

	public static void onAdded(Consumer<Path> callback) {
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}

		addCallbacks.add(callback);
	}

	public static void removeAddedCallback(Consumer<Path> callback) {
		if (callback == null) {
			throw new IllegalArgumentException("Callback cannot be null");
		}

		addCallbacks.remove(callback);
	}

	private static ResourceLocation createResource(Path path) {
		if (path == null || !Files.exists(path)) {
			throw new IllegalArgumentException("File does not exist: " + path);
		}

		String filename = path.getFileName().toString();
		ResourceLocation resource = RUST.resource("dynamic_screenshot/" + Integer.toHexString(filename.hashCode()));

		DynamicTexture texture = new DynamicTexture(
				#if MC >= 21.5
				() -> String.valueOf(resource.hashCode()),
				#endif
				NativeImage.read(Files.newInputStream(path))
		);

		Minecraft.getInstance().getTextureManager().register(resource, texture);

		cache.put(path, resource);
		return resource;
	}

	private static void remove(Path path, WatchEvent.Kind<?> kind) {
		ResourceLocation resource = cache.remove(path);
		if (resource != null) {
			Minecraft.getInstance().getTextureManager().release(resource);
			String reason;
			if (kind == ENTRY_DELETE) {
				reason = "it was deleted";
				List<Runnable> callbacks = removalCallbacks.remove(path);
				if (callbacks != null) {
					callbacks.forEach(Runnable::run);
				}
			} else if (kind == ENTRY_MODIFY) {
				reason = "it was modified";
			} else {
				reason = "of unknown reason";
			}

			RUST.LOGGER.info("Removed cached image resource '{}' because {}", resource, reason);
		}
	}

	private static void threadRun() {
		try(WatchService service = FileSystems.getDefault().newWatchService()) {
			Screenshots.DIRECTORY.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

			while (true) {
				WatchKey key = service.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}

					Path path = Screenshots.DIRECTORY.resolve((Path) event.context());
					Runnable task;
					if (event.kind() == ENTRY_CREATE && Files.isRegularFile(path) && path.toString().endsWith(".png")) {
						task = () -> {
							for(Consumer<Path> callback : addCallbacks) {
								callback.accept(path);
							}
						};
					} else if (event.kind() == ENTRY_DELETE || event.kind() == ENTRY_MODIFY) {
						task = () -> remove(path, event.kind());
					} else {
						continue;
					}

					#if MC >= 21.5
					Minecraft.getInstance().schedule(task);
					#else
					Minecraft.getInstance().tell(task);
					#endif
				}
				key.reset();
			}
		}
	}

	private ImageCache() { throw new UnsupportedOperationException(); }
}
