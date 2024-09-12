package me.Thelnfamous1.mobplayeranimator.compat;

import me.Thelnfamous1.mobplayeranimator.Constants;
import me.Thelnfamous1.mobplayeranimator.MobPlayerAnimatorClient;
import me.Thelnfamous1.mobplayeranimator.api.IllagerModelAccess;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import me.Thelnfamous1.mobplayeranimator.api.part.*;
import me.Thelnfamous1.mobplayeranimator.mixin.client.EMFModelPartWithStateAccessor;
import me.Thelnfamous1.mobplayeranimator.mixin.client.ModelPartAccessor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

import java.util.*;

public class EMFCompat {

    private static final Set<UUID> EMF_ANIMATIONS_HALTED = new HashSet<>();
    private static final Set<UUID> EMF_VANILLA_MODELS_FORCED = new HashSet<>();
    private static final Map<UUID, MPAModelPose> EMF_MODEL_POSES = new HashMap<>();

    public static void registerVariables(){
        EMFAnimationApi.registerSingletonAnimationVariable(
                Constants.MOD_ID,
                "is_playeranimator_animation_active",
                emfTranslationKey("is_playeranimator_animation_active"),
                EMFCompat::isPlayerAnimatorAnimationActive);
    }

    public static boolean isPlayerAnimatorAnimationActive() {
        if (EMFAnimationEntityContext.getEMFEntity() instanceof LivingEntity livingEntity) {
            return PlayerAnimatorHelper.isAnimating(livingEntity)
                    && MobPlayerAnimatorClient.getClientConfigHelper().isAnimatingAnyNonBlacklistedAnimation((livingEntity));
        } else {
            return false;
        }
    }

    private static String emfTranslationKey(String key) {
        return "entity_model_features.config.variable_explanation." + Constants.MOD_ID + "." + key;
    }

