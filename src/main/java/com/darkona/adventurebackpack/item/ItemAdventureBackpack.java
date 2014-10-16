package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.models.ModelAdventureBackpackArmor;
import com.darkona.adventurebackpack.network.GuiBackpackMessage;
import com.darkona.adventurebackpack.network.GuiMessageConstants;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.Textures;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ItemAdventureBackpack extends ArmorAB {

    public ItemAdventureBackpack() {
        super(0, 1);
        //setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName("adventureBackpack");
        setFull3D();
        setMaxStackSize(1);
        setCreativeTab(CreativeTabAB.ADVENTURE_BACKPACK_CREATIVE_TAB);
    }

    @Override
    public void onCreated(ItemStack stack, World par2World, EntityPlayer par3EntityPlayer) {

        super.onCreated(stack, par2World, par3EntityPlayer);
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.stackTagCompound.hasKey("color") || stack.stackTagCompound.getString("color").isEmpty()) {
            stack.stackTagCompound.setString("color", "Standard");
            stack.stackTagCompound.setString("colorName", "Standard");
        }
    }

    public boolean placeBackpack(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, boolean from) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.stackTagCompound.hasKey("color") || stack.stackTagCompound.getString("color").isEmpty()) {
            stack.stackTagCompound.setString("color", "Standard");
            stack.stackTagCompound.setString("colorName", "Standard");
        }

        //world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
        BlockAdventureBackpack backpack = ModBlocks.blockBackpack;

        if (y <= 0 || y >= 255)
            return false;
        if (backpack.canPlaceBlockOnSide(world, x, y, z, side)) {
            if (world.getBlock(x, y, z).getMaterial().isSolid()) {
                switch (side) {
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
                return false;
            if (backpack.canPlaceBlockAt(world, x, y, z)) {
                if (world.setBlock(x, y, z, ModBlocks.blockBackpack)) {
                    backpack.onBlockPlacedBy(world, x, y, z, player, stack);
                    // TODO world.playSoundAtEntity(player, Block.soundClothFootstep.getPlaceSound(), 0.5f, 1.0f);
                    ((TileAdventureBackpack) world.getTileEntity(x, y, z)).loadFromNBT(stack.stackTagCompound);
                    if (from) {
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    } else {
                        player.inventory.armorInventory[2] = null;
                    }
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return placeBackpack(stack, player, world, x, y, z, side, true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
        if (mop == null || mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if (world.isRemote) {
                AdventureBackpack.networkWrapper.sendToServer(new GuiBackpackMessage(GuiMessageConstants.NORMAL_GUI, GuiMessageConstants.FROM_HOLDING));
            }
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean isCurrentItem) {
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (stack.stackTagCompound != null && stack.getTagCompound().getBoolean("Special")) {
            BackpackAbilities.instance.executeAbility(player, world, stack);
        }
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        return super.onDroppedByPlayer(stack, player);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot) {
        InventoryItem inv = new InventoryItem(stack);
        return ModelAdventureBackpackArmor.instance.setTanks(inv.getLeftTank().getFluid(), inv.getRightTank().getFluid()).setItems(inv.getStackInSlot(3),
                inv.getStackInSlot(0));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return Textures.getBackpackTextureStringFromType(stack);
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
                                int metadata) {
        return true;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (stack.stackTagCompound != null) {
            if (stack.stackTagCompound.hasKey("colorName")) {
                list.add(stack.stackTagCompound.getString("colorName"));
            } else if (stack.stackTagCompound.hasKey("color")) {
                list.add(stack.stackTagCompound.getString("color"));
            }
        }
        super.addInformation(stack, player, list, par4);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)

    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List subItems) {
        for (String name : BackpackNames.backpackNames) {
            ItemStack bp = new ItemStack(this, 1, 0);
            NBTTagCompound c = new NBTTagCompound();
            c.setString("color", name);
            c.setString("colorName", BackpackNames.getDisplayNameForColor(name));
            bp.setTagCompound(c);
            subItems.add(bp);
        }
    }

}
