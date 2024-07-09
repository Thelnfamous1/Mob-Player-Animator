package me.Thelnfamous1.mobplayeranimator.mixin;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(value = AnimationStack.class, remap = false)
public interface AnimationStackAccessor {

    @Accessor(value = "layers", remap = false)
    ArrayList<Pair<Integer, IAnimation>> mobplayeranimator$getLayers();
}
