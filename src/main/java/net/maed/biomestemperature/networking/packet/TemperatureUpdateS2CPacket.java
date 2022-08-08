package net.maed.biomestemperature.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.maed.biomestemperature.util.IPlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class TemperatureUpdateS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        IPlayerData playerData = (IPlayerData) client.player;
        if (playerData != null) {
            playerData.getTemperatureManager().currentAmbientTemperature = buf.readFloat();
        }
    }
}
