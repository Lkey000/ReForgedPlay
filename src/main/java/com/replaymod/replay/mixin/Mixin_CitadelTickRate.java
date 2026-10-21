package com.replaymod.replay.mixin;

import com.replaymod.replay.ReplayModReplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Citadelはクライアントのティック速度を毎ティックtimerへ書き込み、
 * リプレイの一時停止(tickLength=Infinity)を解除してしまう。
 * リプレイ再生中だけ、その処理をスキップする。
 * Citadelが無い環境では、@Pseudoによって何も起きない。
 */
@Pseudo
@Mixin(targets = "com.github.alexthe666.citadel.client.tick.ClientTickRateTracker", remap = false)
public class Mixin_CitadelTickRate {

    @Inject(method = "masterTick", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void replaymod$skipDuringReplay(CallbackInfo ci) {
        if (ReplayModReplay.instance != null && ReplayModReplay.instance.getReplayHandler() != null) {
            ci.cancel();
        }
    }
}