package unblonded.swords;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class SwordBlocking implements ClientModInitializer {
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	public static final String MOD_ID = "sword-blocking";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Sword Blocking Mod Initialized");
	}

	public static boolean isEntityBlocking(ClientPlayerEntity player) {
		if (player == null) return false;
		return mc.options.useKey.isPressed() && isSword(player.getMainHandStack().getItem());
	}

	public static Hand getBlockingHand() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return Hand.MAIN_HAND;
		ItemStack main = player.getMainHandStack();
		return isSword(main.getItem()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	public static boolean isSword(Item item) {
		Identifier id = Registries.ITEM.getId(item);
		return id.toString().contains("sword");
	}

	public static Path getConfigPath() {
		return mc.runDirectory.toPath().resolve("config").resolve("swordblocking.json");
	}
}