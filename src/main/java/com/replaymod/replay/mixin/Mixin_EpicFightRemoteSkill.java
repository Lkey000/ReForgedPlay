package com.replaymod.replay.mixin;

import com.replaymod.replay.ReplayModReplay;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Epic Fight's SkillContainer#setSkill rejects every non-local player on the client.
 * In a replay the recorded player is a remote player, so the skills sent by
 * SPChangeSkill were silently dropped. Treat the replayed player as local while a replay is open.
 */
@Pseudo
@Mixin(targets = "yesman.epicfight.skill.SkillContainer", remap = false)
public abstract class Mixin_EpicFightRemoteSkill {
    @Redirect(
            method = "setSkill(Lyesman/epicfight/skill/Skill;Z)Z",
            at = @At(
                    value = "INVOKE",
                    // Epic Fight's own bytecode calls Player#isLocalPlayer by its SRG name
                    target = "Lnet/minecraft/world/entity/player/Player;m_7578_()Z",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private boolean replaymod$treatReplayPlayerAsLocal(PlayerEntity player) {
        return player.isMainPlayer() || ReplayModReplay.instance.getReplayHandler() != null;
    }
}