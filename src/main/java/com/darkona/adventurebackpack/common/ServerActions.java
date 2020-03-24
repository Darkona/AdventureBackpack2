package com.darkona.adventurebackpack.common;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.block.BlockSleepingBag;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.fluids.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.inventory.InventoryCoalJetpack;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.network.WearableModePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.CoordsUtils;
import com.darkona.adventurebackpack.util.Wearing;

import static com.darkona.adventurebackpack.common.Constants.BUCKET;
import static com.darkona.adventurebackpack.common.Constants.Copter.TAG_STATUS;
import static com.darkona.adventurebackpack.common.Constants.TOOL_LOWER;
import static com.darkona.adventurebackpack.common.Constants.TOOL_UPPER;

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
     * Cycles tools. In a cycle. The tool in your hand with the tools in the special tool playerSlot of the backpack,
     * to be precise.
     *
     * @param player    Duh
     * @param isWheelUp A boolean indicating the direction of the switch. Nobody likes to swith always in the same
     *                  direction all the timeInSeconds. That's stupid.
     */
    public static void cycleTool(EntityPlayer player, boolean isWheelUp)
    {
        if (!GeneralReference.isDimensionAllowed(player))
            return;

        int playerSlot = player.inventory.currentItem;
        ItemStack current = player.getCurrentEquippedItem();
        if (SlotTool.isValidTool(current))
        {
            int backpackSlot = isWheelUp ? TOOL_UPPER : TOOL_LOWER;
            InventoryBackpack backpack = Wearing.getWearingBackpackInv(player);
            player.inventory.setInventorySlotContents(playerSlot, backpack.getStackInSlot(backpackSlot));
            backpack.setInventorySlotContents(backpackSlot, current);
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
        FluidStack drained = tank.drain(BUCKET, false);
        boolean done = false;
        if (drained != null && drained.amount >= BUCKET && FluidEffectRegistry.hasFluidEffect(drained.getFluid()))
        {
            done = FluidEffectRegistry.executeFluidEffectsForFluid(drained.getFluid(), player, world);
        }
        return done;
    }

    /**
     * @param player    Duh!
     * @param isWheelUp The direction in which the hose modes will switch.
     * @param action    The type of the action to be performed on the hose.
     *                  Can be HOSE_SWITCH for mode or HOSE_TOGGLE for tank
     */
    public static void switchHose(EntityPlayer player, boolean isWheelUp, boolean action)
    {
        if (Wearing.isHoldingHose(player))
        {
            ItemStack hose = player.inventory.getCurrentItem();
            NBTTagCompound tag = hose.hasTagCompound() ? hose.stackTagCompound : new NBTTagCompound();

            if (!action)
            {
                int mode = ItemHose.getHoseMode(hose);
                if (!ConfigHandler.enableHoseDrink)
                {
                    mode = (mode + 1) % 2;
                }
                else if (isWheelUp)
                {
                    mode = (mode + 1) % 3;
                }
                else
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

        if (BackpackTypes.getType(backpack) == BackpackTypes.PIG)
        {
            BackpackUtils.setBackpackType(backpack, BackpackTypes.PIGMAN);
        }
        else if (BackpackTypes.getType(backpack) == BackpackTypes.DIAMOND)
        {
            BackpackUtils.setBackpackType(backpack, BackpackTypes.ELECTRIC);
        }
    }

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
            }
            else
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

    public static void toggleSleepingBag(EntityPlayer player, boolean isTile, int cX, int cY, int cZ)
    {
        World world = player.worldObj;

        if (!world.provider.canRespawnHere() || world.getBiomeGenForCoords(cX, cZ) == BiomeGenBase.hell)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.cant.sleep.here"));
            player.closeScreen();
            return;
        }

        if (isTile && world.getTileEntity(cX, cY, cZ) instanceof TileAdventureBackpack)
        {
            TileAdventureBackpack te = (TileAdventureBackpack) world.getTileEntity(cX, cY, cZ);
            if (!te.isSleepingBagDeployed())
            {
                int can[] = CoordsUtils.canDeploySleepingBag(world, player, cX, cY, cZ, true);
                if (can[0] > -1)
                {
                    if (te.deploySleepingBag(player, world, can[0], can[1], can[2], can[3]))
                    {
                        player.closeScreen();
                    }
                }
                else if (!world.isRemote)
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.backpack.cant.bag"));
                }
            }
            else
            {
                te.removeSleepingBag(world);
            }
            player.closeScreen();
        }
        else if (!isTile && Wearing.isWearingBackpack(player))
        {
            int can[] = CoordsUtils.canDeploySleepingBag(world, player, cX, cY, cZ, false);
            if (can[0] > -1)
            {
                InventoryBackpack inv = Wearing.getWearingBackpackInv(player);
                if (inv.deploySleepingBag(player, world, can[0], can[1], can[2], can[3]))
                {
                    Block portableBag = world.getBlock(can[1], can[2], can[3]);
                    if (portableBag instanceof BlockSleepingBag)
                    {
                        inv.getExtendedProperties().setBoolean(Constants.TAG_SLEEPING_IN_BAG, true);
                        ((BlockSleepingBag) portableBag).onPortableBlockActivated(world, player, can[1], can[2], can[3]);
                    }
                }
            }
            else if (!world.isRemote)
            {
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.backpack.cant.bag"));
            }
            player.closeScreen();
        }
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
        if (ConfigHandler.allowSoundPiston)
        {
            player.playSound("tile.piston.out", 0.5F, player.getRNG().nextFloat() * 0.25F + 0.6F);
        }
        player.motionY += ConfigHandler.pistonBootsJumpHeight / 10.0F;
        player.jumpMovementFactor += 0.3;
    }

    public static void copterSoundAtLogin(EntityPlayer player)
    {
        byte status = BackpackUtils.getWearableCompound(BackpackProperty.get(player).getWearable()).getByte(TAG_STATUS);

        if (!player.worldObj.isRemote && status != ItemCopterPack.OFF_MODE)
        {
            ModNetwork.sendToNearby(new EntitySoundPacket.Message(EntitySoundPacket.COPTER_SOUND, player), player);
        }
    }

    public static void jetpackSoundAtLogin(EntityPlayer player)
    {
        boolean isBoiling = BackpackUtils.getWearableCompound(BackpackProperty.get(player).getWearable()).getBoolean("boiling");

        if (!player.worldObj.isRemote && isBoiling)
        {
            //ModNetwork.sendToNearby(new EntitySoundPacket.Message(EntitySoundPacket.BOILING_BUBBLES, player), player); //TODO difference?
            ModNetwork.net.sendTo(new EntitySoundPacket.Message(EntitySoundPacket.BOILING_BUBBLES, player), (EntityPlayerMP) player);
        }
    }

    public static void toggleCopterPack(EntityPlayer player, ItemStack copter, byte type)
    {
        String message = "";
        boolean actionPerformed = false;
        byte mode = BackpackUtils.getWearableCompound(copter).getByte(TAG_STATUS);
        byte newMode = ItemCopterPack.OFF_MODE;

        if (type == WearableModePacket.COPTER_ON_OFF)
        {
            if (mode == ItemCopterPack.OFF_MODE)
            {
                newMode = ItemCopterPack.NORMAL_MODE;
                message = "adventurebackpack:messages.copterpack.normal";
                actionPerformed = true;
                if (!player.worldObj.isRemote)
                {
                    ModNetwork.sendToNearby(new EntitySoundPacket.Message(EntitySoundPacket.COPTER_SOUND, player), player);
                }
            }
            else
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
            BackpackUtils.getWearableCompound(copter).setByte(TAG_STATUS, newMode);
            if (player.worldObj.isRemote)
            {
                player.addChatComponentMessage(new ChatComponentTranslation(message));
            }
        }
    }

    public static void toggleToolCycling(EntityPlayer player, ItemStack backpack)
    {
        InventoryBackpack inv = new InventoryBackpack(backpack);
        if (ConfigHandler.enableToolsCycling)
        {
            if (inv.getDisableCycling())
            {
                inv.setDisableCycling(false);
                inv.markDirty();
                if (player.worldObj.isRemote)
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.cycling.on"));
                }
            }
            else
            {
                inv.setDisableCycling(true);
                inv.markDirty();
                if (player.worldObj.isRemote)
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.cycling.off"));
                }
            }
        }
    }

    public static void toggleNightVision(EntityPlayer player, ItemStack backpack)
    {
        InventoryBackpack inv = new InventoryBackpack(backpack);
        if (inv.getDisableNVision())
        {
            inv.setDisableNVision(false);
            inv.markDirty();
            if (player.worldObj.isRemote)
            {
                player.playSound("mob.bat.idle", 0.2F, 1.0F);
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.nightvision.on"));
            }
        }
        else
        {
            inv.setDisableNVision(true);
            inv.markDirty();
            if (player.worldObj.isRemote)
            {
                player.playSound("mob.bat.death", 0.2F, 2.0F);
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.nightvision.off"));
            }
        }
    }

    public static void toggleCoalJetpack(EntityPlayer player, ItemStack jetpack)
    {
        InventoryCoalJetpack inv = new InventoryCoalJetpack(jetpack);
        if (inv.getStatus())
        {
            inv.setStatus(false);
            inv.markDirty();
            if (player.worldObj.isRemote)
            {
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.jetpack.off"));
            }
        }
        else
        {
            inv.setStatus(true);
            inv.markDirty();
            if (player.worldObj.isRemote)
            {
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.jetpack.on"));
            }
        }
    }
}