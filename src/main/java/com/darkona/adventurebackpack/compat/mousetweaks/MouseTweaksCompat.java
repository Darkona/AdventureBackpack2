package com.darkona.adventurebackpack.compat.mousetweaks;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.gui.GuiAdvBackpack;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public final class MouseTweaksCompat
{
    private static Boolean loaded;

    private MouseTweaksCompat()
    {
    }

    private static boolean loaded()
    {
        if (loaded == null) loaded = Loader.isModLoaded("MouseTweaks");
        return loaded;
    }

    public static GuiAdvBackpack backpackGui(EntityPlayer player, TileAdventureBackpack tile)
    {
        return loaded() ? Factory.placed(player, tile) : new GuiAdvBackpack(player, tile);
    }

    public static GuiAdvBackpack backpackGui(EntityPlayer player, InventoryBackpack inventory, boolean wearing)
    {
        return loaded() ? Factory.item(player, inventory, wearing) : new GuiAdvBackpack(player, inventory, wearing);
    }

    private static final class Factory
    {
        static GuiAdvBackpack placed(EntityPlayer player, TileAdventureBackpack tile)
        {
            return new GuiAdvBackpackMT(player, tile);
        }

        static GuiAdvBackpack item(EntityPlayer player, InventoryBackpack inventory, boolean wearing)
        {
            return new GuiAdvBackpackMT(player, inventory, wearing);
        }
    }
}
