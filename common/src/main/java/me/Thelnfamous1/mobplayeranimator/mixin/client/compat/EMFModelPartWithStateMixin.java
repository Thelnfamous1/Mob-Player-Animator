package me.Thelnfamous1.mobplayeranimator.mixin.client.compat;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.Thelnfamous1.MobPlayerAnimatorCommonClient;
import me.Thelnfamous1.mobplayeranimator.compat.EMFCompat;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.EMFModelPartWithState;

import java.util.List;
import java.util.Map;

@Pseudo
@Mixin(value = EMFModelPartWithState.class, remap = false)
public abstract class EMFModelPartWithStateMixin extends EMFModelPart {

    public EMFModelPartWithStateMixin(List<Cube> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);
    }

    @WrapWithCondition(method =
            {
                    "render", // Forge and Fabric
                    "m_104306_", // SRG
            },
            remap = true,
            at = @At(value = "INVOKE", target = "Ltraben/entity_model_features/models/EMFModelPart$Animator;run()V", remap = false))
    private boolean onlyTryAnimateIfPlayerAnimatorNotAnimating(EMFModelPart.Animator animator, PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha){
        if(!MobPlayerAnimatorCommonClient.getClientConfig().is_emf_animation_halt_enabled){
            return true;
        }
        return !EMFCompat.isPlayerAnimatorAnimationActive();
    }
}