    public static void lockToVanillaModelFor(LivingEntity mob, EntityModel<?> model){
        if(hasEMFAnimations(model) && shouldHaltEMFAnimations(mob) && MobPlayerAnimatorClient.getClientConfigHelper().isVanillaModelForcedForEMF(mob)){
            EMFAnimationApi.lockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(mob));
            EMF_VANILLA_MODELS_FORCED.add(mob.getUUID());
        }
    }

    private static boolean shouldHaltEMFAnimations(LivingEntity mob) {
        return PlayerAnimatorHelper.isAnimating(mob)
                && MobPlayerAnimatorClient.getClientConfig().is_emf_animation_halt_enabled
                && MobPlayerAnimatorClient.getClientConfigHelper().isAnimationHaltedForEMF(mob)
                && MobPlayerAnimatorClient.getClientConfigHelper().isAnimatingAnyNonBlacklistedAnimation(mob);
    }

    public static void pauseEMFAnimationsFor(LivingEntity mob, EntityModel<?> model) {
        if(hasEMFAnimations(model) && shouldHaltEMFAnimations(mob) && !MobPlayerAnimatorClient.getClientConfigHelper().isVanillaModelForcedForEMF(mob)){
            EMF_ANIMATIONS_HALTED.add(mob.getUUID());
            Set<ModelPart> partsToPause = new HashSet<>();
            Set<MPABodyPart> animatedParts = PlayerAnimatorHelper.getCurrentlyAnimatedParts(mob);
            boolean activeHead = animatedParts.contains(MPABodyPart.HEAD);
            boolean activeTorso = animatedParts.contains(MPABodyPart.TORSO);
            boolean activeLeftArm = animatedParts.contains(MPABodyPart.LEFT_ARM);
            boolean activeRightArm = animatedParts.contains(MPABodyPart.RIGHT_ARM);
            boolean activeLeftLeg = animatedParts.contains(MPABodyPart.LEFT_LEG);
            boolean activeRightLeg = animatedParts.contains(MPABodyPart.RIGHT_LEG);
            // only pause relevant parts for Player Animator if they are being animated by it
            if(model instanceof PlayerModel<?> playerModel){
                if(activeHead){
                    partsToPause.add(playerModel.head);
                    partsToPause.add(playerModel.hat);
                }
                if(activeTorso){
                    partsToPause.add(playerModel.body);
                }
                if(activeLeftArm){
                    partsToPause.add(playerModel.leftArm);
                    partsToPause.add(playerModel.leftSleeve);
                }
                if(activeRightArm){
                    partsToPause.add(playerModel.rightArm);
                    partsToPause.add(playerModel.rightSleeve);
                }
                if(activeLeftLeg){
                    partsToPause.add(playerModel.leftLeg);
                    partsToPause.add(playerModel.leftPants);
                }
                if(activeRightLeg){
                    partsToPause.add(playerModel.rightLeg);
                    partsToPause.add(playerModel.rightPants);
                }
            } else if(model instanceof HumanoidModel<?> humanoidModel) {
                if(activeHead){
                    partsToPause.add(humanoidModel.head);
                    partsToPause.add(humanoidModel.hat);
                }
                if(activeTorso){
                    partsToPause.add(humanoidModel.body);
                }
                if(activeLeftArm){
                    partsToPause.add(humanoidModel.leftArm);
                }
                if(activeRightArm){
                    partsToPause.add(humanoidModel.rightArm);
                }
                if(activeLeftLeg){
                    partsToPause.add(humanoidModel.leftLeg);
                }
                if(activeRightLeg){
                    partsToPause.add(humanoidModel.rightLeg);
                }
            } else if(model instanceof IllagerModel<?> illagerModel){
                IllagerModelAccess modelAccess = (IllagerModelAccess) illagerModel;
                if(activeHead){
                    partsToPause.add(modelAccess.mobplayeranimator$getHead());
                    partsToPause.add(modelAccess.mobplayeranimator$getHead());
                }
                if(activeTorso){
                    partsToPause.add(modelAccess.mobplayeranimator$getBody());
                }
                if(activeLeftArm || activeRightArm){
                    partsToPause.add(modelAccess.mobplayeranimator$getArms());
                    if(activeLeftArm){
                        partsToPause.add(modelAccess.mobplayeranimator$getLeftArm());
                    }
                    if(activeRightArm){
                        partsToPause.add(modelAccess.mobplayeranimator$getRightArm());
                    }
                }
                if(activeLeftLeg){
                    partsToPause.add(modelAccess.mobplayeranimator$getLeftLeg());
                }
                if(activeRightLeg){
                    partsToPause.add(modelAccess.mobplayeranimator$getRightLeg());
                }
            }
            MPAModelModifier modelModifier = MobPlayerAnimatorClient.getClientConfigHelper().getModelModifier(mob.getType());
            if(modelModifier != null){
                ModelPart root = ((IEMFModel) model).emf$getEMFRootModel();
                // store the original poses for affected parts
                EMF_MODEL_POSES.put(mob.getUUID(), new MPAModelPose(root, modelModifier, animatedParts));
                // Now apply modifications to specific parts
                Collection<ModelPart> modifiedParts = modelModifier.modify(root, animatedParts).values();
                partsToPause.addAll(modifiedParts);
            }
            if(!partsToPause.isEmpty()){
                EMFAnimationApi.pauseCustomAnimationsForThesePartsOfEntity(EMFAnimationApi.emfEntityOf(mob), partsToPause.toArray(ModelPart[]::new));
            }
        }
    }

    public static boolean hasEMFAnimations(EntityModel<?> model){
        IEMFModel emfModel = (IEMFModel) model;
        return emfModel.emf$isEMFModel()
                && ((EMFModelPartWithStateAccessor)emfModel.emf$getEMFRootModel()).mobplayeranimator$getTryAnimate().getAnimation() != null;
    }

    private static void debugChildren(String name, ModelPart modelPart, LivingEntity mob){
        Constants.LOG.info("Children for {} of {}: {}", name, mob, ((ModelPartAccessor)(Object)modelPart).mobplayeranimator$getChildren().keySet());
    }

    public static void resumeEMFAnimationsFor(LivingEntity mob, EntityModel<?> model) {
        if(EMF_ANIMATIONS_HALTED.remove(mob.getUUID())){
            EMFAnimationApi.resumeAllCustomAnimationsForEntity(EMFAnimationApi.emfEntityOf(mob));
        }
        if(EMF_VANILLA_MODELS_FORCED.remove(mob.getUUID())){
            EMFAnimationApi.unlockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(mob));
        }
        // if a model had its parts modified, return all of them to their pre-modification poses
        MPAModelPose modelPose = EMF_MODEL_POSES.remove(mob.getUUID());
        if(modelPose != null){
            modelPose.pose(((IEMFModel)model).emf$getEMFRootModel());
        }
    }
}
