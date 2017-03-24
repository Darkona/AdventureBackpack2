package com.darkona.adventurebackpack.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.models.ModelBackpackArmor;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.events.WearableEvent;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.EnchUtils;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Utils;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class ItemAdventureBackpack extends ItemAB implements IBackWearableItem
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

    /**
     * Return the enchantability factor of the item, most of the timeInSeconds is based on material.
     */
    @Override
    public int getItemEnchantability()
    {
        return 0;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return EnchUtils.isSoulBook(book);
    }

    /**
     * Return whether this item is repairable in an anvil.
     *
     * @param p_82789_1_
     * @param p_82789_2_
     */
    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_)
    {
        return false;
    }

    /**
     * Determines if the durability bar should be rendered for this item.
     * Defaults to vanilla stack.isDamaged behavior.
     * But modders can use this for any data they wish.
     *
     * @param stack The current Item Stack
     * @return True if it should render the 'durability' bar.
     */
    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return false;
    }

    /**
     * Queries the percentage of the 'Durability' bar that should be drawn.
     *
     * @param stack The current ItemStack
     * @return 1.0 for 100% 0 for 0%
     */
    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return "Adventure Backpack";// + stack.getTagCompound().getString("colorName");
    }

    @Override
    public void onCreated(ItemStack stack, World par2World, EntityPlayer par3EntityPlayer)
    {

        super.onCreated(stack, par2World, par3EntityPlayer);
        BackpackNames.setBackpackColorNameFromDamage(stack, stack.getItemDamage());
    }

    public boolean placeBackpack(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, boolean from)
    {
        if (stack.stackSize == 0 || !player.canPlayerEdit(x, y, z, side, stack)) return false;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.stackTagCompound.hasKey("colorName") || stack.stackTagCompound.getString("colorName").isEmpty())
        {
            stack.stackTagCompound.setString("colorName", "Standard");
        }

        // world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
        BlockAdventureBackpack backpack = ModBlocks.blockBackpack;

        if (y <= 0 || y >= world.getHeight())
        {
            return false;
        }
        if (backpack.canPlaceBlockOnSide(world, x, y, z, side))
        {
            if (world.getBlock(x, y, z).getMaterial().isSolid())
            {
                switch (side)
                {
                    case 0:
                        --y;
                        break;
                    case 1:
                        ++y;
                        break;
                    case 2:
                        --z;
                        break;
                    case 3:
                        ++z;
                        break;
                    case 4:
                        --x;
                        break;
                    case 5:
                        ++x;
                        break;
                }
            }
            if (y <= 0 || y >= world.getHeight())
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
                        stack.stackSize--;

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
        if (stack != null && player instanceof EntityPlayerMP && player.openContainer instanceof ContainerBackpack)
        {
            player.closeScreen();
        }
        return super.onDroppedByPlayer(stack, player);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot)
    {
        return new ModelBackpackArmor();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        String modelTexture;
        if (BackpackNames.getBackpackColorName(stack).equals("Standard"))
        {
            modelTexture = Resources.backpackTextureFromString(AdventureBackpack.instance.Holiday).toString();
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

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List subItems)
    {
        for (int i = 0; i < BackpackNames.backpackNames.length; i++)
        {
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

        if (!ConfigHandler.backpackAbilities) return;
        if (world == null || player == null || stack == null) return;

        if (BackpackAbilities.hasAbility(BackpackNames.getBackpackColorName(stack)))
        {
            BackpackAbilities.backpackAbilities.executeAbility(player, world, stack);
        }
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        if (world.isRemote || !ConfigHandler.backpackDeathPlace || EnchUtils.isSoulBounded(stack)
                || player.getEntityWorld().getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            return;
        }

        if (!tryPlace(world, player, stack))
        {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }

        BackpackProperty.get(player).setWearable(null);
    }

    private boolean tryPlace(World world, EntityPlayer player, ItemStack backpack)
    {
        int X = (int) player.posX;
        if (player.posX < 0) X--;
        int Z = (int) player.posZ;
        if (player.posZ < 0) Z--;
        int Y = (int) player.posY;
        if (Y < 1) Y = 1;

        int positions[] = {0, -1, 1, -2, 2, -3, 3, -4, 4, -5, 5, -6, 6};

        for (int shiftY : positions)
        {
            if (Y + shiftY >= 1)
            {
                ChunkCoordinates spawn = Utils.getNearestEmptyChunkCoordinatesSpiral(world, X, Z, X, Y + shiftY, Z, 6, true, 1, (byte) 0, false);
                if (spawn != null)
                {
                    return placeBackpack(backpack, player, world, spawn.posX, spawn.posY, spawn.posZ, ForgeDirection.UP.ordinal(), false);
                }
            }
        }
        return false;
    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {

    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        if (BackpackAbilities.hasRemoval(BackpackNames.getBackpackColorName(stack)))
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
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {

        ResourceLocation modelTexture;

        if (BackpackNames.getBackpackColorName(wearable).equals("Standard"))
        {
            modelTexture = Resources.backpackTextureFromString(AdventureBackpack.instance.Holiday);
        } else
        {
            modelTexture = Resources.backpackTextureFromString(BackpackNames.getBackpackColorName(wearable));
        }
        return modelTexture;
    }
}
