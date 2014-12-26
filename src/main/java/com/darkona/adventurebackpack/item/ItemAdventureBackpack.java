package com.darkona.adventurebackpack.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.models.ModelBackpackArmor;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.events.UnequipBackpackEvent;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModMaterials;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.BackpackContainer;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.network.MessageConstants;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

/**
 * Created on 12/10/2014
 * @author Darkona
 *
 */
@Optional.Interface(iface="baubles.api.IBauble", modid="Baubles", striprefs=true)
public class ItemAdventureBackpack extends ArmorAB implements IBauble
{


    public ItemAdventureBackpack()
    {
        super(0,1);
        setUnlocalizedName("adventureBackpack");
        setFull3D();
        setMaxStackSize(1);
        //setCreativeTab(CreativeTabAB.ADVENTURE_BACKPACK_CREATIVE_TAB);
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
    public int getItemEnchantability()
    {
        return 0;
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
        return super.getDurabilityForDisplay(stack);
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
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.stackTagCompound.hasKey("colorName") || stack.stackTagCompound.getString("colorName").isEmpty())
        {
            stack.stackTagCompound.setString("colorName", "Standard");
        }

       // world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
        BlockAdventureBackpack backpack = ModBlocks.blockBackpack;

        if (y <= 0 || y >= 255)
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
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    } else
                    {
                        player.inventory.armorInventory[2] = null;
                    }
                    UnequipBackpackEvent event = new UnequipBackpackEvent(player, stack);
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
        return placeBackpack(stack, player, world, x, y, z, side, true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
        if (mop == null || mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
        {
            if (world.isRemote)
            {
                ModNetwork.net.sendToServer(new GUIPacket.GUImessage(MessageConstants.NORMAL_GUI, MessageConstants.FROM_HOLDING));
            }
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean isCurrentItem)
    {
        if(entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.openContainer instanceof BackpackContainer)
            {
                player.openContainer.detectAndSendChanges();
            }


        }
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {

        if (stack.stackTagCompound != null &&
                (stack.getTagCompound().getBoolean("special")) || BackpackAbilities.hasAbility(stack.stackTagCompound.getString("colorName")))
        {
            BackpackAbilities.instance.executeAbility(player, world, stack);
        }
    }

    @Override
    public boolean isDamageable()
    {
        return false;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player)
    {
        return super.onDroppedByPlayer(stack, player);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot)
    {
        InventoryItem inv = new InventoryItem(stack);
        return ModelBackpackArmor.instance.setBackpack(inv);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
        String modelTexture;
        if(BackpackNames.getBackpackColorName(stack).equals("Standard"))
        {
            modelTexture = Resources.backpackTextureFromString(AdventureBackpack.instance.Holiday).toString();
        }
        else
        {
            modelTexture = Resources.backpackTexturesStringFromColor(stack);
        }
        return modelTexture;
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
                                int metadata)
    {
        return true;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        NBTTagCompound compound = stack.stackTagCompound;
        if (compound != null)
        {
            if (compound.hasKey("colorName"))
            {
                list.add(compound.getString("colorName"));
            }
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List subItems)
    {
        for(int i = 0; i < BackpackNames.backpackNames.length; i++)
        {
            ItemStack bp = new ItemStack(this, 1, 0);
            bp.setItemDamage(i);
            NBTTagCompound c = new NBTTagCompound();
            c.setString("colorName", BackpackNames.backpackNames[i]);
            bp.setTagCompound(c);
            subItems.add(bp);
        }
        /*for (String name : BackpackNames.backpackNames)
        {

        }*/
    }



    // BAUBLES

    /**
     * This method return the type of bauble this is.
     * Type is used to determine the slots it can go into.
     *
     * @param itemstack
     */
    @Override
    public BaubleType getBaubleType(ItemStack itemstack)
    {
        return BaubleType.AMULET;
    }

    /**
     * This method is called once per tick if the bauble is being worn by a player
     *
     * @param itemstack
     * @param player
     */
    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player)
    {
        this.onArmorTick(player.worldObj, (EntityPlayer)player, itemstack);
        this.onUpdate(itemstack,((EntityPlayer) player).worldObj, player, 0,  false);
    }

    /**
     * This method is called when the bauble is equipped by a player
     *
     * @param itemstack
     * @param player
     */
    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player)
    {

    }

    /**
     * This method is called when the bauble is unequipped by a player
     *
     * @param itemstack
     * @param player
     */
    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player)
    {

    }

    /**
     * can this bauble be placed in a bauble slot
     *
     * @param itemstack
     * @param player
     */
    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player)
    {
        return true;
    }

    /**
     * Can this bauble be removed from a bauble slot
     *
     * @param itemstack
     * @param player
     */
    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player)
    {
        return true;
    }


}
