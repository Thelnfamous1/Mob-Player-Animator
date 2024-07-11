package me.Thelnfamous1.mobplayeranimator.api.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;

public class MPAPartModifier {
    public static final Codec<MPAPartModifier> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ExtraCodecs.VECTOR3F.optionalFieldOf("offset_position").forGetter((o) -> Optional.ofNullable(o.offsetPosition)),
            ExtraCodecs.VECTOR3F.optionalFieldOf("offset_rotation").forGetter((o) -> Optional.ofNullable(o.offsetRotation)),
            ExtraCodecs.VECTOR3F.optionalFieldOf("offset_scale").forGetter((o) -> Optional.ofNullable(o.offsetScale)),
            Codec.BOOL.optionalFieldOf("visible").forGetter((o) -> Optional.ofNullable(o.visible)))
            .apply(instance, (offsetPos, offsetRotation, offsetScale, visibility) -> new MPAPartModifier(
                    offsetPos.orElse(null),
                    offsetRotation.orElse(null),
                    offsetScale.orElse(null),
                    visibility.orElse(null))));
    @Nullable
    private Vector3f offsetPosition;
    @Nullable
    private Vector3f offsetRotation;
    @Nullable
    private Vector3f offsetScale;
    @Nullable
    private Boolean visible;

    public MPAPartModifier(){
    }

    public MPAPartModifier(@Nullable Vector3f offsetPosition, @Nullable Vector3f offsetRotation, @Nullable Vector3f offsetScale, @Nullable Boolean visible){
        this.offsetPosition = offsetPosition;
        this.offsetRotation = offsetRotation;
        this.offsetScale = offsetScale;
        this.visible = visible;
    }

    public static MPAPartModifier create(){
        return new MPAPartModifier();
    }

    public MPAPartModifier withOffsetPos(Vector3f offsetPos){
        this.offsetPosition = offsetPos;
        return this;
    }

    public MPAPartModifier withOffsetRotation(Vector3f offsetRotation){
        this.offsetRotation = offsetRotation;
        return this;
    }

    public MPAPartModifier withOffsetScale(Vector3f offsetRotation){
        this.offsetScale = offsetRotation;
        return this;
    }


    public MPAPartModifier withVisibility(boolean visibility){
        this.visible = visibility;
        return this;
    }

    public void modify(ModelPart part){
        if(this.offsetPosition != null)
            part.offsetPos(this.offsetPosition);
        if(this.offsetRotation != null)
            part.offsetRotation(this.offsetRotation);
        if(this.offsetScale != null)
            part.offsetScale(this.offsetScale);
        if(this.visible != null){
            part.visible = this.visible;
        }
    }
}
