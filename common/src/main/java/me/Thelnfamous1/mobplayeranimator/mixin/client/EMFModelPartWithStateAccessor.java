package me.Thelnfamous1.mobplayeranimator.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.parts.EMFModelPartWithState;

@Pseudo
@Mixin(EMFModelPartWithState.class)
public interface EMFModelPartWithStateAccessor {

    @Accessor(value = "tryAnimate", remap = false)
    EMFModelPart.Animator mobplayeranimator$getTryAnimate();
}
