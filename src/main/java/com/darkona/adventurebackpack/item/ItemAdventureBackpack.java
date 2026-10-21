package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.block.PlacedWearables;
import adventurebackpack.api.IBackWearableItem;
import com.darkona.adventurebackpack.common.AbilityToggle;
import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.models.ModelBackpackArmor;
import com.darkona.adventurebackpack.common.RenderInfo;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.config.ConfigHandler;
import adventurebackpack.api.WearableEvent;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Utils;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.expanded.IBaubleExpanded;
import com.darkona.adventurebackpack.compat.baubles.BackpackBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

@Optional.InterfaceList({
        @Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles"),
        @Optional.Interface(iface = "baubles.api.expanded.IBaubleExpanded", modid = "Baubles|Expanded")
})
public class ItemAdventureBackpack extends ItemAB implements IBackWearableItem, IBauble, IBaubleExpanded
{
    public ItemAdventureBackpack()
    {
        super();
        setUnlocalizedName("adventureBackpack");
        setFull3D();
        setMaxStackSize(1);
    }

    public static Item getItemFromBlock(Block block)
    {
        return block instanceof BlockAdventureBackpack ? ModItems.adventureBackpack : null;
    }

    @Override
    public int getItemEnchantability()
    {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_)
    {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1;
    }

    @Override
    public void onCreated(ItemStack stack, World par2World, EntityPlayer par3EntityPlayer)
    {
        super.onCreated(stack, par2World, par3EntityPlayer);
        BackpackNames.setBackpackColorNameFromDamage(stack, stack.getItemDamage());
    }

