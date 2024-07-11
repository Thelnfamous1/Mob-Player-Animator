package me.Thelnfamous1.mobplayeranimator.api.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelPart;

import java.util.*;
import java.util.stream.Collectors;

public class MPAModelModifier {
    public static final Codec<MPAModelModifier> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(MPAPartPath.CODEC, MPAPartModifier.CODEC).fieldOf("part_modifiers").forGetter(o -> o.partModifiers)
            )
            .apply(instance, MPAModelModifier::new));
    private final Map<MPAPartPath, MPAPartModifier> partModifiers = new HashMap<>();

    public MPAModelModifier(){
    }

    public MPAModelModifier(Map<MPAPartPath, MPAPartModifier> partModifiers) {
        this.partModifiers.putAll(partModifiers);
    }

    public static MPAModelModifier create(){
        return new MPAModelModifier();
    }

    public MPAModelModifier withPartModifier(MPAPartPath partPath, MPAPartModifier partModifier){
        this.partModifiers.put(partPath, partModifier);
        return this;
    }

    public Map<MPAPartPath, ModelPart> modify(ModelPart root){
        Map<MPAPartPath, ModelPart> modifiedParts = new HashMap<>();
        for(Map.Entry<MPAPartPath, MPAPartModifier> entry : this.partModifiers.entrySet()){
            ModelPart part = entry.getKey().findPart(root);
            if(part != null){
                entry.getValue().modify(part);
                modifiedParts.put(entry.getKey(), part);
            }
        }
        return modifiedParts;
    }

    public Map<MPAPartPath, Optional<ModelPart>> getAffectedParts(ModelPart root) {
        return this.partModifiers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Optional.ofNullable(entry.getKey().findPart(root))));
    }

}
