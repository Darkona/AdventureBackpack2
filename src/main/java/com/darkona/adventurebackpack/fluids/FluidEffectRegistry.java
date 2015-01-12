package com.darkona.adventurebackpack.fluids;

import com.darkona.adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.fluids.effects.*;
import com.darkona.adventurebackpack.util.LogHelper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class FluidEffectRegistry
{
    static BiMap<String, FluidEffect> EFFECT_REGISTRY = HashBiMap.create();

    public static FluidEffect WATER_EFFECT;
    public static FluidEffect LAVA_EFFECT;
    public static FluidEffect MILK_EFFECT;
    public static FluidEffect MELON_EFFECT;
    public static FluidEffect SOUP_EFFECT;

    private static int effectIDCounter = 0;

    public static void init()
    {
        EFFECT_REGISTRY.clear();
        WATER_EFFECT = new WaterEffect();
        LAVA_EFFECT = new LavaEffect();
        MILK_EFFECT = new MilkEffect();
        MELON_EFFECT = new MelonJuiceEffect();
        SOUP_EFFECT = new MushroomStewEffect();
    }

    /**
     * @param effect
     * @return
     */
    public static int registerFluidEffect(FluidEffect effect)
    {
        String className = effect.getClass().getName();
        effect.effectID = effectIDCounter;
        if (!EFFECT_REGISTRY.containsKey(className) && effect.fluid != null)
        {
            EFFECT_REGISTRY.put(className, effect);
            LogHelper.info("Registered the class " + className + " as a FluidEffect for " + effect.fluid.getName() + " with the ID " + effectIDCounter);
            effectIDCounter++;
            return effectIDCounter;
        }
        return -1;
    }

    public static Map<String, FluidEffect> getRegisteredFluidEffects()
    {
        return ImmutableMap.copyOf(EFFECT_REGISTRY);
    }

    public static String[] getRegisteredFluids()
    {
        String[] result = new String[EFFECT_REGISTRY.size()];
        int counter = 0;
        for (FluidEffect effect : getRegisteredFluidEffects().values())
        {
            result[counter++] = effect.fluid.getName();
        }
        return result;
    }

    public static boolean hasFluidEffect(Fluid fluid)
    {
        for (FluidEffect effect : getRegisteredFluidEffects().values())
        {
            if (fluid == effect.fluid)
            {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<FluidEffect> getEffectsForFluid(Fluid fluid)
    {
        ArrayList<FluidEffect> effectsForFluid = new ArrayList<FluidEffect>();
        for (FluidEffect effect : EFFECT_REGISTRY.values())
        {
            if (fluid == effect.fluid)
            {
                effectsForFluid.add(effect);
            }
        }
        return effectsForFluid;
    }

    public static boolean executeFluidEffectsForFluid(Fluid fluid, Entity entity, World world)
    {
        boolean executed = false;
        for (FluidEffect effect : EFFECT_REGISTRY.values())
        {
            if (effect != null)
            {
                if (effect.fluid == fluid)
                {
                    effect.affectDrinker(world, entity);
                    executed = true;
                }
            }
        }
        return executed;
    }
}
