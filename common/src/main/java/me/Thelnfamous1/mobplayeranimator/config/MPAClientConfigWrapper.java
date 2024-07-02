package me.Thelnfamous1.mobplayeranimator.config;

import me.Thelnfamous1.mobplayeranimator.Constants;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = Constants.MOD_ID)
public class MPAClientConfigWrapper extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public MPAClientConfig client = new MPAClientConfig();
}