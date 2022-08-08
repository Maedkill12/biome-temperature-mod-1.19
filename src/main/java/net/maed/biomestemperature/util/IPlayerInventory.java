package net.maed.biomestemperature.util;

import net.minecraft.inventory.InventoryChangedListener;

public interface IPlayerInventory {
    void addListener(InventoryChangedListener listener);
    default IPlayerInventory getInstance() {
        return this;
    }
}
