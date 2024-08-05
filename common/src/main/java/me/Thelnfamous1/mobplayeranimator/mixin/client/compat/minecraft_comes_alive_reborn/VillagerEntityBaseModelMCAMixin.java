package me.Thelnfamous1.mobplayeranimator.mixin.client.compat.minecraft_comes_alive_reborn;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Pseudo
@Mixin(targets = {"forge/net/mca/client/model/VillagerEntityBaseModelMCA", "fabric/net/mca/client/model/VillagerEntityBaseModelMCA"})
public abstract class VillagerEntityBaseModelMCAMixin {

    @WrapOperation(method = {"m_6973_", "setupAnim", "setAngles"}, at = {
            @At(value = "INVOKE", target = "Lforge/net/mca/entity/ai/brain/VillagerBrain;isPanicking()Z", remap = false),
            @At(value = "INVOKE", target = "Lfabric/net/mca/entity/ai/brain/VillagerBrain;isPanicking()Z", remap = false)})
    private boolean wrap_isPanickingForPanicAnimation(@Coerce Object instance, Operation<Boolean> original, LivingEntity villager, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
        if(PlayerAnimatorHelper.isAnimating(villager)){
            return false;
        }
        return original.call(instance);
    }
}