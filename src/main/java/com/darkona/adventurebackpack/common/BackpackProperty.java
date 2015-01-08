package com.darkona.adventurebackpack.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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

    public final static String PROPERTY_NAME = "adventureBackpackProp";
    private final EntityPlayer player;


    private ItemStack backpack;
    private ChunkCoordinates campFire;
    private int dimension;

    public BackpackProperty(EntityPlayer player)
    {
        this.player = player;
        backpack = null;
        campFire = null;
    }

    public static final void register(EntityPlayer player)
    {

        player.registerExtendedProperties(PROPERTY_NAME, new BackpackProperty(player));
    }

    public static final BackpackProperty get(EntityPlayer player)
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
        if(backpack!=null)
        {
            compound.setTag("backpack", backpack.writeToNBT(new NBTTagCompound()));
        }
        if(campFire != null)
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
        backpack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("backpack"));
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

    }

    public void setBackpack(ItemStack bp)
    {
        backpack = bp;
    }

    public ItemStack getBackpack()
    {
        return backpack;
    }

    public void setCampFire(ChunkCoordinates cf)
    {
        campFire = cf;
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
}
