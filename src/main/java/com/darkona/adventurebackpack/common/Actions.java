package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.api.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 11/10/2014
 *
 * @author Darkona
 * @see com.darkona.adventurebackpack.handlers.EventHandler
 * @see com.darkona.adventurebackpack.api.FluidEffectRegistry
 * @see com.darkona.adventurebackpack.common.BackpackAbilities
 */
public class Actions {

    /**
     * Adds vertical inertia to the movement in the Y axis of the player, and makes Newton's Laws cry.
     * In other words, makes you jump higher.
     * Also it plays a nice sound effect that will probably get annoying after a while.
     *
     * @param player - The player performing the jump.
     */
    public static void pistonBootsJump(EntityPlayer player) {
        //TODO add configuration for the playing of the sound effect.
        //TODO Maybe configurable jump height too, because why not.
        player.playSound("tile.piston.out", 0.5F, player.getRNG().nextFloat() * 0.25F + 0.6F);
        player.motionY += 0.35;
        player.jumpMovementFactor += 0.3;
    }

    /**
     * Attempts to fill a tank with a fluid from the world.
     *
     * @param world  theWORLD is the intro song of the popular anime Death Note.
     *               It goes like: Hirogaru yami no naka kawashiatta kakumei no chigiri...
     * @param mop    A moving object position you have to get from somewhere. Probably where the player is looking.
     * @param player A player.
     * @param tank   The tank that will be attempted to be filled.
     * @return A FluidStack of the fluid that was removed from the world. One whole bucket worth of it, oh yeah.
     */
    public static FluidStack attemptFill(World world, MovingObjectPosition mop, EntityPlayer player, FluidTank tank) {
        try {
            if (!world.canMineBlock(player, mop.blockX, mop.blockY, mop.blockZ))
                return null;
            if (!player.canPlayerEdit(mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, null))
                return null;
            Fluid fluidBlock = FluidRegistry.lookupFluidForBlock(world.getBlock(mop.blockX, mop.blockY, mop.blockZ));
            if (fluidBlock != null) {
                FluidStack fluid = new FluidStack(fluidBlock, Constants.bucket);
                if (tank.getFluid() == null || tank.getFluid().containsFluid(fluid)) {
                    int accepted = tank.fill(fluid, false);
                    if (accepted > 0) {
                        world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
                        return fluid;
                    }
                }
            }
        } catch (Exception oops) {
            LogHelper.error("Something bad happened while filling the tank OMG. Send the following to your grandma:");
            oops.printStackTrace();
        }
        return null;
    }

