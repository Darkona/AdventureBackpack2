package com.darkona.adventurebackpack.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.IInventoryTanks;

/**
 * Created on 05/01/2015
 *
 * @author Darkona
 */
public class EntityInflatableBoat extends EntityBoat implements IInventoryTanks, IEntityAdditionalSpawnData
{
    @SuppressWarnings({"FieldCanBeLocal"})
    private FluidTank fuelTank;
    private boolean isBoatEmpty;
    private double speedMultiplier;
    private int boatPosRotationIncrements;
    private double boatX;
    private double boatY;
    private double boatZ;
    private double boatYaw;
    private double boatPitch;
    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;
    private boolean inflated = false;
    public float inflation = 0.25f;

    private boolean motorized;

    public boolean isMotorized()
    {
        return motorized;
    }

    public EntityInflatableBoat(World world)
    {
        super(world);
    }

    public EntityInflatableBoat(World world, double posX, double posY, double posZ, boolean motorized)
    {
        super(world, posX, posY, posZ);
        setMotorized(motorized);
        fuelTank = new FluidTank(6000);
    }

    public boolean isInflated()
    {
        return inflated;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        if (!inflated)
        {
            inflation += 0.025;
            if (inflation >= 1.0f)
            {
                inflation = 1;
                inflated = true;
            }
        }
        else
        {
            inflation = 1;
        }

        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        byte b0 = 5;
        double d0 = 0.0D;

        for (int i = 0; i < b0; ++i)
        {
            double d1 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (i + 0) / b0 - 0.125D;
            double d3 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (i + 1) / b0 - 0.125D;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(this.boundingBox.minX, d1, this.boundingBox.minZ, this.boundingBox.maxX, d3, this.boundingBox.maxZ);

            if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water))
            {
                d0 += 1.0D / (double) b0;
            }
        }

        double horizontalMotion = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        double d2;
        double d4;
        int j;

        if (horizontalMotion > 0.26249999999999996D)
        {
            d2 = Math.cos((double) this.rotationYaw * Math.PI / 180.0D);
            d4 = Math.sin((double) this.rotationYaw * Math.PI / 180.0D);

            for (j = 0; (double) j < 1.0D + horizontalMotion * 60.0D; ++j)
            {
                double d5 = (double) (this.rand.nextFloat() * 2.0F - 1.0F);
                double d6 = (double) (this.rand.nextInt(2) * 2 - 1) * 0.7D;
                double d8;
                double d9;

                if (this.rand.nextBoolean())
                {
                    d8 = this.posX - d2 * d5 * 0.8D + d4 * d6;
                    d9 = this.posZ - d4 * d5 * 0.8D - d2 * d6;
                    this.worldObj.spawnParticle("splash", d8, this.posY - 0.125D, d9, this.motionX, this.motionY, this.motionZ);
                }
                else
                {
                    d8 = this.posX + d2 + d4 * d5 * 0.7D;
                    d9 = this.posZ + d4 - d2 * d5 * 0.7D;
                    this.worldObj.spawnParticle("splash", d8, this.posY - 0.125D, d9, this.motionX, this.motionY, this.motionZ);
                }
            }
        }

        double d11;
        double d12;

        if (this.worldObj.isRemote && this.isBoatEmpty)
        {
            if (this.boatPosRotationIncrements > 0)
            {
                d2 = this.posX + (this.boatX - this.posX) / (double) this.boatPosRotationIncrements;
                d4 = this.posY + (this.boatY - this.posY) / (double) this.boatPosRotationIncrements;
                d11 = this.posZ + (this.boatZ - this.posZ) / (double) this.boatPosRotationIncrements;
                d12 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double) this.rotationYaw);
                this.rotationYaw = (float) ((double) this.rotationYaw + d12 / (double) this.boatPosRotationIncrements);
                this.rotationPitch = (float) ((double) this.rotationPitch + (this.boatPitch - (double) this.rotationPitch) / (double) this.boatPosRotationIncrements);
                --this.boatPosRotationIncrements;
                this.setPosition(d2, d4, d11);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                d2 = this.posX + this.motionX;
                d4 = this.posY + this.motionY;
                d11 = this.posZ + this.motionZ;
                this.setPosition(d2, d4, d11);

                if (this.onGround)
                {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }
        }
        else
        {
            if (d0 < 1.0D)
            {
                d2 = d0 * 2.0D - 1.0D;
                this.motionY += 0.03999999910593033D * d2;
            }
            else
            {
                if (this.motionY < 0.0D)
                {
                    this.motionY /= 2.0D;
                }

                this.motionY += 0.007000000216066837D;
            }

            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase) this.riddenByEntity;
                float f = this.riddenByEntity.rotationYaw + -entitylivingbase.moveStrafing * 90.0F;
                this.motionX += -Math.sin((double) (f * (float) Math.PI / 180.0F)) * this.speedMultiplier * (double) entitylivingbase.moveForward * 0.05000000074505806D;
                this.motionZ += Math.cos((double) (f * (float) Math.PI / 180.0F)) * this.speedMultiplier * (double) entitylivingbase.moveForward * 0.05000000074505806D;
            }

            d2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d2 > 0.35D)
            {
                d4 = 0.35D / d2;
                this.motionX *= d4;
                this.motionZ *= d4;
                d2 = 0.35D;
            }

            if (d2 > horizontalMotion && this.speedMultiplier < 0.35D)
            {
                this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;

                if (this.speedMultiplier > 0.35D)
                {
                    this.speedMultiplier = 0.35D;
                }
            }
            else
            {
                this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;

                if (this.speedMultiplier < 0.07D)
                {
                    this.speedMultiplier = 0.07D;
                }
            }

            int l;

            for (l = 0; l < 4; ++l)
            {
                int i1 = MathHelper.floor_double(this.posX + ((double) (l % 2) - 0.5D) * 0.8D);
                j = MathHelper.floor_double(this.posZ + ((double) (l / 2) - 0.5D) * 0.8D);

                for (int j1 = 0; j1 < 2; ++j1)
                {
                    int k = MathHelper.floor_double(this.posY) + j1;
                    Block block = this.worldObj.getBlock(i1, k, j);

                    if (block == Blocks.snow_layer)
                    {
                        this.worldObj.setBlockToAir(i1, k, j);
                        this.isCollidedHorizontally = false;
                    }
                    else if (block == Blocks.waterlily)
                    {
                        this.worldObj.func_147480_a(i1, k, j, true);
                        this.isCollidedHorizontally = false;
                    }
                }
            }

            if (this.onGround)
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            /* if (this.isCollidedHorizontally && d10 > 0.2D)
            {
                if (!this.worldObj.isRemote && !this.isDead)
                {
                    this.setDead();

                    for (l = 0; l < 3; ++l)
                    {
                        this.func_145778_a(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                    }

                    for (l = 0; l < 2; ++l)
                    {
                        this.func_145778_a(Items.stick, 1, 0.0F);
                    }
                }
            }
            else
            {
                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }*/

            this.rotationPitch = 0.0F;
            d4 = (double) this.rotationYaw;
            d11 = this.prevPosX - this.posX;
            d12 = this.prevPosZ - this.posZ;

            if (d11 * d11 + d12 * d12 > 0.001D)
            {
                d4 = (double) ((float) (Math.atan2(d12, d11) * 180.0D / Math.PI));
            }

            double d7 = MathHelper.wrapAngleTo180_double(d4 - (double) this.rotationYaw);

            if (d7 > 20.0D)
            {
                d7 = 20.0D;
            }

            if (d7 < -20.0D)
            {
                d7 = -20.0D;
            }

            this.rotationYaw = (float) ((double) this.rotationYaw + d7);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            if (!this.worldObj.isRemote)
            {
                List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

                if (list != null && !list.isEmpty())
                {
                    for (int k1 = 0; k1 < list.size(); ++k1)
                    {
                        Entity entity = (Entity) list.get(k1);

                        if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityBoat)
                        {
                            entity.applyEntityCollision(this);
                        }
                    }
                }

                if (this.riddenByEntity != null && this.riddenByEntity.isDead)
                {
                    this.riddenByEntity = null;
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (!this.worldObj.isRemote && !this.isDead)
        {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + damage * 10.0F);
            this.setBeenAttacked();
            boolean flag = damageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer) damageSource.getEntity()).capabilities.isCreativeMode;

            if (flag || this.getDamageTaken() > 40.0F)
            {
                if (this.riddenByEntity != null)
                {
                    this.riddenByEntity.mountEntity(this);
                }

                if (!flag)
                {
                    this.entityDropItem(new ItemStack(ModItems.component, 1, motorized ? 8 : 7), 0.0f);
                }

                this.setDead();
            }

            return true;
        }
        else
        {
            return true;
        }
    }

    @Override
    public EntityItem func_145778_a(Item item, int quantity, float someFloat)
    {
        return this.entityDropItem(new ItemStack(item, quantity, 0), someFloat);
    }

    @Override
    public boolean interactFirst(EntityPlayer p_130002_1_)
    {
        if (inflation < 1.0f) return false;
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != p_130002_1_)
        {
            return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                p_130002_1_.mountEntity(this);
            }

            return true;
        }
    }

    /**
     * Save the entity to NBT (calls an abstract helper method to write extra data)
     */
    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        writeEntityToNBT(compound);
    }

    /**
     * Reads the entity from NBT (calls an abstract helper method to read specialized data)
     */
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        readEntityFromNBT(compound);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setFloat("Inflation", inflation);
        compound.setBoolean("Motorized", motorized);
        compound.setBoolean("Inflated", inflated);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("Inflation"))
        {
            inflation = compound.getFloat("Inflation");
        }
        if (compound.hasKey("Motorized"))
        {
            motorized = compound.getBoolean("Motorized");
        }
        if (compound.hasKey("Inflated"))
        {
            inflated = compound.getBoolean("Inflated");
        }
    }

    public void setMotorized(boolean motorized)
    {
        this.motorized = motorized;
    }

    @Override
    public boolean updateTankSlots()
    {
        return false;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {

    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {

    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[0];
    }

    @Override
    public void dirtyInventory()
    {

    }

    @Override
    public void dirtyTanks()
    {

    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack stack)
    {

    }

    @Override
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
        return null;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return 0;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int p_70301_1_)
    {
        return null;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
    {
        return null;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_)
    {
        return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
    {

    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName()
    {
        return null;
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 0;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void markDirty()
    {

    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return false;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return false;
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        data.writeBoolean(this.motorized);
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        this.motorized = data.readBoolean();
    }
}
