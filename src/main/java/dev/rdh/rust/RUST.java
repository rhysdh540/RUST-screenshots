package dev.rdh.rust;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

#if forge
@net.minecraftforge.fml.common.Mod(RUST.ID)
#elif neoforge
@net.neoforged.fml.common.Mod(RUST.ID)
#endif
public class RUST #if fabric implements net.fabricmc.api.ClientModInitializer #endif {
	public static final String ID = "rust";
	public static final Logger LOGGER = LoggerFactory.getLogger("RUST");

	#if fabric @Override public void onInitializeClient() {
	#else public RUST() {
	#endif
		LOGGER.info("hi!!");
	}

	public static ResourceLocation resource(String path) {
		#if MC >= "21.0"
		return ResourceLocation.fromNamespaceAndPath(ID, path);
		#else
		return new ResourceLocation(ID, path);
		#endif
	}
}