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
    /** The player switched the worn backpack's abilities off with the key (#57). Saved, synced, decided by the server. */
    private boolean abilitiesOff = false;
    private int dimension = 0;
    /**
     * Server, memory only: what the last sync sent, compared every tick without building NBT. The wearable is a
     * deep copy: a written stack shares its tag with the live one, so comparing written NBT never saw a change
     * made inside the tag (tanks, fuel, heat, inventory).
     */
    private boolean sentOnce = false;
    private ItemStack sentWearable = null;
    private ChunkCoordinates sentCampFire = null;
    private int sentDimension = 0;
    private boolean sentForceCampFire = false;
    private boolean sentAbilitiesOff = false;
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
     * Called every tick: it compares fields and the wearable's tag, and copies them only when a packet goes out.
     */
    public static void syncToNearIfChanged(EntityPlayer player)
    {
        if (!(player instanceof EntityPlayerMP)) return;
        BackpackProperty props = get(player);
        if (props == null || props.sameAsSent()) return;
        props.sentOnce = true;
        props.sentWearable = ItemStack.copyItemStack(props.wearable);
        props.sentCampFire = props.campFire == null ? null : new ChunkCoordinates(props.campFire);
        props.sentDimension = props.dimension;
        props.sentForceCampFire = props.forceCampFire;
        props.sentAbilitiesOff = props.abilitiesOff;
        syncToNear(player);
    }

    private boolean sameAsSent()
    {
        return sentOnce && forceCampFire == sentForceCampFire && abilitiesOff == sentAbilitiesOff && dimension == sentDimension
                && (campFire == null ? sentCampFire == null : campFire.equals(sentCampFire))
                && ItemStack.areItemStacksEqual(wearable, sentWearable);
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
        if (abilitiesOff) compound.setBoolean("abilitiesOff", true);
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
            abilitiesOff = compound.getBoolean("abilitiesOff");
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

    public boolean isAbilitiesOff()
    {
        return abilitiesOff;
    }

    public void setAbilitiesOff(boolean off)
    {
        abilitiesOff = off;
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

    /**
     * Not saved: whether the worn Scuba Gear had air at its last beat (ItemScubaGear). Between beats the tick only
     * reads this boolean, never the stack's tag. Set on equip and on every beat, cleared on unequip.
     */
    private boolean scubaHasAir = false;
    /** Not saved: the vanilla air bar went down since the last beat of the Scuba Gear, so the beat pays for air. */
    private boolean scubaAirDropped = false;

    public boolean isScubaHasAir()
    {
        return scubaHasAir;
    }

    public void setScubaHasAir(boolean hasAir)
    {
        this.scubaHasAir = hasAir;
    }

    public boolean isScubaAirDropped()
    {
        return scubaAirDropped;
    }

    public void setScubaAirDropped(boolean dropped)
    {
        this.scubaAirDropped = dropped;
    }
}
