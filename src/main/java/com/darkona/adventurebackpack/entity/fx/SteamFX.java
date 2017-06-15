package com.darkona.adventurebackpack.entity.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created on 19/01/2015
 *
 * @author Darkona
 */

@SideOnly(Side.CLIENT)
public class SteamFX extends EntityFX
{
    private float smokeParticleScale;

    public SteamFX(World world, double x, double y, double z, double velX, double velY, double velZ)
    {
        this(world, x, y, z, velX, velY, velZ, 1.0F);
    }

    public SteamFX(World world, double x, double y, double z, double velX, double velY, double velZ, float scale)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.1D;
        this.motionY *= 0.1D;
        this.motionZ *= 0.1D;
        this.motionX += velX;
        this.motionY += velY;
        this.motionZ += velZ;
        this.particleRed = 206;
        this.particleGreen = 206;
        this.particleBlue = 206;
        this.particleScale *= 0.75F;
        this.particleScale *= scale;
        this.smokeParticleScale = this.particleScale;
        this.particleMaxAge = ((int) (8.0D / (Math.random() * 0.8D + 0.2D)));
        this.particleMaxAge = ((int) (this.particleMaxAge * scale));
        this.noClip = true;
    }

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float age = (this.particleAge + par2) / this.particleMaxAge * 32.0F;
        if (age < 0.0F)
        {
            age = 0.0F;
        }
        if (age > 1.0F)
        {
            age = 1.0F;
        }
        this.particleScale = (this.smokeParticleScale * age);
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge)
        {
            setDead();
        }
        setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.motionY += 0.003;
        moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }
        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;
        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
}
