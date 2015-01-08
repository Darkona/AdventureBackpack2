package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.AdventureBackpack;
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
        int counter = 0;
        EntityRegistry.registerModEntity(EntityInflatableBoat.class, "inflatableBoat", counter++, AdventureBackpack.instance, 64, 1, true);
    }
}
