package me.Thelnfamous1.mobplayeranimator.api;

import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class PlayerAnimatorHelper {
    public static void resetBend(ModelPart part) {
        IBendHelper.INSTANCE.bend(part, null);
    }

    public static void setDefaultPivot(ModelPart head, ModelPart body, ModelPart leftArm, ModelPart rightArm, ModelPart leftLeg, ModelPart rightLeg){
        head.setPos(0.0F, 0.0F, 0.0F);
        body.setPos(0.0F, 0.0F, 0.0F);
        body.setRotation(0.0F, 0.0F, 0.0F);
        leftArm.setPos(5.0F, leftArm.y, 0.0F);
        rightArm.setPos(-5.0F, rightArm.y, 0.0F);
        leftLeg.setPos(1.9F, 12.0F, 0.1F);
        rightLeg.setPos(-1.9F, 12.0F, 0.1F);
    }

    public static AnimationApplier getAnimation(LivingEntity entity){
        return ((IAnimatedPlayer) entity).playerAnimator_getAnimation();
    }

    public static boolean isAnimating(LivingEntity entity){
        return getAnimation(entity).isActive();
    }
}
