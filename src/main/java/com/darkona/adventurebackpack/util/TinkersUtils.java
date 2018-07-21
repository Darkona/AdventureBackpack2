package com.darkona.adventurebackpack.util;

import javax.annotation.Nonnull;
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

import com.darkona.adventurebackpack.reference.LoadedMods;

/**
 * Created on 03.02.2018
 *
 * @author Ugachaga
 */
public final class TinkersUtils
{
    public static final ResourceLocation GUI_ICONS = new ResourceLocation("tinker", "textures/gui/icons.png");

    private static final String CLASS_CRAFTING_LOGIC = "tconstruct.tools.logic.CraftingStationLogic";
    private static final String CLASS_CRAFTING_STATION = "tconstruct.tools.inventory.CraftingStationContainer";
    private static final String METHOD_ON_CRAFT_CHANGED = LoadedMods.DEV_ENV ? "onCraftMatrixChanged" : "func_75130_a";
    private static final String FIELD_CRAFT_MATRIX = "craftMatrix";
    private static final String FIELD_CRAFT_RESULT = "craftResult";

    private static final String CLASS_RENDERER = "tconstruct.client.FlexibleToolRenderer";

    private static final String PACKAGE_TCONSTRUCT = "tconstruct";
    private static final String PACKAGE_TOOLS = "tconstruct.items.tools";
    private static final String PACKAGE_AMMO = "tconstruct.weaponry.ammo"; // arrows and bolts
    private static final String PACKAGE_WEAPONS = "tconstruct.weaponry.weapons"; // bows, crossbows, throwing weapons

    private static Class<?> craftingStation;
    private static Object craftingStationInstance;

    private static Class<?> toolRenderer;
    private static Object toolRendererInstance;

    private TinkersUtils() {}

    static
    {
        if (LoadedMods.TCONSTRUCT)
        {
            createCraftingStationInstance();
            createToolRendererInstance();
        }
    }

    private static void createCraftingStationInstance()
    {
        try
        {
            Class craftingLogic = Class.forName(CLASS_CRAFTING_LOGIC);
            Object craftingLogicInstance = craftingLogic.newInstance();
            InventoryPlayer invPlayer = getInventoryPlayer();

            craftingStation = Class.forName(CLASS_CRAFTING_STATION);
            craftingStationInstance = craftingStation
                    .getConstructor(InventoryPlayer.class, craftingLogic, int.class, int.class, int.class)
                    .newInstance(invPlayer, craftingLogicInstance, 0, 0, 0);
        }
        catch (Exception e)
        {
            LogHelper.error("Error getting instance of Tinkers Crafting Station: " + e);
        }
    }

    private static InventoryPlayer getInventoryPlayer()
    {
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
        return invPlayer;
    }

    private static void createToolRendererInstance()
    {
        if (Utils.inClient())
        {
            try
            {
                toolRenderer = Class.forName(CLASS_RENDERER);
                toolRendererInstance = toolRenderer.newInstance();
            }
            catch (Exception e)
            {
                LogHelper.error("Error getting instance of Tinkers Tool Renderer: " + e);
            }
        }
    }

    public static boolean isToolOrWeapon(@Nullable ItemStack stack)
    {
        if (stack == null)
            return false;

        String cn = stack.getItem().getClass().getName();
        return cn.startsWith(PACKAGE_TCONSTRUCT)
                && (cn.startsWith(PACKAGE_TOOLS) || cn.startsWith(PACKAGE_WEAPONS) || cn.startsWith(PACKAGE_AMMO));

    }

    public static boolean isTool(@Nonnull ItemStack stack)
    {
        return LoadedMods.TCONSTRUCT && stack.getItem().getClass().getName().startsWith(PACKAGE_TOOLS);
    }

    public static boolean isTool(String clazzName)
    {
        return LoadedMods.TCONSTRUCT && clazzName.startsWith(PACKAGE_TOOLS);
    }

    @Nullable
    public synchronized static ItemStack getTinkersRecipe(InventoryCrafting craftMatrix)
    {
        if (craftingStationInstance == null)
            return null;

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
        ToolRenderHelper.render(stack, renderType, toolRenderer, toolRendererInstance);
    }
}
