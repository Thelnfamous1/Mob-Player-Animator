package me.Thelnfamous1.mobplayeranimator.mixin.client.compat.customnpcs;

import me.Thelnfamous1.mobplayeranimator.api.HumanoidModelAccess;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.ModelData;
import noppes.npcs.client.model.animation.AnimationHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = AnimationHandler.class, remap = false)
public abstract class AnimationHandlerMixin {

    @Inject(method = "animateBipedPre", at = @At("HEAD"))
    private static void pre_setupAnim(ModelData data, HumanoidModel bipedModel, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci){
        PlayerAnimatorHelper.setDefaultPivot((HumanoidModelAccess) bipedModel);
    }

    @Inject(method = "animateBipedPost", at = @At("TAIL"))
    private static void post_setupAnim(ModelData data, HumanoidModel bipedModel, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci){
        PlayerAnimatorHelper.setEmote(PlayerAnimatorHelper.asMPAModel((HumanoidModelAccess) bipedModel), PlayerAnimatorHelper.getAnimation(livingEntity));
    }

}
