package com.darkona.adventurebackpack.compat.mousetweaks;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.gui.GuiAdvBackpack;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import org.lwjgl.input.Mouse;
import yalter.mousetweaks.api.IMTModGuiContainer;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
public class GuiAdvBackpackMT extends GuiAdvBackpack implements IMTModGuiContainer
{
    private static final Field IGNORE_MOUSE_UP = field("field_146995_H");
    private static final Field DRAG_SPLITTING = field("field_147007_t");

    public GuiAdvBackpackMT(EntityPlayer player, TileAdventureBackpack tileBackpack)
    {
        super(player, tileBackpack);
    }

    public GuiAdvBackpackMT(EntityPlayer player, InventoryBackpack inventoryBackpack, boolean wearing)
    {
        super(player, inventoryBackpack, wearing);
    }

    private static Field field(String name)
    {
        try
        {
            return ReflectionHelper.findField(GuiContainer.class, name);
        } catch (RuntimeException e)
        {
            LogHelper.error("Mouse Tweaks compat: no GuiContainer." + name + ", the right button drag stays vanilla");
            return null;
        }
    }

    @Override
    public int getAPIVersion()
    {
        return 1;
    }

    @Override
    public String getModName()
    {
        return "Adventure Backpack";
    }

    @Override
    public boolean isMouseTweaksDisabled()
    {
        return false;
    }

    @Override
    public boolean isWheelTweakDisabled()
    {
        return false;
    }

    @Override
    public boolean isCraftingOutputSlot(Object modContainer, Object slot)
    {
        return slot instanceof SlotCrafting;
    }

    @Override
    public Object getModContainer()
    {
        return inventorySlots;
    }

    @Override
    public int getModSlotCount(Object modContainer)
    {
        return ((Container) modContainer).inventorySlots.size();
    }

    @Override
    public Object getModSlot(Object modContainer, int slotNumber)
    {
        return ((Container) modContainer).getSlot(slotNumber);
    }

    @Override
    public Object getModSelectedSlot(Object modContainer, int slotCount)
    {
        int mouseX = Mouse.getX() * width / mc.displayWidth - guiLeft;
        int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1 - guiTop;
        Container container = (Container) modContainer;
        for (int i = 0; i < slotCount; i++)
        {
            Slot slot = container.getSlot(i);
            if (mouseX >= slot.xDisplayPosition - 1 && mouseX < slot.xDisplayPosition + 17
                    && mouseY >= slot.yDisplayPosition - 1 && mouseY < slot.yDisplayPosition + 17)
            {
                return slot;
            }
        }
        return null;
    }

    @Override
    public void clickModSlot(Object modContainer, Object slot, int mouseButton, boolean shiftPressed)
    {
        Slot s = (Slot) slot;
        handleMouseClick(s, s.slotNumber, mouseButton, shiftPressed ? 1 : 0);
    }

    @Override
    public void disableRMBDragIfRequired(Object modContainer, Object firstSlot, boolean shouldClick)
    {
        try
        {
            if (IGNORE_MOUSE_UP != null) IGNORE_MOUSE_UP.setBoolean(this, true);
            if (DRAG_SPLITTING != null) DRAG_SPLITTING.setBoolean(this, false);
        } catch (IllegalAccessException e)
        {
            return;
        }
        if (shouldClick && firstSlot != null)
        {
            clickModSlot(modContainer, firstSlot, 1, false);
        }
    }
}
