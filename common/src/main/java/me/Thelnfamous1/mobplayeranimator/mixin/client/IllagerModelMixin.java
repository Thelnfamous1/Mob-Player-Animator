package me.Thelnfamous1.mobplayeranimator.mixin.client;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IPlayerModel;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import me.Thelnfamous1.mobplayeranimator.api.IllagerModelAccess;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractIllager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = IllagerModel.class, priority = 2000) //apply after most modded injections
public abstract class IllagerModelMixin<T extends AbstractIllager> extends HierarchicalModelMixin<T> implements IPlayerModel, IMutableModel, IllagerModelAccess {
    @Shadow @Final private ModelPart head;
    @Shadow @Final private ModelPart hat;
    @Shadow @Final private ModelPart leftArm;
    @Shadow @Final private ModelPart rightArm;
    @Shadow @Final private ModelPart rightLeg;
    @Shadow @Final private ModelPart leftLeg;
    @Shadow @Final private ModelPart arms;
    @Unique
    private SetableSupplier<AnimationProcessor> mobplayeranimator$animation = new SetableSupplier<>();
    @Unique
    private final SetableSupplier<AnimationProcessor> mobplayeranimator$emoteSupplier = new SetableSupplier<>();
    @Unique
    private boolean mobplayeranimator$firstPersonNext = false;
    @Unique
    private ModelPart mobplayeranimator$body;

    public IllagerModelMixin(Function<ResourceLocation, RenderType> $$0) {
        super($$0);
    }

    @Override
    public void setEmoteSupplier(SetableSupplier<AnimationProcessor> emoteSupplier) {
        this.mobplayeranimator$animation = emoteSupplier;
    }

    @Override
    public SetableSupplier<AnimationProcessor> getEmoteSupplier(){
        return this.mobplayeranimator$animation;
    }

