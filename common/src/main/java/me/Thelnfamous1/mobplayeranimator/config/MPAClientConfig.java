package me.Thelnfamous1.mobplayeranimator.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

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

    public MPAClientConfig() {
    }
}
