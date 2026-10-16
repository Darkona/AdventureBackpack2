package com.darkona.adventurebackpack.common;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

public class BackpackAbilities
{
    public static BackpackAbilities backpackAbilities = new BackpackAbilities();

    public static final String NEXT_ABILITY = "abilityNext";
    public static final String RAINBOW_END = "rainbowEnd";

    public static boolean hasAbility(String colorName)
    {
        return AbilityRegistry.INSTANCE.worn(colorName) != null;
    }

    public static boolean hasPlacedAbility(String colorName)
    {
        return AbilityRegistry.INSTANCE.placed(colorName) != null;
    }

    public static boolean hasRemoval(String colorName)
    {
        return AbilityRegistry.INSTANCE.removal(colorName) != null;
    }

    public void executeAbility(EntityPlayer player, World world, Object backpack)
    {
        if (!ConfigHandler.BACKPACK_ABILITIES) return;
        if (backpack instanceof ItemStack)
        {
            String colorName = BackpackNames.getBackpackColorName((ItemStack)backpack);
            try
            {
                IBackpackAbility ability = AbilityRegistry.INSTANCE.worn(colorName);
                if (ability != null && !ApiEvents.abilityCanceled(player, world, colorName, (ItemStack) backpack))
                {
                    ability.onWornTick(player, world, (ItemStack) backpack);
                }
            } catch (RuntimeException oops)
            {
                LogHelper.debug("Backpack ability " + colorName + " failed: " + oops);
            }
        }

        if (backpack instanceof TileAdventureBackpack)
        {
            String colorName = ((TileAdventureBackpack) backpack).getColorName();
            try
            {
                IBackpackAbility ability = AbilityRegistry.INSTANCE.placed(colorName);
                if (ability != null) ability.onPlacedTick(world, (TileAdventureBackpack) backpack);
            } catch (RuntimeException oops)
            {
                LogHelper.debug("Placed backpack ability " + colorName + " failed: " + oops);
            }
        }
    }

    public void executeRemoval(EntityPlayer player, World world,ItemStack backpack )
    {
        String colorName = BackpackNames.getBackpackColorName(backpack);
        try
        {
            IBackpackAbility removal = AbilityRegistry.INSTANCE.removal(colorName);
            if (removal != null) removal.onRemoval(player, world, backpack);
        } catch (RuntimeException oops)
        {
            LogHelper.error("---Something bad happened when removing a backpack---");
            oops.printStackTrace();
        }
    }

    public static void blazePunch(EntityPlayer player, Entity target)
    {
        if (!ConfigHandler.BLAZE_ENABLED || ConfigHandler.BLAZE_PUNCH_FIRE_SECONDS <= 0 || !AbilityToggle.isActive(player)) return;
        if (target == null || target.isImmuneToFire()) return;
        ItemStack held = player.getCurrentEquippedItem();
        boolean empty = held == null || held.getItem() == null;
        if (!AbilityEffects.blazePunchIgnites(empty, !empty && isWeapon(held), ConfigHandler.BLAZE_PUNCH_WITH_NON_WEAPONS)) return;
        if (!Wearing.isWearingTheRightBackpack(player, "Blaze")) return;
        target.setFire(ConfigHandler.BLAZE_PUNCH_FIRE_SECONDS);
    }

    private static boolean isWeapon(ItemStack stack)
    {
        Item item = stack.getItem();
        if (item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemBow) return true;
        try
        {
            return stack.getAttributeModifiers().containsKey(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        } catch (RuntimeException foreignItem)
        {
            return false;
        }
    }

    public static boolean isRainbowRunning(World world, ItemStack backpack)
    {
        return AbilityTimer.remaining(world.getTotalWorldTime(), BackpackUtils.getTimeMark(backpack, RAINBOW_END),
                Utils.secondsToTicks(ConfigHandler.RAINBOW_DURATION_SECONDS)) > 0;
    }

    public static void startRainbowRun(World world, ItemStack backpack)
    {
        BackpackUtils.setTimeMark(backpack, RAINBOW_END, world.getTotalWorldTime() + Utils.secondsToTicks(ConfigHandler.RAINBOW_DURATION_SECONDS));
    }
}
