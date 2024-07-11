package me.Thelnfamous1.mobplayeranimator;

import me.Thelnfamous1.mobplayeranimator.compat.EMFCompat;
import me.Thelnfamous1.mobplayeranimator.config.ClientConfigHelper;
import me.Thelnfamous1.mobplayeranimator.config.MPAClientConfig;
import me.Thelnfamous1.mobplayeranimator.config.MPAClientConfigWrapper;
import me.Thelnfamous1.mobplayeranimator.platform.Services;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraft.world.InteractionResult;

public class MobPlayerAnimatorClient {
    private static MPAClientConfig clientConfig;
    private static ClientConfigHelper clientConfigHelper;
    private static boolean emfLoaded;

    public static void init() {
        if(Services.PLATFORM.isModLoaded("entity_model_features")){
            emfLoaded = true;
            EMFCompat.registerVariables();
        }

        ConfigHolder<MPAClientConfigWrapper> holder = AutoConfig.register(MPAClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        holder.registerSaveListener((ch, ccw) -> onConfigUpdated(ccw));
        // This does not run when the config is first loaded, have to manually update config below
        holder.registerLoadListener((ch, ccw) -> onConfigUpdated(ccw));
        // Manual server config update
        updateClientConfig(holder.getConfig().client);
    }

    private static InteractionResult onConfigUpdated(MPAClientConfigWrapper ccw) {
        updateClientConfig(ccw.client);
        return InteractionResult.PASS;
    }

    private static void updateClientConfig(MPAClientConfig config) {
        clientConfig = config;
        clientConfigHelper = new ClientConfigHelper(config);
        Constants.LOG.info("Client config updated!");
    }

    public static MPAClientConfig getClientConfig() {
        return clientConfig;
    }

    public static ClientConfigHelper getClientConfigHelper() {
        return clientConfigHelper;
    }

    public static boolean isEMFLoaded() {
        return emfLoaded;
    }
}