package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.client.ClientActions;
import com.darkona.adventurebackpack.common.FlamethrowerLogic;
import com.darkona.adventurebackpack.common.FlamethrowerSound;
import com.darkona.adventurebackpack.common.ParticleBeat;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

public class ItemFlamethrower extends ItemAB
{
    private static final int USE_DURATION = 72000;

    public ItemFlamethrower()
    {
        super();
        setUnlocalizedName("flamethrower");
        setFull3D();
        setMaxStackSize(1);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return USE_DURATION;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        boolean wearing = Wearing.isWearingFlamethrowerPack(player);
        if (ItemFlamethrowerPack.isUsable() && wearing)
        {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        if (world.isRemote && FlamethrowerSound.dryTrigger(ItemFlamethrowerPack.isUsable(), wearing, InventoryFlamethrowerPack.isLit(Wearing.getWearingFlamethrowerPack(player))))
        {
            ClientActions.flamethrowerDryClick(player);
        }
        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
        World world = player.worldObj;
        if (!ParticleBeat.flames(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.FLAMETHROWER_FIRE_INTERVAL_TICKS)) return;
        if (!ItemFlamethrowerPack.isUsable()) return;
        ItemStack packStack = Wearing.getWearingFlamethrowerPack(player);
        if (packStack == null) return;
        if (world.isRemote)
        {
            if (InventoryFlamethrowerPack.isLit(packStack)) effects(world, player);
            return;
        }
        InventoryFlamethrowerPack pack = new InventoryFlamethrowerPack(packStack);
        int fuel = pack.getFuel();
        int cost = FlamethrowerLogic.fuelPerBurst(ConfigHandler.FLAMETHROWER_FUEL_PER_BURST, GeneralReference.fuelMultiplier(pack.getFuelFluid()));
        if (!FlamethrowerLogic.canFire(true, pack.isOn(), fuel, cost))
        {
            if (pack.isOn())
            {
                ItemFlamethrowerPack.putOut(player, pack, "adventurebackpack:messages.flamethrower.nofuel");
                pack.markDirty();
            }
            return;
        }
        if (cost > 0)
        {
            pack.takeFuel(FlamethrowerLogic.fuelTaken(fuel, cost));
            if (pack.getFuel() <= 0) ItemFlamethrowerPack.putOut(player, pack, "adventurebackpack:messages.flamethrower.nofuel");
            pack.markDirty();
        }
        burst(world, player, stack);
    }

    private static void burst(World world, EntityPlayer player, ItemStack nozzle)
    {
        double range = ConfigHandler.FLAMETHROWER_RANGE;
        double cos = FlamethrowerLogic.cosHalfAngle(ConfigHandler.FLAMETHROWER_CONE_DEGREES);
        Vec3 look = player.getLookVec();
        double eyeX = player.posX;
        double eyeY = player.posY + player.getEyeHeight();
        double eyeZ = player.posZ;
        double endX = eyeX + look.xCoord * range;
        double endY = eyeY + look.yCoord * range;
        double endZ = eyeZ + look.zCoord * range;
        double widen = range * Math.sqrt(Math.max(0.0, 1.0 - cos * cos)) + 1.0;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(Math.min(eyeX, endX), Math.min(eyeY, endY), Math.min(eyeZ, endZ),
                Math.max(eyeX, endX), Math.max(eyeY, endY), Math.max(eyeZ, endZ)).expand(widen, widen, widen);
        float damage = FlamethrowerLogic.damagePerBurst(ConfigHandler.FLAMETHROWER_DAMAGE);
        DamageSource source = null;
        List<?> found = world.getEntitiesWithinAABBExcludingEntity(player, box);
        for (Object o : found)
        {
            if (!(o instanceof EntityLivingBase)) continue;
            EntityLivingBase target = (EntityLivingBase) o;
            if (target.isDead || target == player.ridingEntity || target == player.riddenByEntity) continue;
            double tx = target.posX - eyeX;
            double ty = (target.boundingBox.minY + target.boundingBox.maxY) * 0.5 - eyeY;
            double tz = target.posZ - eyeZ;
            if (!FlamethrowerLogic.inCone(look.xCoord, look.yCoord, look.zCoord, tx, ty, tz, range, cos)) continue;
            boolean isPlayer = target instanceof EntityPlayer;
            boolean owned = target instanceof EntityTameable && ((EntityTameable) target).isTamed() && ((EntityTameable) target).func_152114_e(player);
            if (!FlamethrowerLogic.mayHurt(false, owned, isPlayer, ConfigHandler.FLAMETHROWER_PVP, isPlayer && player.canAttackPlayer((EntityPlayer) target))) continue;
            if (!player.canEntityBeSeen(target)) continue;
            if (ConfigHandler.FLAMETHROWER_FIRE_SECONDS > 0) target.setFire(ConfigHandler.FLAMETHROWER_FIRE_SECONDS);
            if (damage > 0)
            {
                if (source == null) source = new EntityDamageSource("flamethrower", player).setFireDamage();
                target.attackEntityFrom(source, damage);
            }
        }
        if (ConfigHandler.FLAMETHROWER_IGNITES_BLOCKS) igniteBlock(world, player, nozzle, eyeX, eyeY, eyeZ, endX, endY, endZ);
    }

    private static void igniteBlock(World world, EntityPlayer player, ItemStack nozzle, double eyeX, double eyeY, double eyeZ, double endX, double endY, double endZ)
    {
        MovingObjectPosition hit = world.rayTraceBlocks(Vec3.createVectorHelper(eyeX, eyeY, eyeZ), Vec3.createVectorHelper(endX, endY, endZ));
        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        ForgeDirection side = ForgeDirection.getOrientation(hit.sideHit);
        int x = hit.blockX + side.offsetX;
        int y = hit.blockY + side.offsetY;
        int z = hit.blockZ + side.offsetZ;
        boolean free = world.isAirBlock(x, y, z) && Blocks.fire.canPlaceBlockAt(world, x, y, z);
        boolean spawnProtected = !world.canMineBlock(player, x, y, z);
        boolean canEdit = player.canPlayerEdit(x, y, z, hit.sideHit, nozzle);
        if (!FlamethrowerLogic.mayIgnite(true, free, spawnProtected, canEdit)) return;
        BlockSnapshot before = BlockSnapshot.getBlockSnapshot(world, x, y, z);
        world.setBlock(x, y, z, Blocks.fire);
        if (ForgeEventFactory.onPlayerBlockPlace(player, before, side).isCanceled())
        {
            before.restore(true, false);
        }
    }

    static Vec3 nozzle(EntityPlayer player)
    {
        double yaw = Math.toRadians(player.rotationYaw);
        double sideX = -Math.cos(yaw) * 0.35;
        double sideZ = -Math.sin(yaw) * 0.35;
        Vec3 look = player.getLookVec();
        double eyeY = player.boundingBox.minY + 1.62;
        return Vec3.createVectorHelper(player.posX + sideX + look.xCoord * 0.6, eyeY - 0.3 + look.yCoord * 0.6, player.posZ + sideZ + look.zCoord * 0.6);
    }

    private static void effects(World world, EntityPlayer player)
    {
        Vec3 at = nozzle(player);
        Vec3 look = player.getLookVec();
        int count = ParticleBeat.flameCount(ConfigHandler.RENDER_FLAMETHROWER_PARTICLES);
        double speed = ConfigHandler.FLAMETHROWER_RANGE / 10.0;
        for (int i = 0; i < count; i++)
        {
            world.spawnParticle("flame", at.xCoord, at.yCoord, at.zCoord,
                    look.xCoord * speed + (world.rand.nextDouble() - 0.5) * 0.12,
                    look.yCoord * speed + (world.rand.nextDouble() - 0.5) * 0.12,
                    look.zCoord * speed + (world.rand.nextDouble() - 0.5) * 0.12);
        }
        long now = world.getTotalWorldTime();
        if (count > 0 && ParticleBeat.smoke(now, player.getEntityId(), ConfigHandler.FLAMETHROWER_FIRE_INTERVAL_TICKS))
        {
            world.spawnParticle("largesmoke", at.xCoord + look.xCoord, at.yCoord + look.yCoord, at.zCoord + look.zCoord,
                    look.xCoord * 0.1, 0.05, look.zCoord * 0.1);
        }
    }
}
