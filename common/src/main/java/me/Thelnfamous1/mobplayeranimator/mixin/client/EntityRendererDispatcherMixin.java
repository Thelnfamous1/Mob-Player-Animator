package me.Thelnfamous1.mobplayeranimator.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.Thelnfamous1.mobplayeranimator.MobPlayerAnimatorClient;
import me.Thelnfamous1.mobplayeranimator.compat.EMFCompat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class, priority = 900) // Beat EMF's injection
public class EntityRendererDispatcherMixin {

    @Inject(method = "render",
            at = @At(value = "HEAD"))
    private <E extends Entity> void emf$grabEntity(E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if(MobPlayerAnimatorClient.isEMFLoaded() && entity instanceof LivingEntity mob){
            EMFCompat.lockToVanillaModelFor(mob);
        }
    }
}
