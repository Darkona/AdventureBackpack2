package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.CreativeTabAB;
import com.darkona.adventurebackpack.init.ModMaterials;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Set;

/**
 * Created on 10/10/2014
 *
 * @author Darkona
 */
public class ItemMachete extends ToolAB
{
    private static final Set breakableBlocks = Sets.newHashSet(Blocks.pumpkin,
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
            Blocks.wool);

    public ItemMachete()
    {

        super(ModMaterials.ruggedIron, breakableBlocks);
        setCreativeTab(CreativeTabAB.ADVENTURE_BACKPACK_CREATIVE_TAB);
        setMaxDamage(Items.iron_sword.getMaxDamage() + 250);
        this.setUnlocalizedName("machete");
    }

    @Override
    public float func_150893_a(ItemStack stack, Block block)
    {
        Material material = block.getMaterial();
        if (material == Material.wood) return 4F;
        if (block == Blocks.melon_block) return 2F;
        if (block == Blocks.web) return 10F;
        for (ItemStack stacky : OreDictionary.getOres("treeLeaves"))
        {
            if (stacky.getItem() == Item.getItemFromBlock(block)) return 15F;
        }
        return material == Material.plants || material == Material.vine || material == Material.coral || material == Material.gourd || material == Material.leaves || material == Material.cloth ? 12.0F : 0.5F;

    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_)
    {
        p_77644_1_.damageItem(1, p_77644_3_);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entityLivingBase)
    {
        if (block != Blocks.vine && !(block instanceof IShearable))
        {
            return super.onBlockDestroyed(stack, world, block, x, y, z, entityLivingBase);
        } else
        {
            return true;
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player)
    {
        // Shear leaves, tall grass and other IShearable blocks like shears do.
        return Items.shears.onBlockStartBreak(itemstack, x, y, z, player);
    }

}