    /**
     * Attempts to perform the placement of a fluid in the desired coordinates.
     *
     * @param player The player attempting to perform the pouring.
     * @param world  The world.
     * @param x      X coordinate of the block.
     * @param y      Y coordinate of the block.
     * @param z      Z coordinate of the block.
     * @param tank   The tank that holds the fluid that will be attempted to be poured.
     * @return Returns a FluidStack with the amount of fluid that was drained from the tank.
     */
    public static FluidStack attemptPour(EntityPlayer player, World world, int x, int y, int z, FluidTank tank) {
        //TODO control for other fluids if they can or cannot be replaced.
        try {
            FluidStack fluid = tank.getFluid();
            if (fluid != null) {
                if (fluid.getFluid().canBePlacedInWorld()) {
                    Material material = world.getBlock(x, y, z).getMaterial();
                    boolean flag = !material.isSolid();

                    if (!world.isAirBlock(x, y, z) && !flag) {
                        return null;
                    }
                    /* IN HELL DIMENSION*/
                    if (world.provider.isHellWorld && fluid.getFluid() == FluidRegistry.WATER) {
                        world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F,
                                2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
                        for (int l = 0; l < 12; ++l) {
                            world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D);
                        }
                    } else {
                        /* NOT IN HELL DIMENSION. No, I won't let you put water in the nether. You freak*/
                        FluidStack drainedFluid = tank.drain(Constants.bucket, false);
                        if (drainedFluid != null && drainedFluid.amount >= Constants.bucket) {
                            if (!world.isRemote && flag && !material.isLiquid()) {
                                world.func_147480_a(x, y, z, true);
                            }
                            if (fluid.getFluid() == FluidRegistry.WATER) {
                                world.setBlock(x, y, z, fluid.getFluid().getBlock(), 0, 3);
                            } else {
                                if (fluid.getFluid() == FluidRegistry.LAVA) {
                                    world.setBlock(x, y, z, fluid.getFluid().getBlock(), 0, 3);
                                } else {
                                    world.setBlock(x, y, z, fluid.getFluid().getBlock(), 0, 3);
                                }
                            }
                            return drainedFluid;
                        }
                    }
                }
            }

        } catch (Exception oops) {
            LogHelper.error("Something bad happened when spilling fluid into the world OMG. Here's the stack trace, send it to the author:");
            oops.printStackTrace();
        }
        return null;
    }

    /**
     * @param world  The world. Like, the WHOLE world. That's a lot of stuff. Do stuff with it, like detecting biomes
     *               or whatever.
     * @param player Is a player. To whom  the nice or evil effects you're going to apply will affect.
     *               See? I know the proper use of the words "effect" & "affect".
     * @param tank   The tank that holds the fluid, whose effect will affect the player that's in the world.
     * @return If the effect can be applied, and it is actually applied, returns true.
     */
    public static boolean setFluidEffect(World world, EntityPlayer player, FluidTank tank) {
        FluidStack drained = tank.drain(Constants.bucket, false);
        boolean done = false;
        // Map<Integer, FluidEffect> lol =
        // FluidEffectRegistry.getRegisteredFluidEffects();
        if (drained != null && drained.amount >= Constants.bucket) {

            for (FluidEffect effect : FluidEffectRegistry.getEffectsForFluid(drained.getFluid())) {
                if (effect != null) {
                    effect.affectDrinker(world, player);
                    if (world.isRemote) {
                        //player.sendChatToPlayer(ChatMessageComponent.createFromText(effect.msg));
                    }
                    done = true;
                }
            }
        }
        return done;
    }

    /**
     * @param player    Duh!
     * @param direction The direction in which the hose modes will switch.
     * @param slot      The slot in which the hose gleefully frolicks in the inventory.
     */
    public static void switchHose(EntityPlayer player, int direction, int slot) {
        ItemStack hose = player.inventory.mainInventory[slot];
        NBTTagCompound tag = hose.hasTagCompound() ? hose.stackTagCompound : new NBTTagCompound();
        if (direction < 0) {
            int mode = ItemHose.getHoseMode(hose);
            mode = (mode + 1) % 3;
            tag.setInteger("mode", mode);
        } else {
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
    public static void cycleTool(EntityPlayer player, int direction, int slot) {
        InventoryItem backpack = Wearing.getBackpackInv(player, true);
        ItemStack current = player.getCurrentEquippedItem();
        if (direction < 0) {
            player.inventory.mainInventory[slot] = backpack.getStackInSlot(3);
            backpack.setInventorySlotContentsSafe(3, backpack.getStackInSlot(0));
            backpack.setInventorySlotContentsSafe(0, current);
            backpack.saveChanges();
            player.inventory.closeInventory();
        } else {
            if (direction > 0) {
                player.inventory.mainInventory[slot] = backpack.getStackInSlot(0);
                backpack.setInventorySlotContentsSafe(0, backpack.getStackInSlot(3));
                backpack.setInventorySlotContentsSafe(3, current);
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
    public static boolean tryPlaceOnDeath(EntityPlayer player) {
        ItemStack backpack = Wearing.getWearingBackpack(player);
        if (backpack != null) {
            World world = player.worldObj;
            if (backpack.stackTagCompound.getString("colorName").equals("Creeper")) {
                BackpackAbilities.instance.itemCreeper(player, world, backpack);
            }
            ChunkCoordinates spawn = getNearestEmptyChunkCoordinates(world, (int) player.posX, (int) player.posY, (int) player.posZ, 10, false);
            if (spawn != null) {
                if (((ItemAdventureBackpack) ModItems.adventureBackpack).placeBackpack(player.inventory.armorInventory[2], player, world, spawn.posX, spawn.posY, spawn.posZ,
                        ForgeDirection.UP.ordinal(), false)) {
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
    public static ChunkCoordinates getNearestEmptyChunkCoordinates(World world, int x, int y, int z, int radius, boolean except) {

        for (int i = x; i <= x + radius; ++i) {
            for (int j = y; j <= y + (radius / 2); ++j) {
                for (int k = z; k <= z + (radius); ++k) {
                    if (except && world.isSideSolid(i, j - 1, k, ForgeDirection.UP) && world.isAirBlock(i, j, k) && !areCoordinatesTheSame(x, y, z, i, j, k)) {
                        return new ChunkCoordinates(i, j, k);
                    }
                    if (!except && world.isSideSolid(i, j - 1, k, ForgeDirection.UP) && world.isAirBlock(i, j, k)) {
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
    private static boolean areCoordinatesTheSame(int X1, int Y1, int Z1, int X2, int Y2, int Z2) {
        return (X1 == X2 && Y1 == Y2 && Z1 == Z2);
    }

    /**
     * Electrifying! Transforms a backpack into its electrified version. Shhh this is kinda secret, ok?
     *
     * @param player The player wearing the backpack.
     */
    public static void electrify(EntityPlayer player) {
        ItemStack stack = Wearing.getWearingBackpack(player);
        if (stack.stackTagCompound != null) {
            if (stack.stackTagCompound.hasKey("color") && stack.stackTagCompound.getString("color").contains("PorkchopRaw")) {
                stack.stackTagCompound.setString("color", "Pigman");
                stack.stackTagCompound.setString("colorName", "Zombie Pigman");
            } else if (stack.stackTagCompound.hasKey("color") && !stack.stackTagCompound.getString("color").contains("Pigman")) {
                player.inventory.armorInventory[2].stackTagCompound.setString("color", "Electric");
                stack.stackTagCompound.setString("colorName", "Electric");
            }
        }
    }


}
