package me.Thelnfamous1.mobplayeranimator.compat;

import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import me.Thelnfamous1.mobplayeranimator.Constants;
import me.Thelnfamous1.mobplayeranimator.MobPlayerAnimatorClient;
import me.Thelnfamous1.mobplayeranimator.api.IllagerModelAccess;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EMFCompat {

    private static final Set<UUID> EMF_ANIMATIONS_HALTED = new HashSet<>();
    private static final Set<UUID> EMF_VANILLA_MODELS_FORCED = new HashSet<>();

    public static void registerVariables(){
        EMFAnimationApi.registerSingletonAnimationVariable(
                Constants.MOD_ID,
                "is_playeranimator_animation_active",
                emfTranslationKey("is_playeranimator_animation_active"),
                EMFCompat::isPlayerAnimatorAnimationActive);
    }

    public static boolean isPlayerAnimatorAnimationActive() {
        if (EMFAnimationEntityContext.getEMFEntity() instanceof IAnimatedPlayer animatedPlayer) {
            return animatedPlayer.playerAnimator_getAnimation().isActive();
        } else {
            return false;
        }
    }

    private static String emfTranslationKey(String key) {
        return "entity_model_features.config.variable_explanation." + Constants.MOD_ID + "." + key;
    }

    public static void lockToVanillaModelFor(LivingEntity mob){
        if(PlayerAnimatorHelper.isAnimating(mob)
                && MobPlayerAnimatorClient.getClientConfig().is_emf_animation_halt_enabled
                && MobPlayerAnimatorClient.getClientConfigHelper().isAnimationHaltedForEMF(mob)
                && MobPlayerAnimatorClient.getClientConfigHelper().isVanillaModelForcedForEMF(mob)){
            EMFAnimationApi.lockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(mob));
            EMF_VANILLA_MODELS_FORCED.add(mob.getUUID());
        }
    }

    public static void pauseEMFAnimationsFor(LivingEntity mob, EntityModel<?> model) {
        if(PlayerAnimatorHelper.isAnimating(mob)
                && MobPlayerAnimatorClient.getClientConfig().is_emf_animation_halt_enabled
                && MobPlayerAnimatorClient.getClientConfigHelper().isAnimationHaltedForEMF(mob)
                && !MobPlayerAnimatorClient.getClientConfigHelper().isVanillaModelForcedForEMF(mob)){
            EMF_ANIMATIONS_HALTED.add(mob.getUUID());
            // only pause relevant parts for Player Animator
            if(model instanceof PlayerModel<?> playerModel){
                EMFAnimationApi.pauseCustomAnimationsForThesePartsOfEntity(EMFAnimationApi.emfEntityOf(mob),
                        playerModel.head,
                        playerModel.hat,
                        playerModel.body,
                        playerModel.jacket,
                        playerModel.leftArm,
                        playerModel.leftSleeve,
                        playerModel.rightArm,
                        playerModel.rightSleeve,
                        playerModel.leftLeg,
                        playerModel.leftPants,
                        playerModel.rightLeg,
                        playerModel.rightPants);
            } else if(model instanceof HumanoidModel<?> humanoidModel) {
                EMFAnimationApi.pauseCustomAnimationsForThesePartsOfEntity(EMFAnimationApi.emfEntityOf(mob),
                        humanoidModel.head,
                        humanoidModel.hat,
                        humanoidModel.body,
                        humanoidModel.leftArm,
                        humanoidModel.rightArm,
                        humanoidModel.leftLeg,
                        humanoidModel.rightLeg);
            } else if(model instanceof IllagerModel<?> illagerModel){
                IllagerModelAccess modelAccess = (IllagerModelAccess) illagerModel;
                EMFAnimationApi.pauseCustomAnimationsForThesePartsOfEntity(EMFAnimationApi.emfEntityOf(mob),
                        illagerModel.getHead(),
                        illagerModel.getHat(),
                        modelAccess.mobplayeranimator$getBody(),
                        modelAccess.mobplayeranimator$getArms(),
                        modelAccess.mobplayeranimator$getLeftArm(),
                        modelAccess.mobplayeranimator$getRightArm(),
                        modelAccess.mobplayeranimator$getLeftLeg(),
                        modelAccess.mobplayeranimator$getRightLeg());
            } else{
                EMFAnimationApi.pauseAllCustomAnimationsForEntity(EMFAnimationApi.emfEntityOf(mob));
            }
        }
    }

    public static void resumeEMFAnimationsFor(LivingEntity mob) {
        if(EMF_ANIMATIONS_HALTED.remove(mob.getUUID())){
            EMFAnimationApi.resumeAllCustomAnimationsForEntity(EMFAnimationApi.emfEntityOf(mob));
        }
        if(EMF_VANILLA_MODELS_FORCED.remove(mob.getUUID())){
            EMFAnimationApi.unlockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(mob));
        }
    }
}
