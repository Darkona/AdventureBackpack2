package com.darkona.adventurebackpack.entity;

import com.darkona.adventurebackpack.client.GrappleClient;
import com.darkona.adventurebackpack.common.GrappleLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * The grappling hook of the Clockwork Crossbow. It flies straight from the shooter's eyes, catches the first solid
 * block it meets within {@code crossbow.grapple.maxDistance}, and then pulls its owner towards that point.
 * <p>
 * The server decides everything: where the hook catches, when it lets go (sneak, arrival, timeout, jump request,
 * crossbow out of the hand, owner gone) and it resets the owner's fall distance. A player's movement is computed by
 * his own client in 1.7.10, so the owner's client applies the pull to its own player with the speed the server sent
 * in the spawn data; there is no packet per tick and no teleport. The state (flying or hooked, and the anchor) reaches
 * the clients once, through the data watcher.
 * <p>
 * The work runs only while a hook exists: nothing ticks for a player without one. The hook is never saved: a hook in
 * a chunk being saved, or after a restart, is simply gone.
 */
public class EntityGrapple extends Entity implements IEntityAdditionalSpawnData
{
    private static final int DW_STATE = 16;
    private static final int DW_ANCHOR_X = 17;
    private static final int DW_ANCHOR_Y = 18;
    private static final int DW_ANCHOR_Z = 19;

    public static final byte FLYING = 0;
    public static final byte HOOKED = 1;

    /** Ticks between two checks that the anchor block is still there. */
    private static final int ANCHOR_CHECK_TICKS = 10;

    private EntityPlayer owner;
    private int ownerId = -1;
    /**
     * Server: hotbar slot of the crossbow that fired. The hook lets go when the owner holds something else. Not the
     * stack object: 1.7.10 replaces the held stack with a copy after every right click.
     */
    private int launcherSlot = -1;
    /** Pull speed and release distance chosen by the server, sent to the clients in the spawn data. */
    private double pullSpeed;
    private double releaseDistance;
    private int pullTicks;
    private int anchorBlockX;
    private int anchorBlockY;
    private int anchorBlockZ;
    /** Client: the owner asked to let go (jump); stop pulling before the server's answer arrives. */
    public boolean releasedLocally;

    private final double[] pull = new double[3];

    public EntityGrapple(World world)
    {
        super(world);
        setSize(0.25F, 0.25F);
        ignoreFrustumCheck = true;
    }

    /** A hook leaving {@code owner}'s eyes along his look, fired by the crossbow he holds. Server side. */
    public EntityGrapple(World world, EntityPlayer owner)
    {
        this(world);
        this.owner = owner;
        this.ownerId = owner.getEntityId();
        this.launcherSlot = owner.inventory.currentItem;
        this.pullSpeed = ConfigHandler.GRAPPLE_PULL_SPEED;
        this.releaseDistance = ConfigHandler.GRAPPLE_RELEASE_DISTANCE;
        GrappleLogic.look(owner.rotationYaw, owner.rotationPitch, pull);
        double eyes = owner.boundingBox.minY + GrappleLogic.EYE_HEIGHT - (owner.isSneaking() ? 0.08 : 0.0);
        setLocationAndAngles(owner.posX + pull[0] * 0.3, eyes + pull[1] * 0.3, owner.posZ + pull[2] * 0.3, owner.rotationYaw, owner.rotationPitch);
        double speed = ConfigHandler.GRAPPLE_HOOK_SPEED;
        motionX = pull[0] * speed;
        motionY = pull[1] * speed;
        motionZ = pull[2] * speed;
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(DW_STATE, (byte) FLYING);
        dataWatcher.addObject(DW_ANCHOR_X, 0F);
        dataWatcher.addObject(DW_ANCHOR_Y, 0F);
        dataWatcher.addObject(DW_ANCHOR_Z, 0F);
    }

    public boolean isHooked()
    {
        return dataWatcher.getWatchableObjectByte(DW_STATE) == HOOKED;
    }

    public EntityPlayer getOwner()
    {
        return owner;
    }

    public double getPullSpeed()
    {
        return pullSpeed;
    }

    public double getReleaseDistance()
    {
        return releaseDistance;
    }

    /** The live hook with id {@code id} fired by {@code player}, or null. Tier 2: one map lookup. */
    public static EntityGrapple find(World world, int id, EntityPlayer player)
    {
        Entity entity = world.getEntityByID(id);
        if (entity instanceof EntityGrapple && !entity.isDead && ((EntityGrapple) entity).owner == player)
        {
            return (EntityGrapple) entity;
        }
        return null;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (worldObj.isRemote)
        {
            clientTick();
        } else
        {
            serverTick();
        }
    }

