package me.Thelnfamous1.mobplayeranimator.api.part;

import net.minecraft.client.model.geom.ModelPart;
import org.joml.Vector3f;

public class MPAPartPose {
    private final Vector3f position = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f scale = new Vector3f();
    private final boolean visible;

    public MPAPartPose(ModelPart part){
        this.position.set(part.x, part.y, part.z);
        this.rotation.set(part.xRot, part.yRot, part.zRot);
        this.scale.set(part.xScale, part.yScale, part.zScale);
        this.visible = part.visible;
    }

    public void pose(ModelPart part){
        part.setPos(this.position.x, this.position.y, this.position.z);
        part.setRotation(this.rotation.x, this.rotation.y, this.rotation.z);
        part.xScale = this.scale.x;
        part.yScale = this.scale.y;
        part.zScale = this.scale.z;
        part.visible = this.visible;
    }
}
