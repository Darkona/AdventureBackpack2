package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Ammunition of the Clockwork Crossbow: one cartridge fills the magazine. There is one kind of cartridge. A full
 * cartridge has no NBT, so crafted ones stack; one ejected from a partly used magazine keeps its bolts in
 * {@value #TAG_CHARGES}.
 * <p>
 * 1.0 had elemental cartridges as metadata 1 to 4. They load as normal cartridges, and the metadata is reset to 0
 * the first time one sits in a player's inventory, so they stack with the others.
 */
public class ItemBoltCartridge extends ItemAB
{
    public static final String TAG_CHARGES = "Charges";

    public ItemBoltCartridge()
    {
        super();
        setUnlocalizedName("boltCartridge");
        setMaxDamage(0);
        setMaxStackSize(16);
    }

    /** A cartridge with {@code charges} bolts; full ones carry no NBT. */
    public static ItemStack create(int charges, int capacity)
    {
        ItemStack stack = new ItemStack(ModItems.boltCartridge);
        int stored = CrossbowLogic.ejectedCharges(charges, capacity);
        if (stored > 0)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(TAG_CHARGES, stored);
            stack.setTagCompound(tag);
        }
        return stack;
    }

    public static int getCharges(ItemStack stack, int capacity)
    {
        boolean tagged = stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_CHARGES);
        return CrossbowLogic.cartridgeCharges(tagged, tagged ? stack.getTagCompound().getInteger(TAG_CHARGES) : 0, capacity);
    }

    /** Migration of the 1.0 elemental cartridges: one int compare per tick, a write only on an old stack. */
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
    {
        if (stack.getItemDamage() != 0) stack.setItemDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
    {
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.cartridge.bolts", getCharges(stack, capacity), capacity));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_CHARGES);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        return 1.0 - (double) getCharges(stack, capacity) / capacity;
    }
}
