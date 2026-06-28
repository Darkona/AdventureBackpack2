package com.darkona.adventurebackpack.playerProperties;

import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.network.SyncPropertiesPacket;
import com.darkona.adventurebackpack.util.Utils;
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

    public static final String PROPERTY_NAME = "abp.property";
    protected EntityPlayer player = null;
    private ItemStack wearable = null;
    private ChunkCoordinates campFire = null;
    private NBTTagCompound wearableData = new NBTTagCompound();
    private boolean forceCampFire = false;
    private int dimension = 0;
    private NBTTagCompound lastSentData = null;
    /** Server, memory only: world time of the last particle or sound packet of this player relayed to others (RelayGuard). */
    private long lastRelayTick = Long.MIN_VALUE / 2;
    /**
     * Memory only. Server: the client said its copter is climbing (PlayerActionPacket), so the climb is charged.
     * Client: the last state this client sent. Never saved; a fresh player starts at false.
     */
    private boolean copterClimbing = false;
    /**
     * Server only. True when {@link #wearable} is the very stack in a Baubles slot (compat/baubles). Baubles owns
     * and saves that stack; these properties only point at it, so they never save it to disk nor hand it to the
     * respawn store, which would duplicate it.
     */
    private boolean wearableInBauble = false;

    public boolean isCopterClimbing()
    {
        return copterClimbing;
    }

    public void setCopterClimbing(boolean climbing)
    {
        copterClimbing = climbing;
    }

    public long getLastRelayTick()
    {
        return lastRelayTick;
    }

    public void setLastRelayTick(long tick)
    {
        lastRelayTick = tick;
    }

    public NBTTagCompound getWearableData()
    {
        return wearableData;
    }


    public static void sync(EntityPlayer player)
    {
        if(player instanceof EntityPlayerMP)
        {
            syncToNear(player);
        }
    }

    /**
     * Sends the properties to the player and its trackers only when they differ from the last ones sent.
     * Called every tick: building the compound is local, the packet goes out only on a real change.
     */
    public static void syncToNearIfChanged(EntityPlayer player)
    {
        if (!(player instanceof EntityPlayerMP)) return;
        BackpackProperty props = get(player);
        NBTTagCompound data = props.getSyncData();
        if (!data.equals(props.lastSentData))
        {
            props.lastSentData = data;
            syncToNear(player);
        }
    }

    /** Sends the properties of {@code target} to one player that just started tracking it. */
    public static void syncTo(EntityPlayer target, EntityPlayerMP receiver)
    {
        if (target == null || receiver == null || get(target) == null) return;
        ModNetwork.net.sendTo(new SyncPropertiesPacket.Message(target.getEntityId(), get(target).getSyncData()), receiver);
    }

    public static void syncToNear(EntityPlayer player)
    {
        //Thanks diesieben07!!!
        if(player != null && player instanceof EntityPlayerMP)
        {
            try
            {
                ((EntityPlayerMP) player)
                        .getServerForPlayer()
                        .getEntityTracker()
                        .func_151248_b(player, ModNetwork.net.getPacketFrom(new SyncPropertiesPacket.Message(player.getEntityId(), get(player).getSyncData())));
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public BackpackProperty(EntityPlayer player)
    {
        this.player = player;
    }

    /** What is stored for the player (disk, respawn): a wearable worn in a Baubles slot is left out, Baubles keeps it. */
    public NBTTagCompound getData()
    {
        NBTTagCompound data = new NBTTagCompound();
        saveNBTData(data);

        return data;
    }

    /** What clients see: also the wearable worn in a Baubles slot, so they draw it and show its tanks. */
    public NBTTagCompound getSyncData()
    {
        NBTTagCompound data = new NBTTagCompound();
        write(data, true);
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
        write(compound, false);
    }

    private void write(NBTTagCompound compound, boolean withBauble)
    {
        if (wearable != null && (withBauble || !wearableInBauble)) compound.setTag("wearable", wearable.writeToNBT(new NBTTagCompound()));
        if (campFire != null)
        {
            compound.setInteger("campFireX", campFire.posX);
            compound.setInteger("campFireY", campFire.posY);
            compound.setInteger("campFireZ", campFire.posZ);
            compound.setInteger("campFireDim", dimension);

        }
        compound.setBoolean("forceCampFire",forceCampFire);
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
        if(compound!=null)
        {
            setWearable( compound.hasKey("wearable") ? ItemStack.loadItemStackFromNBT(compound.getCompoundTag("wearable")) : null);
            // Keys must match saveNBTData: a misspelt key reads as 0/false and the campfire spawn is lost
            setCampFire(compound.hasKey("campFireX") ? new ChunkCoordinates(compound.getInteger("campFireX"), compound.getInteger("campFireY"), compound.getInteger("campFireZ")) : null);
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
        this.player = (EntityPlayer)entity;
    }

    public void setWearable(ItemStack bp)
    {
        wearable = bp;
        wearableInBauble = false;
    }

    public boolean isWearableInBauble()
    {
        return wearableInBauble;
    }

    /** Server: wear the stack that sits in a Baubles slot. The caller checked that nothing else is worn. */
    public void linkBauble(ItemStack inSlot)
    {
        wearable = inSlot;
        wearableInBauble = true;
        executeWearableEquipProtocol();
    }

    /** Server: the Baubles slot no longer gives the wearable. Does nothing when the wearable is not from a slot. */
    public void unlinkBauble()
    {
        if (!wearableInBauble) return;
        if (wearable != null) executeWearableUnequipProtocol();
        wearable = null;
        wearableInBauble = false;
    }


    public ItemStack getWearable()
    {
        return wearable != null ? wearable : null ;
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
        if(Utils.notNullAndInstanceOf(wearable.getItem(), IBackWearableItem.class))
        {
            ((IBackWearableItem)wearable.getItem()).onEquippedUpdate(player.getEntityWorld(), player, wearable);
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
        if (Utils.notNullAndInstanceOf(wearable.getItem() , IBackWearableItem.class))
        {
            ((IBackWearableItem) wearable.getItem()).onUnequipped(player.getEntityWorld(), player, wearable);
        }
    }

    /** Not saved: the Piston Boots changed this player's step height and must put it back (ItemPistonBoots). */
    private boolean pistonStepApplied = false;

    public boolean isPistonStepApplied()
    {
        return pistonStepApplied;
    }

    public void setPistonStepApplied(boolean applied)
    {
        this.pistonStepApplied = applied;
    }
}
