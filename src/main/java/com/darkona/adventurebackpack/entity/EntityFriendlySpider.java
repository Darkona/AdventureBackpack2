package com.darkona.adventurebackpack.entity;

import com.darkona.adventurebackpack.common.SpiderCare;
import com.darkona.adventurebackpack.common.SpiderTaming;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.ai.EntityAISpiderFollowOwner;
import com.darkona.adventurebackpack.entity.ai.EntityAISpiderSit;
import com.darkona.adventurebackpack.entity.ai.SpiderNavigate;
import com.darkona.adventurebackpack.util.TickThrottle;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityFriendlySpider extends EntityCreature
{
    private static final int OWNER_CHECK_TICKS = 20;
    private static final int BIT_WALL = 1, BIT_SITTING = 2;
    private static final int WALL_GRACE_TICKS = 5;

    private int jumpTicks;
    private UUID ownerId;
    private final EntityAIControlledByPlayer aiControlledByPlayer;
    private final SpiderNavigate spiderNav;
    private int wallGrace;

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ConfigHandler.SPIDER_TAMED_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.15D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    }

    public EntityFriendlySpider(World world) {
        super(world);
        this.setSize(1.4F, 0.9F);
        this.spiderNav = new SpiderNavigate(this, world);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAISpiderSit(this));
        this.tasks.addTask(2, this.aiControlledByPlayer = new EntityAIControlledByPlayer(this, 0.3F));
        this.tasks.addTask(5, new EntityAISpiderFollowOwner(this, 2.0D));
        this.tasks.addTask(6, new EntityAIWander(this, 0.7D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }
    @Override
    protected boolean isAIEnabled()
    {
        return true;
    }
    @Override
    public int getTalkInterval()
    {
        return 300;
    }
    @Override
    protected String getLivingSound()
    {
        return "mob.spider.say";
    }

    @Override
    protected String getHurtSound()
    {
        return "mob.spider.say";
    }

    @Override
    protected String getDeathSound()
    {
        return "mob.spider.death";
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block)
    {
        this.playSound("mob.spider.step", 0.15F, 1.0F);
    }

    @Override
    protected String getSwimSound()
    {
        return "game.hostile.swim";
    }

    @Override
    protected String getSplashSound()
    {
        return "game.hostile.swim.splash";
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (super.attackEntityFrom(damageSource, amount))
        {
            setSitting(false);
            Entity entity = damageSource.getEntity();

            if (this.riddenByEntity != entity && this.ridingEntity != entity)
            {
                if (entity != this)
                {
                    this.entityToAttack = entity;
                }

                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public float getBlockPathWeight(int p_70783_1_, int p_70783_2_, int p_70783_3_)
    {
        return 0.5F - this.worldObj.getLightBrightness(p_70783_1_, p_70783_2_, p_70783_3_);
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return false;
    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    @Override
    public PathNavigate getNavigator()
    {
        return spiderNav != null ? spiderNav : super.getNavigator();
    }

    public SpiderNavigate navigator()
    {
        return spiderNav;
    }

    @Override
    protected void updateAITick()
    {
        super.updateAITick();
        spiderNav.onUpdateNavigation();
    }

    public void setOwner(UUID owner)
    {
        this.ownerId = owner;
    }

    public UUID getOwnerId()
    {
        return ownerId;
    }

    public boolean isOwnedBy(EntityPlayer player)
    {
        return ownerId != null && player != null && ownerId.equals(player.getUniqueID());
    }

    public EntityPlayer getOwnerPlayer()
    {
        return ownerId == null ? null : worldObj.func_152378_a(ownerId);
    }

    public static boolean canRide(EntityPlayer owner)
    {
        return owner != null && !owner.isDead && ConfigHandler.BACKPACK_ABILITIES && ConfigHandler.SPIDER_ENABLED
                && Wearing.isWearingTheRightBackpack(owner, "Spider");
    }

    public void release()
    {
        if (worldObj.isRemote || isDead) return;
        if (ownerId != null) SpiderTaming.forget(worldObj, ownerId, getUniqueID());
        if (riddenByEntity != null)
        {
            riddenByEntity.mountEntity(null);
        }
        EntitySpider wild = new EntitySpider(worldObj);
        wild.setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
        wild.setHealth(Math.min(getHealth(), wild.getMaxHealth()));
        if (hasCustomNameTag())
        {
            wild.setCustomNameTag(getCustomNameTag());
        }
        setDead();
        worldObj.spawnEntityInWorld(wild);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        if (ownerId != null)
        {
            compound.setLong("OwnerUUIDMost", ownerId.getMostSignificantBits());
            compound.setLong("OwnerUUIDLeast", ownerId.getLeastSignificantBits());
        }
        compound.setBoolean("Sitting", isSitting());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        ownerId = compound.hasKey("OwnerUUIDMost") && compound.hasKey("OwnerUUIDLeast")
                ? new UUID(compound.getLong("OwnerUUIDMost"), compound.getLong("OwnerUUIDLeast"))
                : null;
        setSitting(compound.getBoolean("Sitting"));
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ConfigHandler.SPIDER_TAMED_HEALTH);
        setHealth(SpiderCare.clampLoadedHealth(getHealth(), getMaxHealth()));
    }

    @Override
    public void onDeath(DamageSource source)
    {
        super.onDeath(source);
        if (!worldObj.isRemote && ownerId != null)
        {
            SpiderTaming.forget(worldObj, ownerId, getUniqueID());
        }
    }

    @Override
    public void setDead()
    {
        boolean wasAlive = !isDead;
        super.setDead();
        if (wasAlive && worldObj != null && !worldObj.isRemote && ownerId != null)
        {
            SpiderTaming.forget(worldObj, ownerId, getUniqueID());
        }
    }

    @Override
    protected boolean func_146066_aG()
    {
        return true;
    }
    @Override
    public ItemStack getHeldItem()
    {
        return null;
    }

    @Override
    public ItemStack getEquipmentInSlot(int slot)
    {
        return null;
    }

    @Override
    public void setCurrentItemOrArmor(int slot, ItemStack stack)
    {
    }

    @Override
    public ItemStack[] getLastActiveItems()
    {
        return new ItemStack[0];
    }

    @Override
    public boolean canRiderInteract() {
        return false;
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        if (this.worldObj.isRemote) return false;
        ItemStack held = player.getCurrentEquippedItem();
        SpiderCare.Action action = SpiderCare.interactAction(isOwnedBy(player), this.riddenByEntity != null,
                player.isSneaking(), handOf(held), canRide(player), SpiderCare.canHeal(getHealth(), getMaxHealth()));
        switch (action)
        {
            case RELEASE:
                release();
                return true;
            case TOGGLE_SIT:
                setSitting(!isSitting());
                getNavigator().clearPathEntity();
                setJumping(false);
                return true;
            case HEAL:
                setHealth(SpiderCare.healedHealth(getHealth(), getMaxHealth(), ConfigHandler.SPIDER_HEAL_AMOUNT));
                if (!player.capabilities.isCreativeMode && --held.stackSize <= 0)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
                worldObj.setEntityState(this, (byte) 7);
                playSound("random.eat", 0.6F, 0.9F + rand.nextFloat() * 0.2F);
                return true;
            case MOUNT:
                setSitting(false);
                getNavigator().clearPathEntity();
                player.mountEntity(this);
                return true;
            default:
                return false;
        }
    }

    private static SpiderCare.Hand handOf(ItemStack held)
    {
        if (held == null || held.getItem() == null) return SpiderCare.Hand.EMPTY;
        Item item = held.getItem();
        if (item == Item.itemRegistry.getObject(ConfigHandler.SPIDER_TAME_ITEM)) return SpiderCare.Hand.TAME_ITEM;
        if (item == Item.itemRegistry.getObject(ConfigHandler.SPIDER_HEAL_ITEM)) return SpiderCare.Hand.HEAL_ITEM;
        return SpiderCare.Hand.OTHER;
    }

    @Override
    public void handleHealthUpdate(byte id)
    {
        if (id == 7)
        {
            for (int i = 0; i < 7; ++i)
            {
                worldObj.spawnParticle("heart", posX + (rand.nextFloat() * width * 2.0F) - width,
                        posY + 0.5D + (rand.nextFloat() * height), posZ + (rand.nextFloat() * width * 2.0F) - width,
                        rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D, rand.nextGaussian() * 0.02D);
            }
        }
        else
        {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    public boolean canBeSteered() {
        return true;
    }

    @Override
    protected Entity findPlayerToAttack() {
        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    public boolean canBePushed()
    {
        return !isClimbingWall() && super.canBePushed();
    }

    @Override
    protected void collideWithNearbyEntities()
    {
        if (!isClimbingWall()) super.collideWithNearbyEntities();
    }

    private boolean isClimbingWall()
    {
        return spiderNav != null && spiderNav.isClimbing() && riddenByEntity == null;
    }

    @Override
    public void applyEntityCollision(Entity entity)
    {
        if (!isOwnerEntity(entity)) super.applyEntityCollision(entity);
    }

    @Override
    protected void collideWithEntity(Entity entity)
    {
        if (!isOwnerEntity(entity)) super.collideWithEntity(entity);
    }

    private boolean isOwnerEntity(Entity entity)
    {
        return ownerId != null && entity instanceof EntityPlayer && ownerId.equals(entity.getUniqueID());
    }

    @Override
    public boolean shouldRiderFaceForward(EntityPlayer player) {
        return true;
    }
    public boolean isBesideClimbableBlock()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & BIT_WALL) != 0;
    }

    public void setBesideClimbableBlock(boolean beside)
    {
        setSpiderFlag(BIT_WALL, beside);
    }

    public boolean isSitting()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & BIT_SITTING) != 0;
    }

    public void setSitting(boolean sitting)
    {
        setSpiderFlag(BIT_SITTING, sitting);
    }

    private void setSpiderFlag(int mask, boolean on)
    {
        byte flags = this.dataWatcher.getWatchableObjectByte(16);
        byte updated = SpiderCare.withBit(flags, mask, on);
        if (updated != flags) this.dataWatcher.updateObject(16, Byte.valueOf(updated));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote && this.dataWatcher.hasChanges())
        {
            this.dataWatcher.func_111144_e();
        }
        if (this.riddenByEntity != null && this.riddenByEntity.isDead)
        {
            this.riddenByEntity = null;
        }
        if (!this.worldObj.isRemote)
        {
            if (this.isCollidedHorizontally) wallGrace = WALL_GRACE_TICKS;
            else if (wallGrace > 0) wallGrace--;
            this.setBesideClimbableBlock(this.isCollidedHorizontally || wallGrace > 0);
            if (!this.isDead && TickThrottle.every(this.worldObj.getTotalWorldTime(), this.getEntityId(), OWNER_CHECK_TICKS))
            {
                heartbeat();
            }
        }
    }

    private void heartbeat()
    {
        if (ownerId == null)
        {
            release();
            return;
        }
        SpiderTaming.touch(worldObj, ownerId, getUniqueID(), worldObj.provider.dimensionId,
                MathHelper.floor_double(posX) >> 4, MathHelper.floor_double(posZ) >> 4);
        if (riddenByEntity instanceof EntityPlayer && !canRide((EntityPlayer) riddenByEntity))
        {
            riddenByEntity.mountEntity(null);
        }
    }

    private void normalLivingUpdateWithNoAI(){
        if (this.jumpTicks > 0)
        {
            --this.jumpTicks;
        }

        if (this.newPosRotationIncrements > 0)
        {
            double d0 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double d1 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double d2 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double) this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else if (!this.isClientWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D)
        {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D)
        {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D)
        {
            this.motionZ = 0.0D;
        }

        this.worldObj.theProfiler.startSection("ai");

        if (this.isMovementBlocked())
        {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        }
        else if (this.isClientWorld())
        {
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");

        if (this.isJumping)
        {
            if (!this.isInWater() && !this.handleLavaMovement())
            {
                if (this.onGround && this.jumpTicks == 0)
                {
                    this.jump();
                    this.jumpTicks = 10;
                }
            }
            else
            {
                this.motionY += 0.03999999910593033D;
            }
        }
        else
        {
            this.jumpTicks = 0;
        }
        this.setJumping(false);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");

        if (!this.worldObj.isRemote)
        {
            this.collideWithNearbyEntities();
        }

        this.worldObj.theProfiler.endSection();
    }

    @Override
    public void onLivingUpdate() {
        if (this.riddenByEntity != null)
        {
            normalLivingUpdateWithNoAI();
        } else
        {
            super.onLivingUpdate();
        }
        this.updateArmSwingProgress();
    }

    @Override
    public double getMountedYOffset() {
        return super.getMountedYOffset();
    }

    @Override
    public void moveEntityWithHeading(float strafe, float forward) {
        if (this.riddenByEntity != null)
        {
            this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
            this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            strafe = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
            forward = ((EntityLivingBase)this.riddenByEntity).moveForward;

            if (forward <= 0.0F)
            {
                forward *= 0.25F;
            }
            this.stepHeight = 1.0F;
            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.2F;

            if (!this.worldObj.isRemote)
            {
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
                super.moveEntityWithHeading(strafe, forward);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d0 = this.posX - this.prevPosX;
            double d1 = this.posZ - this.prevPosZ;
            float f4 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

            if (f4 > 1.0F)
            {
                f4 = 1.0F;
            }

            this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
        else
        {
            this.stepHeight = 0.5F;
            this.jumpMovementFactor = 0.02F;
            super.moveEntityWithHeading(strafe, forward);
            if (spiderNav != null && spiderNav.isDescending() && (isBesideClimbableBlock() || spiderNav.isClinging()))
            {
                if (this.motionY < -0.15D) this.motionY = -0.15D;
                this.fallDistance = 0.0F;
            }
        }
    }

    @Override
    protected void attackEntity(Entity p_70785_1_, float p_70785_2_)
    {
        float f1 = this.getBrightness(1.0F);

        if (f1 > 0.5F && this.rand.nextInt(100) == 0)
        {
            this.entityToAttack = null;
        }
        else
        {
            if (p_70785_2_ > 2.0F && p_70785_2_ < 6.0F && this.rand.nextInt(10) == 0)
            {
                if (this.onGround)
                {
                    double d0 = p_70785_1_.posX - this.posX;
                    double d1 = p_70785_1_.posZ - this.posZ;
                    float f2 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
                    this.motionX = d0 / (double)f2 * 0.5D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                    this.motionZ = d1 / (double)f2 * 0.5D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                    this.motionY = 0.4000000059604645D;
                }
            }
            else
            {
                super.attackEntity(p_70785_1_, p_70785_2_);
            }
        }
    }

    @Override
    protected Item getDropItem()
    {
        return Items.string;
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        super.dropFewItems(p_70628_1_, p_70628_2_);

        if (p_70628_1_ && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + p_70628_2_) > 0))
        {
            this.dropItem(Items.spider_eye, 1);
        }
    }

    @Override
    public boolean isOnLadder()
    {
        if (!this.isBesideClimbableBlock()) return false;
        return this.riddenByEntity != null || !ConfigHandler.SPIDER_SMART_CLIMB || (spiderNav != null && spiderNav.wantsLadder());
    }

    @Override
    public void setInWeb() {}

    @Override
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect p_70687_1_)
    {
        return p_70687_1_.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(p_70687_1_);
    }
}
