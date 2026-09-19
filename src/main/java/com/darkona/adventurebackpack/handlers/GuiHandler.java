package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAirCompressor;
import com.darkona.adventurebackpack.client.gui.GuiAirCompressor;
import com.darkona.adventurebackpack.client.gui.GuiScubaGear;
import com.darkona.adventurebackpack.client.gui.GuiAdvBackpack;
import com.darkona.adventurebackpack.client.gui.GuiCopterPack;
import com.darkona.adventurebackpack.client.gui.GuiSteamJetpack;
import com.darkona.adventurebackpack.inventory.*;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class GuiHandler implements IGuiHandler
{
    /** 9 and 10 are kept for the Flamethrower Pack, 12 for the Fuel Station (notes/diseno-equipo-espalda.md). */
    public static final byte AIR_COMPRESSOR = 11;
    public static final byte SCUBA_WEARING = 8;
    public static final byte SCUBA_HOLDING = 7;
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
                    return new ContainerCopter(player, new InventoryCopterPack(Wearing.getHoldingCopter(player)),false);
                }
                break;
            case COPTER_WEARING:
                if (Wearing.isWearingCopter(player))
                {
                    return new ContainerCopter(player, new InventoryCopterPack(Wearing.getWearingCopter(player)), true);
                }
                break;
            case JETPACK_HOLDING:
                if (Wearing.isHoldingSteam(player))
                {
                    return new ContainerJetpack(player, new InventorySteamJetpack(Wearing.getHoldingSteam(player)),false);
                }
                break;
            case JETPACK_WEARING:
                if (Wearing.isWearingSteam(player))
                {
                    return new ContainerJetpack(player, new InventorySteamJetpack(Wearing.getWearingSteam(player)), true);
                }
                break;
            case SCUBA_HOLDING:
                if (Wearing.isHoldingScuba(player))
                {
                    return new ContainerScubaGear(player, new InventoryScubaGear(Wearing.getHoldingScuba(player)), false);
                }
                break;
            case SCUBA_WEARING:
                if (Wearing.isWearingScuba(player))
                {
                    return new ContainerScubaGear(player, new InventoryScubaGear(Wearing.getWearingScuba(player)), true);
                }
                break;
            case AIR_COMPRESSOR:
                if (world.getTileEntity(x, y, z) instanceof TileAirCompressor)
                {
                    return new ContainerAirCompressor(player.inventory, (TileAirCompressor) world.getTileEntity(x, y, z));
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
                    return new GuiCopterPack(player, new InventoryCopterPack(Wearing.getHoldingCopter(player)),false);
                }
                break;
            case COPTER_WEARING:
                if (Wearing.isWearingCopter(player))
                {
                    return new GuiCopterPack(player, new InventoryCopterPack(Wearing.getWearingCopter(player)), true);
                }
                break;
            case JETPACK_HOLDING:
                if (Wearing.isHoldingSteam(player))
                {
                    return new GuiSteamJetpack(player, new InventorySteamJetpack(Wearing.getHoldingSteam(player)),false);
                }
                break;
            case JETPACK_WEARING:
                if (Wearing.isWearingSteam(player))
                {
                    return new GuiSteamJetpack(player, new InventorySteamJetpack(Wearing.getWearingSteam(player)), true);
                }
                break;
            case SCUBA_HOLDING:
                if (Wearing.isHoldingScuba(player))
                {
                    return new GuiScubaGear(player, new InventoryScubaGear(Wearing.getHoldingScuba(player)), false);
                }
                break;
            case SCUBA_WEARING:
                if (Wearing.isWearingScuba(player))
                {
                    return new GuiScubaGear(player, new InventoryScubaGear(Wearing.getWearingScuba(player)), true);
                }
                break;
            case AIR_COMPRESSOR:
                if (world.getTileEntity(x, y, z) instanceof TileAirCompressor)
                {
                    return new GuiAirCompressor(player.inventory, (TileAirCompressor) world.getTileEntity(x, y, z));
                }
                break;
            default:
                player.closeScreen();
                break;
        }
        return null;
    }
}
