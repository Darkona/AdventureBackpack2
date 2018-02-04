package com.darkona.adventurebackpack.util;

import java.util.UUID;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.FakePlayer;
import cpw.mods.fml.common.FMLCommonHandler;

import com.darkona.adventurebackpack.config.ConfigHandler;

import com.mojang.authlib.GameProfile;

/**
 * Created on 03.02.2018
 *
 * @author Ugachaga
 */
public class TinkersUtils
{
    private static final String CLASS_CRAFTING_LOGIC = "tconstruct.tools.logic.CraftingStationLogic";
    private static final String CLASS_CRAFTING_STATION = "tconstruct.tools.inventory.CraftingStationContainer";
    private static final String METHOD_ON_CRAFT_CHANGED = ConfigHandler.IS_DEVENV ? "onCraftMatrixChanged" : "func_75130_a";
    private static final String FIELD_CRAFT_MATRIX = "craftMatrix";
    private static final String FIELD_CRAFT_RESULT = "craftResult";

    private static final String CLASS_RENDERER = "tconstruct.client.FlexibleToolRenderer";
    //private static final String CLASS_RENDERER = "tconstruct.client.ToolCoreRenderer";
    private static final String METHOD_RENDERER = "renderItem";
    private static final Object[] EMPTY_OBJECT = {};

    private static final String TAG_INFI_TOOL = "InfiTool";

    private static Class<?> craftingStation;
    private static Object craftingStationInstance;
    private static Object toolRendererInstance;

    private TinkersUtils() {}

    static
    {
        if (ConfigHandler.IS_TCONSTRUCT)
        {
            try
            {
                Class craftingLogic = Class.forName(CLASS_CRAFTING_LOGIC);
                Object craftingLogicInstance = craftingLogic.newInstance();

                WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0];
                UUID FAKE_UUID = UUID.fromString("521e749d-2ac0-3459-af7a-160b4be5c62b");
                GameProfile FAKE_PROFILE = new GameProfile(FAKE_UUID, "[Adventurer]");

                craftingStation = Class.forName(CLASS_CRAFTING_STATION);
                craftingStationInstance = craftingStation
                        .getConstructor(InventoryPlayer.class, craftingLogic, int.class, int.class, int.class)
                        .newInstance(new InventoryPlayer(new FakePlayer(world, FAKE_PROFILE)), craftingLogicInstance, 0, 0, 0);
            }
            catch (Exception e)
            {
                LogHelper.error("Error getting Tinkers Crafting Station instance: " + e.getMessage());
            }

            try
            {
                toolRendererInstance = Class.forName(CLASS_RENDERER).newInstance();
            }
            catch (Exception e)
            {
                LogHelper.error("Error getting Tinkers Tool Renderer instance: " + e.getMessage());
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
            LogHelper.error("Error during reflection in getTinkersRecipe: " + e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static float getToolRotationAngle(ItemStack stack, boolean isLowerSlot)
    {
        return isLowerSlot ? -45F : 45F;
    }

    public static void renderTool(ItemStack stack, IItemRenderer.ItemRenderType renderType)
    {
        try
        {
            Class.forName(CLASS_RENDERER)
                    .getMethod(METHOD_RENDERER, IItemRenderer.ItemRenderType.class, ItemStack.class, Object[].class)
                    .invoke(toolRendererInstance, renderType, stack, EMPTY_OBJECT);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    public static ResourceLocation getTinkersIcons()
    {
        return new ResourceLocation("tinker", "textures/gui/icons.png");
    }
}
