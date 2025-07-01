package unblonded.swords;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = SwordBlocking.getConfigPath();

    // Lazy initialization - this will auto-initialize when first accessed
    public static final Config config = new Config();

    public float[] handRenderTransform;
    public boolean swordBlocking;
    public boolean customHandRender;
    public boolean oldHead;

    private Config() {
        // Default values
        handRenderTransform = new float[] {1.0f, 0.0f, 0.0f, 0.0f};
        swordBlocking = true;
        customHandRender = false;
        oldHead = false;

        // Load immediately when instance is created
        load();
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

    private void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                Config loaded = GSON.fromJson(json, Config.class);

                // Validate and copy loaded values to this instance
                if (loaded != null) {
                    this.handRenderTransform = loaded.handRenderTransform != null ?
                            loaded.handRenderTransform : new float[] {1.0f, 0.0f, 0.0f, 0.0f};
                    this.swordBlocking = loaded.swordBlocking;
                    this.customHandRender = loaded.customHandRender;
                    this.oldHead = loaded.oldHead;

                    System.out.println("Config loaded successfully!");
                    System.out.println("Loaded values: swordBlocking=" + swordBlocking +
                            ", customHandRender=" + customHandRender +
                            ", transform=" + java.util.Arrays.toString(handRenderTransform));
                } else {
                    System.out.println("Config file was empty or invalid, using defaults");
                    save(); // Create the config file with defaults
                }
            } else {
                System.out.println("No config file found, using defaults");
                save(); // Create the config file with defaults
            }
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
            e.printStackTrace(); // Add stack trace for debugging
            System.out.println("Using default values");
        }
    }
}