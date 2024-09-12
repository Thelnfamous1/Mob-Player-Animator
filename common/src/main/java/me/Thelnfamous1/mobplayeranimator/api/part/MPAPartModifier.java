package me.Thelnfamous1.mobplayeranimator.api.part;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

public class MPAPartModifier {
    public static final Codec<MPAPartModifier> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(MPABodyPart.CODEC).xmap(Sets::newHashSet, Lists::newArrayList)
                    .optionalFieldOf("animated_group").forGetter((o) -> Optional.ofNullable(o.animatedGroup)),
            ExtraCodecs.VECTOR3F.optionalFieldOf("offset_position").forGetter((o) -> Optional.ofNullable(o.offsetPosition)),
            ExtraCodecs.VECTOR3F.optionalFieldOf("offset_rotation").forGetter((o) -> Optional.ofNullable(o.offsetRotation)),
            ExtraCodecs.VECTOR3F.optionalFieldOf("offset_scale").forGetter((o) -> Optional.ofNullable(o.offsetScale)),
            Codec.BOOL.optionalFieldOf("visible").forGetter((o) -> Optional.ofNullable(o.visible)))
            .apply(instance, (parent, offsetPos, offsetRotation, offsetScale, visibility) -> new MPAPartModifier(
                    parent.orElse(null),
                    offsetPos.orElse(null),
                    offsetRotation.orElse(null),
                    offsetScale.orElse(null),
                    visibility.orElse(null))));
    @Nullable
    private final HashSet<MPABodyPart> animatedGroup;
    @Nullable
    private final Vector3f offsetPosition;
    @Nullable
    private final Vector3f offsetRotation;
    @Nullable
    private final Vector3f offsetScale;
    @Nullable
    private final Boolean visible;
    private final Set<MPABodyPart> animatedGroupUnmodifiable;

    public MPAPartModifier(@Nullable HashSet<MPABodyPart> animatedGroup, @Nullable Vector3f offsetPosition, @Nullable Vector3f offsetRotation, @Nullable Vector3f offsetScale, @Nullable Boolean visible){
        this.animatedGroup = animatedGroup;
        this.offsetPosition = offsetPosition;
        this.offsetRotation = offsetRotation;
        this.offsetScale = offsetScale;
        this.visible = visible;
        this.animatedGroupUnmodifiable = this.animatedGroup != null ? Collections.unmodifiableSet(this.animatedGroup) : Set.of();
    }

    public static Builder builder(){
        return new Builder();
    }

    public Set<MPABodyPart> getAnimatedGroup() {
        return this.animatedGroupUnmodifiable;
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

    public static class Builder{
        @Nullable
        private HashSet<MPABodyPart> animatedGroup;
        @Nullable
        private Vector3f offsetPosition;
        @Nullable
        private Vector3f offsetRotation;
        @Nullable
        private Vector3f offsetScale;
        @Nullable
        private Boolean visible;

        public Builder withAnimatedGroup(MPABodyPart... parents){
            this.animatedGroup = Arrays.stream(parents).collect(Collectors.toCollection(HashSet::new));
            return this;
        }

        public Builder withOffsetPos(Vector3f offsetPos){
            this.offsetPosition = offsetPos;
            return this;
        }

        public Builder withOffsetRotation(Vector3f offsetRotation){
            this.offsetRotation = offsetRotation;
            return this;
        }

        public Builder withOffsetScale(Vector3f offsetRotation){
            this.offsetScale = offsetRotation;
            return this;
        }


        public Builder withVisibility(Boolean visibility){
            this.visible = visibility;
            return this;
        }

        public MPAPartModifier build(){
            return new MPAPartModifier(this.animatedGroup, this.offsetPosition, this.offsetRotation, this.offsetScale, this.visible);
        }
    }
}
