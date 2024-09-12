package me.Thelnfamous1.mobplayeranimator.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.*;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import me.Thelnfamous1.mobplayeranimator.api.part.MPABodyPart;
import me.Thelnfamous1.mobplayeranimator.mixin.AnimationStackAccessor;
import me.Thelnfamous1.mobplayeranimator.platform.Services;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractIllager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class PlayerAnimatorHelper {

    /**
     * Convenience method to retrieve the animation of a player or mob
     *
     * @param entity A player or mob
     * @return The animation of the player or mob, or null if neither
     */
    @Nullable
    public static AnimationApplier getAnimation(LivingEntity entity){
        if(entity instanceof IAnimatedPlayer animatedPlayer){
            return animatedPlayer.playerAnimator_getAnimation();
        }
        return null;
    }

    /**
     * Convenience method to determine if a player or mob is animating
     * @param entity A player or mob
     * @return Whether the player or mob is animating
     */
    public static boolean isAnimating(LivingEntity entity){
        AnimationApplier animation = getAnimation(entity);
        return animation != null && animation.isActive();
    }

    /**
     * Called at the end of {@link net.minecraft.client.renderer.entity.LivingEntityRenderer#setupRotations(LivingEntity, PoseStack, float, float, float)}
     *
     * @param animation The animation to be applied to the model being rotated by the renderer
     * @param matrixStack The PoseStack of the model being rotated by the renderer
     * @param tickDelta The change in tick provided to the renderer
     */
    public static void applyBodyRotations(@Nullable AnimationApplier animation, PoseStack matrixStack, float tickDelta) {
        if(animation == null) return;

        animation.setTickDelta(tickDelta);
        if(animation.isActive()){
            //These are additive properties
            Vec3f position = animation.get3DTransform("body", TransformType.POSITION, Vec3f.ZERO);
            matrixStack.translate(position.getX(), position.getY() + 0.7, position.getZ());
            Vec3f rotation = animation.get3DTransform("body", TransformType.ROTATION, Vec3f.ZERO);
            matrixStack.mulPose(Axis.ZP.rotation(rotation.getZ()));    //roll
            matrixStack.mulPose(Axis.YP.rotation(rotation.getY()));    //pitch
            matrixStack.mulPose(Axis.XP.rotation(rotation.getX()));    //yaw
            matrixStack.translate(0, - 0.7d, 0);
        }
    }

    /**
     * Called at the end of {@link net.minecraft.client.model.IllagerModel#IllagerModel(ModelPart)}
     * @param root The root of the model
     * @param model The humanoid model
     */
    public static void initBend(ModelPart root, HumanoidModelAccess model) {
        // Copied from PlayerAnimator's BipedEntityModelMixin#initBend
        IBendHelper.INSTANCE.initBend(root.getChild("body"), Direction.DOWN);
        IBendHelper.INSTANCE.initBend(root.getChild("right_arm"), Direction.UP);
        IBendHelper.INSTANCE.initBend(root.getChild("left_arm"), Direction.UP);
        IBendHelper.INSTANCE.initBend(root.getChild("right_leg"), Direction.UP);
        IBendHelper.INSTANCE.initBend(root.getChild("left_leg"), Direction.UP);
        ((IUpperPartHelper)(Object) model.mobplayeranimator$getRightArm()).setUpperPart(true);
        ((IUpperPartHelper)(Object) model.mobplayeranimator$getLeftArm()).setUpperPart(true);
        ((IUpperPartHelper)(Object) model.mobplayeranimator$getHead()).setUpperPart(true);
        ((IUpperPartHelper)(Object) model.mobplayeranimator$getHat()).setUpperPart(true);
    }

    /**
     * Called at the end of {@link net.minecraft.client.model.EntityModel#copyPropertiesTo(EntityModel)}
     * @param model The humanoid model
     * @param animation The animation to provide to the model
     */
    public static void setAnimation(IMutableModel model, SetableSupplier<AnimationProcessor> animation) {
        // Copied from PlayerAnimator's BipedEntityModelMixin#copyMutatedAttributes
        if(animation != null) {
            model.setEmoteSupplier(animation);
        }
    }

    /**
     * Called at the beginning of {@link net.minecraft.client.model.HierarchicalModel#renderToBuffer(PoseStack, VertexConsumer, int, int, float, float, float, float)}
     * @param matrices
     * @param vertices
     * @param light
     * @param overlay
     * @param red
     * @param green
     * @param blue
     * @param alpha
     * @param animation The animation to apply to the model parts
     * @param headParts The parts of the head, vanilla does not include hats here
     * @param bodyParts The parts of the body, vanilla includes hats here
     * @return If the custom renderToBuffer logic was handled
     */
    public static boolean bendRenderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, SetableSupplier<AnimationProcessor> animation, Iterable<ModelPart> headParts, Iterable<ModelPart> bodyParts) {
        // copied from PlayerAnimator's BipedEntityModelMixin#renderToBuffer
        if(Helper.isBendEnabled() && animation.get() != null && animation.get().isActive()){
            headParts.forEach((part)->{
                if(! ((IUpperPartHelper)(Object)part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            bodyParts.forEach((part)->{
                if(! ((IUpperPartHelper)(Object)part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });

            SetableSupplier<AnimationProcessor> emoteSupplier = animation;
            matrices.pushPose();
            IBendHelper.rotateMatrixStack(matrices, emoteSupplier.get().getBend("body"));
            headParts.forEach((part)->{
                if(((IUpperPartHelper)(Object)part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            bodyParts.forEach((part)->{
                if(((IUpperPartHelper)(Object)part).isUpperPart()){
                    part.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            });
            matrices.popPose();
            return true;
        }
        return false;
    }

    /**
     * Called at the end of {@link net.minecraft.client.model.IllagerModel#IllagerModel(ModelPart)}} and {@link net.minecraft.client.model.HumanoidModel#HumanoidModel(ModelPart, Function)}}
     * @param model The humanoid model
     * @param emoteSupplier The animation to provide to the model
     */
    public static void initEmoteSupplier(IMutableModel model, SetableSupplier<AnimationProcessor> emoteSupplier) {
        // Copied from PlayerAnimator's PlayerModelMixin#initBendableStuff
        emoteSupplier.set(null);
        model.setEmoteSupplier(emoteSupplier);
    }

    /**
     * Called at the beginning of {@link net.minecraft.client.model.IllagerModel#setupAnim(AbstractIllager, float, float, float, float, float)} and {@link net.minecraft.client.model.HumanoidModel#setupAnim(LivingEntity, float, float, float, float, float)}
     * @param model The humanoid model
     */
    public static void setDefaultPivot(HumanoidModelAccess model){
        // Copied from PlayerAnimator's PlayerModelMixin#setDefaultPivot
        model.mobplayeranimator$getHead().setPos(0.0F, 0.0F, 0.0F);
        model.mobplayeranimator$getHead().setRotation(model.mobplayeranimator$getHead().xRot, model.mobplayeranimator$getHead().yRot, 0.0F);
        model.mobplayeranimator$getBody().setPos(0.0F, 0.0F, 0.0F);
        model.mobplayeranimator$getBody().setRotation(0.0F, 0.0F, 0.0F);
        model.mobplayeranimator$getLeftArm().setPos(5.0F, model.mobplayeranimator$getLeftArm().y, 0.0F);
        model.mobplayeranimator$getRightArm().setPos(-5.0F, model.mobplayeranimator$getRightArm().y, 0.0F);
        model.mobplayeranimator$getLeftLeg().setPos(1.9F, 12.0F, 0.1F);
        model.mobplayeranimator$getRightLeg().setPos(-1.9F, 12.0F, 0.1F);
    }

    /**
     * Called at the end of
     * {@link net.minecraft.client.model.IllagerModel#setupAnim(AbstractIllager, float, float, float, float, float)},
     * {@link net.minecraft.client.model.HumanoidModel#setupAnim(LivingEntity, float, float, float, float, float)}
     * and {@link net.minecraft.client.model.PiglinModel#setupAnim(Mob, float, float, float, float, float)}
     * @param model The humanoid model
     * @param emote The emote to apply to the model
     */
    public static <T extends HumanoidModelAccess & IMutableModel & FirstPersonTracker> void setEmote(T model, @Nullable AnimationApplier emote) {
        if(emote == null){
            return;
        }
        // Copied from PlayerAnimator's PlayerModelMixin#setEmote
        if(!model.mobplayeranimator$isFirstPersonNext() && emote.isActive()) {
            model.getEmoteSupplier().set(emote);

            emote.updatePart("head", model.mobplayeranimator$getHead());
            model.mobplayeranimator$getHat().copyFrom(model.mobplayeranimator$getHead());

            emote.updatePart("torso", model.mobplayeranimator$getBody());
            emote.updatePart("leftArm", model.mobplayeranimator$getLeftArm());
            emote.updatePart("rightArm", model.mobplayeranimator$getRightArm());
            emote.updatePart("leftLeg", model.mobplayeranimator$getLeftLeg());
            emote.updatePart("rightLeg", model.mobplayeranimator$getRightLeg());
        }
        else {
            model.mobplayeranimator$setFirstPersonNext(false);
            model.getEmoteSupplier().set(null);
            resetBend(model.mobplayeranimator$getBody());
            resetBend(model.mobplayeranimator$getLeftArm());
            resetBend(model.mobplayeranimator$getRightArm());
            resetBend(model.mobplayeranimator$getLeftLeg());
            resetBend(model.mobplayeranimator$getRightLeg());
        }
    }

    private static void resetBend(ModelPart part) {
        IBendHelper.INSTANCE.bend(part, null);
    }

    public static boolean recursiveAnimationTest(IAnimation animation, Predicate<KeyframeAnimationPlayer> animationTest) {
        if (animation instanceof KeyframeAnimationPlayer keyframeAnimationPlayer) {
            return animationTest.test(keyframeAnimationPlayer);
        } else if (animation instanceof ModifierLayer<?> modifierLayer) {
            IAnimation layerAnimation = modifierLayer.getAnimation();
            return layerAnimation != null && recursiveAnimationTest(layerAnimation, animationTest);
        } else if (animation instanceof AnimationContainer<?> animationContainer) {
            IAnimation containerAnim = animationContainer.getAnim();
            return containerAnim != null && recursiveAnimationTest(containerAnim, animationTest);
        } else if (animation instanceof AnimationStack animationStack) {
            ArrayList<Pair<Integer, IAnimation>> prioritizedLayers = ((AnimationStackAccessor) animationStack).mobplayeranimator$getLayers();
            for (Pair<Integer, IAnimation> prioritizedLayer : prioritizedLayers) {
                IAnimation layer = prioritizedLayer.getRight();
                if (recursiveAnimationTest(layer, animationTest)) return true;
            }
            return false;
        }
        return false;
    }

    private static void recursiveAnimationConsume(IAnimation animation, Consumer<KeyframeAnimationPlayer> animationConsumer) {
        if (animation instanceof KeyframeAnimationPlayer keyframeAnimationPlayer) {
            animationConsumer.accept(keyframeAnimationPlayer);
        } else if (animation instanceof ModifierLayer<?> modifierLayer) {
            IAnimation layerAnimation = modifierLayer.getAnimation();
            if(layerAnimation != null) recursiveAnimationConsume(layerAnimation, animationConsumer);
        } else if (animation instanceof AnimationContainer<?> animationContainer) {
            IAnimation containerAnim = animationContainer.getAnim();
            if(containerAnim != null) recursiveAnimationConsume(containerAnim, animationConsumer);
        } else if (animation instanceof AnimationStack animationStack) {
            ArrayList<Pair<Integer, IAnimation>> prioritizedLayers = ((AnimationStackAccessor) animationStack).mobplayeranimator$getLayers();
            for (Pair<Integer, IAnimation> prioritizedLayer : prioritizedLayers) {
                IAnimation layer = prioritizedLayer.getRight();
                recursiveAnimationConsume(layer, animationConsumer);
            }
        }
    }

    public static Set<MPABodyPart> getCurrentlyAnimatedParts(LivingEntity entity){
        if(entity instanceof IAnimatedPlayer animatedPlayer){
            AnimationStack animationStack = animatedPlayer.getAnimationStack();
            return getCurrentlyAnimatedParts(animationStack);
        }
        return Set.of();
    }

    public static Set<MPABodyPart> getCurrentlyAnimatedParts(IAnimation animation){
        Set<MPABodyPart> activeParts = new HashSet<>();
        recursiveAnimationConsume(animation, keyframeAnimationPlayer -> addCurrentlyTransformedParts(keyframeAnimationPlayer, activeParts));
        return activeParts;
    }

    private static void addCurrentlyTransformedParts(KeyframeAnimationPlayer keyframeAnimationPlayer, Set<MPABodyPart> activeParts) {
        if(!keyframeAnimationPlayer.isActive()) return;

        for(Map.Entry<String, KeyframeAnimationPlayer.BodyPart> part : keyframeAnimationPlayer.bodyParts.entrySet()){
            if(isActive(part.getValue())){
                String partName = part.getKey();
                MPABodyPart mpaBodyPart = MPABodyPart.byName(partName);
                if(mpaBodyPart != null){
                    activeParts.add(mpaBodyPart);
                } else if(Services.PLATFORM.isDevelopmentEnvironment()){
                    throw new IllegalArgumentException("Invalid part name for MPABodyPart: " + partName);
                }
            }
        }
    }

    public static boolean isActive(KeyframeAnimationPlayer.BodyPart bodyPart) {
        return bodyPart.part != null && bodyPart.part.isEnabled();
    }
}