    public boolean placeBackpack(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, boolean from)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!player.canPlayerEdit(x, y, z, side, stack)) return false;
        if (!stack.stackTagCompound.hasKey("colorName") || stack.stackTagCompound.getString("colorName").isEmpty())
        {
            stack.stackTagCompound.setString("colorName", "Standard");
        }

        BlockAdventureBackpack backpack = ModBlocks.blockBackpack;

        if (backpack.canPlaceBlockOnSide(world, x, y, z, side))
        {
            y = PlacedWearables.placeY(world, x, y, z, side);
            if (y < 0)
            {
                return false;
            }
            if (backpack.canPlaceBlockAt(world, x, y, z))
            {
                if (world.setBlock(x, y, z, ModBlocks.blockBackpack))
                {
                    backpack.onBlockPlacedBy(world, x, y, z, player, stack);
                    world.playSoundAtEntity(player, BlockAdventureBackpack.soundTypeCloth.getStepResourcePath(), 0.5f, 1.0f);
                    ((TileAdventureBackpack) world.getTileEntity(x, y, z)).loadFromNBT(stack.stackTagCompound);
                    if (from)
                    {
                        --stack.stackSize;
                    } else
                    {
                        BackpackProperty.get(player).setWearable(null);
                    }
                    WearableEvent event = new WearableEvent(player, stack);
                    MinecraftForge.EVENT_BUS.post(event);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return player.canPlayerEdit(x, y, z, side, stack) && placeBackpack(stack, player, world, x, y, z, side, true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
        if (mop == null || mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
        {
            if (world.isRemote)
            {
                ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.BACKPACK_GUI, GUIPacket.FROM_HOLDING));
            }
        }
        return stack;
    }

    @Override
    public boolean isDamageable()
    {
        return false;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot)
    {
        ModelBackpackArmor model = armorModel;
        if (model == null) armorModel = model = new ModelBackpackArmor();
        return model;
    }

    @SideOnly(Side.CLIENT)
    private static ModelBackpackArmor armorModel;

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        String modelTexture;
        if (BackpackNames.getBackpackColorName(stack).equals("Standard"))
        {
            modelTexture = Resources.holidayTexture(AdventureBackpack.instance.Holiday).toString();
        } else
        {
            modelTexture = Resources.backpackTexturesStringFromColor(stack);
        }
        return modelTexture;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        NBTTagCompound backpackData = BackpackUtils.getBackpackData(stack);
        if (backpackData != null)
        {
            if (backpackData.hasKey("colorName"))
            {
                list.add(backpackData.getString("colorName"));
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List subItems)
    {
        for (int i = 0; i < BackpackNames.backpackNames.length; i++)
        {
            if (!ConfigHandler.isBackpackTypeEnabled(BackpackNames.backpackNames[i])) continue;
            ItemStack bp = new ItemStack(this, 1, 0);
            bp.setItemDamage(i);
            NBTTagCompound c = new NBTTagCompound();
            c.setString("colorName", BackpackNames.backpackNames[i]);
            BackpackUtils.setBackpackData(bp, c);
            subItems.add(bp);
        }
    }

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        if (world == null || player == null || stack == null) return;
        if (!AbilityToggle.isActive(player)) return;

        BackpackAbilities.backpackAbilities.executeAbility(player, world, stack);
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        if (world.isRemote) return;
        if (ConfigHandler.BACKPACK_DEATH_PLACE)
        {
            placeOrDropOnDeath(world, player, stack);
        } else
        {
            dropOnDeath(world, player, stack);
        }
    }

    public static void deathEffects(World world, EntityPlayer player, ItemStack stack)
    {
        if (AbilityToggle.isActive(player) && ConfigHandler.CREEPER_DEATH_EXPLOSION && "Creeper".equals(BackpackNames.getBackpackColorName(stack)))
        {
            world.createExplosion(player, player.posX, player.posY, player.posZ, ConfigHandler.CREEPER_DEATH_EXPLOSION_POWER, false);
        }
    }

    public ChunkCoordinates placeOrDropOnDeath(World world, EntityPlayer player, ItemStack stack)
    {
        deathEffects(world, player, stack);
        ChunkCoordinates placed = tryPlace(world, player, stack);
        if (placed == null)
        {
            player.dropPlayerItemWithRandomChoice(stack, false);
            BackpackProperty.get(player).setWearable(null);
        }
        return placed;
    }

    public void dropOnDeath(World world, EntityPlayer player, ItemStack stack)
    {
        deathEffects(world, player, stack);
        player.dropPlayerItemWithRandomChoice(stack, false);
        BackpackProperty.get(player).setWearable(null);
    }

    private ChunkCoordinates tryPlace(World world, EntityPlayer player, ItemStack backpack)
    {
        int X = MathHelper.floor_double(player.posX);
        int Z = MathHelper.floor_double(player.posZ);
        ChunkCoordinates spawn = Utils.getNearestEmptyChunkCoordinatesSpiral(world, X, MathHelper.floor_double(player.posY), Z, 12, 6, false, false);
        return spawn != null && placeBackpack(backpack, player, world, spawn.posX, spawn.posY, spawn.posZ, ForgeDirection.UP.ordinal(), false) ? spawn : null;
    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {
    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        if(BackpackAbilities.hasRemoval(BackpackNames.getBackpackColorName(stack)))
        {
            BackpackAbilities.backpackAbilities.executeRemoval(player, world, stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelAdventureBackpack.setWearable(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable){
        return modelTexture(RenderInfoCache.get(wearable));
    }

    @SideOnly(Side.CLIENT)
    public static ResourceLocation modelTexture(RenderInfo info)
    {
        if (info.texture == null)
        {
            info.texture = RenderInfo.STANDARD.equals(info.colorName)
                    ? Resources.holidayTexture(AdventureBackpack.instance.Holiday)
                    : Resources.backpackTextureFromString(info.colorName);
        }
        return (ResourceLocation) info.texture;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public BaubleType getBaubleType(ItemStack itemstack)
    {
        return BaubleType.BELT;
    }

    @Override
    @Optional.Method(modid = "Baubles|Expanded")
    public String[] getBaubleTypes(ItemStack itemstack)
    {
        return BackpackBauble.TYPES;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player)
    {
        BackpackBauble.tick(itemstack, player);
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player)
    {
        BackpackBauble.onEquipped(itemstack, player);
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player)
    {
        BackpackBauble.onUnequipped(itemstack, player);
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player)
    {
        return BackpackBauble.canEquip(itemstack, player);
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player)
    {
        return true;
    }
}
