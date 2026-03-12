package committee.nova.mods.dg;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "globedimension";
	public static final String MOD_NAME = "Chunk-In-A-Globe";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	// 1.21.1: new ResourceLocation(ns, path) constructor is private.
	// Use ResourceLocation.fromNamespaceAndPath() instead.
	public static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
