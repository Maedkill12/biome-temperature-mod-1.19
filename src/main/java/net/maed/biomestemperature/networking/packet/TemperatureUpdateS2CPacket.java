package net.maed.biomestemperature.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.maed.biomestemperature.biome.WorldTemperatureManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class TemperatureUpdateS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        WorldTemperatureManager.currentAmbientTemperature = buf.readFloat();
    }
}
