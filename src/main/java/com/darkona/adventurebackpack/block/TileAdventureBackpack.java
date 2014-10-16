package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public class TileAdventureBackpack extends TileEntity implements IAdvBackpack {

    public ItemStack[] inventory;
    private FluidTank leftTank;
    private FluidTank rightTank;
    private boolean needsUpdate = false;
    private boolean equipped = false;
    private boolean sleepingBagDeployed = false;
    private boolean special;
    private int sbdir;
    private int sbx;
    private int sby;
    private int sbz;
    private int checkTime = 0;
    private String color;
    private String colorName;
    public int lastTime;
    public int luminosity;

    public TileAdventureBackpack() {
        leftTank = new FluidTank(Constants.basicTankCapacity);
        rightTank = new FluidTank(Constants.basicTankCapacity);
        inventory = new ItemStack[Constants.inventorySize];
        setColor("Standard");
        setColorName("Standard");
        luminosity = 0;
        lastTime = 0;
        checkTime = 0;

    }

   /* public boolean deploySleepingBag(EntityPlayer player, World world, int x, int y, int z, int meta) {
       // Block sleepingBag = ModBlocks.sleepingbag;
        if (world.setBlock(x, y, z, BlockInfo.SLEEPINGBAG_ID, meta, 3))
        {
            world.playSoundAtEntity(player, Block.soundClothFootstep.getPlaceSound(), 0.5f, 1.0f);
            sbx = x;
            sby = y;
            sbz = z;
            sbdir=meta;
            return sleepingBagDeployed = ((BlockSleepingBag) sleepingBag).placeBlock(world, x,y,z,meta);
        }
        return false;
    }

    public boolean removeSleepingBag(World world) {
        if (sleepingBagDeployed)
        {
            if (world.getBlockId(sbx, sby, sbz) == ABPBlocks.sleepingbag.blockID)
            {
                world.setBlock(sbx, sby, sbz, 0);
                world.removeBlockTileEntity(sbx, sby, sbz);
                // TODO play a sound here
                this.sleepingBagDeployed = false;
                saveChanges();
                return true;
            }
        }else{
            this.sleepingBagDeployed = false;
            saveChanges();
        }
        return false;
    }*/

    //=====================================================GETTERS====================================================//

    public String getColorName() {
        return this.colorName;
    }

    @Override
    public ItemStack[] getInventory() {
        return this.inventory;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public String getColor() {
        return color;
    }

    @Override
    public FluidTank getLeftTank() {
        return leftTank;
    }

    @Override
    public FluidTank getRightTank() {
        return rightTank;
    }

    //=====================================================SETTERS====================================================//


    public void setColorName(String string) {
        this.colorName = string;
    }

    public void setLeftTank(FluidTank leftTank) {
        this.leftTank = leftTank;
        markDirty();
    }

    public void setRightTank(FluidTank rightTank) {
        this.rightTank = rightTank;
        markDirty();
    }

    public void setColor(String color) {
        this.color = color;
        markDirty();
    }

    //=====================================================BOOLEANS===================================================//
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    public boolean isSBDeployed() {
        return this.sleepingBagDeployed;
    }

    public boolean isSpecial() {
        return special;
    }

    //=======================================================NBT======================================================//
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        sleepingBagDeployed = compound.getBoolean("sleepingbag");
        sbx = compound.getInteger("sbx");
        sby = compound.getInteger("sby");
        sbz = compound.getInteger("sbz");
        sbdir = compound.getInteger("sbdir");
        luminosity = compound.getInteger("lumen");
        special = compound.getBoolean("special");
        loadFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tankLeft = new NBTTagCompound();
        NBTTagCompound tankRight = new NBTTagCompound();

        NBTTagList items = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack stack = inventory[i];
            if (stack != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag("ABPItems", items);
        compound.setBoolean("sleepingbag", sleepingBagDeployed);
        compound.setInteger("sbx", sbx);
        compound.setInteger("sby", sby);
        compound.setInteger("sbz", sbz);
        compound.setInteger("lumen", luminosity);
        compound.setInteger("sbdir", sbdir);

        compound.setString("color", color);
        compound.setString("colorName", colorName);
        compound.setTag("rightTank", rightTank.writeToNBT(tankRight));
        compound.setTag("leftTank", leftTank.writeToNBT(tankLeft));
        compound.setInteger("lastTime", lastTime);
        compound.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        super.writeToNBT(compound);
    }

    public void loadFromNBT(NBTTagCompound compound) {
        if (compound != null) {
            NBTTagList items = compound.getTagList("ABPItems", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
                byte slot = item.getByte("Slot");
                if (slot >= 0 && slot < inventory.length) {
                    inventory[slot] = ItemStack.loadItemStackFromNBT(item);
                }
            }
            leftTank.readFromNBT(compound.getCompoundTag("leftTank"));
            rightTank.readFromNBT(compound.getCompoundTag("rightTank"));
            color = compound.getString("color");
            colorName = compound.getString("colorName");
            lastTime = compound.getInteger("lastTime");
        }
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return compound;
    }

    //====================================================INVENTORY===================================================//
    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {
        markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stacky) {
        if (stacky.getItem() instanceof ItemAdventureBackpack) return false;
        if (Block.getBlockFromItem(stacky.getItem()) instanceof BlockAdventureBackpack) return false;
        return true;
    }

    @Override
    public ItemStack decrStackSize(int i, int count) {
        ItemStack itemstack = getStackInSlot(i);

        if (itemstack != null) {
            if (itemstack.stackSize <= count) {
                setInventorySlotContents(i, null);
            } else {
                itemstack = itemstack.splitStack(count);
                markDirty();
            }
        }

        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {

        inventory[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
        onInventoryChanged();
        markDirty();
    }

    public void onInventoryChanged() {
        boolean changed = false;
        for (int i = 0; i < inventory.length; i++) {
            if (i == 6 && inventory[i] != null) {
                changed = updateTankSlots(getLeftTank(), i);
            }

            if (i == 8 && inventory[i] != null) {
                changed = updateTankSlots(getRightTank(), i);
            }
        }
    }

    public void setInventorySlotContentsSafe(int slot, ItemStack itemstack) {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    private ItemStack decrStackSizeSafe(int slot, int amount) {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null) {
            if (stack.stackSize <= amount) {
                setInventorySlotContents(slot, null);
            } else {
                stack = stack.splitStack(amount);
            }
        }
        return stack;
    }


    //===================================================TILE ENTITY==================================================//

    //SEND SYNC PACKET
    @Override
    public Packet getDescriptionPacket() {
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, writeToNBT());
    }

    //RECEIV SYNC PACKET - This is necessary for the TileEntity to load the nbt as soon as it is loaded and be rendered
    //properly when the custom renderer reads it
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void updateEntity() {
        if (isSpecial() && !colorName.isEmpty())
            BackpackAbilities.instance.executeAbility(null, this.worldObj, this);

        //Check for backpack luminosity
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
            checkTime = 20;
        } else {
            checkTime--;
        }
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
                            setInventorySlotContentsSafe(slotOut, newCont);
                            decrStackSizeSafe(slotIn, 1);
                            tank.drain(data.fluid.amount, true);

                        } else if (stackOut == null) {
                            ItemStack newCont = FluidContainerRegistry.fillFluidContainer(data.fluid, stackIn);
                            newCont.stackSize = 1;
                            setInventorySlotContentsSafe(slotOut, newCont);
                            decrStackSizeSafe(slotIn, 1);
                            tank.drain(data.fluid.amount, true);

                        }
                    } else if (stackIn.isItemEqual(data.filledContainer) && tank.fill(data.fluid, false) >= data.fluid.amount) {

                        if (stackOut != null && stackOut.isItemEqual(data.emptyContainer) && stackOut.stackSize < stackOut.getMaxStackSize()) {
                            if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                                setInventorySlotContentsSafe(slotOut, new ItemStack(data.emptyContainer.getItem(), stackOut.stackSize + 1));
                            }
                            decrStackSizeSafe(slotIn, 1);
                            tank.fill(data.fluid, true);
                        } else if (stackOut == null) {
                            if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                                setInventorySlotContentsSafe(slotOut, new ItemStack(data.emptyContainer.getItem(), 1));
                            }
                            decrStackSizeSafe(slotIn, 1);
                            tank.fill(data.fluid, true);
                        }
                    }
                }
            }
        } else if (tank.getFluid() == null) {
            for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
                if (stackIn.isItemEqual(data.filledContainer) && tank.fill(data.fluid, false) >= data.fluid.amount) {

                    if (stackOut != null && stackOut.isItemEqual(data.emptyContainer) && stackOut.stackSize < stackOut.getMaxStackSize()) {
                        if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                            setInventorySlotContentsSafe(slotOut, new ItemStack(data.emptyContainer.getItem(), stackOut.stackSize + 1));
                        }
                        decrStackSizeSafe(slotIn, 1);
                        tank.fill(data.fluid, true);

                    } else if (stackOut == null) {
                        if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                            setInventorySlotContentsSafe(slotOut, new ItemStack(data.emptyContainer.getItem(), 1));
                        }
                        decrStackSizeSafe(slotIn, 1);
                        tank.fill(data.fluid, true);
                    }
                }
            }
        }
        markDirty();
        return false;
    }

    @Override
    public TileAdventureBackpack getTile() {
        return this;
    }

    //=====================================================BACKPACK===================================================//
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
        stacky.setTagCompound(this.writeToNBT());

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


}
