package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderBolt extends RenderArrow
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "textures/entity/bolt.png");

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TEXTURE;
    }
}
