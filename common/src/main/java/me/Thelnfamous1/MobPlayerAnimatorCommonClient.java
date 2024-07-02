package me.Thelnfamous1;

import me.Thelnfamous1.mobplayeranimator.config.MPAClientConfig;
import me.Thelnfamous1.mobplayeranimator.config.MPAClientConfigWrapper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

public class MobPlayerAnimatorCommonClient {
    private static MPAClientConfig clientConfig;
    public static void init() {
        AutoConfig.register(MPAClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        clientConfig = AutoConfig.getConfigHolder(MPAClientConfigWrapper.class).getConfig().client;
    }

    public static MPAClientConfig getClientConfig() {
        return clientConfig;
    }
}