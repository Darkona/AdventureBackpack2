package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.item.ItemSteamJetpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.item.ItemCrossbow;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.item.ItemFlamethrowerPack;
import com.darkona.adventurebackpack.fluids.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.network.RelayGuard;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
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

public class ServerActions
{
    public static final boolean HOSE_SWITCH = false;
    public static final boolean HOSE_TOGGLE = true;

    public static void cycleTool(EntityPlayer player, int direction, int slot)
    {
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

    public static void toggleFlamethrower(EntityPlayer player)
    {
        if (player == null || player.worldObj.isRemote || !ItemFlamethrowerPack.isUsable()) return;
        ItemStack pack = Wearing.getWearingFlamethrowerPack(player);
        if (pack == null) return;
        InventoryFlamethrowerPack inv = new InventoryFlamethrowerPack(pack);
        if (inv.isOn())
        {
            inv.setOn(false);
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.flamethrower.off"));
        } else if (inv.getFuel() <= 0 && ConfigHandler.FLAMETHROWER_FUEL_PER_BURST > 0)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.flamethrower.nofuel"));
            return;
        } else
        {
            inv.setOn(true);
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.flamethrower.on"));
            if (ConfigHandler.FLAMETHROWER_SOUND) player.worldObj.playSoundAtEntity(player, "fire.ignite", 1.0F, 1.0F);
        }
        inv.markDirty();
    }

    public static void toggleAbilities(EntityPlayer player)
    {
        BackpackProperty props = BackpackProperty.get(player);
        if (props == null) return;
        int result = AbilityToggle.toggle(ConfigHandler.BACKPACK_ABILITIES, props.isAbilitiesOff());
        if (result != AbilityToggle.GLOBALLY_DISABLED)
        {
            props.setAbilitiesOff(result == AbilityToggle.TURNED_OFF);
            ItemStack backpack = Wearing.getWearingBackpack(player);
            if (result == AbilityToggle.TURNED_OFF && backpack != null
                    && BackpackAbilities.hasRemoval(BackpackNames.getBackpackColorName(backpack)))
            {
                BackpackAbilities.backpackAbilities.executeRemoval(player, player.worldObj, backpack);
            }
            BackpackProperty.syncToNearIfChanged(player);
        }
        if (!ConfigHandler.ABILITY_TOGGLE_MESSAGE) return;
        switch (result)
        {
            case AbilityToggle.TURNED_OFF:
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.abilities.off"));
                break;
            case AbilityToggle.TURNED_ON:
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.abilities.on"));
                break;
            default:
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.abilities.disabled"));
                break;
        }
    }

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

    public static boolean setFluidEffect(World world, EntityPlayer player, FluidTank tank)
    {
        FluidStack drained = tank.drain(Constants.bucket, false);
        boolean done = false;
        if (drained != null && drained.amount >= Constants.bucket && FluidEffectRegistry.hasFluidEffect(drained.getFluid())
                && !ApiEvents.drinkCanceled(player, world, drained.getFluid(), ConfigHandler.HOSE_DRINK_AMOUNT))
        {
            done = FluidEffectRegistry.executeFluidEffectsForFluid(drained.getFluid(), player, world);
        }
        return done;
    }

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

    public static void leakArrow(EntityPlayer player, ItemStack bow, int charge)
    {
        World world = player.worldObj;
        Random itemRand = new Random();
        InventoryBackpack backpack = new InventoryBackpack(Wearing.getWearingBackpack(player));

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
                backpack.consumeInventoryItem(Items.arrow);
                backpack.dirtyInventory();
            }

