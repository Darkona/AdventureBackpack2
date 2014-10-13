package com.darkona.adventurebackpack.blocks;

import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.items.ItemAdventureBackpack;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public class AdventureBackpackTileEntity extends TileEntity implements IAdvBackpack {

    public ItemStack[] inventory;
    public FluidTank leftTank;
    public FluidTank rightTank;
    public boolean needsUpdate = false;
    public boolean equipped = false;
    public boolean sleepingBagDeployed = false;
    private int sbdir;
    private int sbx;
    private int sby;
    private int sbz;
    private int checkTime = 0;
    private String color;
    private String colorName;
    public int lastTime;
    public int luminosity;

    public AdventureBackpackTileEntity() {
        leftTank = new FluidTank(Constants.basicTankCapacity);
        rightTank = new FluidTank(Constants.basicTankCapacity);
        inventory = new ItemStack[Constants.inventorySize];
        setColor("Standard");
        setColorName("Standard");
        luminosity = 0;
        lastTime = 0;
        checkTime = 0;
    }

    //=============================================== GETTERS ========================================================//
    public String getColor() {
        return this.color;
    }

    public String getColorName() {
        return this.colorName;
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    public FluidTank getLeftTank() {
        return this.leftTank;
    }

    public FluidTank getRightTank() {
        return this.rightTank;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory[slot];
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    //================================================ SETTERS =======================================================//
    public void setColorName(String string) {
        this.colorName = string;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setLeftTank(FluidTank leftTank) {
        this.leftTank = leftTank;
    }

    public void setRightTank(FluidTank rightTank) {
        this.rightTank = rightTank;

    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        inventory[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (!colorName.isEmpty())
            BackpackAbilities.instance.executeAbility(null, this.worldObj, this);
        if (checkTime == 0) {
            int lastLumen = luminosity;
            int left = (leftTank.getFluid() != null) ? leftTank.getFluid().getFluid().getLuminosity() : 0;
            int right = (rightTank.getFluid() != null) ? rightTank.getFluid().getFluid().getLuminosity() : 0;
            luminosity = Math.max(left, right);
            if (luminosity != lastLumen) {
                int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
                worldObj.setBlock(xCoord, yCoord, zCoord, ModBlocks.blockBackpack, meta, 3);
                worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, luminosity);
            }
            /*if(worldObj.getBlock(sbx, sby, sbz) != ModBlocks.sleepingBag)
            {
                sleepingBagDeployed = false;
            }*/
            checkTime = 400;
            //saveChanges();
        } else {
            checkTime--;
        }
        super.updateEntity();
    }

    public boolean equip(World world, EntityPlayer player, int x, int y, int z) {
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        stacky.setTagCompound(this.writeToNBT());
        // removeSleepingBag(world);

        if (player.inventory.armorInventory[2] == null) {
            player.inventory.armorInventory[2] = stacky;
            return true;

        } else if (player.inventory.addItemStackToInventory(stacky)) {
            return true;
        } else {
            return drop(world, player, x, y, z);
        }
    }

    public boolean drop(World world, EntityPlayer player, int x, int y, int z) {
        // removeSleepingBag(world);
        if (player.capabilities.isCreativeMode) return true;
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        stacky.stackTagCompound = this.writeToNBT();

        float spawnX = x + world.rand.nextFloat();
        float spawnY = y + world.rand.nextFloat();
        float spawnZ = z + world.rand.nextFloat();

        EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, stacky);

        float mult = 0.05F;

        droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * mult;
        droppedItem.motionY = (4 + world.rand.nextFloat()) * mult;
        droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * mult;

        return world.spawnEntityInWorld(droppedItem);
    }

    //================================================== BOOLEANS ====================================================//
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }
    @Override
    public boolean isItemValidForSlot(int i, ItemStack stacky) {
        if (stacky.getItem() instanceof ItemAdventureBackpack) return false;
        if (Block.getBlockFromItem(stacky.getItem()) instanceof BlockAdventureBackpack) return false;
        return true;
    }


    //====================================================== NBT ======================================================//
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound != null) {
            //read whatever minecraft is saving here
            super.readFromNBT(compound);

            //Sleeping bag, if its deployed, the coordinates, and the direction it is facing
            sleepingBagDeployed = compound.getBoolean("sleepingbag");
            sbx = compound.getInteger("sbx");
            sby = compound.getInteger("sby");
            sbz = compound.getInteger("sbz");
            sbdir = compound.getInteger("sbdir");

            //TileEntity values to reflect on the block
            luminosity = compound.getInteger("lumen");

            //Inventory
            NBTTagList items = compound.getTagList("ABPItems", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.tagCount(); ++i) {
                NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
                byte slot = item.getByte("Slot");
                if (slot >= 0 && slot < inventory.length) {
                    inventory[slot] = ItemStack.loadItemStackFromNBT(item);
                }
            }

            //Tanks
            leftTank.readFromNBT(compound.getCompoundTag("leftTank"));
            rightTank.readFromNBT(compound.getCompoundTag("rightTank"));

            //Backpack Appearance
            color = compound.getString("color");
            colorName = compound.getString("colorName");

            //Time for action triggering
            lastTime = compound.getInteger("lastTime");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {

        //Sleeping bag, if its deployed, the coordinates, and the direction it is facing
        compound.setBoolean("sleepingbag", sleepingBagDeployed);
        compound.setInteger("sbx", sbx);
        compound.setInteger("sby", sby);
        compound.setInteger("sbz", sbz);
        compound.setInteger("sbdir", sbdir);

        //Data for block appearance and behaviour
        compound.setInteger("lumen", luminosity);

        //Inventory
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (getStackInSlot(i) != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                getStackInSlot(i).writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag("ABPItems", items);

        //Tanks
        NBTTagCompound tankLeft = new NBTTagCompound();
        NBTTagCompound tankRight = new NBTTagCompound();
        compound.setTag("rightTank", rightTank.writeToNBT(tankRight));
        compound.setTag("leftTank", leftTank.writeToNBT(tankLeft));

        //Backpack Appearance
        compound.setString("color", color);
        compound.setString("colorName", colorName);

        //Time for action triggering
        compound.setInteger("lastTime", lastTime);

        //Write whatever Minecraft wants to save there
        super.writeToNBT(compound);

        //Mark this TE for saving, supposedly
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return compound;
    }

    public boolean isSBDeployed() {
        return this.sleepingBagDeployed;
    }
    // ===================================================== INVENTORY ACTIONS =======================================//

    @Override
    public void openInventory() {

    }
    @Override
    public void closeInventory() {
        markDirty();
    }

    @Override
    public boolean updateTankSlots(FluidTank tank, int slotIn) {

        int slotOut = slotIn + 1;
        ItemStack stackIn = getStackInSlot(slotIn);
        ItemStack stackOut = getStackInSlot(slotOut);

        if (tank.getFluid() != null) {
            for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
                if (data.fluid.isFluidEqual(tank.getFluid())) {

                    if (stackIn.isItemEqual(data.emptyContainer) && tank.drain(data.fluid.amount, false).amount >= data.fluid.amount) {

                        if (stackOut != null && stackOut.isItemEqual(data.filledContainer) && stackOut.stackSize < stackOut.getMaxStackSize()) {
                            ItemStack newCont = FluidContainerRegistry.fillFluidContainer(data.fluid, stackIn);
                            newCont.stackSize = stackOut.stackSize + 1;
                            setInventorySlotContents(slotOut, newCont);
                            decrStackSize(slotIn, 1);
                            tank.drain(data.fluid.amount, true);
                            //saveChanges();

                        } else if (stackOut == null) {
                            ItemStack newCont = FluidContainerRegistry.fillFluidContainer(data.fluid, stackIn);
                            newCont.stackSize = 1;
                            setInventorySlotContents(slotOut, newCont);
                            decrStackSize(slotIn, 1);
                            tank.drain(data.fluid.amount, true);
                            //saveChanges();

                        }
                    } else if (stackIn.isItemEqual(data.filledContainer) && tank.fill(data.fluid, false) >= data.fluid.amount) {

                        if (stackOut != null && stackOut.isItemEqual(data.emptyContainer) && stackOut.stackSize < stackOut.getMaxStackSize()) {
                            if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                                setInventorySlotContents(slotOut, new ItemStack(data.emptyContainer.getItem(), stackOut.stackSize + 1));
                            }
                            decrStackSize(slotIn, 1);
                            tank.fill(data.fluid, true);
                            //saveChanges();

                        } else if (stackOut == null) {
                            if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                                setInventorySlotContents(slotOut, new ItemStack(data.emptyContainer.getItem(), 1));
                            }
                            decrStackSize(slotIn, 1);
                            tank.fill(data.fluid, true);
                            //saveChanges();
                        }
                    }
                }
            }
        } else if (tank.getFluid() == null) {
            for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
                if (stackIn.isItemEqual(data.filledContainer) && tank.fill(data.fluid, false) >= data.fluid.amount) {

                    if (stackOut != null && stackOut.isItemEqual(data.emptyContainer) && stackOut.stackSize < stackOut.getMaxStackSize()) {
                        if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                            setInventorySlotContents(slotOut, new ItemStack(data.emptyContainer.getItem(), stackOut.stackSize + 1));
                        }
                        decrStackSize(slotIn, 1);
                        tank.fill(data.fluid, true);
                        //saveChanges();

                    } else if (stackOut == null) {
                        if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                            setInventorySlotContents(slotOut, new ItemStack(data.emptyContainer.getItem(), 1));
                        }
                        decrStackSize(slotIn, 1);
                        tank.fill(data.fluid, true);
                        //saveChanges();
                    }
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack decrStackSize(int i, int count) {
        ItemStack itemstack = getStackInSlot(i);
        if (itemstack != null) {
            if (itemstack.stackSize <= count) {
                setInventorySlotContents(i, null);
            } else {
                itemstack = itemstack.splitStack(count);
            }
        }
        return itemstack;
    }

}
