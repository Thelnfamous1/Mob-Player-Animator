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

    public MPAClientConfig() {
    }
}
