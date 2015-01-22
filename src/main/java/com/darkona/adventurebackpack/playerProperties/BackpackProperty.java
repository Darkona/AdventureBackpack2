package com.darkona.adventurebackpack.playerProperties;

import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.*;
import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemSteamJetpack;
import com.darkona.adventurebackpack.network.SyncPropertiesPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
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

    public Container getContainer()
    {
        return container;
    }

    public IInventory getInventory()
    {
        return inventory;
    }

    public NBTTagCompound getWearableData()
    {
        return wearableData;
    }

    private Container container = null;
    private IInventory inventory = null;

    public static void sync(EntityPlayer player)
    {
        if(player instanceof EntityPlayerMP)
        {
            syncToNear(player);
        }
    }

    public static void syncToNear(EntityPlayer player)
    {
        //Thanks diesieben07!!!
        if(player != null && player instanceof EntityPlayerMP)
        {
            try
            {
                SyncPropertiesPacket.Message msg = new SyncPropertiesPacket.Message(player.getEntityId(), get(player).getData());
                ((EntityPlayerMP) player).getServerForPlayer().getEntityTracker().func_151248_b(player, ModNetwork.net.getPacketFrom(msg));
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public BackpackProperty(EntityPlayer player)
    {
        this.player = player;
    }

    public NBTTagCompound getData()
    {
        NBTTagCompound data = new NBTTagCompound();
        if(wearable != null) data.setTag("wearable", wearable.writeToNBT(new NBTTagCompound()));
        if (campFire != null)
        {
            data.setInteger("campFireX", campFire.posX);
            data.setInteger("campFireY", campFire.posY);
            data.setInteger("campFireZ", campFire.posZ);
            data.setInteger("campFireDim", dimension);
        }
        data.setBoolean("forceCampfire",forceCampFire);
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
        if(wearable != null) compound.setTag("wearable", wearable.writeToNBT(new NBTTagCompound()));
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
            setWearable((compound.hasKey("wearable")) ? ItemStack.loadItemStackFromNBT(compound.getCompoundTag("wearable")) : null);
            setCampFire( new ChunkCoordinates(compound.getInteger("campFireX"), compound.getInteger("campFireY"), compound.getInteger("campFireZ")));
            dimension = compound.getInteger("compFireDim");
            forceCampFire = compound.getBoolean("forceCampfire");
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
        if(wearable == null){
            container = null;
            inventory = null;
            return;
        }
        if(wearable.getItem() instanceof ItemAdventureBackpack)
        {
            if(inventory != null && inventory instanceof InventoryBackpack)
            {
                ((InventoryBackpack) inventory).setContainerStack(wearable);
                //inventory.openInventory();
                ((ContainerBackpack) container).refresh();
            }else
            {
                inventory = new InventoryBackpack(wearable);
                container = new ContainerBackpack(player, (InventoryBackpack) inventory, ContainerBackpack.SOURCE_WEARING);
            }
        }
        if (wearable.getItem() instanceof ItemCopterPack)
        {
            if (inventory != null && inventory instanceof InventoryCopterPack)
            {
                ((InventoryCopterPack) inventory).setContainerStack(wearable);
                //inventory.openInventory();
                ((ContainerCopter) container).refresh();
            } else
            {
                inventory = new InventoryCopterPack(wearable);
                container = new ContainerCopter(player, (InventoryCopterPack) inventory, true);
            }
        }
        if (wearable.getItem() instanceof ItemSteamJetpack)
        {
            if (inventory != null && inventory instanceof InventorySteamJetpack)
            {
                ((InventorySteamJetpack) inventory).setContainerStack(wearable);
                //inventory.openInventory();
                ((ContainerJetpack) container).refresh();
            } else
            {
                inventory = new InventorySteamJetpack(wearable);
                container = new ContainerJetpack(player, (InventorySteamJetpack) inventory, true);
            }
        }
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

    public boolean isForceCampFire()
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
        if(wearable != null && wearable.getItem() instanceof IBackWearableItem)
        {
            ((IBackWearableItem)wearable.getItem()).onEquippedUpdate(player.getEntityWorld(), player, wearable);
        }
    }

    public void executeWearableDeathProtocol()
    {
        if (wearable != null && wearable.getItem() instanceof IBackWearableItem)
        {
            ((IBackWearableItem) wearable.getItem()).onPlayerDeath(player.getEntityWorld(), player, wearable);
        }
    }

    public void executeWearableEquipProtocol()
    {
        if (wearable != null && wearable.getItem() instanceof IBackWearableItem)
        {
            ((IBackWearableItem) wearable.getItem()).onEquipped(player.getEntityWorld(), player, wearable);
        }
    }

    public void executeWearableUnequipProtocol()
    {
        if (wearable != null && wearable.getItem() instanceof IBackWearableItem)
        {
            ((IBackWearableItem) wearable.getItem()).onUnequipped(player.getEntityWorld(), player, wearable);
        }
    }
}
