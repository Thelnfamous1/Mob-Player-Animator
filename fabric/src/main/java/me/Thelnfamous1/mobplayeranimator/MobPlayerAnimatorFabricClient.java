package me.Thelnfamous1.mobplayeranimator;

import net.fabricmc.api.ClientModInitializer;

public class MobPlayerAnimatorFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MobPlayerAnimatorClient.init();
    }
}
