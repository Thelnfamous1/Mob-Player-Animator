package me.Thelnfamous1.mobplayeranimator.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import tallestegg.guardvillagers.client.models.GuardModel;
import tallestegg.guardvillagers.entities.Guard;

@Pseudo
@Mixin(value = GuardModel.class, remap = false)
public abstract class GuardModelMixin {

    @WrapWithCondition(method = "setupAnim", remap = false,
            at = @At(value = "INVOKE", target = "Ltallestegg/guardvillagers/client/models/GuardModel;holdWeaponHigh(Ltallestegg/guardvillagers/entities/Guard;)V", remap = false)
    )
    private boolean onlyAnimateWeaponHighIfAllowed(GuardModel model, Guard guard) {
        return !PlayerAnimatorHelper.isAnimating(guard);
    }
}
