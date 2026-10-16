package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.CampfireCooking;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class TileCampfire extends TileEntity
{
    private ItemStack cooking;
    private long readyAt;
    private boolean hasOwner;
    private long ownerMost;
    private long ownerLeast;
    private boolean cookingClient;
    private EntityItem displayItem;

    public TileCampfire()
    {
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    public static ItemStack cookResult(ItemStack raw)
    {
        if (raw == null || raw.getItem() == null) return null;
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(raw);
        boolean enabled = ConfigHandler.CAMPFIRE_ENABLED && ConfigHandler.CAMPFIRE_COOKING_ENABLED;
        boolean food = result != null && result.getItem() instanceof ItemFood;
        return CampfireCooking.canCook(enabled, result != null && result.getItem() != null, food, ConfigHandler.CAMPFIRE_ONLY_FOOD) ? result : null;
    }

    public int insert(ItemStack held, EntityPlayer player, long now)
    {
        boolean busy = cooking != null;
        int take = CampfireCooking.take(held.stackSize, busy ? cooking.stackSize : 0, busy && sameItem(cooking, held), ConfigHandler.CAMPFIRE_MAX_PER_USE);
        if (take <= 0) return 0;
        if (busy)
        {
            cooking.stackSize += take;
        } else
        {
            cooking = held.copy();
            cooking.stackSize = take;
            readyAt = now + ConfigHandler.CAMPFIRE_TICKS_PER_ITEM;
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), ConfigHandler.CAMPFIRE_TICKS_PER_ITEM);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        UUID id = player.getUniqueID();
        hasOwner = true;
        ownerMost = id.getMostSignificantBits();
        ownerLeast = id.getLeastSignificantBits();
        markDirty();
        return take;
    }

    public void onBlockUpdate(long now)
    {
        if (cooking == null) return;
        if (!CampfireCooking.ready(now, readyAt))
        {
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), CampfireCooking.delay(now, readyAt));
            return;
        }
        ItemStack result = cookResult(cooking);
        if (result == null)
        {
            deliver(cooking);
            stop();
            return;
        }
        deliver(result.copy());
        worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.fizz", 0.4F, 1.6F + worldObj.rand.nextFloat() * 0.4F);
        if (--cooking.stackSize <= 0)
        {
            stop();
        } else
        {
            readyAt = now + ConfigHandler.CAMPFIRE_TICKS_PER_ITEM;
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), ConfigHandler.CAMPFIRE_TICKS_PER_ITEM);
            markDirty();
        }
    }

    public void dropContents()
    {
        if (cooking == null) return;
        ItemStack raw = cooking;
        cooking = null;
        drop(raw);
    }

    public boolean isCooking()
    {
        return worldObj != null && worldObj.isRemote ? cookingClient : cooking != null;
    }

    private void stop()
    {
        cooking = null;
        hasOwner = false;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private void deliver(ItemStack stack)
    {
        EntityPlayer owner = hasOwner ? worldObj.func_152378_a(new UUID(ownerMost, ownerLeast)) : null;
        if (owner != null && !owner.isDead
                && CampfireCooking.inReturnRange(owner.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5), ConfigHandler.CAMPFIRE_RETURN_RANGE)
                && owner.inventory.addItemStackToInventory(stack))
        {
            worldObj.playSoundAtEntity(owner, "random.pop", 0.2F, ((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        if (stack.stackSize > 0) drop(stack);
    }

    private void drop(ItemStack stack)
    {
        EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.3, zCoord + 0.5, stack);
        item.motionX = (worldObj.rand.nextFloat() - 0.5F) * 0.1F;
        item.motionY = 0.2F;
        item.motionZ = (worldObj.rand.nextFloat() - 0.5F) * 0.1F;
        item.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(item);
    }

    private static boolean sameItem(ItemStack a, ItemStack b)
    {
        return a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if (cooking != null)
        {
            compound.setTag("cooking", cooking.writeToNBT(new NBTTagCompound()));
            compound.setLong("readyAt", readyAt);
            if (hasOwner)
            {
                compound.setLong("ownerMost", ownerMost);
                compound.setLong("ownerLeast", ownerLeast);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        cooking = compound.hasKey("cooking") ? ItemStack.loadItemStackFromNBT(compound.getCompoundTag("cooking")) : null;
        readyAt = compound.getLong("readyAt");
        hasOwner = compound.hasKey("ownerMost");
        ownerMost = compound.getLong("ownerMost");
        ownerLeast = compound.getLong("ownerLeast");
    }

    public EntityItem getDisplayItem()
    {
        return cookingClient ? displayItem : null;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("cooking", cooking != null);
        if (cooking != null)
        {
            compound.setInteger("item", Item.getIdFromItem(cooking.getItem()));
            compound.setInteger("damage", cooking.getItemDamage());
        }
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        NBTTagCompound compound = pkt.func_148857_g();
        cookingClient = compound != null && compound.getBoolean("cooking");
        Item item = cookingClient ? Item.getItemById(compound.getInteger("item")) : null;
        if (item == null)
        {
            displayItem = null;
            return;
        }
        int damage = compound.getInteger("damage");
        ItemStack shown = displayItem == null ? null : displayItem.getEntityItem();
        if (shown != null && shown.getItem() == item && shown.getItemDamage() == damage) return;
        displayItem = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, new ItemStack(item, 1, damage));
        displayItem.hoverStart = 0.0F;
    }
}
