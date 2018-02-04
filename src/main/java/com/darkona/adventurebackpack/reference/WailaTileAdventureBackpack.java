package com.darkona.adventurebackpack.reference;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.common.event.FMLInterModComms;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.handlers.TooltipEventHandler;
import com.darkona.adventurebackpack.util.Utils;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

/**
 * Created on 03.02.2018
 *
 * @author Ugachaga
 */
public class WailaTileAdventureBackpack implements IWailaDataProvider
{
    public static void init()
    {
        FMLInterModComms.sendMessage("Waila", "register", "com.darkona.adventurebackpack.reference.WailaTileAdventureBackpack.callbackRegister");
    }

    //TODO change icon

    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.registerHeadProvider(new WailaTileAdventureBackpack(), TileAdventureBackpack.class);
        registrar.registerBodyProvider(new WailaTileAdventureBackpack(), TileAdventureBackpack.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        addHeadToBackpack(currenttip, accessor);
        return currenttip;
    }

    private static void addHeadToBackpack(List<String> currenttip, IWailaDataAccessor accessor)
    {
        if (accessor.getNBTData().hasKey(Constants.WEARABLE_TAG))
        {
            NBTTagCompound backpackTag = accessor.getNBTData().getCompoundTag(Constants.WEARABLE_TAG);
            addHeadToBackpack(currenttip, backpackTag);
        }
    }

    private static void addHeadToBackpack(List<String> currenttip, NBTTagCompound backpackTag)
    {
        currenttip.remove(0);
        BackpackTypes type = BackpackTypes.getType(backpackTag.getByte("type"));
        String skin = "";
        if (type != BackpackTypes.STANDARD)
            skin = " [" + Utils.getColoredSkinName(type) + EnumChatFormatting.WHITE + "]";
        currenttip.add(EnumChatFormatting.WHITE + "Adventure Backpack" + skin);
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        addTipToBackpack(currenttip, accessor);
        return currenttip;
    }

    private static void addTipToBackpack(List<String> currenttip, IWailaDataAccessor accessor)
    {
        TileEntity te = accessor.getTileEntity();
        if (te instanceof TileAdventureBackpack)
        {
            if (accessor.getNBTData().hasKey(Constants.WEARABLE_TAG))
            {
                NBTTagCompound backpackTag = accessor.getNBTData().getCompoundTag(Constants.WEARABLE_TAG);
                addTipToBackpack(currenttip, backpackTag);
            }
        }
    }

    private static void addTipToBackpack(List<String> currenttip, NBTTagCompound backpackTag)
    {
        NBTTagList itemList = backpackTag.getTagList(Constants.INVENTORY, NBT.TAG_COMPOUND);
        currenttip.add(TooltipEventHandler.local("backpack.slots.used") + ": " + TooltipEventHandler.inventoryTooltip(itemList));

        FluidTank tank = new FluidTank(Constants.BASIC_TANK_CAPACITY);

        tank.readFromNBT(backpackTag.getCompoundTag(Constants.LEFT_TANK));
        currenttip.add(EnumChatFormatting.RESET + TooltipEventHandler.local("backpack.tank.left")
                + ": " + TooltipEventHandler.tankTooltip(tank));

        tank.readFromNBT(backpackTag.getCompoundTag(Constants.RIGHT_TANK));
        currenttip.add(EnumChatFormatting.RESET + TooltipEventHandler.local("backpack.tank.right")
                + ": " + TooltipEventHandler.tankTooltip(tank));
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
    {
        return null;
    }
}
