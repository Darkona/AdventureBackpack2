package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.SyncPropertiesPacket;
import com.darkona.adventurebackpack.reference.ModInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Created on 24/10/2014
 *
 * @author Darkona
 */
public class BackpackProperty implements IExtendedEntityProperties
{

    public final static String PROPERTY_NAME = ModInfo.MOD_ID + ".properties";
    protected EntityPlayer player;
    protected World world;
    private ItemStack backpack;
    private ChunkCoordinates campFire;
    private boolean forceCampFire;
    private int dimension;

    public void sync()
    {
        if(player.worldObj.isRemote)return;
        NBTTagCompound compound = new NBTTagCompound();
        saveNBTData(compound);
        ModNetwork.net.sendTo(new SyncPropertiesPacket.Message(compound), (EntityPlayerMP)player);
    }

    public BackpackProperty(EntityPlayer player)
    {
        this.player = player;
        try{
            campFire = (player != null) ? player.getBedLocation(player.dimension) : null;
        }catch(NullPointerException e)
        {
        }
        forceCampFire = false;
    }

    public NBTTagCompound getData()
    {
        NBTTagCompound data = new NBTTagCompound();
        saveNBTData(data);
        return data;
    }
    public static void register(EntityPlayer player)
    {
        player.registerExtendedProperties(PROPERTY_NAME, new BackpackProperty(player));
    }

    public static BackpackProperty get(EntityPlayer player)
    {
        return (BackpackProperty) player.getExtendedProperties(PROPERTY_NAME);
    }

    /**
     * Called when the entity that this class is attached to is saved.
     * Any custom entity data  that needs saving should be saved here.
     *
     * @param compound The compound to save to.
     */
    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        if (backpack != null)
        {
            compound.setTag("backpack", backpack.writeToNBT(new NBTTagCompound()));
        }
        if (campFire != null)
        {
            compound.setInteger("campFireX", campFire.posX);
            compound.setInteger("campFireY", campFire.posY);
            compound.setInteger("campFireZ", campFire.posZ);
            compound.setInteger("campFireDim", dimension);
        }

    }

    /**
     * Called when the entity that this class is attached to is loaded.
     * In order to hook into this, you will need to subscribe to the EntityConstructing event.
     * Otherwise, you will need to initialize manually.
     *
     * @param compound The compound to load from.
     */
    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        if(compound==null)return;
        backpack = (compound.hasKey("backpack")) ? ItemStack.loadItemStackFromNBT(compound.getCompoundTag("backpack")) : null;
        campFire = new ChunkCoordinates(compound.getInteger("campFireX"), compound.getInteger("campFireY"), compound.getInteger("campFireZ"));
        dimension = compound.getInteger("compFireDim");
    }

    /**
     * Used to initialize the extended properties with the entity that this is attached to, as well
     * as the world object.
     * Called automatically if you register with the EntityConstructing event.
     * May be called multiple times if the extended properties is moved over to a new entity.
     * Such as when a player switches dimension {Minecraft re-creates the player entity}
     *
     * @param entity The entity that this extended properties is attached to
     * @param world  The world in which the entity exists
     */
    @Override
    public void init(Entity entity, World world)
    {
        this.player = (EntityPlayer)entity;
        this.world = world;
    }

    public void setWearable(ItemStack bp)
    {
        backpack = bp;
        sync();
    }

    public ItemStack getWearable()
    {
        return backpack;
    }

    public void setCampFire(ChunkCoordinates cf)
    {
        campFire = cf;
        sync();
    }

    public ChunkCoordinates getCampFire()
    {
        return campFire;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public void setDimension(int dimension)
    {
        this.dimension = dimension;
    }

    public int getDimension()
    {
        return dimension;
    }

    public boolean isForceCampFire()
    {
        return forceCampFire;
    }

    public void setForceCampFire(boolean forceCampFire)
    {
        this.forceCampFire = forceCampFire;
    }
}
