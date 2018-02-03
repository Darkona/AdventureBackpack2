package com.darkona.adventurebackpack.util;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Created on 03.02.2018
 *
 * @author Ugachaga
 */
public class TinkersUtils
{
    private static final String CLASS_CRAFTING_LOGIC = "tconstruct.tools.logic.CraftingStationLogic";
    private static final String CLASS_CRAFTING_STATION = "tconstruct.tools.inventory.CraftingStationContainer";
    private static final String METHOD_ON_CRAFT_CHANGED = "onCraftMatrixChanged";
    private static final String FIELD_CRAFT_MATRIX = "craftMatrix";
    private static final String FIELD_CRAFT_RESULT = "craftResult";
    private static final String TAG_INFI_TOOL = "InfiTool";

    private static Class<?> craftingStation;
    private static Object craftingStationInstance;

    private TinkersUtils() {}

    static
    {
        if (ConfigHandler.IS_TCONSTRUCT)
        {
            try
            {
                Class craftingLogic = Class.forName(CLASS_CRAFTING_LOGIC);
                Object craftingLogicInstance = craftingLogic.newInstance();

                craftingStation = Class.forName(CLASS_CRAFTING_STATION);
                craftingStationInstance = craftingStation
                        .getConstructor(InventoryPlayer.class, craftingLogic, int.class, int.class, int.class)
                        .newInstance(new InventoryPlayer(null), craftingLogicInstance, 0, 0, 0);
            }
            catch (Exception e)
            {
                LogHelper.error("Error getting instance of Tinkers Construct: " + e.getMessage());
            }
        }
    }

    public static boolean isTool(ItemStack stack)
    {
        return ConfigHandler.IS_TCONSTRUCT
                && stack != null && stack.hasTagCompound() && stack.stackTagCompound.hasKey(TAG_INFI_TOOL);
    }

    public static ItemStack getTinkersRecipe(InventoryCrafting craftMatrix)
    {
        try
        {
            craftingStation
                    .getField(FIELD_CRAFT_MATRIX)
                    .set(craftingStationInstance, craftMatrix);

            craftingStation
                    .getMethod(METHOD_ON_CRAFT_CHANGED, IInventory.class)
                    .invoke(craftingStationInstance, craftMatrix);

            return ((IInventory) craftingStation
                    .getField(FIELD_CRAFT_RESULT)
                    .get(craftingStationInstance))
                    .getStackInSlot(0);
        }
        catch (Exception e)
        {
            LogHelper.error("Error during reflection in TinkersUtils");
            //e.printStackTrace();
            return null;
        }
    }

    public static ResourceLocation getTinkersIcons()
    {
        return new ResourceLocation("tinker", "textures/gui/icons.png");
    }

}
