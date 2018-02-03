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
public class WailaAB implements IWailaDataProvider
{
    /*public void init()
    {
        FMLInterModComms.sendMessage("Waila", "register", "com.darkona.adventurebackpack.reference.WailaAB.callbackRegister");
    }*/

    public static void callbackRegister(IWailaRegistrar registrar)
    {
        registrar.registerHeadProvider(new WailaAB(), TileAdventureBackpack.class);
        registrar.registerBodyProvider(new WailaAB(), TileAdventureBackpack.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        currenttip.remove(0);
        NBTTagCompound backpackTag = accessor.getNBTData().getCompoundTag(Constants.WEARABLE_TAG);
        BackpackTypes type = BackpackTypes.getType(backpackTag.getByte("type"));
        String skin = "";
        if (type != BackpackTypes.STANDARD)
            skin = " [" + Utils.getColoredSkinName(type) + EnumChatFormatting.WHITE + "]";
        currenttip.add(EnumChatFormatting.WHITE + "Adventure Backpack" + skin);
        return currenttip;
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
