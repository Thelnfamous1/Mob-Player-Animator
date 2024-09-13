package me.Thelnfamous1.mobplayeranimator.api.part;

import me.Thelnfamous1.mobplayeranimator.api.HumanoidModelAccess;
import net.minecraft.client.model.geom.PartPose;

public class HumanoidBodyPose {
    private final PartPose headPose;
    private final PartPose bodyPose;
    private final PartPose leftArmPose;
    private final PartPose rightArmPose;
    private final PartPose leftLegPose;
    private final PartPose rightLegPose;

    public HumanoidBodyPose(PartPose headPose, PartPose bodyPose, PartPose leftArmPose, PartPose rightArmPose, PartPose leftLegPose, PartPose rightLegPose){
        this.headPose = headPose;
        this.bodyPose = bodyPose;
        this.leftArmPose = leftArmPose;
        this.rightArmPose = rightArmPose;
        this.leftLegPose = leftLegPose;
        this.rightLegPose = rightLegPose;
    }

    public void pose(HumanoidModelAccess modelAccess){
        modelAccess.mobplayeranimator$getHead().loadPose(this.headPose);
        modelAccess.mobplayeranimator$getBody().loadPose(this.bodyPose);
        modelAccess.mobplayeranimator$getLeftArm().loadPose(this.leftArmPose);
        modelAccess.mobplayeranimator$getRightArm().loadPose(this.rightArmPose);
        modelAccess.mobplayeranimator$getLeftLeg().loadPose(this.leftLegPose);
        modelAccess.mobplayeranimator$getRightLeg().loadPose(this.rightLegPose);
    }
}
