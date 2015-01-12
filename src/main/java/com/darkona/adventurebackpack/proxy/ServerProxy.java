package com.darkona.adventurebackpack.proxy;

import com.darkona.adventurebackpack.common.BackpackProperty;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.SyncPropertiesPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 22/12/2014
 *
 * @author Darkona
 */
public class ServerProxy implements IProxy
{
    private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();

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
        NBTTagCompound playerData = extractPlayerProps(player.getCommandSenderName());
        if (playerData != null)
        {
            BackpackProperty.get(player).loadNBTData(playerData);
            ModNetwork.net.sendTo(new SyncPropertiesPacket.Message(playerData), (EntityPlayerMP)player);
        }
    }

    public static void storePlayerProps(String name, NBTTagCompound compound)
    {
        extendedEntityData.put(name, compound);
    }


    public static NBTTagCompound extractPlayerProps(String name)
    {
        return extendedEntityData.remove(name);
    }
}
