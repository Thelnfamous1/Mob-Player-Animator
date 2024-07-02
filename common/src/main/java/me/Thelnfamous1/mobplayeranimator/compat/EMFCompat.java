package me.Thelnfamous1.mobplayeranimator.compat;

import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import me.Thelnfamous1.mobplayeranimator.Constants;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

public class EMFCompat {

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
}
