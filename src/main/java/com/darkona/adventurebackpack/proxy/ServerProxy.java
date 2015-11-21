package com.darkona.adventurebackpack.proxy;

import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created on 22/12/2014
 *
 * @author Darkona
 */
public class ServerProxy implements IProxy
{
    private static final Map<UUID, NBTTagCompound> extendedEntityData = new HashMap<UUID, NBTTagCompound>();

    @Override
    public void init()
    {

    }

    @Override
    public void registerKeybindings()
    {

    }

    @Override
    public void initNetwork()
    {

    }

    @Override
    public void joinPlayer(EntityPlayer player)
    {
        NBTTagCompound playerData = extractPlayerProps(player.getUniqueID());

        if (playerData != null)
        {
            LogHelper.info("Stored properties retrieved");
            BackpackProperty.get(player).loadNBTData(playerData);
            BackpackProperty.syncToNear(player);
        }else{
            LogHelper.info("Data is null! WTF!");
        }
    }

    @Override
    public void synchronizePlayer(int id, NBTTagCompound compound)
    {

    }

    public static void storePlayerProps(EntityPlayer player)
    {
        try
        {
            NBTTagCompound data = BackpackProperty.get(player).getData();
            if(data.hasKey("wearable"))
            {
                LogHelper.info("Storing wearable: " + ItemStack.loadItemStackFromNBT(data.getCompoundTag("wearable")).getDisplayName());
            }
            extendedEntityData.put(player.getUniqueID(), data);
            LogHelper.info("Stored player properties for dead player");
        }
        catch(Exception ex)
        {
            LogHelper.error("Something went wrong while saving player properties: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public static NBTTagCompound extractPlayerProps(UUID playerID)
    {
        return extendedEntityData.remove(playerID);
    }
}
