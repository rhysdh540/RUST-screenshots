package dev.rdh.rust;

import com.google.common.reflect.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.rdh.rust.customization.ConfigManager;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

#if forge
@net.minecraftforge.fml.common.Mod(RUST.ID)
#elif neoforge
@net.neoforged.fml.common.Mod(RUST.ID)
#endif
public class RUST #if fabric implements net.fabricmc.api.ClientModInitializer #endif {
	public static final String ID = "rust";
	public static final Logger LOGGER = LoggerFactory.getLogger("RUST");

	public static final boolean IS_DEV_ENV =
			#if fabric
			net.fabricmc.loader.api.FabricLoader.getInstance().isDevelopmentEnvironment()
			#elif neoforge
			!net.neoforged.fml.loading.FMLLoader.isProduction()
			#elif forge
			!net.minecraftforge.fml.loading.FMLLoader.isProduction()
			#else
			net.minecraft.SharedConstants.IS_RUNNING_IN_IDE
			#endif;

	public static final Path CONFIG_PATH =
			#if fabric
			net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve(ID);
			#elif neoforge
			net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().resolve(ID);
			#elif forge
			net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get().resolve(ID);
			#else #error "Unsupported platform" #endif

	static {
		try {
			Files.createDirectories(CONFIG_PATH);
			Reflection.initialize(ConfigManager.class);
		} catch (IOException e) {
			LOGGER.error("Could not create config directory", e);
		}
	}

	#if fabric @Override public void onInitializeClient() {
	#else public RUST() {
	#endif
		LOGGER.info("hi!!");
	}

	public static ResourceLocation resource(String path) {
		#if MC >= 21.0
		return ResourceLocation.fromNamespaceAndPath(ID, path);
		#else
		return new ResourceLocation(ID, path);
		#endif
	}
}