    private void serverTick()
    {
        if (owner == null || owner.isDead || owner.worldObj != worldObj || !holdsLauncher(owner))
        {
            release();
            return;
        }
        if (!isHooked())
        {
            fly();
            return;
        }
        pullTicks++;
        if (pullTicks % ANCHOR_CHECK_TICKS == 0 && !isSolid(anchorBlockX, anchorBlockY, anchorBlockZ))
        {
            release();
            return;
        }
        double distance = GrappleLogic.pull(owner.posX, owner.boundingBox.minY + GrappleLogic.BODY_CENTER, owner.posZ, posX, posY, posZ, pullSpeed, pull);
        if (GrappleLogic.release(owner.isSneaking(), distance, releaseDistance, pullTicks, ConfigHandler.GRAPPLE_MAX_PULL_TICKS, ConfigHandler.GRAPPLE_MAX_DISTANCE) != GrappleLogic.HOLD)
        {
            release();
            return;
        }
        if (ConfigHandler.GRAPPLE_NO_FALL_DAMAGE) owner.fallDistance = 0F;
    }

    /** One tick of flight: catch the first solid block on the way, or come back past the range or the loaded world. */
    private void fly()
    {
        double nextX = posX + motionX;
        double nextY = posY + motionY;
        double nextZ = posZ + motionZ;
        if (!worldObj.blockExists(MathHelper.floor_double(nextX), MathHelper.floor_double(nextY), MathHelper.floor_double(nextZ)))
        {
            setDead();
            return;
        }
        MovingObjectPosition hit = worldObj.func_147447_a(Vec3.createVectorHelper(posX, posY, posZ), Vec3.createVectorHelper(nextX, nextY, nextZ), false, true, false);
        if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            if (!isSolid(hit.blockX, hit.blockY, hit.blockZ))
            {
                setDead();
                return;
            }
            setPosition(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
            motionX = motionY = motionZ = 0;
            anchorBlockX = hit.blockX;
            anchorBlockY = hit.blockY;
            anchorBlockZ = hit.blockZ;
            dataWatcher.updateObject(DW_ANCHOR_X, (float) posX);
            dataWatcher.updateObject(DW_ANCHOR_Y, (float) posY);
            dataWatcher.updateObject(DW_ANCHOR_Z, (float) posZ);
            dataWatcher.updateObject(DW_STATE, HOOKED);
            worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 0.6F);
            if (ConfigHandler.GRAPPLE_NO_FALL_DAMAGE) owner.fallDistance = 0F;
            return;
        }
        setPosition(nextX, nextY, nextZ);
        double dx = posX - owner.posX;
        double dy = posY - owner.boundingBox.minY - GrappleLogic.EYE_HEIGHT;
        double dz = posZ - owner.posZ;
        if (GrappleLogic.outOfRange(dx * dx + dy * dy + dz * dz, ConfigHandler.GRAPPLE_MAX_DISTANCE))
        {
            setDead();
        }
    }

    private boolean holdsLauncher(EntityPlayer player)
    {
        ItemStack held = player.getHeldItem();
        return player.inventory.currentItem == launcherSlot && held != null && held.getItem() == ModItems.cwxbow;
    }

    private boolean isSolid(int x, int y, int z)
    {
        Block block = worldObj.getBlock(x, y, z);
        return block.getMaterial().isSolid() && block.getMaterial().blocksMovement();
    }

    /** Lets go of the owner. Server side; the client hook disappears with the entity. */
    public void release()
    {
        if (owner != null && isHooked() && ConfigHandler.GRAPPLE_NO_FALL_DAMAGE) owner.fallDistance = 0F;
        setDead();
    }

    private void clientTick()
    {
        if (owner == null && ownerId >= 0)
        {
            Entity entity = worldObj.getEntityByID(ownerId);
            if (entity instanceof EntityPlayer) owner = (EntityPlayer) entity;
        }
        if (isHooked())
        {
            setPosition(dataWatcher.getWatchableObjectFloat(DW_ANCHOR_X), dataWatcher.getWatchableObjectFloat(DW_ANCHOR_Y), dataWatcher.getWatchableObjectFloat(DW_ANCHOR_Z));
            motionX = motionY = motionZ = 0;
            if (owner != null) GrappleClient.pull(this, owner, pull);
        } else
        {
            setPosition(posX + motionX, posY + motionY, posZ + motionZ);
        }
    }

    @Override
    public void setDead()
    {
        super.setDead();
        if (worldObj.isRemote) GrappleClient.forget(this);
    }

    @Override
    public boolean isInRangeToRenderDist(double distanceSq)
    {
        return distanceSq < 64.0 * 64.0;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    /** Never saved with the chunk: a hook only makes sense while its owner holds the crossbow. */
    @Override
    public boolean writeToNBTOptional(NBTTagCompound tag)
    {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
        setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeInt(ownerId);
        buffer.writeFloat((float) pullSpeed);
        buffer.writeFloat((float) releaseDistance);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        ownerId = buffer.readInt();
        pullSpeed = buffer.readFloat();
        releaseDistance = buffer.readFloat();
    }
}
