package net.maed.biomestemperature.mixin;

import net.maed.biomestemperature.util.IPlayerInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin implements IPlayerInventory {
    public List<InventoryChangedListener> listeners;

    @Override
    public void addListener(InventoryChangedListener listener) {
        if (listeners == null) {
            listeners = Lists.newArrayList();
        }
        listeners.add(listener);
    }

    @Inject(method = "markDirty", at = @At("TAIL"))
    protected void onMarkDirty(CallbackInfo info) {
        if (this.listeners != null) {
            for (InventoryChangedListener inventoryChangedListener : this.listeners) {
                inventoryChangedListener.onInventoryChanged((Inventory) getInstance());
            }
        }
    }
}
