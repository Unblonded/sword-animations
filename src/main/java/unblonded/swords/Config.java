package unblonded.swords;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public static final Config config = new Config();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = SwordBlocking.getConfigPath();

    public float[] handRenderTransform;
    public boolean swordBlocking;
    public boolean customHandRender;

    public Config() {
        // Default values
        handRenderTransform = new float[] {1.0f, 0.0f, 0.0f, 0.0f};
        swordBlocking = true;
        customHandRender = false;
        load(); // Load saved settings on creation
    }

    public void save() {
        try {
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json);
            System.out.println("Config saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                Config loaded = GSON.fromJson(json, Config.class);

                // Copy loaded values to this instance
                this.handRenderTransform = loaded.handRenderTransform != null ? loaded.handRenderTransform : new float[] {1.0f, 0.0f, 0.0f, 0.0f};
                this.swordBlocking = loaded.swordBlocking;
                this.customHandRender = loaded.customHandRender;

                System.out.println("Config loaded successfully!");
            } else {
                System.out.println("No config file found, using defaults");
                save(); // Create the config file with defaults
            }
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
            System.out.println("Using default values");
        }
    }
}