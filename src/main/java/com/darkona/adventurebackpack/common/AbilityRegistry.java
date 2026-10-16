package com.darkona.adventurebackpack.common;

import adventurebackpack.api.BackpackShape;
import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.ability.BatAbility;
import com.darkona.adventurebackpack.common.ability.BlazeAbility;
import com.darkona.adventurebackpack.common.ability.CactusAbility;
import com.darkona.adventurebackpack.common.ability.ChickenAbility;
import com.darkona.adventurebackpack.common.ability.CowAbility;
import com.darkona.adventurebackpack.common.ability.CreeperAbility;
import com.darkona.adventurebackpack.common.ability.DragonAbility;
import com.darkona.adventurebackpack.common.ability.MelonAbility;
import com.darkona.adventurebackpack.common.ability.MooshroomAbility;
import com.darkona.adventurebackpack.common.ability.OcelotAbility;
import com.darkona.adventurebackpack.common.ability.PigAbility;
import com.darkona.adventurebackpack.common.ability.PigmanAbility;
import com.darkona.adventurebackpack.common.ability.RainbowAbility;
import com.darkona.adventurebackpack.common.ability.SlimeAbility;
import com.darkona.adventurebackpack.common.ability.SquidAbility;
import com.darkona.adventurebackpack.common.ability.SunflowerAbility;
import com.darkona.adventurebackpack.common.ability.WolfAbility;
import com.darkona.adventurebackpack.inventory.BackpackLayouts;
import com.darkona.adventurebackpack.util.LogHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class AbilityRegistry
{
    public static final AbilityRegistry INSTANCE = new AbilityRegistry();

    private final Map<String, IBackpackAbility> all = new HashMap<String, IBackpackAbility>();
    private final Map<String, IBackpackAbility> worn = new HashMap<String, IBackpackAbility>();
    private final Map<String, IBackpackAbility> placed = new HashMap<String, IBackpackAbility>();
    private final Map<String, IBackpackAbility> removal = new HashMap<String, IBackpackAbility>();
    private final Map<String, IBackpackAbility> shaped = new HashMap<String, IBackpackAbility>();
    private volatile boolean frozen;

    public void register(String colorName, IBackpackAbility ability)
    {
        if (colorName == null || colorName.isEmpty()) throw new IllegalArgumentException("A backpack ability needs a colour name");
        if (ability == null) throw new IllegalArgumentException("The backpack ability of " + colorName + " is null");
        if (frozen)
        {
            throw new IllegalStateException("Cannot register the backpack ability of " + colorName
                    + ": the ability registry is frozen (register in preInit or init, before postInit ends)");
        }
        if (all.containsKey(colorName))
        {
            throw new IllegalStateException("The backpack colour " + colorName + " already has an ability: "
                    + all.get(colorName).getClass().getName());
        }
        all.put(colorName, ability);
        if (overrides(ability, "onWornTick", EntityPlayer.class, World.class, ItemStack.class))
        {
            worn.put(colorName, ability);
        }
        if (overrides(ability, "onPlacedTick", World.class, TileEntity.class))
        {
            placed.put(colorName, ability);
        }
        if (overrides(ability, "onRemoval", EntityPlayer.class, World.class, ItemStack.class))
        {
            removal.put(colorName, ability);
        }
        if (overrides(ability, "getShape", BackpackShape.class))
        {
            shaped.put(colorName, ability);
        }
        if (this == INSTANCE) BackpackLayouts.invalidate();
    }

    public void freeze()
    {
        frozen = true;
    }

    public boolean isFrozen()
    {
        return frozen;
    }

    public IBackpackAbility worn(String colorName)
    {
        return worn.get(colorName);
    }

    public IBackpackAbility placed(String colorName)
    {
        return placed.get(colorName);
    }

    public IBackpackAbility removal(String colorName)
    {
        return removal.get(colorName);
    }

    public BackpackShape shape(String colorName, BackpackShape configured)
    {
        IBackpackAbility ability = shaped.get(colorName);
        if (ability == null) return configured;
        try
        {
            BackpackShape asked = ability.getShape(configured);
            return asked == null ? configured : asked;
        } catch (RuntimeException e)
        {
            LogHelper.error("The backpack ability of " + colorName + " failed to give its window shape: " + e);
            return configured;
        }
    }

    public Set<String> shapedColors()
    {
        return Collections.unmodifiableSet(new TreeSet<String>(shaped.keySet()));
    }

    public Set<String> wornColors()
    {
        return Collections.unmodifiableSet(new TreeSet<String>(worn.keySet()));
    }

    public Set<String> placedColors()
    {
        return Collections.unmodifiableSet(new TreeSet<String>(placed.keySet()));
    }

    public Set<String> removalColors()
    {
        return Collections.unmodifiableSet(new TreeSet<String>(removal.keySet()));
    }

    public void registerBuiltIn()
    {
        register("Sunflower", new SunflowerAbility());
        register("Bat", new BatAbility());
        register("Squid", new SquidAbility());
        register("Pigman", new PigmanAbility());
        register("Blaze", new BlazeAbility());
        register("Cactus", new CactusAbility());
        register("Melon", new MelonAbility());
        register("Pig", new PigAbility());
        register("Slime", new SlimeAbility());
        register("Chicken", new ChickenAbility());
        register("Dragon", new DragonAbility());
        register("Creeper", new CreeperAbility());
        register("Cow", new CowAbility());
        register("Mooshroom", new MooshroomAbility());
        register("Wolf", new WolfAbility());
        register("Ocelot", new OcelotAbility());
        register("Rainbow", new RainbowAbility());
    }

    private static boolean overrides(IBackpackAbility ability, String hook, Class<?>... params)
    {
        try
        {
            return ability.getClass().getMethod(hook, params).getDeclaringClass() != IBackpackAbility.class;
        } catch (NoSuchMethodException impossible)
        {
            throw new IllegalStateException("IBackpackAbility has no hook " + hook, impossible);
        }
    }
}
