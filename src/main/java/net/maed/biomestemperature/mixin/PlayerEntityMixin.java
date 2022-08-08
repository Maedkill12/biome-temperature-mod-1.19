package net.maed.biomestemperature.mixin;

import net.maed.biomestemperature.biome.WorldTemperatureManager;
import net.maed.biomestemperature.util.IPlayerData;
import net.maed.biomestemperature.util.IPlayerInventory;
import net.maed.biomestemperature.util.ItemTemperature;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IPlayerData, InventoryChangedListener {

    @Shadow public abstract PlayerInventory getInventory();

    private static final TrackedData<Integer> FROZEN = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> HEAT_TICK = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private NbtCompound persistentData;
    private DataTracker customData;
    private WorldTemperatureManager worldTemperatureManager;

    private int syncedHeat;
    private int syncedFrozen;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo info) {
        IPlayerInventory inventory = (IPlayerInventory) getInventory();
        inventory.addListener(this);

        customData = new DataTracker((PlayerEntity) getInstance());
        customData.startTracking(FROZEN, 0);
        customData.startTracking(HEAT_TICK, 0);

        worldTemperatureManager = new WorldTemperatureManager();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        worldTemperatureManager.update(getInstance());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo info) {
        persistentData = nbt.getCompound("biomestemperature.player_data");
        setCustomFrozenTick(nbt.getInt("frozen"));
        setCustomHeatTick(nbt.getInt("heat"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.put("biomestemperature.player_data", persistentData);
        nbt.putInt("frozen", getCustomFrozenTick());
        nbt.putInt("heat", getCustomHeatTick());
    }

    @Inject(method = "equipStack", at = @At("TAIL"))
    public void equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo info) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            updateTemperature();
        }
    }

    @Override
    public NbtCompound getPersistentData() {
        if (persistentData == null) {
            persistentData = new NbtCompound();
        }

        return persistentData;
    }

    @Override
    public void setCustomFrozenTick(int value) {
        customData.set(FROZEN, value);
    }

    @Override
    public int getCustomFrozenTick() {
        return customData.get(FROZEN);
    }

    @Override
    public int getCustomHeatTick() {
        return customData.get(HEAT_TICK);
    }

    @Override
    public void setCustomHeatTick(int value) {
        customData.set(HEAT_TICK, value);
    }

    @Override
    public int getMinHeatDamageTicks() {
        return 140;
    }

    @Override
    public float getHeatScale() {
        int i = this.getMinHeatDamageTicks();
        return (float)Math.min(this.getCustomHeatTick(), i) / (float)i;
    }

    @Override
    public boolean hasHeat() {
        return getCustomHeatTick() >= getMinHeatDamageTicks();
    }

    @Override
    public int getSyncedHeat() {
        return syncedHeat;
    }

    @Override
    public void setSyncedHeat(int syncedHeat) {
        this.syncedHeat = syncedHeat;
    }

    @Override
    public int getSyncedFrozen() {
        return syncedFrozen;
    }

    @Override
    public void setSyncedFrozen(int syncedFrozen) {
        this.syncedFrozen = syncedFrozen;
    }

    @Override
    public WorldTemperatureManager getTemperatureManager() {
        return worldTemperatureManager;
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        updateTemperature();
    }

    private void updateTemperature() {
        PlayerEntity player = (PlayerEntity) getInstance();
        AtomicReference<Float> temperature = new AtomicReference<>(0.0f);
        player.getInventory().armor.forEach(itemStack -> {
            temperature.set(temperature.get() + ItemTemperature.getTemperature(itemStack));
        });
        getPersistentData().putFloat("temperature", temperature.get());
    }
}
