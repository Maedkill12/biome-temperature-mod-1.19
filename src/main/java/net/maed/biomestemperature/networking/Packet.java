package net.maed.biomestemperature.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.maed.biomestemperature.BiomesTemperature;
import net.maed.biomestemperature.networking.packet.FreezeUpdateS2CPacket;
import net.maed.biomestemperature.networking.packet.HeatUpdateS2CPacket;
import net.maed.biomestemperature.networking.packet.TemperatureUpdateS2CPacket;
import net.minecraft.util.Identifier;

public class Packet {
    public static final Identifier HEAT_SYNC = new Identifier(BiomesTemperature.MOD_ID, "heat_sync");
    public static final Identifier FREEZE_SYNC = new Identifier(BiomesTemperature.MOD_ID, "freeze_sync");
    public static final Identifier TEMPERATURE_SYNC = new Identifier(BiomesTemperature.MOD_ID, "temperature_sync");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(HEAT_SYNC, HeatUpdateS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(FREEZE_SYNC, FreezeUpdateS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(TEMPERATURE_SYNC, TemperatureUpdateS2CPacket::receive);
    }
}
