package me.Thelnfamous1.mobplayeranimator.config;

import me.Thelnfamous1.mobplayeranimator.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ClientConfigHelper {
    private final MPAClientConfig clientConfig;
    private final Set<EntityType<?>> emfAnimationHaltBlacklist = new HashSet<>();
    private final Set<EntityType<?>> emfForceVanillaModels = new HashSet<>();

    public ClientConfigHelper(MPAClientConfig clientConfig){
        this.clientConfig = clientConfig;
        fillSetWithEntries(clientConfig.emf_animation_halt_blacklist, this.emfAnimationHaltBlacklist, "emf_animation_halt_blacklist");
        fillSetWithEntries(clientConfig.emf_force_vanilla_models, this.emfForceVanillaModels, "emf_force_vanilla_models");
    }

    private static void fillSetWithEntries(String[] configEntries, Set<EntityType<?>> configSet, String configName) {
        for(String entry : configEntries){
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if(id == null){
                Constants.LOG.error("Could not parse " + configName + " entry {}, not a valid namespaced id", entry);
            } else{
                Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
                entityType.ifPresentOrElse(et -> {
                    configSet.add(et);
                    Constants.LOG.info("Entered {} into the " + configName + " set!", id);
                }, () -> Constants.LOG.error("Could not find " + configName + " entry {}, not a valid entity type", id));
            }
        }
    }

    public boolean isAnimationHaltedForEMF(Entity entity){
        return !this.emfAnimationHaltBlacklist.contains(entity.getType());
    }

    public boolean isVanillaModelForcedForEMF(Entity entity){
        return this.emfForceVanillaModels.contains(entity.getType());
    }
}
