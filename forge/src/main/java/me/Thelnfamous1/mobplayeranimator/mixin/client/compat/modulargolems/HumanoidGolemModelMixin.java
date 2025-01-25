package me.Thelnfamous1.mobplayeranimator.mixin.client.compat.modulargolems;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(HumanoidGolemModel.class)
public class HumanoidGolemModelMixin {

    @WrapOperation(method = "setupAnim(Ldev/xkmc/modulargolems/content/entity/humanoid/HumanoidGolemEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Ldev/xkmc/modulargolems/content/entity/humanoid/HumanoidGolemEntity;isAggressive()Z"))
    private boolean wrap_isAggressive_setupAnim(HumanoidGolemEntity instance, Operation<Boolean> original){
        if(PlayerAnimatorHelper.isAnimating(instance)){
            return false;
        }
        return original.call(instance);
    }
}
