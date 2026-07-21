package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.ToolCycleInput;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.util.FluidMigration;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.network.CycleToolPacket;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class ClientEventHandler
{
    /**
     * Makes the tool tips of the backpacks have the Tank information displayed below.
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void toolTips(ItemTooltipEvent event)
    {
        if (event.itemStack.getItem() instanceof ItemAdventureBackpack)
        {
            NBTTagCompound compound = event.itemStack.stackTagCompound;
            // The tanks live in "backpackData" (InventoryBackpack.saveToNBT); very old stacks kept them at the root
            if (compound != null && compound.hasKey("backpackData")) compound = compound.getCompoundTag("backpackData");
            FluidTank tank = new FluidTank(ConfigHandler.BACKPACK_TANK_CAPACITY);
            String tankInfo = "";
            if (compound != null)
            {
                if (compound.hasKey("leftTank"))
                {
                    FluidMigration.readTank(tank, compound.getCompoundTag("leftTank"));
                    String name = tank.getFluid() == null ? "" : tank.getFluid().getLocalizedName();
                    tankInfo = EnumChatFormatting.BLUE + "Left Tank: " + tank.getFluidAmount() + "/" + tank.getCapacity() + " " + name;

                    event.toolTip.add(tankInfo);
                }
                if (compound.hasKey("rightTank"))
                {
                    FluidMigration.readTank(tank, compound.getCompoundTag("rightTank"));
                    String name = tank.getFluid() == null ? "" : tank.getFluid().getLocalizedName();
                    tankInfo = EnumChatFormatting.RED + "Right Tank: " + tank.getFluidAmount() + "/" + tank.getCapacity() + " " + name;

                    event.toolTip.add(tankInfo);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void mouseWheelDetect(MouseEvent event)
    {
        /*Special thanks go to MachineMuse, both for inspiration and the event. God bless you girl.*/
        if (event.dwheel == 0) return;
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player == null || player.isDead) return;
        int direction = ToolCycleInput.wheelDirection(ConfigHandler.TOOL_CYCLE_ENABLED, ConfigHandler.TOOL_CYCLE_MODE,
                Keybindings.isToolModifierDown(player), event.dwheel, ConfigHandler.TOOL_CYCLE_INVERT_WHEEL);
        if (direction != 0 && cycleHeldTool(player, direction))
        {
            event.setCanceled(true);
        }
    }

    /**
     * Sends the intent to swap the held tool with the backpack's tool slots, or to change the held hose's mode,
     * and applies it locally for a smooth hand; the server checks it again. True when something was cycled.
     */
    @SideOnly(Side.CLIENT)
    public static boolean cycleHeldTool(EntityPlayer player, int direction)
    {
        ItemStack backpack = Wearing.getWearingBackpack(player);
        if (backpack == null || !(backpack.getItem() instanceof ItemAdventureBackpack)) return false;
        int slot = player.inventory.currentItem;
        ItemStack heldItem = player.inventory.getStackInSlot(slot);
        if (heldItem == null) return false;

        if (ServerActions.canCycleTool(backpack, heldItem))
        {
            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(direction, slot, CycleToolPacket.CYCLE_TOOL_ACTION));
            ServerActions.cycleTool(player, direction, slot);
            return true;
        }
        if (heldItem.getItem() instanceof ItemHose)
        {
            ModNetwork.net.sendToServer(new CycleToolPacket.CycleToolMessage(direction, slot, CycleToolPacket.SWITCH_HOSE_ACTION));
            ServerActions.switchHose(player, ServerActions.HOSE_SWITCH, direction, slot);
            return true;
        }
        return false;
    }

}
