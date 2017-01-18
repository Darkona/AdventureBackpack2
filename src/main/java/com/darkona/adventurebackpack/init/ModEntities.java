package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;

import cpw.mods.fml.common.registry.EntityRegistry;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class ModEntities
{

    public static void init()
    {
        //Color color1 = new Color(227,129,216);
        //Color color2 = new Color(0, 0, 0);
        //int color_int1 = (color1.getRed() << 16) + (color1.getGreen() << 8) + color1.getBlue();
        //int color_int2 = (color2.getRed() << 16) + (color2.getGreen() << 8) + color2.getBlue();
        int counter = 0;
        EntityRegistry.registerModEntity(EntityInflatableBoat.class, "inflatableBoat", counter++, AdventureBackpack.instance, 64, 1, true);

        EntityRegistry.registerModEntity(EntityFriendlySpider.class, "rideableSpider", counter++, AdventureBackpack.instance, 64, 2, true);
        //EntityRegistry.registerGlobalEntityID(EntityRideableSpider.class, "rideableSpider", EntityRegistry.findGlobalUniqueEntityId(),color_int2,color_int1);
    }
}
