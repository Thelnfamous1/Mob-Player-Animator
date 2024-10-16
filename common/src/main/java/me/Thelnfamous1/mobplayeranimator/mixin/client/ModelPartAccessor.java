package me.Thelnfamous1.mobplayeranimator.mixin.client;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {

    @Accessor("children")
    Map<String, ModelPart> mobplayeranimator$getChildren();
}
