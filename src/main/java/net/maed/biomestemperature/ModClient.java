package net.maed.biomestemperature;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.maed.biomestemperature.client.gui.hud.HudOverlay;
import net.maed.biomestemperature.client.gui.hud.ModInGameHud;
import net.maed.biomestemperature.networking.Packet;
import net.minecraft.client.MinecraftClient;

public class ModClient implements ClientModInitializer {
    public static ModInGameHud modInGameHud;

    @Override
    public void onInitializeClient() {
        modInGameHud = new ModInGameHud(MinecraftClient.getInstance());

        Packet.registerS2CPackets();

        HudRenderCallback.EVENT.register(new HudOverlay());
    }
}
