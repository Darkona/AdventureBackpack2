package com.darkona.adventurebackpack.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class Visuals
{

    public static void NyanParticles(EntityPlayer player, World world)
    {
       // World world = player.worldObj;
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
        //(world.rand.nextFloat() - 0.25F) * 0.25F
        Vec3 playerPosition = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        Vec3 victor = Vec3.createVectorHelper(-0.25D, -0.19D, -0.40D);
        victor.rotateAroundY(-player.renderYawOffset * 3.141593F / 180.0F);
        //victor = victor.addVector(-player.motionX * 0.2D, -player.motionY * 0.2D, -player.motionZ * 0.2D);
        Vec3 finalPosition = playerPosition.addVector(victor.xCoord, victor.yCoord, victor.zCoord);
        world.spawnParticle("smoke", finalPosition.xCoord, finalPosition.yCoord, finalPosition.zCoord, 0, -0.4, 0);
    }

}
