package com.darkona.adventurebackpack.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Created by Darkona on 11/10/2014.
 */
public class ItemComponent extends ItemAB {

    private IIcon sleepingBagIcon;
    private IIcon backpackTankIcon;
    private IIcon hoseHeadIcon;
    private IIcon macheteHandleIcon;

    public ItemComponent() {
        setNoRepair();
        setHasSubtypes(true);
        this.setUnlocalizedName("backpackComponent");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(super.getUnlocalizedName("sleepingBag").substring(this.getUnlocalizedName().indexOf(".") + 1));
        sleepingBagIcon = iconRegister.registerIcon(super.getUnlocalizedName("sleepingBag").substring(this.getUnlocalizedName().indexOf(".") + 1));
        backpackTankIcon = iconRegister.registerIcon(super.getUnlocalizedName("backpackTank").substring(this.getUnlocalizedName().indexOf(".") + 1));
        hoseHeadIcon = iconRegister.registerIcon(super.getUnlocalizedName("hoseHead").substring(this.getUnlocalizedName().indexOf(".") + 1));
        macheteHandleIcon = iconRegister.registerIcon(super.getUnlocalizedName("macheteHandle").substring(this.getUnlocalizedName().indexOf(".") + 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        switch (damage) {
            case 1:
                return sleepingBagIcon;
            case 2:
                return backpackTankIcon;
            case 3:
                return hoseHeadIcon;
            case 4:
                return macheteHandleIcon;
        }
        return itemIcon;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        switch (getDamage(stack)) {
            case 1:
                return super.getUnlocalizedName("sleepingBag");
            case 2:
                return super.getUnlocalizedName("backpackTank");
            case 3:
                return super.getUnlocalizedName("hoseHead");
            case 4:
                return super.getUnlocalizedName("macheteHandle");
        }
        return super.getUnlocalizedName("backpackComponent");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 1; i <= 4; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }
}
