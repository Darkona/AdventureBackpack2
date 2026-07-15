package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.entity.EntityBolt;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * A crossbow bolt flies and sticks exactly like an arrow, so the vanilla arrow renderer draws it; only the texture
 * changes, one per bolt type (iron head, short shaft, fletching in the type's colour). The textures follow the
 * vanilla {@code arrow.png} layout. Locations are built once (performance contract, tier 4).
 */
@SideOnly(Side.CLIENT)
public class RenderBolt extends RenderArrow
{
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[CrossbowLogic.TYPES];

    static
    {
        String[] names = {"bolt", "boltBlazing", "boltFreezing", "boltPoison", "boltStunning"};
        for (int type = 0; type < CrossbowLogic.TYPES; type++)
        {
            TEXTURES[type] = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "textures/entity/" + names[type] + ".png");
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        int type = entity instanceof EntityBolt ? ((EntityBolt) entity).getBoltType() : CrossbowLogic.NORMAL;
        return TEXTURES[CrossbowLogic.clampType(type)];
    }
}
