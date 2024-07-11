package me.Thelnfamous1.mobplayeranimator.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.Thelnfamous1.mobplayeranimator.MobPlayerAnimator;
import me.Thelnfamous1.mobplayeranimator.compat.EMFCompat;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<
        T extends LivingEntity,
        M extends EntityModel<T>
        >
        extends EntityRenderer<T>
        implements RenderLayerParent<T, M> {
    @Shadow protected M model;

    @Shadow public abstract M getModel();

    protected LivingEntityRendererMixin(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Inject(method = "setupRotations", at = @At("RETURN"))
    private void post_setupRotations(T entity, PoseStack matrixStack, float $$2, float $$3, float tickDelta, CallbackInfo ci){
        if(entity instanceof Mob mob){
            this.mobplayeranimator$applyBodyRotations(mob, matrixStack, tickDelta);
        }
    }

    @Unique
    protected void mobplayeranimator$applyBodyRotations(Mob entity, PoseStack matrixStack, float tickDelta) {
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", shift = At.Shift.BEFORE))

    protected void pre_renderToBuffer(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5, CallbackInfo ci) {
        if(MobPlayerAnimator.isEMFLoaded()){
            EMFCompat.pauseEMFAnimationsFor($$0, this.getModel());
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))

    protected void post_renderToBuffer(T $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5, CallbackInfo ci) {
        if(MobPlayerAnimator.isEMFLoaded()){
            EMFCompat.resumeEMFAnimationsFor($$0, this.getModel());
        }
    }
}
