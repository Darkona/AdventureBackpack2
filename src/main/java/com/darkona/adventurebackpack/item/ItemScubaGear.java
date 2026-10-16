package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.block.BlockGear;
import adventurebackpack.api.IBackWearableItem;
import com.darkona.adventurebackpack.block.TileAirCompressor;
import com.darkona.adventurebackpack.common.ScubaRules;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.AirContainers;
import com.darkona.adventurebackpack.inventory.InventoryScubaGear;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.TickThrottle;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemScubaGear extends ItemAB implements IBackWearableItem
{
    public ItemScubaGear()
    {
        super();
        setUnlocalizedName("scubaGear");
        setFull3D();
        setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote && AirContainers.usable())
        {
            ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.SCUBA_GUI, GUIPacket.FROM_HOLDING));
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileAirCompressor)) return BlockGear.useGearOn(stack, player, world, x, y, z, side);
        if (!world.isRemote) ((TileAirCompressor) te).fillGearByHand(player, stack);
        return true;
    }

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        if (!ConfigHandler.SCUBA_ENABLED) return;
        BackpackProperty props = BackpackProperty.get(player);
        if (!player.isInsideOfMaterial(Material.water))
        {
            props.setScubaAirDropped(false);
            return;
        }
        if (player.getAir() < ScubaRules.MAX_AIR)
        {
            props.setScubaAirDropped(true);
            if (props.isScubaHasAir()) player.setAir(ScubaRules.MAX_AIR);
        }
        long now = world.getTotalWorldTime();
        if (world.isRemote && props.isScubaHasAir() && TickThrottle.every(now, player.getEntityId(), ConfigHandler.RENDER_SCUBA_BUBBLE_TICKS))
        {
            bubbles(world, player);
        }
        if (TickThrottle.every(now, player.getEntityId(), ConfigHandler.SCUBA_AIR_INTERVAL_TICKS))
        {
            beat(world, player, stack, props);
        }
    }

    private void beat(World world, EntityPlayer player, ItemStack stack, BackpackProperty props)
    {
        InventoryScubaGear inv = new InventoryScubaGear(stack);
        int air = inv.getAir();
        if (!world.isRemote && ScubaRules.consumesNow(true, props.isScubaAirDropped(), air > 0))
        {
            int due = ScubaRules.airPerBeat(ConfigHandler.SCUBA_AIR_PER_SECOND, ConfigHandler.SCUBA_AIR_INTERVAL_TICKS);
            if (due > 0)
            {
                inv.takeAir(due);
                inv.markDirty();
            }
            int after = inv.getAir();
            if (ScubaRules.shouldWarn(air, after, ConfigHandler.SCUBA_LOW_AIR_WARNING))
            {
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.scuba.lowair"));
                if (ConfigHandler.SCUBA_WARNING_SOUND) world.playSoundAtEntity(player, "random.click", 0.6F, 0.6F);
            }
            if (ScubaRules.becameEmpty(air, after))
            {
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.scuba.empty"));
            }
            air = after;
        }
        props.setScubaAirDropped(false);
        props.setScubaHasAir(air > 0);
    }

    private static void bubbles(World world, EntityPlayer player)
    {
        double yaw = Math.toRadians(player.rotationYaw);
        double dx = -Math.sin(yaw) * 0.3;
        double dz = Math.cos(yaw) * 0.3;
        double y = player.boundingBox.minY + 1.45;
        for (int i = 0; i < 3; i++)
        {
            world.spawnParticle("bubble", player.posX + dx + (world.rand.nextDouble() - 0.5) * 0.1, y + i * 0.08,
                    player.posZ + dz + (world.rand.nextDouble() - 0.5) * 0.1, dx * 0.1, 0.12, dz * 0.1);
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
        BackpackProperty props = BackpackProperty.get(player);
        props.setScubaHasAir(new InventoryScubaGear(stack).getAir() > 0);
        props.setScubaAirDropped(false);
    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        BackpackProperty props = BackpackProperty.get(player);
        props.setScubaHasAir(false);
        props.setScubaAirDropped(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelScubaGear;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.modelTextures("scubaGear");
    }
}
