package net.maed.biomestemperature.util;

import net.maed.biomestemperature.biome.WorldTemperatureManager;
import net.minecraft.nbt.NbtCompound;

public interface IPlayerData {
    default IPlayerData getInstance() {
        return this;
    }

    NbtCompound getPersistentData();
    WorldTemperatureManager getTemperatureManager();

    int getCustomFrozenTick();
    void setCustomFrozenTick(int value);

    int getSyncedFrozen();
    void setSyncedFrozen(int frozen);

    int getCustomHeatTick();
    void setCustomHeatTick(int value);
    int getMinHeatDamageTicks();
    boolean hasHeat();
    float getHeatScale();

    int getSyncedHeat();
    void setSyncedHeat(int heat);
}
