package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.QuiverRules;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public final class Quiver
{
    private Quiver()
    {
    }

    public static boolean accepts(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return false;
        QuiverRules.Filter filter = ConfigHandler.QUIVER_FILTER;
        String name = names.of(stack.getItem());
        if (filter.accepts(name, stack.getItemDamage(), null)) return true;
        return filter.usesOres() && OreLists.accepts(filter, stack);
    }

    static final class OreLists
    {
        private static QuiverRules.Filter filter;
        private static String[] names;
        private static List<ItemStack>[] lists;

        private OreLists()
        {
        }

        @SuppressWarnings("unchecked")
        static synchronized boolean accepts(QuiverRules.Filter current, ItemStack stack)
        {
            if (current != filter)
            {
                filter = current;
                names = current.oreNames();
                lists = new List[names.length];
            }
            for (int i = 0; i < names.length; i++)
            {
                List<ItemStack> list = lists[i];
                if (list == null)
                {
                    if (!OreDictionary.doesOreNameExist(names[i])) continue;
                    list = lists[i] = OreDictionary.getOres(names[i], false);
                }
                for (int j = 0; j < list.size(); j++)
                {
                    if (OreDictionary.itemMatches(list.get(j), stack, false)) return true;
                }
            }
            return false;
        }
    }

    interface Names
    {
        String of(Item item);
    }

    static Names names = new Names()
    {
        @Override
        public String of(Item item)
        {
            Object name = Item.itemRegistry.getNameForObject(item);
            return name == null ? null : name.toString();
        }
    };

    public static int count(ItemStack[] inventory, int offset, int slots)
    {
        int items = 0;
        for (int q = 0; q < slots; q++)
        {
            int i = offset + q;
            if (i < 0 || i >= inventory.length) break;
            ItemStack stack = inventory[i];
            if (stack != null) items += stack.stackSize;
        }
        return items;
    }

    public static int level(ItemStack[] inventory, int offset, int slots)
    {
        return slots <= 0 ? QuiverRules.LEVEL_EMPTY : QuiverRules.level(count(inventory, offset, slots));
    }

    public static boolean sealRightTank(FluidTank left, FluidTank right, boolean quiver)
    {
        if (!quiver)
        {
            right.setCapacity(ConfigHandler.BACKPACK_TANK_CAPACITY);
            return false;
        }
        boolean movedAny = false;
        FluidStack r = right.getFluid();
        if (r != null && r.amount > 0)
        {
            FluidStack l = left.getFluid();
            boolean leftEmpty = l == null || l.amount <= 0;
            int moved = QuiverRules.movedToLeft(leftEmpty, !leftEmpty && l.isFluidEqual(r), leftEmpty ? 0 : l.amount, r.amount, left.getCapacity());
            if (moved > 0)
            {
                if (leftEmpty)
                {
                    FluidStack into = r.copy();
                    into.amount = moved;
                    left.setFluid(into);
                } else
                {
                    l.amount += moved;
                }
                r.amount -= moved;
                if (r.amount <= 0) right.setFluid(null);
                movedAny = true;
            }
        }
        FluidStack rest = right.getFluid();
        right.setCapacity(rest == null ? 0 : rest.amount);
        return movedAny;
    }
}
