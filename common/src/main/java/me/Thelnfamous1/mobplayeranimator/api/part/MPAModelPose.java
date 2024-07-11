package me.Thelnfamous1.mobplayeranimator.api.part;

import net.minecraft.client.model.geom.ModelPart;

import java.util.HashMap;
import java.util.Map;

public class MPAModelPose {
    private final Map<MPAPartPath, MPAPartPose> partPoses = new HashMap<>();

    public MPAModelPose(ModelPart root, MPAModelModifier modelModifier){
        modelModifier.getAffectedParts(root).forEach((partPath, optional) ->
                optional.ifPresent(part -> this.partPoses.put(partPath, new MPAPartPose(part))));
    }

    public void pose(ModelPart root){
        for(Map.Entry<MPAPartPath, MPAPartPose> entry : this.partPoses.entrySet()){
            ModelPart part = entry.getKey().findPart(root);
            if(part != null){
                entry.getValue().pose(part);
            }
        }
    }
}
