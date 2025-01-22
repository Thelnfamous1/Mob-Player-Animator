package me.Thelnfamous1.mobplayeranimator.mixin.client;

import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin_Mob<T extends LivingEntity> extends HumanoidModelMixin<T>{

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;copyFrom(Lnet/minecraft/client/model/geom/ModelPart;)V", ordinal = 0))
    private void setEmote(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci){
        if(!(livingEntity instanceof AbstractClientPlayer)){
            PlayerAnimatorHelper.setEmote(this, PlayerAnimatorHelper.getAnimation(livingEntity));
        }
    }
}
