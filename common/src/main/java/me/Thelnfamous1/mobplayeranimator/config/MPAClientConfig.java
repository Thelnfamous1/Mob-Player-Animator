package me.Thelnfamous1.mobplayeranimator.config;

import com.mojang.serialization.JsonOps;
import me.Thelnfamous1.mobplayeranimator.api.part.MPAModelModifier;
import me.Thelnfamous1.mobplayeranimator.api.part.MPAPartModifier;
import me.Thelnfamous1.mobplayeranimator.api.part.MPAPartPath;
import me.Thelnfamous1.mobplayeranimator.api.part.MPABodyPart;
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
    public String[] emf_force_vanilla_models = new String[]{};
    @ConfigEntry.Gui.Excluded
    @Comment("""
            For these entities, apply specific model modifications to their EMF model to adjust it during an EMF animation halt. \n
            Refer to the wiki for formatting instructions: https://github.com/Thelnfamous1/Mob-Player-Animator/wiki/EMF-Model-Modifiers. \n
            Use a JSON validator such as https://jsonlint.com/ to ensure your JSON strings are correct.
            """ )
    public LinkedHashMap<String, String> emf_model_modifiers = new LinkedHashMap<>() {
        {
            this.put("minecraft:vindicator", MPAModelModifier.CODEC
                    .encodeStart(
                            JsonOps.INSTANCE,
                            MPAModelModifier.builder()
                                    .withPartModifier(MPAPartPath.of("left_leg#EMF_left_leg"), MPAPartModifier.builder()
                                            .withAnimatedGroup(MPABodyPart.LEFT_LEG)
                                            .withOffsetPos(KeyframeAnimations.posVec(0, -12, 0))
                                            .build())
                                    .withPartModifier(MPAPartPath.of("right_leg#EMF_right_leg"), MPAPartModifier.builder()
                                            .withAnimatedGroup(MPABodyPart.RIGHT_LEG)
                                            .withOffsetPos(KeyframeAnimations.posVec(0, -12, 0))
                                            .build())
                                    .withPartModifier(MPAPartPath.of("left_arm"), MPAPartModifier.builder()
                                            .withAnimatedGroup(MPABodyPart.LEFT_ARM)
                                            .withVisibility(true)
                                            .build())
                                    .withPartModifier(MPAPartPath.of("right_arm"), MPAPartModifier.builder()
                                            .withAnimatedGroup(MPABodyPart.RIGHT_ARM)
                                            .withVisibility(true)
                                            .build())
                                    .withPartModifier(MPAPartPath.of("body#EMF_body#EMF_arms_rotation"), MPAPartModifier.builder()
                                            .withAnimatedGroup(MPABodyPart.LEFT_ARM, MPABodyPart.RIGHT_ARM)
                                            .withVisibility(false)
                                            .build())
                                    .build()
                    )
                    .result().get().toString());
        }
    };
    @ConfigEntry.Gui.Tooltip
    public boolean guess_emf_part_modifier_animated_groups = true;

    public MPAClientConfig() {
    }
}
