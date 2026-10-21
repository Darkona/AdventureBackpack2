package com.darkona.adventurebackpack.playerProperties;

import com.darkona.adventurebackpack.common.FlamethrowerSound;
import adventurebackpack.api.IInventoryTanks;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.inventory.QuiverFeed;
import adventurebackpack.api.IBackWearableItem;
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

public class BackpackProperty implements IExtendedEntityProperties
{
    public static final String PROPERTY_NAME = "abp.property";
    protected EntityPlayer player = null;
    private ItemStack wearable = null;
    private ChunkCoordinates campFire = null;
    private NBTTagCompound wearableData = new NBTTagCompound();
    private boolean abilitiesOff = false;
    private int dimension = 0;
    private boolean sentOnce = false;
    private final StackSnapshot sentWearable = new StackSnapshot();
    private int lastChangeSyncTick = Integer.MIN_VALUE;
    private ChunkCoordinates sentCampFire = null;
    private int sentDimension = 0;
    private boolean sentAbilitiesOff = false;
    private NBTTagCompound sentObserverData = null;
    private long lastRelayTick = Long.MIN_VALUE / 2;
    private boolean copterClimbing = false;
    private boolean wearableInBauble = false;
    private QuiverFeed quiverFeed = null;
    private FlamethrowerSound.State flameSound = null;

    public FlamethrowerSound.State getFlameSound()
    {
        if (flameSound == null) flameSound = new FlamethrowerSound.State();
        return flameSound;
    }

    public QuiverFeed getQuiverFeed()
    {
        return quiverFeed;
    }

    public QuiverFeed createQuiverFeed()
    {
        if (quiverFeed == null) quiverFeed = new QuiverFeed();
        return quiverFeed;
    }

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

    public static void syncToNearIfChanged(EntityPlayer player)
    {
        if (!(player instanceof EntityPlayerMP)) return;
        syncToNearIfChanged((EntityPlayerMP) player, get(player));
    }

    public static void syncToNearIfChanged(EntityPlayerMP player, BackpackProperty props)
    {
        if (props == null) return;
        if (!SyncInterval.due(player.ticksExisted, props.lastChangeSyncTick, ConfigHandler.WORN_SYNC_INTERVAL_TICKS)) return;
        if (props.sameAsSent()) return;
        props.lastChangeSyncTick = player.ticksExisted;
        props.send(player, false);
    }

    private void send(EntityPlayerMP owner, boolean force)
    {
        sentOnce = true;
        sentWearable.take(wearable);
        sentCampFire = campFire == null ? null : new ChunkCoordinates(campFire);
        sentDimension = dimension;
        sentAbilitiesOff = abilitiesOff;
        try
        {
            NBTTagCompound ownerData = getSyncData();
            ModNetwork.net.sendTo(new SyncPropertiesPacket.Message(owner.getEntityId(), ownerData), owner);
            NBTTagCompound observerData = ObserverView.properties(ownerData);
            if (force || !observerData.equals(sentObserverData))
            {
                sentObserverData = observerData;
                owner.getServerForPlayer().getEntityTracker()
                        .func_151247_a(owner, ModNetwork.net.getPacketFrom(new SyncPropertiesPacket.Message(owner.getEntityId(), observerData)));
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private boolean sameAsSent()
    {
        return sentOnce && abilitiesOff == sentAbilitiesOff && dimension == sentDimension
                && (campFire == null ? sentCampFire == null : campFire.equals(sentCampFire))
                && sentWearable.matches(wearable);
    }

    public static void syncTo(EntityPlayer target, EntityPlayerMP receiver)
    {
        if (target == null || receiver == null || get(target) == null) return;
        NBTTagCompound data = get(target).getSyncData();
        if (receiver != target) data = ObserverView.properties(data);
        ModNetwork.net.sendTo(new SyncPropertiesPacket.Message(target.getEntityId(), data), receiver);
    }

    public static void syncToNear(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP && get(player) != null)
        {
            get(player).send((EntityPlayerMP) player, true);
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

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        if (liveGear instanceof InventorySteamJetpack && ((InventorySteamJetpack) liveGear).isCurrentFor(wearable))
        {
            ((InventorySteamJetpack) liveGear).flushCounters();
        }
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
        if (abilitiesOff) compound.setBoolean("abilitiesOff", true);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        if(compound!=null)
        {
            setWearable( compound.hasKey("wearable") ? ItemStack.loadItemStackFromNBT(compound.getCompoundTag("wearable")) : null);
            setCampFire(compound.hasKey("campFireX") ? new ChunkCoordinates(compound.getInteger("campFireX"), compound.getInteger("campFireY"), compound.getInteger("campFireZ")) : null);
            dimension = compound.getInteger("campFireDim");
            abilitiesOff = compound.getBoolean("abilitiesOff");
        }
    }

    @Override
    public void init(Entity entity, World world)
    {
        this.player = (EntityPlayer)entity;
    }

    public void setWearable(ItemStack bp)
    {
        wearable = bp;
        wearableInBauble = false;
        liveGear = null;
    }

    public boolean isWearableInBauble()
    {
        return wearableInBauble;
    }

    public void linkBauble(ItemStack inSlot)
    {
        wearable = inSlot;
        wearableInBauble = true;
        executeWearableEquipProtocol();
    }

    public void unlinkBauble()
    {
        if (!wearableInBauble) return;
        if (quiverFeed != null) quiverFeed.finish(player);
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

    private boolean pistonStepApplied = false;

    public boolean isPistonStepApplied()
    {
        return pistonStepApplied;
    }

    public void setPistonStepApplied(boolean applied)
    {
        this.pistonStepApplied = applied;
    }

    private boolean scubaHasAir = false;
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

    private IInventoryTanks liveGear = null;

    public IInventoryTanks getLiveGear()
    {
        return liveGear;
    }

    public void setLiveGear(IInventoryTanks inventory)
    {
        liveGear = inventory;
    }
}
