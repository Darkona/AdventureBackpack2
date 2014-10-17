package com.darkona.adventurebackpack.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class Visuals
{

    public static void NyanParticles(EntityPlayer player)
    {
        World world = player.worldObj;
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
}
