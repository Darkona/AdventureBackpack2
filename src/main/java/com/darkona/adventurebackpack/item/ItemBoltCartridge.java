package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import java.util.List;

/**
 * Ammunition of the Clockwork Crossbow: one cartridge fills the magazine. The metadata is the bolt type
 * ({@link CrossbowLogic#NORMAL} to {@link CrossbowLogic#STUNNING}). A full cartridge has no NBT, so crafted ones
 * stack; one ejected from a partly used magazine keeps its bolts in {@value #TAG_CHARGES}.
 */
public class ItemBoltCartridge extends ItemAB
{
    public static final String TAG_CHARGES = "Charges";

    /** Unlocalized names per type. */
    static final String[] NAMES = {"boltCartridge", "boltCartridgeBlazing", "boltCartridgeFreezing", "boltCartridgePoison", "boltCartridgeStunning"};
    /** Icons per type: a brass clip of bolts with a stripe in the type's colour. */
    static final String[] ICONS = {"boltCartridge", "boltCartridgeBlazing", "boltCartridgeFreezing", "boltCartridgePoison", "boltCartridgeStunning"};

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemBoltCartridge()
    {
        super();
        setUnlocalizedName("boltCartridge");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(16);
    }

    /** A cartridge of {@code type} with {@code charges} bolts; full ones carry no NBT. */
    public static ItemStack create(int type, int charges, int capacity)
    {
        ItemStack stack = new ItemStack(ModItems.boltCartridge, 1, CrossbowLogic.clampType(type));
        int stored = CrossbowLogic.ejectedCharges(charges, capacity);
        if (stored > 0)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(TAG_CHARGES, stored);
            stack.setTagCompound(tag);
        }
        return stack;
    }

    public static int getType(ItemStack stack)
    {
        return CrossbowLogic.clampType(stack.getItemDamage());
    }

    public static int getCharges(ItemStack stack, int capacity)
    {
        boolean tagged = stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_CHARGES);
        return CrossbowLogic.cartridgeCharges(tagged, tagged ? stack.getTagCompound().getInteger(TAG_CHARGES) : 0, capacity);
    }

    /** Translated name of a bolt type, for the crossbow tooltip. */
    public static String typeName(int type)
    {
        return StatCollector.translateToLocal("item." + ModInfo.MOD_ID + ":" + NAMES[CrossbowLogic.clampType(type)] + ".name");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName(NAMES[getType(stack)]);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        icons = new IIcon[ICONS.length];
        for (int i = 0; i < ICONS.length; i++)
        {
            icons[i] = register.registerIcon(ModInfo.MOD_ID + ":" + ICONS[i]);
        }
        itemIcon = icons[CrossbowLogic.NORMAL];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        return icons[CrossbowLogic.clampType(damage)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item, 1, CrossbowLogic.NORMAL));
        if (ConfigHandler.CROSSBOW_ELEMENTAL_ENABLED)
        {
            for (int type = CrossbowLogic.BLAZING; type < CrossbowLogic.TYPES; type++)
            {
                list.add(new ItemStack(item, 1, type));
            }
        }
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
