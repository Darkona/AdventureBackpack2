package com.darkona.adventurebackpack.items;

import com.darkona.adventurebackpack.init.ModMaterials;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Set;

/**
 * Created by Darkona on 10/10/2014.
 */
public class ItemMachete extends ToolAB {
    private static final Set breakableBlocks = Sets.newHashSet(new Block[]{
            Blocks.pumpkin,
            Blocks.web,
            Blocks.leaves,
            Blocks.leaves2,
            Blocks.melon_block,
            Blocks.melon_stem,
            Blocks.brown_mushroom,
            Blocks.red_flower,
            Blocks.red_mushroom,
            Blocks.cactus,
            Blocks.cocoa,
            Blocks.hay_block,
            Blocks.carrots,
            Blocks.potatoes,
            Blocks.red_mushroom_block,
            Blocks.brown_mushroom_block,
            Blocks.reeds,
            Blocks.grass,
            Blocks.tallgrass,
            Blocks.yellow_flower,
            Blocks.waterlily,
            Blocks.wheat,
            Blocks.wool});
    private float field_150934_a;

    public ItemMachete() {
        super(ModMaterials.ruggedIron, breakableBlocks);
        setMaxDamage(Items.iron_sword.getMaxDamage() + 250);
        this.field_150934_a = ModMaterials.ruggedIron.getDamageVsEntity();
        this.setUnlocalizedName("machete");
    }

    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        Material material = block.getMaterial();
        if (material == Material.wood) return 4F;
        if (block == Blocks.melon_block) return 2F;
        if (block == Blocks.web) return 10F;
        for (ItemStack stacky : OreDictionary.getOres("treeLeaves")) {
            if (stacky.getItem() == Item.getItemFromBlock(block)) return 15F;
        }
        return material == Material.plants || material == Material.vine || material == Material.coral || material == Material.gourd || material == Material.leaves || material == Material.cloth ? 12.0F : 0.5F;

    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_) {
        p_77644_1_.damageItem(1, p_77644_3_);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entityLivingBase) {
        if (block != Blocks.vine && !(block instanceof IShearable)) {
            return super.onBlockDestroyed(stack, world, block, x, y, z, entityLivingBase);
        } else {
            return true;
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        /*
        if (player.worldObj.isRemote)
        {
            return false;
        }
        int id = player.worldObj.getBlockId(x, y, z);
        for(ItemStack stacky : OreDictionary.getOres("treeLeaves")){
			if (stacky.itemID == id) return false;
		}

        if (Block.blocksList[id] instanceof IShearable )
        {
            IShearable target = (IShearable)Block.blocksList[id];
            if (target.isShearable(itemstack, player.worldObj, x, y, z))
            {
                ArrayList<ItemStack> drops = target.onSheared(itemstack, player.worldObj, x, y, z,
                		EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
                Random rand = new Random();

                for(ItemStack stack : drops)
                {
                    float f = 0.7F;
                    double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
                    entityitem.delayBeforeCanPickup = 10;
                    player.worldObj.spawnEntityInWorld(entityitem);
                }

                itemstack.damageItem(1, player);
                player.addStat(StatList.mineBlockStatArray[id], 1);
            }
        }
        return false;
         */
        return new ItemStack(new ItemShears()).getItem().onBlockStartBreak(itemstack, x, y, z, player);
    }
}
