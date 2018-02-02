package com.darkona.adventurebackpack.playerProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.network.SyncPropertiesPacket;
import com.darkona.adventurebackpack.util.Utils;

/**
 * Created on 24/10/2014
 *
 * @author Darkona
 */
public class BackpackProperty implements IExtendedEntityProperties
{
    private static final String PROPERTY_NAME = "abp.property";
    private EntityPlayer player = null;
    private ItemStack wearable = null;
    private ChunkCoordinates campFire = null;
    private NBTTagCompound wearableData = new NBTTagCompound();
    private boolean forceCampFire = false;
    private int dimension = 0;

    private boolean isWakingUpInPortableBag = false;

    public void setWakingUpInPortableBag(boolean b)
    {
        this.isWakingUpInPortableBag = b;
    }

    public boolean isWakingUpInPortableBag()
    {
        return this.isWakingUpInPortableBag;
    }

    public NBTTagCompound getWearableData()
    {
        return wearableData;
    }

    public static void sync(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP)
        {
            syncToNear((EntityPlayerMP) player);
        }
    }

    private static void syncToNear(EntityPlayerMP player)
    {
        //Thanks diesieben07!!!
        try
        {
            player.getServerForPlayer().getEntityTracker()
                    .func_151248_b(player, ModNetwork.net.getPacketFrom(new SyncPropertiesPacket
                            .Message(player.getEntityId(), get(player).getData())));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public BackpackProperty(EntityPlayer player)
    {
        this.player = player;
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
        if (wearable != null) compound.setTag("wearable", wearable.writeToNBT(new NBTTagCompound()));
        if (campFire != null)
        {
            compound.setInteger("campFireX", campFire.posX);
            compound.setInteger("campFireY", campFire.posY);
            compound.setInteger("campFireZ", campFire.posZ);
            compound.setInteger("campFireDim", dimension); //TODO use it for check dim
        }
        compound.setBoolean("forceCampFire", forceCampFire);
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
        if (compound != null)
        {
            setWearable(compound.hasKey("wearable") ? ItemStack.loadItemStackFromNBT(compound.getCompoundTag("wearable")) : null);
            setCampFire(new ChunkCoordinates(compound.getInteger("campFireX"), compound.getInteger("campFireY"), compound.getInteger("campFireZ")));
            dimension = compound.getInteger("campFireDim");
            forceCampFire = compound.getBoolean("forceCampFire");
        }
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
        this.player = (EntityPlayer) entity;
    }

    public void setWearable(ItemStack bp)
    {
        wearable = bp;
    }

    public ItemStack getWearable()
    {
        return wearable;
    }

    public void setCampFire(ChunkCoordinates cf)
    {
        campFire = cf;
    }

    public boolean hasWearable()
    {
        return wearable != null;
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

    public boolean isForcedCampFire()
    {
        return forceCampFire;
    }

    public void setForceCampFire(boolean forceCampFire)
    {
        this.forceCampFire = forceCampFire;
    }

    //Scary names for methods because why not
    public void executeWearableUpdateProtocol()
    {
        if (Utils.notNullAndInstanceOf(wearable.getItem(), IBackWearableItem.class))
        {
            ((IBackWearableItem) wearable.getItem()).onEquippedUpdate(player.getEntityWorld(), player, wearable);
        }
    }

    public void executeWearableDeathProtocol()
    {
        if (Utils.notNullAndInstanceOf(wearable.getItem(), IBackWearableItem.class))
        {
            ((IBackWearableItem) wearable.getItem()).onPlayerDeath(player.getEntityWorld(), player, wearable);
        }
    }

    public void executeWearableEquipProtocol()
    {
        if (Utils.notNullAndInstanceOf(wearable.getItem(), IBackWearableItem.class))
        {
            ((IBackWearableItem) wearable.getItem()).onEquipped(player.getEntityWorld(), player, wearable);
        }
    }

    public void executeWearableUnequipProtocol()
    {
        if (Utils.notNullAndInstanceOf(wearable.getItem(), IBackWearableItem.class))
        {
            ((IBackWearableItem) wearable.getItem()).onUnequipped(player.getEntityWorld(), player, wearable);
        }
    }
}