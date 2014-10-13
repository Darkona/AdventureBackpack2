package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.api.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.items.ItemAdventureBackpack;
import com.darkona.adventurebackpack.items.ItemHose;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 11/10/2014.
 */
public class Actions {
    public static boolean cycling = false;
    public static boolean switching = false;
    public static boolean bedActivated = false;


    public static void pistonBootsJump(EntityPlayer player) {
        player.playSound("tile.piston.out", 0.5F, player.getRNG().nextFloat() * 0.25F + 0.6F);
        player.motionY += 0.35;
        player.jumpMovementFactor += 0.3;
    }

    public static FluidStack attemptFill(World world, MovingObjectPosition mop, EntityPlayer player, FluidTank tank) {
        try {
            if (!world.canMineBlock(player, mop.blockX, mop.blockY, mop.blockZ))
                return null;
            if (!player.canPlayerEdit(mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, null))
                return null;
            int fluidID = Utils.isBlockRegisteredAsFluid(world.getBlock(mop.blockX, mop.blockY, mop.blockZ));
            FluidStack fluid = new FluidStack(fluidID, Constants.bucket);
            // To-do make it dependent on tank name from the hose
            if (fluidID > -1) {
                if (tank.getFluid() == null || tank.getFluid().containsFluid(fluid)) {
                    int accepted = tank.fill(fluid, false);
                    if (accepted > 0) {
                        world.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
                        return fluid;
                    }
                }
            }
        } catch (Exception oops) {
            System.out.println("Something bad happened while filling the tank D:");
            oops.printStackTrace();
        }
        return null;
    }

    public static FluidStack attemptPour(EntityPlayer player, World world, int x, int y, int z, FluidTank tank) {
        try {
            FluidStack fluid = tank.getFluid();
            if (fluid != null) {
                if (fluid.getFluid().canBePlacedInWorld()) {
                    Material material = world.getBlock(x, y, z).getMaterial();
                    boolean flag = !material.isSolid();

                    if (!world.isAirBlock(x, y, z) && !flag) {
                        return null;
                    }

                    if (world.provider.isHellWorld && fluid.getFluid() == FluidRegistry.WATER) { /* HELL */
                        world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F,
                                2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
                        for (int l = 0; l < 12; ++l) {
                            world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0.0D, 0.0D, 0.0D);
                        }
                    } else {
                        /* NOT HELL */
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

        } catch (Exception ex) {
            System.out.println("Something bad happened when spilling fluid into the world D:");
            ex.printStackTrace();
        }
        return null;
    }


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

    public static void switchHose(EntityPlayer player, int direction, int slot) {
        // player.inventory.currentItem = slot;
        if (!switching && !cycling) {
            switching = true;

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
            switching = false;
        }

    }

    public static void cycleTool(EntityPlayer player, int direction, int slot) {
        if (!cycling && !switching) {
            cycling = true;
            InventoryItem backpack = Utils.getBackpackInv(player, true);
            ItemStack current = player.getCurrentEquippedItem();
            if (direction < 0) {
                player.inventory.setInventorySlotContents(slot, backpack.getStackInSlot(3));
                backpack.setInventorySlotContents(3, backpack.getStackInSlot(0));
                backpack.setInventorySlotContents(0, current);
            } else {
                if (direction > 0) {
                    player.inventory.mainInventory[slot] = backpack.inventory[0];
                    backpack.setInventorySlotContents(0, backpack.getStackInSlot(3));
                    backpack.setInventorySlotContents(3, current);
                }
            }
        }
        cycling = false;
    }

    public static boolean tryPlaceOnDeath(EntityPlayer player) {
        ItemStack backpack = Utils.getWearingBackpack(player);
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

    public static ChunkCoordinates getNearestEmptyChunkCoordinates(World world, int x, int y, int z, int radius, boolean except) {

        for (int i = x; i <= x + radius; ++i) {
            for (int j = y; j <= y + (radius / 2); ++j) {
                for (int k = z; k <= z + (radius); ++k) {
                    if (except && world.isSideSolid(i, j - 1, k, ForgeDirection.UP) && world.isAirBlock(i, j, k) && !compareCoordinates(x, y, z, i, j, k)) {
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

    private static boolean compareCoordinates(int X1, int Y1, int Z1, int X2, int Y2, int Z2) {
        return (X1 == X2 && Y1 == Y2 && Z1 == Z2);
    }

    public static void electrify(EntityPlayer player) {
        ItemStack stack = Utils.getWearingBackpack(player);
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
