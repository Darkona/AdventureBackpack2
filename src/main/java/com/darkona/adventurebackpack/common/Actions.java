package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.api.FluidEffectRegistry;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.Random;

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 * @see com.darkona.adventurebackpack.handlers.PlayerEventHandler
 * @see com.darkona.adventurebackpack.api.FluidEffectRegistry
 * @see com.darkona.adventurebackpack.common.BackpackAbilities
 */
public class Actions
{

    public static final boolean HOSE_SWITCH = false;
    public static final boolean HOSE_TOGGLE = true;

    /**
     * Adds vertical inertia to the movement in the Y axis of the player, and makes Newton's Laws cry.
     * In other words, makes you jump higher.
     * Also it plays a nice sound effect that will probably get annoying after a while.
     *
     * @param player - The player performing the jump.
     */
    public static void pistonBootsJump(EntityPlayer player)
    {
        //TODO add configuration for the playing of the sound effect.
        //TODO Maybe configurable jump height too, because why not.
        player.playSound("tile.piston.out", 0.5F, player.getRNG().nextFloat() * 0.25F + 0.6F);
        player.motionY += 0.35;
        player.jumpMovementFactor += 0.3;
    }

    /**
     * @param world  The world. Like, the WHOLE world. That's a lot of stuff. Do stuff with it, like detecting biomes
     *               or whatever.
     * @param player Is a player. To whom  the nice or evil effects you're going to apply will affect.
     *               See? I know the proper use of the words "effect" & "affect".
     * @param tank   The tank that holds the fluid, whose effect will affect the player that's in the world.
     * @return If the effect can be applied, and it is actually applied, returns true.
     */
    public static boolean setFluidEffect(World world, EntityPlayer player, FluidTank tank)
    {
        FluidStack drained = tank.drain(Constants.bucket, false);
        boolean done = false;
        if (drained != null && drained.amount >= Constants.bucket)
        {
            setFluidEffect(world, player, drained.getFluid());
        }
        return done;
    }

    public static boolean setFluidEffect(World world, EntityPlayer player, Fluid fluid)
    {

        boolean done = false;
        for (FluidEffect effect : FluidEffectRegistry.getEffectsForFluid(fluid))
        {
            if (effect != null)
            {
                effect.affectDrinker(world, player);
                if (world.isRemote)
                {
                    //player.sendChatToPlayer(ChatMessageComponent.createFromText(effect.msg));
                }
                done = true;
            }
        }
        return true;
    }

    /**
     * @param player    Duh!
     * @param direction The direction in which the hose modes will switch.
     * @param action    The type of the action to be performed on the hose.
     *                  Can be HOSE_SWITCH for mode or HOSE_TOGGLE for tank
     * @param slot      The slot in which the hose gleefully frolicks in the inventory.
     */
    public static void switchHose(EntityPlayer player, boolean action, int direction, int slot)
    {
        ItemStack hose = player.inventory.mainInventory[slot];
        NBTTagCompound tag = hose.hasTagCompound() ? hose.stackTagCompound : new NBTTagCompound();
        if (action == Actions.HOSE_SWITCH)
        {
            int mode = ItemHose.getHoseMode(hose);
            if (direction > 0)
            {
                mode = (mode + 1) % 3;
            } else if (direction < 0)
            {
                mode = (mode - 1 < 0) ? 2 : mode - 1;
            }
            tag.setInteger("mode", mode);
        }

        if (action == Actions.HOSE_TOGGLE)
        {
            int tank = ItemHose.getHoseTank(hose);
            tank = (tank + 1) % 2;
            tag.setInteger("tank", tank);
        }
        hose.setTagCompound(tag);
    }

    /**
     * Cycles tools. In a cycle. The tool in your hand with the tools in the special tool slots of the backpack, to be precise.
     *
     * @param player    - Duh
     * @param direction - An integer indicating the direction of the switch. Nobody likes to swith always inthe same
     *                  direction all the time. That's stupid.
     * @param slot      The slot that will be switched with the backpack.
     */
    public static void cycleTool(EntityPlayer player, int direction, int slot)
    {
        InventoryItem backpack = Wearing.getBackpackInv(player, true);
        ItemStack current = player.getCurrentEquippedItem();
        if (direction < 0)
        {
            player.inventory.mainInventory[slot] = backpack.getStackInSlot(3);
            backpack.setInventorySlotContentsNoSave(3, backpack.getStackInSlot(0));
            backpack.setInventorySlotContentsNoSave(0, current);
            backpack.saveChanges();
            player.inventory.closeInventory();
        } else
        {
            if (direction > 0)
            {
                player.inventory.mainInventory[slot] = backpack.getStackInSlot(0);
                backpack.setInventorySlotContentsNoSave(0, backpack.getStackInSlot(3));
                backpack.setInventorySlotContentsNoSave(3, current);
                backpack.saveChanges();
            }

        }
    }

