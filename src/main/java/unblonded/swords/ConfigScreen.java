package unblonded.swords;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Config config = Config.config;

    private final List<TextFieldWidget> transformFields = new ArrayList<>();
    private ButtonWidget swordBlockingButton;
    private ButtonWidget customHandRenderButton;

    public ConfigScreen() {
        super(Text.literal("Config Editor"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int y = 40;

        String[] labels = {"Scale", "X", "Y", "Z"};
        for (int i = 0; i < 4; i++) {
            TextFieldWidget field = new TextFieldWidget(this.textRenderer, centerX - 50, y, 100, 20, Text.literal(""));
            field.setPlaceholder(Text.literal(labels[i]));
            field.setText(String.valueOf(config.handRenderTransform[i]));
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
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}