package com.darkona.adventurebackpack.entity;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * A bolt shot by the Clockwork Crossbow. It flies, sticks and hurts like a vanilla arrow (and, with no renderer of
 * its own, it is drawn as one); it only adds its type, read by {@code CrossbowEventHandler} when it hits. The type
 * reaches the client in the spawn data so a future renderer can tint it.
 */
public class EntityBolt extends EntityArrow implements IEntityAdditionalSpawnData
{
    /** Vanilla multiplies the speed given to the arrow constructor by this. */
    private static final float ARROW_SPEED_FACTOR = 1.5F;

    private int boltType = CrossbowLogic.NORMAL;

    public EntityBolt(World world)
    {
        super(world);
    }

    /**
     * @param speed blocks per tick as the bolt leaves the crossbow
     * @param type  one of the {@link CrossbowLogic} bolt types
     */
    public EntityBolt(World world, EntityLivingBase shooter, float speed, int type)
    {
        super(world, shooter, speed / ARROW_SPEED_FACTOR);
        this.boltType = CrossbowLogic.clampType(type);
        if (boltType == CrossbowLogic.BLAZING)
        {
            setFire(100);
        }
    }

    public int getBoltType()
    {
        return boltType;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        tag.setByte("BoltType", (byte) boltType);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        boltType = CrossbowLogic.clampType(tag.getByte("BoltType"));
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeByte(boltType);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        boltType = CrossbowLogic.clampType(buffer.readByte());
    }
}
