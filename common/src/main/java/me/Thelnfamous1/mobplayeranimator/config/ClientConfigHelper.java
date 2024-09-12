package me.Thelnfamous1.mobplayeranimator.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.kosmx.playerAnim.api.layered.*;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import me.Thelnfamous1.mobplayeranimator.Constants;
import me.Thelnfamous1.mobplayeranimator.api.PlayerAnimatorHelper;
import me.Thelnfamous1.mobplayeranimator.api.part.MPAModelModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClientConfigHelper {
    private final MPAClientConfig clientConfig;
    private final Set<EntityType<?>> emfAnimationHaltBlacklist = new HashSet<>();
    private final Set<EntityType<?>> emfForceVanillaModels = new HashSet<>();
    private final Set<ResourceLocation> emfAnimationHaltAnimationBlacklist = new HashSet<>();
    private final Map<EntityType<?>, MPAModelModifier> emfModelModifiers = new LinkedHashMap<>();

    public ClientConfigHelper(MPAClientConfig clientConfig, boolean log){
        this.clientConfig = clientConfig;
        fillEntityTypeSetWithEntries(clientConfig.emf_animation_halt_blacklist, this.emfAnimationHaltBlacklist, "emf_animation_halt_blacklist", log);
        fillEntityTypeSetWithEntries(clientConfig.emf_force_vanilla_models, this.emfForceVanillaModels, "emf_force_vanilla_models", log);
        fillResourceLocationSetWithEntries(clientConfig.emf_animation_halt_animation_blacklist, this.emfAnimationHaltAnimationBlacklist, "emf_animation_halt_animation_blacklist", log);
        clientConfig.emf_model_modifiers.forEach((key, value) -> {
            Codec<? extends EntityType<?>> resourceLocationToEntityType = getResourceLocationToEntityTypeCodec();
            EntityType<?> entityType = resourceLocationToEntityType.parse(JsonOps.INSTANCE, new JsonPrimitive(key)).result().orElse(null);
            if(entityType == null) {
                if(log) Constants.LOG.error("Could not parse {} entry key {}, not a valid namespaced id", "emf_model_modifiers", key);
                return;
            }
            MPAModelModifier modelModifier = MPAModelModifier.CODEC.parse(JsonOps.INSTANCE, new Gson().fromJson(value, JsonObject.class)).getOrThrow(false, Constants.LOG::error);
            if(modelModifier == null){
                if(log) Constants.LOG.error("Could not parse {} entry value {} mapped to {}, not a valid model modifier", "emf_model_modifiers", key, value);
                return;
            }
            if(log) Constants.LOG.debug("Entered {}:{} into the " + "emf_model_modifiers" + " map!", key, value);
            this.emfModelModifiers.put(entityType, modelModifier);
        });
    }

    private static Codec<? extends EntityType<?>> getResourceLocationToEntityTypeCodec() {
        return ResourceLocation.CODEC.comapFlatMap(rl -> {
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) {
                return DataResult.success(BuiltInRegistries.ENTITY_TYPE.get(rl));
            }
            return DataResult.error(() -> String.format("Could not parse %s, not a valid entity type", rl));
        }, BuiltInRegistries.ENTITY_TYPE::getKey);
    }

    private static void fillResourceLocationSetWithEntries(String[] configEntries, Set<ResourceLocation> configSet, String configName, boolean log) {
        for(String entry : configEntries){
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if(id == null){
                if(log) Constants.LOG.error("Could not parse " + configName + " entry {}, not a valid namespaced id", entry);
            } else{
                configSet.add(id);
                if(log) Constants.LOG.info("Entered {} into the " + configName + " set!", id);
            }
        }
    }

    private static void fillEntityTypeSetWithEntries(String[] configEntries, Set<EntityType<?>> configSet, String configName, boolean log) {
        for(String entry : configEntries){
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if(id == null){
                if(log) Constants.LOG.error("Could not parse " + configName + " entry {}, not a valid namespaced id", entry);
            } else{
                Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
                entityType.ifPresentOrElse(et -> {
                    configSet.add(et);
                    if(log) Constants.LOG.info("Entered {} into the " + configName + " set!", id);
                }, () -> {
                    if(log) Constants.LOG.error("Could not find " + configName + " entry {}, not a valid entity type", id);
                });
            }
        }
    }

    public boolean isAnimationHaltedForEMF(Entity entity){
        return !this.emfAnimationHaltBlacklist.contains(entity.getType());
    }

    public boolean isVanillaModelForcedForEMF(Entity entity){
        return this.emfForceVanillaModels.contains(entity.getType());
    }

    public boolean isAnimatingAnyNonBlacklistedAnimation(LivingEntity entity){
        if(entity instanceof IAnimatedPlayer animatedPlayer){
            AnimationStack animationStack = animatedPlayer.getAnimationStack();
            if(this.emfAnimationHaltAnimationBlacklist.isEmpty()){
                return animationStack.isActive();
            }
            return isAnyNonBlacklistedAnimationActive(animationStack, this.emfAnimationHaltAnimationBlacklist);
        }
        return false;
    }

    private static boolean isAnyNonBlacklistedAnimationActive(IAnimation animation, Collection<ResourceLocation> blacklistedAnimationIds){
        return PlayerAnimatorHelper.recursiveAnimationTest(animation, keyframeAnimationPlayer -> isNonBlacklistedAnimationActive(keyframeAnimationPlayer, blacklistedAnimationIds));
    }

    private static boolean isNonBlacklistedAnimationActive(KeyframeAnimationPlayer keyframeAnimationPlayer, Collection<ResourceLocation> blacklistedAnimationIds) {
        if (keyframeAnimationPlayer.isActive()) {
            if(blacklistedAnimationIds.isEmpty()) return true;

            for (ResourceLocation id : blacklistedAnimationIds) {
                KeyframeAnimation blacklistedAnimation = PlayerAnimationRegistry.getAnimation(id);
                if (!keyframeAnimationPlayer.getData().equals(blacklistedAnimation)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public MPAModelModifier getModelModifier(EntityType<?> type) {
        return this.emfModelModifiers.get(type);
    }
}
