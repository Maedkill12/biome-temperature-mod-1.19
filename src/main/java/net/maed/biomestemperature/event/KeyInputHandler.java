package net.maed.biomestemperature.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.maed.biomestemperature.config.Configs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    private static final String KEY_BIOME_TEMPERATURE_CATEGORY = "key.biome.temperature.category";
    private static final String KEY_TEMPERATURE_SCALE = "key.temperature.scale";

    private static KeyBinding temperatureScale;

    private static void handleInput() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (temperatureScale.wasPressed()) {
                Configs.TEMPERATURE_SCALE++;
                Configs.TEMPERATURE_SCALE = Configs.TEMPERATURE_SCALE > 2 ? 0 : Configs.TEMPERATURE_SCALE;
                String scale = Configs.TEMPERATURE_SCALE == 2 ? "K" : Configs.TEMPERATURE_SCALE == 1 ? "°F" : "°C";
                client.player.sendMessage(Text.translatable("temperature.scale.changed", scale));
            }
        });
    }

    public static void register() {
        temperatureScale = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TEMPERATURE_SCALE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEY_BIOME_TEMPERATURE_CATEGORY
        ));

        handleInput();
    }
}
