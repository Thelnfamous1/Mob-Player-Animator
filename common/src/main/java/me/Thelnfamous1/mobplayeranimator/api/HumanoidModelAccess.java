package me.Thelnfamous1.mobplayeranimator.api;

import me.Thelnfamous1.mobplayeranimator.api.part.HumanoidBodyPose;
import net.minecraft.client.model.geom.ModelPart;

public interface HumanoidModelAccess {

    ModelPart mobplayeranimator$getHead();

    ModelPart mobplayeranimator$getHat();
    ModelPart mobplayeranimator$getBody();

    ModelPart mobplayeranimator$getLeftArm();

    ModelPart mobplayeranimator$getRightArm();

    ModelPart mobplayeranimator$getLeftLeg();

    ModelPart mobplayeranimator$getRightLeg();

    HumanoidBodyPose mobplayeranimator$getInitialBodyPose();
}