    /**
     * Tries, i stress, TRIES to place a backpack in the world. Called when the player dies what was probaby
     * a very horrible, untimely death, maybe in lava. It looks for a block nearby to place it.
     *
     * @param player
     * @return Whether or not the backpack could be placed somewhere.
     */
    public static boolean tryPlaceOnDeath(EntityPlayer player)
    {
        ItemStack backpack = Wearing.getWearingBackpack(player);
        if (backpack != null)
        {
            World world = player.worldObj;
            if (backpack.stackTagCompound.getString("colorName").equals("Creeper"))
            {
                BackpackAbilities.instance.itemCreeper(player, world, backpack);
            }
            ChunkCoordinates spawn = getNearestEmptyChunkCoordinates(world, (int) player.posX, (int) player.posY, (int) player.posZ, 10, false);
            if (spawn != null)
            {
                if (((ItemAdventureBackpack) ModItems.adventureBackpack).placeBackpack(player.inventory.armorInventory[2], player, world, spawn.posX, spawn.posY, spawn.posZ,
                        ForgeDirection.UP.ordinal(), false))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets you the nearest Empty Chunk Coordinates, free of charge! Looks in three dimensions and finds a block
     * that a: can have stuff placed on it and b: has space above it.
     *
     * @param world  The world object.
     * @param x
     * @param y
     * @param z      The coordinates of the central point of the search.
     * @param radius The radius of the search. If set to higher numbers, will create a ton of lag
     * @param except Wheter or not to include the origin of the search as a valid block.
     * @return The coordinates of the block in the chunk of the world of the game of the server of the owner of the computer.
     */
    public static ChunkCoordinates getNearestEmptyChunkCoordinates(World world, int x, int y, int z, int radius, boolean except)
    {

        for (int i = x; i <= x + radius; ++i)
        {
            for (int j = y; j <= y + (radius / 2); ++j)
            {
                for (int k = z; k <= z + (radius); ++k)
                {
                    if (except && world.isSideSolid(i, j - 1, k, ForgeDirection.UP) && world.isAirBlock(i, j, k) && !areCoordinatesTheSame(x, y, z, i, j, k))
                    {
                        return new ChunkCoordinates(i, j, k);
                    }
                    if (!except && world.isSideSolid(i, j - 1, k, ForgeDirection.UP) && world.isAirBlock(i, j, k))
                    {
                        return new ChunkCoordinates(i, j, k);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Compares two coordinates. Heh.
     *
     * @param X1 First coordinate X.
     * @param Y1 First coordinate Y.
     * @param Z1 First coordinate Z.
     * @param X2 Second coordinate X.
     * @param Y2 Second coordinate Y.
     * @param Z2 Second coordinate Z. I really didn't need to type all that, its obvious.
     * @return If both coordinates are the same, returns true. This is the least helpful javadoc ever.
     */
    private static boolean areCoordinatesTheSame(int X1, int Y1, int Z1, int X2, int Y2, int Z2)
    {
        return (X1 == X2 && Y1 == Y2 && Z1 == Z2);
    }

    /**
     * Electrifying! Transforms a backpack into its electrified version. Shhh this is kinda secret, ok?
     *
     * @param player The player wearing the backpack.
     */
    public static void electrify(EntityPlayer player)
    {
        ItemStack stack = Wearing.getWearingBackpack(player);
        if (BackpackNames.getBackpackColorName(stack).equals("Pig"))
        {
            stack.stackTagCompound.setString("color", "Pigman");
            stack.stackTagCompound.setString("colorName", "Zombie Pigman");
        }
        if (BackpackNames.getBackpackColorName(stack).equals("BlockDiamond"))
        {
            stack.stackTagCompound.setString("color", "Electric");
            stack.stackTagCompound.setString("colorName", "Electric");
        }
    }

    /**
     * @param player
     * @param bow
     * @param charge
     */
    public static void leakArrow(EntityPlayer player, ItemStack bow, int charge)
    {
        World world = player.worldObj;
        Random itemRand = new Random();
        InventoryItem backpack = new InventoryItem(Wearing.getWearingBackpack(player));

        //this is all vanilla code for the bow
        boolean flag = player.capabilities.isCreativeMode
                || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) > 0;

        if (flag || backpack.hasItem(Items.arrow))
        {
            float f = (float) charge / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;
            if ((double) f < 0.1D)
            {
                return;
            }
            if (f > 1.0F)
            {
                f = 1.0F;
            }
            EntityArrow entityarrow = new EntityArrow(world, player, f * 2.0F);
            if (f == 1.0F)
            {
                entityarrow.setIsCritical(true);
            }
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bow);
            if (power > 0)
            {
                entityarrow.setDamage(entityarrow.getDamage() + (double) power * 0.5D + 0.5D);
            }
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bow);
            if (punch > 0)
            {
                entityarrow.setKnockbackStrength(punch);
            }
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bow) > 0)
            {
                entityarrow.setFire(100);
            }

            bow.damageItem(1, player);
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag)
            {
                entityarrow.canBePickedUp = 2;
            } else
            {
                /*
                * From here, instead of leaking an arrow to the player inventory, which may be full and then it would be
                * pointless, leak an arrow straight from the backpack ^_^
                *
                * It could be possible to switch a whole stack with the player inventory, fire the arrow, and then
                * switch back, but that's stupid.
                *
                * That's how you make a quiver (for vanilla bows at least, or anything that uses the events and vanilla
                * arrows) Until we have an event that fires when a player consumes items in his/her inventory.
                *
                * I should make a pull request. Too lazy, though.
                * */
                backpack.consumeInventoryItem(Items.arrow);
                backpack.saveChanges();
            }

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(entityarrow);
                LogHelper.info("Fired an arrow!");
            }
        }
    }

    /**
     * @param player
     * @param coordX
     * @param coordY
     * @param coordZ
     */
    public static void toggleSleepingBag(EntityPlayer player, int coordX, int coordY, int coordZ)
    {
        World world = player.worldObj;
        //PacketDispatcher.sendPacketToPlayer(PacketHandler.makePacket(5, 0, coordX, coordY, coordZ), (Player) player);
        if (world.getTileEntity(coordX, coordY, coordZ) instanceof TileAdventureBackpack)
        {
            TileAdventureBackpack te = (TileAdventureBackpack) world.getTileEntity(coordX, coordY, coordZ);
            if (!te.isSBDeployed())
            {
                int can[] = canDeploySleepingBag(coordX, coordY, coordZ);
                if (can[0] > -1)
                {
                    if (te.deploySleepingBag(player, world, can[1], can[2], can[3], can[0]))
                    {
                        player.closeScreen();
                    }
                } else if (!world.isRemote)
                {
                    // player.addChatMessage("Can't deploy the sleeping bag");
                }
            } else
            {
                te.removeSleepingBag(world);
            }
            player.closeScreen();
        }

    }

    public static int[] canDeploySleepingBag(int coordX, int coordY, int coordZ)
    {
        World world = Minecraft.getMinecraft().theWorld;
        TileAdventureBackpack te = (TileAdventureBackpack) world.getTileEntity(coordX, coordY, coordZ);
        int newMeta = -1;

        if (!te.isSBDeployed())
        {
            int meta = world.getBlockMetadata(coordX, coordY, coordZ);
            switch (meta)
            {
                case 0:
                    --coordZ;
                    if (world.isAirBlock(coordX, coordY, coordZ) && world.getBlock(coordX, coordY - 1, coordZ).getMaterial().isSolid())
                    {
                        if (world.isAirBlock(coordX, coordY, coordZ - 1) && world.getBlock(coordX, coordY - 1, coordZ - 1).getMaterial().isSolid())
                        {
                            newMeta = 2;
                        }
                    }
                    break;
                case 1:
                    ++coordX;
                    if (world.isAirBlock(coordX, coordY, coordZ) && world.getBlock(coordX, coordY - 1, coordZ).getMaterial().isSolid())
                    {
                        if (world.isAirBlock(coordX + 1, coordY, coordZ) && world.getBlock(coordX + 1, coordY - 1, coordZ).getMaterial().isSolid())
                        {
                            newMeta = 3;
                        }
                    }
                    break;
                case 2:
                    ++coordZ;
                    if (world.isAirBlock(coordX, coordY, coordZ) && world.getBlock(coordX, coordY - 1, coordZ).getMaterial().isSolid())
                    {
                        if (world.isAirBlock(coordX, coordY, coordZ + 1) && world.getBlock(coordX, coordY - 1, coordZ + 1).getMaterial().isSolid())
                        {
                            newMeta = 0;
                        }
                    }
                    break;
                case 3:
                    --coordX;
                    if (world.isAirBlock(coordX, coordY, coordZ) && world.getBlock(coordX, coordY - 1, coordZ).getMaterial().isSolid())
                    {
                        if (world.isAirBlock(coordX - 1, coordY, coordZ) && world.getBlock(coordX - 1, coordY - 1, coordZ).getMaterial().isSolid())
                        {
                            newMeta = 1;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        int result[] = {newMeta, coordX, coordY, coordZ};
        return result;
    }
}
