package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.fluids.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.network.RelayGuard;
import com.darkona.adventurebackpack.network.WearableModePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.Random;

/**
 * Created on 23/12/2014
 *
 * @author Darkona
 */
public class ServerActions
{
    public static final boolean HOSE_SWITCH = false;
    public static final boolean HOSE_TOGGLE = true;

    /**
     * Cycles tools. In a cycle. The tool in your hand with the tools in the special tool slots of the backpack, to be precise.
     *
     * @param player    - Duh
     * @param direction - An integer indicating the direction of the switch. Nobody likes to swith always inthe same
     *                  direction all the timeInSeconds. That's stupid.
     * @param slot      The slot that will be switched with the backpack.
     */
    public static void cycleTool(EntityPlayer player, int direction, int slot)
    {
        // The client sends the slot it scrolled on; the server's currentItem can lag behind it, so the
        // swap uses the stack that is in that slot right now and checks it again (#56, #67, #78).
        if (direction == 0 || !isHotbarSlot(slot) || !Wearing.isWearingBackpack(player)) return;
        try
        {
            ItemStack current = player.inventory.mainInventory[slot];
            if (!canCycleTool(Wearing.getWearingBackpack(player), current)) return;
            InventoryBackpack backpack = Wearing.getBackpackInv(player, true);
            backpack.openInventory();
            ItemStack[] tools = {current, backpack.getStackInSlot(Constants.upperTool), backpack.getStackInSlot(Constants.lowerTool)};
            rotateTools(tools, direction);
            player.inventory.mainInventory[slot] = tools[0];
            backpack.setInventorySlotContentsNoSave(Constants.upperTool, tools[1]);
            backpack.setInventorySlotContentsNoSave(Constants.lowerTool, tools[2]);
            backpack.markDirty();
            player.inventory.markDirty();
        } catch (Exception oops)
        {
            LogHelper.debug("Exception trying to cycle tools.");
            oops.printStackTrace();
        }
    }

    /** True when the held stack may go into the backpack's tool slots by cycling. */
    public static boolean canCycleTool(ItemStack backpack, ItemStack held)
    {
        if (held == null) return false;
        return SlotTool.isValidTool(held)
                || (held.getItem() == Items.bow && "Skeleton".equals(BackpackNames.getBackpackColorName(backpack)) && !SlotTool.isBlacklisted(held));
    }

    public static boolean isHotbarSlot(int slot)
    {
        return slot >= 0 && slot < 9;
    }

