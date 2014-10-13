package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.blocks.AdventureBackpackTileEntity;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.entity.ai.EntityAIAvoidPlayerWithBackpack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Darkona on 12/10/2014.
 */
public class BackpackAbilities {
    public static BackpackAbilities instance = new BackpackAbilities();

    public static boolean hasAbility(String colorName) {
        for (String valid : validWearingBackpacks) {
            if (valid.equals(colorName))
                return true;
        }
        return false;
    }

    public void executeAbility(EntityPlayer player, World world, Object backpack) {

        if (backpack instanceof ItemStack) {
            for (String valid : validWearingBackpacks) {
                if (valid.equals(((ItemStack) backpack).stackTagCompound.getString("colorName"))) {
                    try {
                        this.getClass().getMethod("item" + valid, EntityPlayer.class, World.class, ItemStack.class).invoke(instance, player, world, backpack);
                    } catch (Exception oops) {
                        // oops.printStackTrace(); Discard silently, nobody
                        // cares.
                    }
                }
            }
        } else if (backpack instanceof AdventureBackpackTileEntity) {
            for (String valid : validTileBackpacks) {
                if (valid.equals(((AdventureBackpackTileEntity) backpack).getColorName())) {
                    try {
                        this.getClass().getMethod("tile" + valid, World.class, AdventureBackpackTileEntity.class).invoke(instance, world, backpack);
                    } catch (Exception oops) {
                        // oops.printStackTrace(); Discard silently, nobody
                        // cares.
                    }
                }
            }
        }
    }

    private static String[] validWearingBackpacks = {"Cactus", "Cow", "Pig", "Dragon", "Slime", "Chicken", "Wolf", "Ocelot"};

    private static String[] validTileBackpacks = {"Cactus"};

    private int ticks(int seconds) {
        return seconds * 20;
    }

