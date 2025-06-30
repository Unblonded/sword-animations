package unblonded.swords;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

public class ConfigCommand implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("swordblocking")
                    .executes(context -> {
                        MinecraftClient client = MinecraftClient.getInstance();
                        client.send(() -> client.setScreen(new ConfigScreen()));
                        return 1;
                    }));
        });
    }
}