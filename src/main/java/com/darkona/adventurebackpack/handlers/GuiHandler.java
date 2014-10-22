package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.gui.GuiAdvBackpack;
import com.darkona.adventurebackpack.client.gui.GuiCraftAdvBackpack;
import com.darkona.adventurebackpack.inventory.BackCraftContainer;
import com.darkona.adventurebackpack.inventory.BackpackContainer;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Darkona on 12/10/2014.
 */
public class GuiHandler implements IGuiHandler
{

    public GuiHandler()
    {
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        InventoryItem inv;
        TileEntity te;
        switch (ID)
        {
            case 0:
                //ADVENTUREBACKPACK GUI FROM TILE ENTITY
                te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileAdventureBackpack)
                {
                    return new BackpackContainer(player.inventory, (TileAdventureBackpack) te, BackpackContainer.SOURCE_TILE);
                }
                break;
            case 1:
                //ADVENTUREBACKPACK GUI FROM WEARING BACKPACK/KEYBIND
                inv = Wearing.getBackpackInv(player, true);
                if (inv.getInventoryItem() != null)
                {
                    return new BackpackContainer(player.inventory, inv, BackpackContainer.SOURCE_ITEM);
                }
                break;
            case 2:
                //ADVENTUREBACKPACK GUI FROM HOLDING BACKPACK /RIGHT CLICK
                inv = Wearing.getBackpackInv(player, false);
                if (inv.getInventoryItem() != null)
                {
                    return new BackpackContainer(player.inventory, inv, BackpackContainer.SOURCE_ITEM);
                }
                break;
            case 3:
                //ADVENTUREBACKPACK CRAFT GUI FROM TILE ENTITY
                te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileAdventureBackpack)
                {
                    return new BackCraftContainer(player, (TileAdventureBackpack) te);
                }
                break;
            case 4:
                //ADVENTUREBACKPACK CRAFT GUI FROM WEARING BACKPACK/KEYBIND
                inv = Wearing.getBackpackInv(player, true);
                if (inv.getInventoryItem() != null)
                {
                    return new BackCraftContainer(player, world, inv);
                }
                break;
            case 5:
                //ADVENTUREBACKPACK CRAFT GUI FROM HOLDING BACKPACK / RIGHT CLICK
                inv = Wearing.getBackpackInv(player, false);
                if (inv.getInventoryItem() != null)
                {
                    return new BackCraftContainer(player, world, inv);
                }
                break;
            default:
                player.closeScreen();
                break;
        }

        return null;

    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        InventoryItem inv;
        TileEntity te;
        switch (ID)
        {
            case 0:
                //ADVENTUREBACKPACK GUI FROM TILE ENTITY
                te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileAdventureBackpack)
                {
                    return new GuiAdvBackpack(player, (TileAdventureBackpack) te);
                }
                break;
            case 1:
                //ADVENTUREBACKPACK GUI FROM WEARING BACKPACK/KEYBIND
                inv = Wearing.getBackpackInv(player, true);
                if (inv.getInventoryItem() != null)
                {
                    return new GuiAdvBackpack(player, inv, true);
                }
                break;
            case 2:
                //ADVENTUREBACKPACK GUI FROM HOLDING BACKPACK /RIGHT CLICK
                inv = Wearing.getBackpackInv(player, false);
                if (inv.getInventoryItem() != null)
                {
                    return new GuiAdvBackpack(player, inv, false);
                }
                break;
            case 3:
                //ADVENTUREBACKPACK CRAFT GUI FROM TILE ENTITY
                te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileAdventureBackpack)
                {
                    return new GuiCraftAdvBackpack(player, (TileAdventureBackpack) te);
                }
                break;
            case 4:
                //ADVENTUREBACKPACK CRAFT GUI FROM WEARING BACKPACK/KEYBIND
                inv = Wearing.getBackpackInv(player, true);
                if (inv.getInventoryItem() != null)
                {
                    return new GuiCraftAdvBackpack(player, inv, true);
                }
                break;
            case 5:
                //ADVENTUREBACKPACK CRAFT GUI FROM HOLDING BACKPACK / RIGHT CLICK
                inv = Wearing.getBackpackInv(player, false);
                if (inv.getInventoryItem() != null)
                {
                    return new GuiCraftAdvBackpack(player, inv, false);
                }
                break;
            default:
                player.closeScreen();
                break;
        }
        return null;
    }
}
