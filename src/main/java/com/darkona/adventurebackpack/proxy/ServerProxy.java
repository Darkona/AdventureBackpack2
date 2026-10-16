package com.darkona.adventurebackpack.proxy;

import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            BackpackProperty.get(player).loadNBTData(playerData);
            BackpackProperty.syncToNear(player);
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
            extendedEntityData.put(player.getUniqueID(), data);
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