    @Override
    protected boolean mobplayeranimator$bendAnimation(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if(Helper.isBendEnabled() && this.mobplayeranimator$animation.get() != null && this.mobplayeranimator$animation.get().isActive()){
            this.mobplayeranimator$headParts().forEach((part)->{
                if(! ((IUpperPartHelper)(Object)part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            this.mobplayeranimator$bodyParts().forEach((part)->{
                if(! ((IUpperPartHelper)(Object)part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });

            SetableSupplier<AnimationProcessor> emoteSupplier = this.mobplayeranimator$animation;
            matrices.pushPose();
            IBendHelper.rotateMatrixStack(matrices, emoteSupplier.get().getBend("body"));
            this.mobplayeranimator$headParts().forEach((part)->{
                if(((IUpperPartHelper)(Object)part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            this.mobplayeranimator$bodyParts().forEach((part)->{
                if(((IUpperPartHelper)(Object)part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            matrices.popPose();
            return true;
        }
        return false;
    }

    @Unique
    private Iterable<ModelPart> mobplayeranimator$headParts() {
        return ImmutableList.of(this.head);
    }

    @Unique
    private Iterable<ModelPart> mobplayeranimator$bodyParts() {
        return ImmutableList.of(this.mobplayeranimator$body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
    }

    @Override
    protected void mobplayeranimator$copyMutatedAttributes(EntityModel<T> otherModel) {
        if(this.mobplayeranimator$animation != null) {
            ((IMutableModel) otherModel).setEmoteSupplier(this.mobplayeranimator$animation);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBendableStuff(ModelPart root, CallbackInfo ci){
        // Copied from PlayerAnimator's BipedEntityModelMixin#initBend
        IBendHelper.INSTANCE.initBend(root.getChild("body"), Direction.DOWN);
        IBendHelper.INSTANCE.initBend(root.getChild("right_arm"), Direction.UP);
        IBendHelper.INSTANCE.initBend(root.getChild("left_arm"), Direction.UP);
        IBendHelper.INSTANCE.initBend(root.getChild("right_leg"), Direction.UP);
        IBendHelper.INSTANCE.initBend(root.getChild("left_leg"), Direction.UP);
        ((IUpperPartHelper)(Object)rightArm).setUpperPart(true);
        ((IUpperPartHelper)(Object)leftArm).setUpperPart(true);
        ((IUpperPartHelper)(Object)head).setUpperPart(true);
        ((IUpperPartHelper)(Object)hat).setUpperPart(true);

        // Copied from PlayerAnimator's PlayerModelMixin#initBendableStuff
        mobplayeranimator$emoteSupplier.set(null);

        this.setEmoteSupplier(mobplayeranimator$emoteSupplier);

        // IllagerModel does not store the "body" ModelPart as a field
        this.mobplayeranimator$body = root.getChild("body");
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/monster/AbstractIllager;FFFFF)V", at = @At(value = "HEAD"))
    private void setDefaultBeforeRender(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci){
        //to not make everything wrong
        PlayerAnimatorHelper.setDefaultPivot(this.head, this.mobplayeranimator$body, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
    }

    @WrapWithCondition(method = "setupAnim",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZFF)V")
    )
    private boolean onlyAnimateZombieArmsIfAllowed(ModelPart leftArm, ModelPart rightArm, boolean aggressive, float attackTime, float bob,
                                                   T illager,
                                                   float $$1,
                                                   float $$2,
                                                   float $$3,
                                                   float $$4,
                                                   float $$5) {
        return !PlayerAnimatorHelper.isAnimating(illager);
    }

    @WrapWithCondition(method = "setupAnim",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/AnimationUtils;swingWeaponDown(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/world/entity/Mob;FF)V")
    )
    private boolean onlyAnimateWeaponSwingIfAllowed(ModelPart rightArm, ModelPart leftArm, Mob mob, float attackTime, float bob) {
        return !PlayerAnimatorHelper.isAnimating(mob);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/monster/AbstractIllager;FFFFF)V", at = @At("TAIL"))
    private void setEmote(T illager, float $$1, float $$2, float $$3, float $$4, float $$5, CallbackInfo ci){
        if(!mobplayeranimator$firstPersonNext && PlayerAnimatorHelper.isAnimating(illager)){
            this.arms.visible = false;
            this.leftArm.visible = true;
            this.rightArm.visible = true;

            AnimationApplier emote = PlayerAnimatorHelper.getAnimation(illager);
            mobplayeranimator$emoteSupplier.set(emote);

            emote.updatePart("head", this.head);
            this.hat.copyFrom(this.head);

            emote.updatePart("leftArm", this.leftArm);
            emote.updatePart("rightArm", this.rightArm);
            emote.updatePart("leftLeg", this.leftLeg);
            emote.updatePart("rightLeg", this.rightLeg);
            emote.updatePart("torso", this.mobplayeranimator$body);


        }
        else {
            mobplayeranimator$firstPersonNext = false;
            mobplayeranimator$emoteSupplier.set(null);
            PlayerAnimatorHelper.resetBend(this.mobplayeranimator$body);
            PlayerAnimatorHelper.resetBend(this.leftArm);
            PlayerAnimatorHelper.resetBend(this.rightArm);
            PlayerAnimatorHelper.resetBend(this.leftLeg);
            PlayerAnimatorHelper.resetBend(this.rightLeg);
        }
    }

    @Override
    public void playerAnimator_prepForFirstPersonRender() {
        mobplayeranimator$firstPersonNext = true;
    }

    @Override
    public ModelPart mobplayeranimator$getBody() {
        return this.mobplayeranimator$body;
    }

    @Override
    public ModelPart mobplayeranimator$getLeftArm() {
        return this.leftArm;
    }

    @Override
    public ModelPart mobplayeranimator$getRightArm() {
        return this.rightArm;
    }

    @Override
    public ModelPart mobplayeranimator$getLeftLeg() {
        return this.leftLeg;
    }

    @Override
    public ModelPart mobplayeranimator$getRightLeg() {
        return this.rightLeg;
    }

    @Override
    public ModelPart mobplayeranimator$getArms() {
        return this.arms;
    }
}
