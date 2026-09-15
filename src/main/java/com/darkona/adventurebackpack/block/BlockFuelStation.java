package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.handlers.GuiHandler;
import com.darkona.adventurebackpack.item.IFuelPack;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

/**
 * The Fuel Station block. Right click opens its GUI. Riding a motorized Inflatable Boat, right click fills the boat's
 * tank. Sneak + right click with a fuel pack in hand, or worn, fills that pack. Every transfer moves all that fits at
 * once, on the server.
 */
public class BlockFuelStation extends BlockStation
{
    public BlockFuelStation()
    {
        super("blockFuelStation", "fuelStation");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileFuelStation();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileFuelStation)) return false;
        if (world.isRemote) return true;
        TileFuelStation station = (TileFuelStation) te;
        if (!ConfigHandler.FUELSTATION_ENABLED)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.fuelstation.disabled"));
            return true;
        }
        if (player.ridingEntity instanceof EntityInflatableBoat && ((EntityInflatableBoat) player.ridingEntity).isMotorized())
        {
            FluidStack fuel = station.getTank().getFluid();
            FluidStack before = fuel == null ? null : fuel.copy();
            int moved = station.fillTank(((EntityInflatableBoat) player.ridingEntity).getFuelTank(), GeneralReference.isValidFuel(before));
            report(player, before, moved);
            return true;
        }
        if (player.isSneaking())
        {
            ItemStack pack = fuelPack(player.getHeldItem());
            if (pack == null) pack = fuelPack(Wearing.getWearingWearable(player));
            if (pack != null)
            {
                FluidStack fuel = station.getTank().getFluid();
                FluidStack before = fuel == null ? null : fuel.copy();
                report(player, before, station.fillPackStack(pack));
                return true;
            }
        }
        FMLNetworkHandler.openGui(player, AdventureBackpack.instance, GuiHandler.FUEL_STATION, world, x, y, z);
        return true;
    }

    private static ItemStack fuelPack(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof IFuelPack ? stack : null;
    }

    private static void report(EntityPlayer player, FluidStack fuel, int moved)
    {
        if (fuel == null || fuel.amount <= 0 || fuel.getFluid() == null)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.fuelstation.empty"));
        } else if (moved > 0)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.fuelstation.filled", moved, new ChatComponentTranslation(fuel.getUnlocalizedName())));
        } else
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.fuelstation.wrongfuel"));
        }
    }
}
