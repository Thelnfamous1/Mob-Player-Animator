package me.Thelnfamous1.mobplayeranimator.api.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.Thelnfamous1.mobplayeranimator.MobPlayerAnimatorClient;
import net.minecraft.client.model.geom.ModelPart;

import java.util.*;
import java.util.stream.Collectors;

public class MPAModelModifier {
    public static final Codec<MPAModelModifier> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(MPAPartPath.CODEC, MPAPartModifier.CODEC).fieldOf("part_modifiers").forGetter(o -> o.partModifiers)
            )
            .apply(instance, MPAModelModifier::new));
    private final Map<MPAPartPath, MPAPartModifier> partModifiers = new HashMap<>();

    public MPAModelModifier(Map<MPAPartPath, MPAPartModifier> partModifiers) {
        this.partModifiers.putAll(partModifiers);
    }

    public static Builder builder(){
        return new Builder();
    }

    public Map<MPAPartPath, ModelPart> modify(ModelPart root, Set<MPABodyPart> animatedParts){
        Map<MPAPartPath, ModelPart> modifiedParts = new HashMap<>();
        for(Map.Entry<MPAPartPath, MPAPartModifier> entry : this.partModifiers.entrySet()){
            MPAPartPath partPath = entry.getKey();
            ModelPart part = partPath.findPart(root);
            if(part != null){
                MPAPartModifier partModifier = entry.getValue();
                if(canApplyPartModifier(partPath, partModifier, animatedParts)){
                    partModifier.modify(part);
                    modifiedParts.put(partPath, part);
                }
            }
        }
        return modifiedParts;
    }

    private static boolean canApplyPartModifier(MPAPartPath partPath, MPAPartModifier partModifier, Set<MPABodyPart> animatedParts) {
        Set<MPABodyPart> animatedGroup = partModifier.getAnimatedGroup();
        if(animatedGroup.isEmpty() && MobPlayerAnimatorClient.getClientConfig().guess_emf_part_modifier_animated_groups){
            animatedGroup = DefaultAnimatedGroups.guessAnimatedGroup(partPath, animatedGroup);
        }
        boolean canModify = animatedGroup.isEmpty();
        for(MPABodyPart animatedPart : animatedParts){
            if(animatedGroup.contains(animatedPart)){
                canModify = true;
                break;
            }
        }
        return canModify;
    }

    public Map<MPAPartPath, Optional<ModelPart>> getAffectedParts(ModelPart root, Set<MPABodyPart> animatedParts) {
        return this.partModifiers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> canApplyPartModifier(entry.getKey(), entry.getValue(), animatedParts) ? Optional.ofNullable(entry.getKey().findPart(root)) : Optional.empty()));
    }

    public static class Builder{
        private final Map<MPAPartPath, MPAPartModifier> partModifiers = new HashMap<>();

        public Builder() {
        }

        public Builder withPartModifier(MPAPartPath partPath, MPAPartModifier partModifier){
            this.partModifiers.put(partPath, partModifier);
            return this;
        }

        public MPAModelModifier build(){
            return new MPAModelModifier(this.partModifiers);
        }
    }

}
