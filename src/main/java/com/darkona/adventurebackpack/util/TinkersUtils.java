package com.darkona.adventurebackpack.util;

import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.FakePlayer;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;

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
    private static final String METHOD_ON_CRAFT_CHANGED = ConfigHandler.IS_DEVENV ? "onCraftMatrixChanged" : "func_75130_a";
    private static final String FIELD_CRAFT_MATRIX = "craftMatrix";
    private static final String FIELD_CRAFT_RESULT = "craftResult";

    private static final String CLASS_RENDERER = "tconstruct.client.FlexibleToolRenderer";
    private static final String METHOD_RENDERER = "renderItem";
    private static final Object[] EMPTY_OBJECT = {};

    private static final String PACKAGE_INFI_TOOLS = "tconstruct.items.tools";

    private static Class<?> craftingStation;
    private static Object craftingStationInstance;
    private static Object toolRendererInstance;

    private TinkersUtils() {}

    static
    {
        if (ConfigHandler.IS_TCONSTRUCT)
        {
            getCraftingStationInstance();

            if (Utils.inClient())
            {
                try
                {
                    toolRendererInstance = Class.forName(CLASS_RENDERER).newInstance();
                }
                catch (Exception e)
                {
                    LogHelper.error("Error getting Tinkers Tool Renderer instance: " + e);
                }
            }
        }
    }

    private static void getCraftingStationInstance()
    {
        try
        {
            Class craftingLogic = Class.forName(CLASS_CRAFTING_LOGIC);
            Object craftingLogicInstance = craftingLogic.newInstance();
            InventoryPlayer invPlayer;

            if (Utils.inServer())
            {
                WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0];
                UUID fakeUuid = UUID.fromString("521e749d-2ac0-3459-af7a-160b4be5c62b");
                GameProfile fakeProfile = new GameProfile(fakeUuid, "[Adventurer]");
                invPlayer = new InventoryPlayer(new FakePlayer(world, fakeProfile));
            }
            else
            {
                invPlayer = Minecraft.getMinecraft().thePlayer.inventory;
            }

            craftingStation = Class.forName(CLASS_CRAFTING_STATION);
            craftingStationInstance = craftingStation
                    .getConstructor(InventoryPlayer.class, craftingLogic, int.class, int.class, int.class)
                    .newInstance(invPlayer, craftingLogicInstance, 0, 0, 0);

            LogHelper.info("Tinkers Crafting Station instance created");
        }
        catch (Exception e)
        {
            LogHelper.error("Error getting Tinkers Crafting Station instance: " + e);
            //e.printStackTrace();
        }
    }

    public static boolean isTool(ItemStack stack)
    {
        return ConfigHandler.IS_TCONSTRUCT
                && stack != null && stack.getItem().getClass().getName().startsWith(PACKAGE_INFI_TOOLS);
    }

    public static boolean isTool(String clazzName)
    {
        return ConfigHandler.IS_TCONSTRUCT && clazzName.startsWith(PACKAGE_INFI_TOOLS);
    }

    @Nullable
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
            LogHelper.error("Error during reflection in getTinkersRecipe: " + e);
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
