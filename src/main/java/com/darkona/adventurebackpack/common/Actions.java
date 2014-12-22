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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

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
        player.motionY += 0.30;
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
        if (hose != null && hose.getItem() instanceof ItemHose)
        {
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
            player.inventory.mainInventory[slot] = backpack.getStackInSlot(Constants.upperTool);
            backpack.setInventorySlotContentsNoSave(Constants.upperTool, backpack.getStackInSlot(Constants.lowerTool));
            LogHelper.info("Item of class " + backpack.getStackInSlot(Constants.lowerTool).getItem().getClass().getName());
            backpack.setInventorySlotContentsNoSave(Constants.lowerTool, current);
            backpack.saveChanges();
            player.inventory.closeInventory();
        } else
        {
            if (direction > 0)
            {
                player.inventory.mainInventory[slot] = backpack.getStackInSlot(Constants.lowerTool);
                backpack.setInventorySlotContentsNoSave(Constants.lowerTool, backpack.getStackInSlot(Constants.upperTool));
                backpack.setInventorySlotContentsNoSave(Constants.upperTool, current);
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

           /* for(int i = (int)player.posY - 5; i <= player.posY + 5; i+)
            {*/
                ChunkCoordinates spawn = getNearestEmptyChunkCoordinates(world, (int) player.posX, (int) player.posZ,(int) player.posX, (int) player.posY, (int) player.posZ, 12, false, 1,(byte) 0);

                if (spawn != null)
                {
                    if (((ItemAdventureBackpack) ModItems.adventureBackpack).placeBackpack(player.inventory.armorInventory[2], player, world, spawn.posX, spawn.posY, spawn.posZ,
                            ForgeDirection.UP.ordinal(), false))
                    {
                        return true;
                    }
                }
            /*}*/
        }
        return false;
    }



    private static ChunkCoordinates checkCoords(World world, int origX, int origZ, int X,int Y, int Z, boolean except)
    {
        //LogHelper.info("Checking coordinates in X="+X+", Y="+Y+", Z="+Z);
        if (except && world.isSideSolid(X, Y - 1, Z, ForgeDirection.UP) && world.isAirBlock(X, Y, Z) && !areCoordinatesTheSame(origX, Y, origZ, X, Y, Z))
        {
            //LogHelper.info("Found spot with the exception of the death point");
            return new ChunkCoordinates(X, Y, Z);
        }
        if (!except && world.isSideSolid(X, Y - 1, Z, ForgeDirection.UP) && world.isAirBlock(X, Y, Z))
        {
            //LogHelper.info("Found spot without exceptions");
            return new ChunkCoordinates(X, Y, Z);
        }
        return null;
    }

    /**
     * Gets you the nearest Empty Chunk Coordinates, free of charge! Looks in three dimensions and finds a block
     * that a: can have stuff placed on it and b: has space above it.
     *
     * @param world  The world object.
     * @param origX Original X coordinate
     * @param origZ Original Z coordinate
     * @param X
     * @param Y
     * @param Z      The coordinates of the central point of the search.
     * @param radius The radius of the search. If set to higher numbers, will create a ton of lag
     * @param except Wheter or not to include the origin of the search as a valid block.
     * @param steps number of steps of the recursive recursiveness that recurses through the recursion. It is the first size of the spiral, should be one (1) always at the first call.
     * @param pass Pass switch for the witchcraft I can't quite explain.
     * @return The coordinates of the block in the chunk of the world of the game of the server of the owner of the computer, where you can place something above it.
     */
    public static ChunkCoordinates getNearestEmptyChunkCoordinates(World world, int origX, int origZ, int X,int Y, int Z, int radius, boolean except, int steps, byte pass)
    {
        //Spiral search, because I'm awesome :)
        //This is so the backpack tries to get placed near the death point first
        //And then goes looking farther away at each step
       // Steps mod 2 == 0 => X++, Z--
        //Steps mod 2 == 1 => X--, Z++

        //
        if(steps >= radius) return null;
        int i = X, j = Z;
        if (steps % 2 == 0)
        {
            if(pass == 0)
            {
                for(;i <= X + steps; i++){
                    ChunkCoordinates coords = checkCoords(world, origX, origZ, X, Y, Z, except);
                    if(coords != null)
                    {
                        return coords;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinates(world, origX,origZ,i,Y,j,radius,except,steps,pass);
            }
            if(pass == 1)
            {
                for(;j >= Z - steps; j--){
                    ChunkCoordinates coords = checkCoords(world, origX, origZ, X, Y, Z, except);
                    if(coords != null)
                    {
                        return coords;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinates(world, origX,origZ,i,Y,j,radius,except,steps,pass);
            }
        }

        if(steps % 2 == 1)
        {
            if(pass == 0)
            {
                for(;i >= X - steps; i--){
                    ChunkCoordinates coords = checkCoords(world, origX, origZ, X, Y, Z, except);
                    if(coords != null)
                    {
                        return coords;
                    }
                }
                pass++;
                return getNearestEmptyChunkCoordinates(world, origX,origZ,i,Y,j,radius,except,steps,pass);
            }
            if(pass == 1)
            {
                for(;j <= Z + steps; j++){
                    ChunkCoordinates coords = checkCoords(world, origX, origZ, X, Y, Z, except);
                    if(coords != null)
                    {
                        return coords;
                    }
                }
                pass--;
                steps++;
                return getNearestEmptyChunkCoordinates(world, origX,origZ,i,Y,j,radius,except,steps,pass);
            }
        }
       /* if (except && world.isSideSolid(X, Y - 1, Z, ForgeDirection.UP) && world.isAirBlock(X, Y, Z) && !areCoordinatesTheSame(x, y, z, X, Y, Z))
        {
            return new ChunkCoordinates(X, Y, Z);
        }
        if (!except && world.isSideSolid(X, Y - 1, Z, ForgeDirection.UP) && world.isAirBlock(X, Y, Z))
        {
            return new ChunkCoordinates(X, Y, Z);
        }*/



       /* Old code. Still works, though.
       for (int i = x - radius; i <= x + radius; i++)
        {
            for (int j = y - (radius / 2); j <= y + (radius / 2); j++)
            {
                for (int k = z + radius; k <= z + (radius); k++)
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
        }*/
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
        ItemStack backpack = Wearing.getWearingBackpack(player);
        if (BackpackNames.getBackpackColorName(backpack).equals("Pig"))
        {
            BackpackNames.setBackpackColorName(backpack, "Pigman");
        }
        if (BackpackNames.getBackpackColorName(backpack).equals("Diamond"))
        {
            BackpackNames.setBackpackColorName(backpack, "Electric");
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
                //LogHelper.info("Fired an arrow!");
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
                } else if (world.isRemote)
                {
                    player.addChatComponentMessage(new ChatComponentText("Can't deploy the sleeping bag! Check the surrounding area."));
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
            switch (meta & 3)
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