    /**
     * Rotates {held, upperTool, lowerTool} in place. A negative direction takes the upper tool into the hand,
     * a positive one the lower tool; every stack ends in exactly one place.
     */
    public static void rotateTools(Object[] tools, int direction)
    {
        Object held = tools[0];
        if (direction < 0)
        {
            tools[0] = tools[1];
            tools[1] = tools[2];
            tools[2] = held;
        } else if (direction > 0)
        {
            tools[0] = tools[2];
            tools[2] = tools[1];
            tools[1] = held;
        }
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
        if (drained != null && drained.amount >= Constants.bucket && FluidEffectRegistry.hasFluidEffect(drained.getFluid()))
        {
            done = FluidEffectRegistry.executeFluidEffectsForFluid(drained.getFluid(), player, world);
        }
        return done;
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
        if (!isHotbarSlot(slot)) return;
        ItemStack hose = player.inventory.mainInventory[slot];
        if (hose != null && hose.getItem() instanceof ItemHose)
        {
            NBTTagCompound tag = hose.hasTagCompound() ? hose.stackTagCompound : new NBTTagCompound();
            if (!action)
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

            if (action)
            {
                int tank = ItemHose.getHoseTank(hose);
                tank = (tank + 1) % 2;
                tag.setInteger("tank", tank);
            }
            hose.setTagCompound(tag);
        }
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
        InventoryBackpack backpack = new InventoryBackpack(Wearing.getWearingBackpack(player));

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
                backpack.dirtyInventory();
            }

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(entityarrow);
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
        // The coordinates come from the client: only a backpack within reach can deploy its bag
        if (player.getDistanceSq(coordX + 0.5, coordY + 0.5, coordZ + 0.5) > 64) return;
        if (world.getTileEntity(coordX, coordY, coordZ) instanceof TileAdventureBackpack)
        {
            TileAdventureBackpack te = (TileAdventureBackpack) world.getTileEntity(coordX, coordY, coordZ);
            if (!te.isSBDeployed() && ConfigHandler.SLEEPINGBAG_ENABLED)
            {
                int can[] = canDeploySleepingBag(world, coordX, coordY, coordZ);
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

    public static int[] canDeploySleepingBag(World world, int coordX, int coordY, int coordZ)
    {
        TileAdventureBackpack te = (TileAdventureBackpack) world.getTileEntity(coordX, coordY, coordZ);
        int newMeta = -1;

        if (!te.isSBDeployed())
        {
            int meta = world.getBlockMetadata(coordX, coordY, coordZ);
            switch (meta & 3)
            {
                case 0:
                    --coordZ;
                    if (Utils.isFreeSpace(world, coordX, coordY, coordZ) && world.getBlock(coordX, coordY - 1, coordZ).getMaterial().isSolid())
                    {
                        if (Utils.isFreeSpace(world, coordX, coordY, coordZ - 1) && world.getBlock(coordX, coordY - 1, coordZ - 1).getMaterial().isSolid())
                        {
                            newMeta = 2;
                        }
                    }
                    break;
                case 1:
                    ++coordX;
                    if (Utils.isFreeSpace(world, coordX, coordY, coordZ) && world.getBlock(coordX, coordY - 1, coordZ).getMaterial().isSolid())
                    {
                        if (Utils.isFreeSpace(world, coordX + 1, coordY, coordZ) && world.getBlock(coordX + 1, coordY - 1, coordZ).getMaterial().isSolid())
                        {
                            newMeta = 3;
                        }
                    }
                    break;
                case 2:
                    ++coordZ;
                    if (Utils.isFreeSpace(world, coordX, coordY, coordZ) && world.getBlock(coordX, coordY - 1, coordZ).getMaterial().isSolid())
                    {
                        if (Utils.isFreeSpace(world, coordX, coordY, coordZ + 1) && world.getBlock(coordX, coordY - 1, coordZ + 1).getMaterial().isSolid())
                        {
                            newMeta = 0;
                        }
                    }
                    break;
                case 3:
                    --coordX;
                    if (Utils.isFreeSpace(world, coordX, coordY, coordZ) && world.getBlock(coordX, coordY - 1, coordZ).getMaterial().isSolid())
                    {
                        if (Utils.isFreeSpace(world, coordX - 1, coordY, coordZ) && world.getBlock(coordX - 1, coordY - 1, coordZ).getMaterial().isSolid())
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

    public static void toggleCopterPack(EntityPlayer player, ItemStack copter, byte type)
    {
        if (!ItemCopterPack.isUsable()) return;
        String message = "";
        boolean actionPerformed = false;

        if (!copter.hasTagCompound())
        {
            copter.stackTagCompound = new NBTTagCompound();
        }
        if (!copter.stackTagCompound.hasKey("status"))
        {
            copter.stackTagCompound.setByte("status", ItemCopterPack.OFF_MODE);
        }

        byte mode = copter.stackTagCompound.getByte("status");
        byte newMode = ItemCopterPack.OFF_MODE;

        if (type == WearableModePacket.COPTER_ON_OFF)
        {
            if (mode == ItemCopterPack.OFF_MODE)
            {
                // Starting the engine takes fuel, and it does not start without that much in the tank
                InventoryCopterPack inv = new InventoryCopterPack(copter);
                if (!FlightCost.canStart(inv.getFuelTank().getFluidAmount(), ConfigHandler.COPTER_START_FUEL))
                {
                    if (player.worldObj.isRemote)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.copterpack.outoffuel"));
                    }
                    return;
                }
                if (ConfigHandler.COPTER_START_FUEL > 0) inv.consumeFuel(ConfigHandler.COPTER_START_FUEL);
                newMode = ItemCopterPack.NORMAL_MODE;
                message = "adventurebackpack:messages.copterpack.normal";
                actionPerformed = true;
                // Turning it on and off quickly does not flood the nearby players with start sounds
                if (player instanceof EntityPlayerMP && RelayGuard.mayRelayFrom((EntityPlayerMP) player))
                {
                    ModNetwork.sendToNearby(new EntitySoundPacket.Message(EntitySoundPacket.COPTER_SOUND,player), player);

                }
            } else
            {
                newMode = ItemCopterPack.OFF_MODE;
                message = "adventurebackpack:messages.copterpack.off";
                actionPerformed = true;
            }
        }

        if (type == WearableModePacket.COPTER_TOGGLE && mode != ItemCopterPack.OFF_MODE)
        {
            if (mode == ItemCopterPack.NORMAL_MODE)
            {
                newMode = ItemCopterPack.HOVER_MODE;
                message = "adventurebackpack:messages.copterpack.hover";
                actionPerformed = true;
            }
            if (mode == ItemCopterPack.HOVER_MODE)
            {
                newMode = ItemCopterPack.NORMAL_MODE;
                message = "adventurebackpack:messages.copterpack.normal";
                actionPerformed = true;
            }
        }

        if (actionPerformed)
        {
            copter.stackTagCompound.setByte("status", newMode);
            if (player.worldObj.isRemote)
            {
                player.addChatComponentMessage(new ChatComponentTranslation(message));
            }

        }
    }

    public static void toggleSteamJetpack(EntityPlayer player, ItemStack jetpack, byte on_off)
    {
        InventorySteamJetpack inv = new InventorySteamJetpack(jetpack);
        if(inv.getStatus())
        {
            inv.setStatus(false);
            inv.markDirty();
            if (player.worldObj.isRemote)
            {
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.jetpack.off"));
            }
        }else{
            inv.setStatus(true);
            inv.markDirty();
            if (player.worldObj.isRemote)
            {
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.jetpack.on"));
            }
        }
    }
}