            if (!world.isRemote)
            {
                world.spawnEntityInWorld(entityarrow);
            }
        }
    }

    public static void toggleSleepingBag(EntityPlayer player, int coordX, int coordY, int coordZ)
    {
        World world = player.worldObj;
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

    public static void pistonBootsJump(EntityPlayer player)
    {
        player.playSound("tile.piston.out", 0.5F, player.getRNG().nextFloat() * 0.25F + 0.6F);
        player.motionY += 0.30;
        player.jumpMovementFactor += 0.3;
        if (PistonLeap.leaps(ConfigHandler.PISTONBOOTS_LEAP_ENABLED, player.isSprinting(), ConfigHandler.PISTONBOOTS_LEAP_REQUIRES_SPRINT))
        {
            double scale = PistonLeap.scale(player.motionX, player.motionZ, ConfigHandler.PISTONBOOTS_LEAP_BOOST, ConfigHandler.PISTONBOOTS_LEAP_MAX_SPEED);
            player.motionX *= scale;
            player.motionZ *= scale;
        }
    }

    public static void keyAction(EntityPlayer player, KeyActions.Key key, int direction)
    {
        if (player == null || player.worldObj.isRemote || key == null) return;
        int slot = player.inventory.currentItem;
        switch (KeyContext.resolve(key, player, true))
        {
            case TOGGLE_ABILITIES:
                toggleAbilities(player);
                break;
            case TOGGLE_COPTER:
                toggleCopterPack(player, Wearing.getWearingCopter(player), true);
                break;
            case COPTER_MODE:
                toggleCopterPack(player, Wearing.getWearingCopter(player), false);
                break;
            case TOGGLE_JETPACK:
                toggleSteamJetpack(player, Wearing.getWearingSteam(player));
                break;
            case TOGGLE_PILOT:
                toggleFlamethrower(player);
                break;
            case BOAT_MOTOR:
                ((EntityInflatableBoat) player.ridingEntity).toggleMotor(player);
                break;
            case HOSE_TANK:
                switchHose(player, HOSE_TOGGLE, 0, slot);
                break;
            case HOSE_MODE:
                switchHose(player, HOSE_SWITCH, direction, slot);
                break;
            case CYCLE_TOOL:
                cycleTool(player, direction, slot);
                break;
            case CROSSBOW_MODE:
                ItemCrossbow.cycleMode(player);
                break;
            default:
                break;
        }
    }

    public static void toggleCopterPack(EntityPlayer player, ItemStack copter, boolean power)
    {
        if (copter == null || player.worldObj.isRemote || !ItemCopterPack.isUsable()) return;
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

        if (power)
        {
            if (mode == ItemCopterPack.OFF_MODE)
            {
                InventoryCopterPack inv = new InventoryCopterPack(copter);
                if (!FlightCost.canStart(inv.getFuelTank().getFluidAmount(), ConfigHandler.COPTER_START_FUEL))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.copterpack.outoffuel"));
                    return;
                }
                if (ConfigHandler.COPTER_START_FUEL > 0) inv.consumeFuel(ConfigHandler.COPTER_START_FUEL);
                newMode = ItemCopterPack.NORMAL_MODE;
                message = "adventurebackpack:messages.copterpack.normal";
                actionPerformed = true;
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
        } else if (mode == ItemCopterPack.NORMAL_MODE)
        {
            newMode = ItemCopterPack.HOVER_MODE;
            message = "adventurebackpack:messages.copterpack.hover";
            actionPerformed = true;
        } else if (mode == ItemCopterPack.HOVER_MODE)
        {
            newMode = ItemCopterPack.NORMAL_MODE;
            message = "adventurebackpack:messages.copterpack.normal";
            actionPerformed = true;
        }

        if (actionPerformed)
        {
            copter.stackTagCompound.setByte("status", newMode);
            player.addChatComponentMessage(new ChatComponentTranslation(message));
        }
    }

    public static void toggleSteamJetpack(EntityPlayer player, ItemStack jetpack)
    {
        if (jetpack == null || player.worldObj.isRemote) return;
        InventorySteamJetpack inv = ItemSteamJetpack.liveInventory(player, jetpack);
        boolean on = !inv.getStatus();
        inv.setStatus(on);
        inv.markDirty();
        player.addChatComponentMessage(new ChatComponentTranslation(on ? "adventurebackpack:messages.jetpack.on" : "adventurebackpack:messages.jetpack.off"));
    }
}
