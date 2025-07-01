package unblonded.swords;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Config config = Config.config; // Use getInstance()

    private final List<TextFieldWidget> transformFields = new ArrayList<>();
    private ButtonWidget swordBlockingButton;
    private ButtonWidget customHandRenderButton;
    private ButtonWidget oldHeadButton;

    // Hot reload tracking
    private int hotReloadTimer = 0;
    private final String[] previousText = new String[4];
    private final int[] fieldYPositions = new int[4];
    private static final int HOT_RELOAD_DELAY = 2; // Delay in ticks (0.5 seconds)

    public ConfigScreen() {
        super(Text.literal("Sword Blocking Config Editor"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int y = 40;

        String[] labels = {"Scale", "X", "Y", "Z"};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            TextFieldWidget field = new TextFieldWidget(this.textRenderer, centerX - 50, y, 100, 20, Text.literal(""));
            field.setPlaceholder(Text.literal(labels[i]));
            field.setText(String.valueOf(config.handRenderTransform[i]));
            previousText[i] = field.getText();
            fieldYPositions[i] = y;
            this.addDrawableChild(field);
            transformFields.add(field);
            y += 25;
        }

        swordBlockingButton = ButtonWidget.builder(getToggleText("Sword Blocking", config.swordBlocking), btn -> {
            config.swordBlocking = !config.swordBlocking;
            btn.setMessage(getToggleText("Sword Blocking", config.swordBlocking));
            config.save(); // Save immediately when toggled
        }).dimensions(centerX - 75, y, 150, 20).build();
        this.addDrawableChild(swordBlockingButton);
        y += 25;

        oldHeadButton = ButtonWidget.builder(getToggleText("Old Head Holding", config.oldHead), btn -> {
            config.oldHead = !config.oldHead;
            btn.setMessage(getToggleText("Old Head Holding", config.oldHead));
            config.save();
        }).dimensions(centerX - 75, y, 150, 20).build();
        this.addDrawableChild(oldHeadButton);
        y += 25;

        customHandRenderButton = ButtonWidget.builder(getToggleText("Custom Hand Render", config.customHandRender), btn -> {
            config.customHandRender = !config.customHandRender;
            btn.setMessage(getToggleText("Custom Hand Render", config.customHandRender));
            config.save(); // Save immediately when toggled
        }).dimensions(centerX - 75, y, 150, 20).build();
        this.addDrawableChild(customHandRenderButton);
        y += 30;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> {
            applyChanges();
            this.close();
        }).dimensions(centerX - 50, y, 100, 20).build());
    }

    @Override
    public void tick() {
        super.tick();

        for (int i = 0; i < 4; i++) {
            String current = transformFields.get(i).getText();
            if (!current.equals(previousText[i])) {
                previousText[i] = current;
                hotReloadTimer = HOT_RELOAD_DELAY;
            }
        }

        if (hotReloadTimer > 0) {
            hotReloadTimer--;
            if (hotReloadTimer == 0) {
                applyTransformChanges();
            }
        }
    }

    private void applyTransformChanges() {
        // Apply only text field changes for hot reload
        boolean changed = false;
        for (int i = 0; i < 4; i++) {
            try {
                float newValue = Float.parseFloat(transformFields.get(i).getText());
                if (config.handRenderTransform[i] != newValue) {
                    config.handRenderTransform[i] = newValue;
                    changed = true;
                }
            } catch (NumberFormatException ignored) {
                // Keep original value if parsing fails
            }
        }

        if (changed) {
            config.save();
            System.out.println("Hot reload: Transform values updated");
        }
    }

    private void applyChanges() {
        // Apply text field changes
        for (int i = 0; i < 4; i++) {
            try {
                config.handRenderTransform[i] = Float.parseFloat(transformFields.get(i).getText());
            } catch (NumberFormatException ignored) {
                // Keep original value if parsing fails
            }
        }
        // Save the config after applying changes
        config.save();
    }

    @Override
    public void close() {
        // Apply changes when screen is closed (including ESC key)
        applyChanges();
        super.close();
    }

    private Text getToggleText(String label, boolean value) {
        return Text.literal(label + ": " + (value ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        // Show hot reload status
        if (hotReloadTimer > 0) {
            context.drawTextWithShadow(this.textRenderer, "Updating...", 10, 10, 0x00FF00);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}