package com.darkona.adventurebackpack.proxy;

import com.darkona.adventurebackpack.common.BackpackProperty;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
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
            BackpackProperty.get(player).loadNBTData(playerData);
            BackpackProperty.get(player).sync();
        }
    }

    @Override
    public void synchronizePlayer(EntityPlayer player, NBTTagCompound compound)
    {
        LogHelper.info("Sending synchronization message");

    }

    public static void storePlayerProps(UUID playerID, NBTTagCompound compound)
    {
        extendedEntityData.put(playerID, compound);
    }


    public static NBTTagCompound extractPlayerProps(UUID playerID)
    {
        return extendedEntityData.remove(playerID);
    }
}
