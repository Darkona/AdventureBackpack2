package com.darkona.adventurebackpack.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.gui.GuiAdvBackpack;
import com.darkona.adventurebackpack.client.gui.GuiCoalJetpack;
import com.darkona.adventurebackpack.client.gui.GuiCopterPack;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.ContainerCopter;
import com.darkona.adventurebackpack.inventory.ContainerJetpack;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.inventory.InventoryCoalJetpack;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.util.Wearing;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class GuiHandler implements IGuiHandler
{
    public static final byte JETPACK_WEARING = 6;
    public static final byte JETPACK_HOLDING = 5;
    public static final byte COPTER_WEARING = 4;
    public static final byte COPTER_HOLDING = 3;
    public static final byte BACKPACK_HOLDING = 2;
    public static final byte BACKPACK_WEARING = 1;
    public static final byte BACKPACK_TILE = 0;

    public GuiHandler()
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case BACKPACK_TILE:
                if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileAdventureBackpack)
                {
                    return new ContainerBackpack(player, (TileAdventureBackpack) world.getTileEntity(x, y, z), ContainerBackpack.SOURCE_TILE);
                }
                break;
            case BACKPACK_WEARING:
                if (Wearing.isWearingBackpack(player))
                {
                    return new ContainerBackpack(player, new InventoryBackpack(Wearing.getWearingBackpack(player)), ContainerBackpack.SOURCE_WEARING);
                }
                break;
            case BACKPACK_HOLDING:
                if (Wearing.isHoldingBackpack(player))
                {
                    return new ContainerBackpack(player, new InventoryBackpack(Wearing.getHoldingBackpack(player)), ContainerBackpack.SOURCE_HOLDING);
                }
                break;
            case COPTER_HOLDING:
                if (Wearing.isHoldingCopter(player))
                {
                    return new ContainerCopter(player, new InventoryCopterPack(Wearing.getHoldingCopter(player)), false);
                }
                break;
            case COPTER_WEARING:
                if (Wearing.isWearingCopter(player))
                {
                    return new ContainerCopter(player, new InventoryCopterPack(Wearing.getWearingCopter(player)), true);
                }
                break;
            case JETPACK_HOLDING:
                if (Wearing.isHoldingJetpack(player))
                {
                    return new ContainerJetpack(player, new InventoryCoalJetpack(Wearing.getHoldingJetpack(player)), false);
                }
                break;
            case JETPACK_WEARING:
                if (Wearing.isWearingJetpack(player))
                {
                    return new ContainerJetpack(player, new InventoryCoalJetpack(Wearing.getWearingJetpack(player)), true);
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
        switch (ID)
        {
            case BACKPACK_TILE:
                if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileAdventureBackpack)
                {
                    return new GuiAdvBackpack(player, (TileAdventureBackpack) world.getTileEntity(x, y, z));
                }
                break;
            case BACKPACK_WEARING:
                if (Wearing.isWearingBackpack(player))
                {
                    return new GuiAdvBackpack(player, new InventoryBackpack(Wearing.getWearingBackpack(player)), true);
                }
                break;
            case BACKPACK_HOLDING:
                if (Wearing.isHoldingBackpack(player))
                {
                    return new GuiAdvBackpack(player, new InventoryBackpack(Wearing.getHoldingBackpack(player)), false);
                }
                break;
            case COPTER_HOLDING:
                if (Wearing.isHoldingCopter(player))
                {
                    return new GuiCopterPack(player, new InventoryCopterPack(Wearing.getHoldingCopter(player)), false);
                }
                break;
            case COPTER_WEARING:
                if (Wearing.isWearingCopter(player))
                {
                    return new GuiCopterPack(player, new InventoryCopterPack(Wearing.getWearingCopter(player)), true);
                }
                break;
            case JETPACK_HOLDING:
                if (Wearing.isHoldingJetpack(player))
                {
                    return new GuiCoalJetpack(player, new InventoryCoalJetpack(Wearing.getHoldingJetpack(player)), false);
                }
                break;
            case JETPACK_WEARING:
                if (Wearing.isWearingJetpack(player))
                {
                    return new GuiCoalJetpack(player, new InventoryCoalJetpack(Wearing.getWearingJetpack(player)), true);
                }
                break;
            default:
                player.closeScreen();
                break;
        }
        return null;
    }
}
