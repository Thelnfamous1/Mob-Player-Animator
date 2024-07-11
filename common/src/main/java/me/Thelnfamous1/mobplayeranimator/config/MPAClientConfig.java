package me.Thelnfamous1.mobplayeranimator.config;

import com.mojang.serialization.JsonOps;
import me.Thelnfamous1.mobplayeranimator.api.part.MPAModelModifier;
import me.Thelnfamous1.mobplayeranimator.api.part.MPAPartModifier;
import me.Thelnfamous1.mobplayeranimator.api.part.MPAPartPath;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.client.animation.KeyframeAnimations;

import java.util.LinkedHashMap;

@Config(
        name = "client"
)
public class MPAClientConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean is_emf_animation_halt_enabled = true;
    @ConfigEntry.Gui.Tooltip
    public String[] emf_animation_halt_blacklist = new String[]{};
    @ConfigEntry.Gui.Tooltip
    public String[] emf_animation_halt_animation_blacklist = new String[]{"seriousplayeranimations:blank_loop"};
    @ConfigEntry.Gui.Tooltip
    public String[] emf_force_vanilla_models = new String[]{"minecraft:vindicator"};
    @ConfigEntry.Gui.Excluded
    @Comment("""
            For these entities, apply specific model modifications to their EMF model to adjust it during an EMF animation halt. \n
            Refer to the wiki for formatting instructions: https://github.com/Thelnfamous1/Mob-Player-Animator/wiki. \n
            Use a JSON validator such as https://jsonlint.com/ to ensure your JSON strings are correct.
            """ )
    public LinkedHashMap<String, String> emf_model_modifiers = new LinkedHashMap<>() {
        {
            this.put("minecraft:vindicator", MPAModelModifier.CODEC
                    .encodeStart(
                            JsonOps.INSTANCE,
                            MPAModelModifier.create()
                                    .withPartModifier(MPAPartPath.of("left_leg#EMF_left_leg"), new MPAPartModifier().withOffsetPos(KeyframeAnimations.posVec(0, -12, 0)))
                                    .withPartModifier(MPAPartPath.of("right_leg#EMF_right_leg"), new MPAPartModifier().withOffsetPos(KeyframeAnimations.posVec(0, -12, 0)))
                                    .withPartModifier(MPAPartPath.of("left_arm"), new MPAPartModifier().withVisibility(true))
                                    .withPartModifier(MPAPartPath.of("right_arm"), new MPAPartModifier().withVisibility(true))
                                    .withPartModifier(MPAPartPath.of("body#EMF_body#EMF_arms_rotation"), new MPAPartModifier().withVisibility(false))
                    )
                    .result().get().toString());
        }
    };

    public MPAClientConfig() {
    }
}
