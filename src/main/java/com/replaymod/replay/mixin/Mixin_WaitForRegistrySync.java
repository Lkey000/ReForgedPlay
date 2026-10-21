package com.replaymod.replay.mixin;

import com.replaymod.replay.util.ReplayPacketGate;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(GameData.class)
public class Mixin_WaitForRegistrySync {

    @Inject(method = "injectSnapshot", at = @At("RETURN"), remap = false)
    private static void onInjectSnapshotComplete(
            Map snapshot, boolean isLocalWorld, boolean isRemote,
            CallbackInfoReturnable<Object> cir) {
        ReplayPacketGate.markRegistrySyncComplete();
    }
}