package com.darkona.adventurebackpack.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.darkona.adventurebackpack.entity.fx.SteamFX;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class Visuals
{
    public static void NyanParticles(EntityPlayer player, World world)
    {
        int i = 2;
        for (int j = 0; j < i * 3; ++j)
        {
            float f = world.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = world.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * i * 0.5F * f1;
            world.spawnParticle("note",
                    player.posX + f2,
                    player.boundingBox.minY + 0.8f,
                    player.posZ + f3,
                    (double) (float) Math.pow(2.0D, (world.rand.nextInt(169) - 12) / 12.0D) / 24.0D,
                    -1.0D,
                    0.0D);
        }
    }

    public static void SlimeParticles(EntityPlayer player, World world)
    {
        int i = 3;
        for (int j = 0; j < i * 2; ++j)
        {
            float f = world.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = world.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * i * 0.5F * f1;
            world.spawnParticle("slime", player.posX + f2, player.boundingBox.minY, player.posZ + f3, 0.0D, 0.0625D, 0.0D);
        }
    }

    public static void CopterParticles(EntityPlayer player, World world)
    {
        Vec3 playerPosition = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        Vec3 victor = Vec3.createVectorHelper(-0.25D, -0.19D, -0.40D);
        victor.rotateAroundY(-player.renderYawOffset * 3.141593F / 180.0F);
        Vec3 finalPosition = playerPosition.addVector(victor.xCoord, victor.yCoord, victor.zCoord);
        world.spawnParticle("smoke", finalPosition.xCoord, finalPosition.yCoord, finalPosition.zCoord, 0, -0.4, 0);
    }

    public static void JetpackParticles(EntityPlayer player, World world)
    {
        Vec3 playerPosition = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        Vec3 victor = Vec3.createVectorHelper(-0.5D, -0.5D, -0.5D);
        Vec3 victoria = Vec3.createVectorHelper(0.5D, -0.5D, -0.5D);
        victor.rotateAroundY(-player.renderYawOffset * 3.141593F / 180.0F);
        victoria.rotateAroundY(-player.renderYawOffset * 3.141593F / 180.0F);
        Vec3 leftPosition = victor.addVector(playerPosition.xCoord, playerPosition.yCoord, playerPosition.zCoord);
        Vec3 rightPosition = victoria.addVector(playerPosition.xCoord, playerPosition.yCoord, playerPosition.zCoord);
        for (int i = 0; i < 4; i++)
        {
            spawnParticle("steam", leftPosition.xCoord, leftPosition.yCoord, leftPosition.zCoord, 0.04 * world.rand.nextGaussian(), -0.8, 0.04 * world.rand.nextGaussian());
            spawnParticle("steam", rightPosition.xCoord, rightPosition.yCoord, rightPosition.zCoord, 0.04 * world.rand.nextGaussian(), -0.8, 0.04 * world.rand.nextGaussian());
        }
    }

    private static Minecraft mc = Minecraft.getMinecraft();
    private static World theWorld = mc.theWorld;

    public static EntityFX spawnParticle(String particleName, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null)
        {
            int particleSetting = mc.gameSettings.particleSetting;

            if (particleSetting == 1 && theWorld.rand.nextInt(3) == 0)
            {
                particleSetting = 2;
            }

            double renderX = mc.renderViewEntity.posX - x;
            double renderY = mc.renderViewEntity.posY - y;
            double renderZ = mc.renderViewEntity.posZ - z;
            EntityFX entityFX = null;
            double var22 = 16.0D;

            if (renderX * renderX + renderY * renderY + renderZ * renderZ > var22 * var22)
            {
                return null;
            }
            else if (particleSetting > 1)
            {
                return null;
            }
            else
            {
                if (particleName.equals("steam"))
                {
                    entityFX = new SteamFX(theWorld, x, y, z, (float) motionX, (float) motionY, (float) motionZ);
                }
                mc.effectRenderer.addEffect(entityFX);
                return entityFX;
            }
        }
        return null;
    }
}
