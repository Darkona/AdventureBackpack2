package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.PlayerActionPacket;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ItemSteamJetpack extends ItemAB implements IBackWearableItem
{

    public static byte OFF_MODE = 0;
    public static byte NORMAL_MODE = 1;

    public ItemSteamJetpack()
    {
        super();
        setUnlocalizedName("steamJetpack");
        setFull3D();
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.JETPACK_GUI, GUIPacket.FROM_HOLDING));
        }
        return stack;
    }

    private void runFirebox(InventorySteamJetpack inv)
    {
        if (inv.getBurnTicks() <= 0)
        {
            inv.setBurnTicks(inv.consumeFuel());
            inv.currentItemBurnTime = inv.getBurnTicks();
        }
        inv.dirtyInventory();
    }

    private void runHeater(InventorySteamJetpack inv, World world, EntityPlayer player)
    {
        int temperature = inv.getTemperature();
        int burnTicks = inv.getBurnTicks() - 1;
        int coolTicks = inv.getCoolTicks() - 1;
        //Run the boiler: increase/maintain heat if there's burn ticks, or decrease temperature if there aren't

        if (burnTicks > 0)
        {
            if (temperature < InventorySteamJetpack.MAX_TEMPERATURE)
            {
                if (burnTicks % inv.getIncreasingFactor() == 0)
                {
                    ++temperature;
                    coolTicks = coolTicks < 5000 ? coolTicks + 100 : coolTicks;
                }
            }
        } else if (burnTicks <= 0)
        {
            inv.currentItemBurnTime = 0;
            if (coolTicks % inv.getDecreasingFactor() == 0)
            {
                int minTemp = 25;
                BiomeDictionary.Type[] thisBiome = BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords((int) player.posX, (int) player.posZ));
                for (BiomeDictionary.Type type : thisBiome)
                {
                    switch (type)
                    {
                        case COLD:
                            minTemp = 0;
                            break;
                        case HOT:
                            minTemp = 30;
                            break;
                        case NETHER:
                            minTemp = 45;
                            break;
                        case SNOWY:
                            minTemp = 0;
                            break;
                        default:
                            minTemp = 25;
                            break;
                    }
                }
                temperature = (temperature - 1 >= minTemp) ? temperature - 1 : 0;
            }
        }
        inv.setTemperature(temperature);
        inv.setCoolTicks(coolTicks);
        inv.setBurnTicks(burnTicks <= 0 ? 0 : burnTicks);
    }

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        InventorySteamJetpack inv = (InventorySteamJetpack)BackpackProperty.get(player).getInventory();
        inv.openInventory();
        boolean mustFizzz = !inv.isInUse();

        boolean canUse = inv.getSteamTank().getFluidAmount() >= 5;

        int steamConsumed = 10;

        if (inv.getStatus())
        {
            runFirebox(inv);
        }
        runHeater(inv, world, player);
        runBoiler(inv, world, player);
        inv.dirtyBoiler();

        //Elevation
        if (world.isRemote)
        {
            if (inv.getStatus() && canUse && Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed())
            {
                inv.setInUse(true);
                elevate(player);
                ModNetwork.net.sendToServer(new EntityParticlePacket.Message(EntityParticlePacket.JETPACK_PARTICLE,player));
            } else
            {
                inv.setInUse(false);
            }
        }

        if (inv.isInUse())
        {
            player.moveFlying(player.moveStrafing, player.moveForward, 0.02f);
            if(player.fallDistance > 1)
            {

                player.fallDistance -= 1;
            }
            if(player.motionY >= 0)
            {
                player.fallDistance = 0;
            }
        }

        if (world.isRemote)
        {
            if (inv.isInUse())
            {
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.JETPACK_IN_USE));
                if (mustFizzz)
                {
                    ModNetwork.net.sendToServer(new EntitySoundPacket.Message(EntitySoundPacket.JETPACK_FIZZ, player));
                }
            } else
            {
                ModNetwork.net.sendToServer(new PlayerActionPacket.ActionMessage(PlayerActionPacket.JETPACK_NOT_IN_USE));
            }
            /*
            if (!player.onGround)
            {
                LogHelper.info("FallDistance: " + player.fallDistance);
                LogHelper.info("MotionY: " + player.motionY);
            }
*/
        } else
        {
            if (inv.isInUse())
            {
                inv.getSteamTank().drain(steamConsumed, true);
                if (inv.getSteamTank().getFluidAmount() == 0)
                {
                    inv.setInUse(false);
                }
            }
            inv.closeInventory();
        }

    }

    private void runBoiler(InventorySteamJetpack inv, World world, EntityPlayer player)
    {
        int temperature = inv.getTemperature();
        boolean mustSSSSS = !inv.isLeaking();
        boolean mustBlublub = !inv.isBoiling();
        boolean boiling = inv.isBoiling();
        boolean leaking = inv.isLeaking();

        if (temperature >= 100 && inv.getWaterTank().getFluidAmount() > 0)
        {
            if(!boiling)boiling = true;

            if (!world.isRemote && mustBlublub)
            {
                ModNetwork.net.sendTo(new EntitySoundPacket.Message(EntitySoundPacket.BOILING_BUBBLES, player), (EntityPlayerMP) player);
            }
        } else
        {
            if (boiling)
            {
                boiling = false;
            }
        }

        if (boiling)
        {
            if (inv.getSteamTank().getFluidAmount() < inv.getSteamTank().getCapacity())
            {
                if (inv.getWaterTank().getFluid() != null)
                {
                    int steam = inv.getWaterTank().drain((temperature / 100), true).amount;
                    inv.getSteamTank().fill(new FluidStack(FluidRegistry.getFluid("steam"), steam * 4), true);
                    inv.dirtyTanks();
                }
            }
        }

        if (inv.getSteamTank().getFluidAmount() < inv.getSteamTank().getCapacity() - 100)
        {
            if (leaking)
            {
                leaking = false;
            }
        } else
        {
            if (!leaking)
            {
                leaking = true;
                if (!world.isRemote && mustSSSSS)
                {
                    ModNetwork.net.sendTo(new EntitySoundPacket.Message(EntitySoundPacket.LEAKING_STEAM, player), (EntityPlayerMP) player);
                }
            }
        }
        inv.setBoiling(boiling);
        inv.setLeaking(leaking);
        inv.setTemperature(temperature);
    }

    public static void elevate(EntityPlayer player)
    {
        if (player.motionY <= 0.32 && player.posY < 100)
        {
            player.motionY += 0.1;
        } else
        {
            if (player.posY < 100) player.motionY = Math.max(player.motionY, 0.32);
            if (player.posY > 100) player.motionY = 0.32 - ((player.posY % 100) / 100);
        }
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        onUnequipped(world, player, stack);
        player.dropPlayerItemWithRandomChoice(stack.copy(), false);
        BackpackProperty.get(player).setWearable(null);
    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        inv.calculateLostTime();
    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        inv.setBoiling(false);
        inv.setInUse(false);
        inv.setLeaking(false);
        inv.setStatus(false);
        inv.setSystemTime(System.currentTimeMillis());
        inv.markDirty();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelSteamJetpack.setWearable(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("steamJetpack");
    }
}
