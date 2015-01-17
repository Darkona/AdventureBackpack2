package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.gui.GuiAdvBackpack;
import com.darkona.adventurebackpack.client.gui.GuiCopterPack;
import com.darkona.adventurebackpack.client.gui.GuiSteamJetpack;
import com.darkona.adventurebackpack.inventory.*;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
        InventoryBackpack inv;
        InventoryCopterPack inv2;
        InventorySteamJetpack inv3;
        TileEntity te;

        switch (ID)
        {
            case BACKPACK_TILE:
                te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileAdventureBackpack)
                {
                    return new ContainerBackpack(player, (TileAdventureBackpack) te, ContainerBackpack.SOURCE_TILE);
                }
                break;
            case BACKPACK_WEARING:
                inv = Wearing.getBackpackInv(player, true);
                if (inv.getParentItemStack() != null)
                {
                    return new ContainerBackpack(player, inv, ContainerBackpack.SOURCE_WEARING);
                }
                break;
            case BACKPACK_HOLDING:
                inv = Wearing.getBackpackInv(player, false);
                if (inv.getParentItemStack() != null)
                {
                    return new ContainerBackpack(player, inv, ContainerBackpack.SOURCE_HOLDING);
                }
                break;
            case COPTER_HOLDING:
                inv2 = new InventoryCopterPack(Wearing.getHoldingCopter(player));
                if (inv2.getParentItemStack() != null)
                {
                    return new ContainerCopter(player, inv2,false);
                }
                break;
            case COPTER_WEARING:
                inv2 = new InventoryCopterPack(Wearing.getWearingCopter(player));
                if (inv2.getParentItemStack() != null)
                {
                    return new ContainerCopter(player, inv2,true);
                }
                break;
            case JETPACK_HOLDING:
                inv3 = new InventorySteamJetpack(Wearing.getHoldingSteam(player));
                if (inv3.getParentItemStack() != null)
                {
                    return new ContainerJetpack(player, inv3,false);
                }
                break;
            case JETPACK_WEARING:
                inv3 = new InventorySteamJetpack(Wearing.getWearingSteam(player));
                if (inv3.getParentItemStack() != null)
                {
                    return new ContainerJetpack(player, inv3,true);
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
        InventoryBackpack inv;
        InventoryCopterPack inv2;
        InventorySteamJetpack inv3;
        TileEntity te;
        switch (ID)
        {
            case BACKPACK_TILE:
                te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileAdventureBackpack)
                {
                    return new GuiAdvBackpack(player, (TileAdventureBackpack) te);
                }
                break;
            case BACKPACK_WEARING:
                inv = Wearing.getBackpackInv(player, true);
                if (inv.getParentItemStack() != null)
                {
                    return new GuiAdvBackpack(player, inv, true);
                }
                break;
            case BACKPACK_HOLDING:
                inv = Wearing.getBackpackInv(player, false);
                if (inv.getParentItemStack() != null)
                {
                    return new GuiAdvBackpack(player, inv, false);
                }
                break;
            case COPTER_HOLDING:
                inv2 = new InventoryCopterPack(Wearing.getHoldingCopter(player));
                if (inv2.getParentItemStack() != null)
                {
                    return new GuiCopterPack(player, inv2,false);
                }
                break;
            case COPTER_WEARING:
                inv2 = new InventoryCopterPack(Wearing.getWearingCopter(player));
                if (inv2.getParentItemStack() != null)
                {
                    return new GuiCopterPack(player, inv2,true);
                }
                break;
            case JETPACK_HOLDING:
                inv3 = new InventorySteamJetpack(Wearing.getHoldingSteam(player));
                if (inv3.getParentItemStack() != null)
                {
                    return new GuiSteamJetpack(player, inv3,false);
                }
                break;
            case JETPACK_WEARING:
                inv3 = new InventorySteamJetpack(Wearing.getWearingSteam(player));
                if (inv3.getParentItemStack() != null)
                {
                    return new GuiSteamJetpack(player, inv3,true);
                }
                break;
            default:
                player.closeScreen();
                break;
        }
        return null;
    }
}
