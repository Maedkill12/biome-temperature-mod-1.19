package net.maed.biomestemperature.mixin;

import net.maed.biomestemperature.ModClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin   {

    @Shadow private volatile boolean paused;

    @Inject(method = "tick", at = @At("HEAD"))
    public void onInit(CallbackInfo info) {
        ModClient.modInGameHud.tick(paused);
    }

}
