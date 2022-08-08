package net.maed.biomestemperature.biome;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.maed.biomestemperature.entity.damage.ModDamageSource;
import net.maed.biomestemperature.networking.Packet;
import net.maed.biomestemperature.util.IPlayerData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class WorldTemperatureManager {
    private static float biomeTemperature = 0;
    private static float ambientTemperature = 0;
    public static float currentAmbientTemperature = 0;
    private static final float  DELTA_HEIGHT = 0.35f;

    public static void update(IPlayerData playerData) {
        PlayerEntity player = (PlayerEntity) playerData;
        World world = player.world;
        if (!world.isClient) {
            Biome biome = world.getBiome(player.getBlockPos()).value();

            biomeTemperature = biome.getTemperature() * 32f;
            int light = world.getLightLevel(LightType.BLOCK, player.getBlockPos());
            int ambientDarkness = world.getAmbientDarkness();
            float heightTemp = DELTA_HEIGHT * (63 - player.getBlockPos().getY());
            int rain = 0;

            if (world.hasRain(player.getBlockPos())) {
                rain = -10;
                if (world.isThundering()) {
                    rain = -15;
                }
            }

            ambientTemperature = biomeTemperature + (float) light - ambientDarkness + playerData.getPersistentData().getFloat("temperature") + heightTemp + rain;

            if (currentAmbientTemperature != ambientTemperature) {
                currentAmbientTemperature = ambientTemperature;
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeFloat(currentAmbientTemperature);
                ServerPlayNetworking.send((ServerPlayerEntity) player, Packet.TEMPERATURE_SYNC, buf);
            }

            int frozenTick = playerData.getCustomFrozenTick();
            if (currentAmbientTemperature <= 0.0) {
                frozenTick = Math.min(player.getMinFreezeDamageTicks(), frozenTick + 1);
            } else {
                frozenTick = Math.max(0, frozenTick - 2);
            }

            playerData.setCustomFrozenTick(frozenTick);
            player.setFrozenTicks(playerData.getCustomFrozenTick());

            if (player.isFrozen()) {
                player.damage(DamageSource.FREEZE, 0.5f);
            }

            int heatTick = playerData.getCustomHeatTick();
            if (currentAmbientTemperature >= 50) {
                heatTick = Math.min(playerData.getMinHeatDamageTicks(), heatTick + 1);
            } else {
                heatTick = Math.max(0, heatTick - 2);
            }

            playerData.setCustomHeatTick(heatTick);

            if (playerData.hasHeat()) {
                player.damage(ModDamageSource.HOT, 0.5f);
            }

            if (heatTick != playerData.getSyncedHeat()) {
                playerData.setSyncedHeat(heatTick);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(playerData.getCustomHeatTick());
                ServerPlayNetworking.send((ServerPlayerEntity) player, Packet.HEAT_SYNC, buf);
            }

            if (frozenTick != playerData.getSyncedFrozen()) {
                playerData.setSyncedFrozen(frozenTick);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(playerData.getCustomFrozenTick());
                ServerPlayNetworking.send((ServerPlayerEntity) player, Packet.FREEZE_SYNC, buf);
            }
        }
    }
}
