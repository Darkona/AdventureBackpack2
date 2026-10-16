package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.KeyActions;
import com.darkona.adventurebackpack.common.KeyContext;
import com.darkona.adventurebackpack.common.ToolCycleInput;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.common.RenderInfo;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.common.ShownKey;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;

public class ClientEventHandler
{
    private final ShownKey tipKey = new ShownKey();
    private String tipLeft;
    private String tipRight;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void toolTips(ItemTooltipEvent event)
    {
        ItemStack stack = event.itemStack;
        if (!(stack.getItem() instanceof ItemAdventureBackpack)) return;
        NBTTagCompound root = stack.stackTagCompound;
        if (root == null || !root.hasKey("backpackData")) return;
        RenderInfo info = RenderInfoCache.get(stack);
        if (tipKey.changed(stack, info.version(), Minecraft.getMinecraft().gameSettings.language))
        {
            refreshTankTips(info, root.getCompoundTag("backpackData"));
        }
        if (tipLeft != null) event.toolTip.add(tipLeft);
        if (tipRight != null) event.toolTip.add(tipRight);
    }

    private void refreshTankTips(RenderInfo info, NBTTagCompound backpackData)
    {
        tipLeft = backpackData.hasKey("leftTank")
                ? EnumChatFormatting.BLUE + tankLine("adventurebackpack:tooltip.backpack.leftTank", info.leftFluid, info.leftCapacity) : null;
        tipRight = backpackData.hasKey("rightTank")
                ? EnumChatFormatting.RED + tankLine("adventurebackpack:tooltip.backpack.rightTank", info.rightFluid, info.rightCapacity) : null;
    }

    private static String tankLine(String key, FluidStack fluid, int capacity)
    {
        int amount = fluid == null ? 0 : fluid.amount;
        String name = fluid == null ? "" : fluid.getLocalizedName();
        return StatCollector.translateToLocalFormatted(key, amount, capacity, name);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void mouseWheelDetect(MouseEvent event)
    {
        if (event.dwheel == 0) return;
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player == null || player.isDead) return;
        int direction = ToolCycleInput.wheelDirection(ConfigHandler.TOOL_CYCLE_ENABLED, ConfigHandler.TOOL_CYCLE_MODE,
                player.isSneaking(), event.dwheel, ConfigHandler.TOOL_CYCLE_INVERT_WHEEL);
        if (direction == 0) return;
        KeyActions.Action action = KeyContext.resolve(KeyActions.Key.STATE, player, true);
        if (action != KeyActions.Action.HOSE_MODE && action != KeyActions.Action.CYCLE_TOOL) return;
        if (KeybindHandler.perform(player, KeyActions.Key.STATE, direction, action))
        {
            event.setCanceled(true);
        }
    }
}
