package com.replaymod.recording.mixin;

import com.replaymod.recording.ReplayModRecording;
import com.replaymod.recording.packet.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Epic Fight's server->client packets are not captured by the normal recorder.
 * Save every packet Epic Fight sends to a client into the replay (same approach as the ReplayFight mod).
 */
@Pseudo
@Mixin(targets = "yesman.epicfight.network.EpicFightNetworkManager", remap = false)
public abstract class Mixin_EpicFightRecording {

    @Invoker(value = "createVanillaPacket", remap = false)
    private static Packet<?> replaymod$callCreateVanillaPacket(
            Object msg, PacketDistributor.PacketTarget target, Object[] args) {
        throw new AssertionError();
    }

    @Inject(
            method = "sendToClient(Ljava/lang/Object;Lnet/minecraftforge/network/PacketDistributor$PacketTarget;[Ljava/lang/Object;)V",
            at = @At("HEAD"),
            remap = false,
            require = 0
    )
    private static void replaymod$recordEpicFightPacket(
            Object msg, PacketDistributor.PacketTarget target, Object[] args, CallbackInfo ci) {
        // If the ReplayFight mod is installed it already does this; avoid recording everything twice.
        ModList mods = ModList.get();
        if (mods != null && mods.isLoaded("replayfight")) return;

        ReplayModRecording recording = ReplayModRecording.instance;
        if (recording == null || recording.getConnectionEventHandler() == null) return;
        PacketListener listener = recording.getConnectionEventHandler().getPacketListener();
        if (listener == null) return;

        listener.save(replaymod$callCreateVanillaPacket(msg, target, args));
    }
}