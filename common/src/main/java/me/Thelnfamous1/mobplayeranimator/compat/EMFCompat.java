package me.Thelnfamous1.mobplayeranimator.compat;

import me.Thelnfamous1.mobplayeranimator.Constants;
import me.Thelnfamous1.mobplayeranimator.MobPlayerAnimatorClient;
import me.Thelnfamous1.mobplayeranimator.api.IllagerModelAccess;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import me.Thelnfamous1.mobplayeranimator.api.part.*;
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

    public static void lockToVanillaModelFor(LivingEntity mob){
        if(shouldHaltActiveAnimation(mob) && MobPlayerAnimatorClient.getClientConfigHelper().isVanillaModelForcedForEMF(mob)){
            EMFAnimationApi.lockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(mob));
            EMF_VANILLA_MODELS_FORCED.add(mob.getUUID());
        }
    }

    private static boolean shouldHaltActiveAnimation(LivingEntity mob) {
        return PlayerAnimatorHelper.isAnimating(mob)
                && MobPlayerAnimatorClient.getClientConfig().is_emf_animation_halt_enabled
                && MobPlayerAnimatorClient.getClientConfigHelper().isAnimationHaltedForEMF(mob)
                && MobPlayerAnimatorClient.getClientConfigHelper().isAnimatingAnyNonBlacklistedAnimation(mob);
    }

    public static void pauseEMFAnimationsFor(LivingEntity mob, EntityModel<?> model) {
        if(shouldHaltActiveAnimation(mob) && !MobPlayerAnimatorClient.getClientConfigHelper().isVanillaModelForcedForEMF(mob)){
            EMF_ANIMATIONS_HALTED.add(mob.getUUID());
            List<ModelPart> pausedParts = new ArrayList<>();
            // store the original poses for affected parts, then modify them
            MPAModelModifier modelModifier = MobPlayerAnimatorClient.getClientConfigHelper().getModelModifier(mob.getType());
            if(modelModifier != null){
                ModelPart root = ((IEMFModel) model).emf$getEMFRootModel();
                EMF_MODEL_POSES.put(mob.getUUID(), new MPAModelPose(root, modelModifier));
                pausedParts.addAll(modelModifier.modify(root).values()); // have to add all modified parts so EMF does not override any animations
            }
            // only pause relevant parts for Player Animator
            if(model instanceof PlayerModel<?> playerModel){
                pausedParts.add(playerModel.head);
                pausedParts.add(playerModel.hat);
                pausedParts.add(playerModel.body);
                pausedParts.add(playerModel.leftArm);
                pausedParts.add(playerModel.leftSleeve);
                pausedParts.add(playerModel.rightArm);
                pausedParts.add(playerModel.rightSleeve);
                pausedParts.add(playerModel.leftLeg);
                pausedParts.add(playerModel.leftPants);
                pausedParts.add(playerModel.rightLeg);
                pausedParts.add(playerModel.rightPants);
            } else if(model instanceof HumanoidModel<?> humanoidModel) {
                pausedParts.add(humanoidModel.head);
                pausedParts.add(humanoidModel.hat);
                pausedParts.add(humanoidModel.body);
                pausedParts.add(humanoidModel.leftArm);
                pausedParts.add(humanoidModel.rightArm);
                pausedParts.add(humanoidModel.leftLeg);
                pausedParts.add(humanoidModel.rightLeg);
            } else if(model instanceof IllagerModel<?> illagerModel){
                IllagerModelAccess modelAccess = (IllagerModelAccess) illagerModel;
                pausedParts.add(illagerModel.getHead());
                pausedParts.add(illagerModel.getHat());
                pausedParts.add(modelAccess.mobplayeranimator$getBody());
                pausedParts.add(modelAccess.mobplayeranimator$getArms());
                pausedParts.add(modelAccess.mobplayeranimator$getLeftArm());
                pausedParts.add(modelAccess.mobplayeranimator$getRightArm());
                pausedParts.add(modelAccess.mobplayeranimator$getLeftLeg());
                pausedParts.add(modelAccess.mobplayeranimator$getRightLeg());
            }
            if(!pausedParts.isEmpty()){
                EMFAnimationApi.pauseCustomAnimationsForThesePartsOfEntity(EMFAnimationApi.emfEntityOf(mob), pausedParts.toArray(ModelPart[]::new));
            }
        }
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
