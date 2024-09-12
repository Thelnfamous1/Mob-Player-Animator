package me.Thelnfamous1.mobplayeranimator.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import me.Thelnfamous1.mobplayeranimator.MobPlayerAnimatorClient;
import me.Thelnfamous1.mobplayeranimator.compat.EMFCompat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityRenderDispatcher.class, priority = 900) // Beat EMF's injection
public class EntityRendererDispatcherMixin {

    @ModifyExpressionValue(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;"))
    private <E extends Entity> EntityRenderer<? super E> emf$grabEntity(EntityRenderer<? super E> renderer, E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if(MobPlayerAnimatorClient.isEMFLoaded() && entity instanceof LivingEntity mob && renderer instanceof LivingEntityRenderer<?,?> livingRenderer){
            EMFCompat.lockToVanillaModelFor(mob, livingRenderer.getModel());
        }
        return renderer;
    }
}