    private boolean isUnderRain(EntityPlayer player) {
        return player.worldObj.canLightningStrikeAt(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ))
                || player.worldObj.canLightningStrikeAt(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY + player.height),
                MathHelper.floor_double(player.posZ));
    }

    public void itemCactus(EntityPlayer player, World world, ItemStack backpack) {
        int lastDropTime = (backpack.stackTagCompound.hasKey("lastTime")) ? backpack.stackTagCompound.getInteger("lastTime") - 1 : 5;

        int drops = 0;
        if (player.isInWater())
            drops += 2;
        if (isUnderRain(player))
            drops += 1;

        if (lastDropTime <= 0 && drops > 0) {
            InventoryItem inv = Utils.getBackpackInv(player, true);
            FluidStack raindrop = new FluidStack(FluidRegistry.WATER, drops);
            inv.leftTank.fill(raindrop, true);
            inv.rightTank.fill(raindrop, true);
            inv.onInventoryChanged();
            lastDropTime = 5;
        }
        backpack.stackTagCompound.setInteger("lastTime", lastDropTime);
    }

    public void itemPig(EntityPlayer player, World world, ItemStack backpack) {
        int oinkTime = backpack.stackTagCompound.hasKey("lastTime") ? backpack.stackTagCompound.getInteger("lastTime") - 1 : ticks(30);
        if (oinkTime <= 0) {
            world.playSoundAtEntity(player, "mob.pig.say", 0.8f, 1f);
            oinkTime = ticks(world.rand.nextInt(31) + 30);
        }
        backpack.stackTagCompound.setInteger("lastTime", oinkTime);
    }

    public void itemSlime(EntityPlayer player, World world, ItemStack backpack) {
        if (player.onGround && player.isSprinting()) {
            int i = 1;
            for (int j = 0; j < i * 2; ++j) {
                float f = world.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f1 = world.rand.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * i * 0.5F * f1;
                world.spawnParticle("slime", player.posX + f2, player.boundingBox.minY, player.posZ + f3, 0.0D, 0.0D, 0.0D);
            }
            int slimeTime = backpack.stackTagCompound.hasKey("lastTime") ? backpack.stackTagCompound.getInteger("lastTime") - 1 : 5;
            if (slimeTime <= 0) {
                world.playSoundAtEntity(player, "mob.slime.small", 0.4F, (world.rand.nextFloat() - world.rand.nextFloat()) * 1F);
                slimeTime = 5;
            }
            backpack.stackTagCompound.setInteger("lastTime", slimeTime);
        }
    }

    public void itemChicken(EntityPlayer player, World world, ItemStack backpack) {

        if (Utils.isWearingBackpack(player)) {
            int eggTime = backpack.stackTagCompound.hasKey("lastTime") ? backpack.stackTagCompound.getInteger("lastTime") - 1 : ticks(300);
            if (eggTime <= 0) {
                player.playSound("mob.chicken.plop", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F + 1.0F);
                if (!world.isRemote) player.dropItem(new ItemEgg(), 1);
                eggTime = ticks(world.rand.nextInt(301) + 300);
            }
            backpack.stackTagCompound.setInteger("lastTime", eggTime);
        }
    }

    public void itemMelon(EntityPlayer player, World world, ItemStack backpack) {
        int lastDropTime = (backpack.stackTagCompound.hasKey("lastTime")) ? backpack.stackTagCompound.getInteger("lastTime") - 1 : 5;

        int drops = 0;
        if (player.isInWater())
            drops += 5;
        if (isUnderRain(player))
            drops += 2;

        if (lastDropTime <= 0 && drops > 0) {
            InventoryItem inv = Utils.getBackpackInv(player, true);
            FluidStack raindrop = new FluidStack(FluidRegistry.getFluid("melonJuice"), drops);
            inv.leftTank.fill(raindrop, true);
            inv.rightTank.fill(raindrop, true);
            inv.onInventoryChanged();
            lastDropTime = 5;
        }
        backpack.stackTagCompound.setInteger("lastTime", lastDropTime);
    }

    public void itemDragon(EntityPlayer player, World world, ItemStack backpack) {

    }

    public void itemCreeper(EntityPlayer player, World world, ItemStack backpack) {
        world.createExplosion(player, player.posX, player.posY, player.posZ, 4.0F, false);
    }

    public void itemCow(EntityPlayer player, World world, ItemStack backpack) {

    }

    @SuppressWarnings("unchecked")
    public void itemWolf(EntityPlayer player, World world, ItemStack backpack) {
        int lastCheckTime = (backpack.getTagCompound().hasKey("lastTime")) ? backpack.getTagCompound().getInteger("lastTime") - 1 : 20;

        if (lastCheckTime <= 0) {
            List<EntityWolf> wolves = player.worldObj.getEntitiesWithinAABB(
                    EntityWolf.class,
                    AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                            player.posX + 1.0D, player.posY + 1.0D,
                            player.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
            if (wolves.isEmpty()) return;

            for (EntityWolf wolf : wolves) {
                if (wolf.isAngry() && wolf.getAttackTarget() == player) {
                    wolf.setAngry(wolf.isAngry() ? false : false);
                    wolf.setAttackTarget(null);
                    wolf.setRevengeTarget(null);
                    Iterator<?> i2 = wolf.targetTasks.taskEntries.iterator();
                    while (i2.hasNext()) {
                        ((EntityAIBase) i2.next()).resetTask();
                    }
                }
            }
            lastCheckTime = 20;
        } else {
            lastCheckTime--;
        }
        backpack.getTagCompound().setInteger("lastTime", lastCheckTime);
    }

    public void itemBlaze(EntityPlayer player, World world, ItemStack backpack) {

    }

    @SuppressWarnings("unchecked")
    public void itemOcelot(EntityPlayer player, World world, ItemStack backpack) {
        int lastCheckTime = (backpack.getTagCompound().hasKey("lastTime")) ? backpack.getTagCompound().getInteger("lastTime") - 1 : 20;

        if (lastCheckTime <= 0) {
            List<EntityCreeper> creepers = player.worldObj.getEntitiesWithinAABB(
                    EntityCreeper.class,
                    AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                            player.posX + 1.0D, player.posY + 1.0D,
                            player.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));

            for (EntityCreeper creeper : creepers) {
                boolean set = true;
                EntityAIAvoidPlayerWithBackpack task = new EntityAIAvoidPlayerWithBackpack(creeper, EntityPlayer.class, 10.0F, 1.0, 1.3, "Ocelot");

                for (Object entry : creeper.tasks.taskEntries) {
                    if (((EntityAITasks.EntityAITaskEntry) entry).action instanceof EntityAIAvoidPlayerWithBackpack) {
                        set = false;
                    }
                }

                if (set) {
                    //System.out.println("Found creeper who doesn't know to fear the backpack, making it a pussy now");
                    creeper.tasks.addTask(3, task);
                }
            }
            lastCheckTime = 20;
        }
        backpack.getTagCompound().setInteger("lastTime", lastCheckTime);
    }
    /* ==================================== TILE ABILITIES ==========================================*/

    public void tileCactus(World world, AdventureBackpackTileEntity backpack) {
        if (world.isRaining() && world.canBlockSeeTheSky(backpack.xCoord, backpack.yCoord, backpack.zCoord)) {
            int dropTime = backpack.lastTime - 1;
            if (dropTime <= 0) {
                FluidStack raindrop = new FluidStack(FluidRegistry.WATER, 2);
                backpack.leftTank.fill(raindrop, true);
                backpack.rightTank.fill(raindrop, true);
                dropTime = 5;
            }
            backpack.lastTime = dropTime;
            // backpack.onInventoryChanged();
        }
    }
}
