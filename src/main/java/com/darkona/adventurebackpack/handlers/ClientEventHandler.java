package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.KeyActions;
import com.darkona.adventurebackpack.common.KeyContext;
import com.darkona.adventurebackpack.common.ToolCycleInput;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.common.RenderInfo;
import com.darkona.adventurebackpack.common.RenderInfoCache;
import com.darkona.adventurebackpack.common.ShownKey;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemTelescope;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import com.darkona.adventurebackpack.client.render.RendererItemTelescope;
import com.darkona.adventurebackpack.client.render.TelescopePose;
import com.darkona.adventurebackpack.item.ItemRope;
import com.darkona.adventurebackpack.util.Wearing;
import com.darkona.adventurebackpack.inventory.widget.classic.RopeWidget;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;

public class ClientEventHandler
{
    private static boolean ropeShown;
    private static final int[] shownRope = new int[9];
    private static ItemStack hiddenTelescope;
    private static int hiddenSlot;
    private static final ResourceLocation SCOPE = new ResourceLocation("adventurebackpack", "textures/gui/telescope_scope.png");

    private RenderPlayer posedRenderer;
    private final ShownKey tipKey = new ShownKey();
    private String tipLeft;
    private String tipRight;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void telescopeZoom(FOVUpdateEvent event)
    {
        ItemStack using = event.entity.getItemInUse();
        if (using != null && using.getItem() instanceof ItemTelescope && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) event.newfov = ItemTelescope.ZOOM;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void telescopeOverlay(RenderGameOverlayEvent.Pre event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS && event.type != RenderGameOverlayEvent.ElementType.HELMET) return;
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack using = mc.thePlayer == null ? null : mc.thePlayer.getItemInUse();
        if (using == null || !(using.getItem() instanceof ItemTelescope) || mc.gameSettings.thirdPersonView != 0) return;
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            event.setCanceled(true);
            return;
        }
        ScaledResolution res = event.resolution;
        int w = res.getScaledWidth();
        int h = res.getScaledHeight();
        int left = (w - h) / 2;
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(SCOPE);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(left, h, -90.0D, 0.0D, 1.0D);
        t.addVertexWithUV(left + h, h, -90.0D, 1.0D, 1.0D);
        t.addVertexWithUV(left + h, 0, -90.0D, 1.0D, 0.0D);
        t.addVertexWithUV(left, 0, -90.0D, 0.0D, 0.0D);
        t.draw();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        t.startDrawingQuads();
        t.setColorOpaque_I(0);
        t.addVertex(0, h, -90.0D);
        t.addVertex(left, h, -90.0D);
        t.addVertex(left, 0, -90.0D);
        t.addVertex(0, 0, -90.0D);
        t.addVertex(left + h, h, -90.0D);
        t.addVertex(w, h, -90.0D);
        t.addVertex(w, 0, -90.0D);
        t.addVertex(left + h, 0, -90.0D);
        t.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static boolean isScoping(EntityPlayer player)
    {
        if (player == null) return false;
        ItemStack using = player.getItemInUse();
        return using != null && using.getItem() instanceof ItemTelescope;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void telescopeHand(RenderHandEvent event)
    {
        if (isScoping(Minecraft.getMinecraft().thePlayer)) event.setCanceled(true);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void telescopePose(RenderPlayerEvent.Pre event)
    {
        TelescopePose.end();
        RenderPlayer renderer = event.renderer;
        if (renderer == null || !isScoping(event.entityPlayer)) return;
        if (renderer != posedRenderer)
        {
            TelescopePose.install(renderer.modelBipedMain);
            posedRenderer = renderer;
        }
        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;
        for (int i = 0; i < armor.length; i++)
        {
            if (armor[i] == null) continue;
            int slot = 3 - i;
            ModelBiped fallback = slot == 2 ? renderer.modelArmor : renderer.modelArmorChestplate;
            TelescopePose.install(ForgeHooksClient.getArmorModel(event.entityPlayer, armor[i], slot, fallback));
        }
        TelescopePose.begin(event.entityPlayer, event.entityPlayer.isSneaking());
        InventoryPlayer inventory = event.entityPlayer.inventory;
        if (inventory.currentItem >= 0 && inventory.currentItem < 9 && inventory.mainInventory[inventory.currentItem] != null)
        {
            hiddenTelescope = inventory.mainInventory[inventory.currentItem];
            hiddenSlot = inventory.currentItem;
            inventory.mainInventory[hiddenSlot] = null;
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void ropeTotalBefore(RenderGameOverlayEvent.Pre event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null || !Wearing.isWearingBackpack(player)) return;
        int stored = storedRope(Wearing.getWearingBackpack(player));
        if (stored <= 0) return;
        ItemStack[] main = player.inventory.mainInventory;
        int total = stored;
        for (ItemStack stack : main)
        {
            if (ItemRope.is(stack)) total += stack.stackSize;
        }
        for (int i = 0; i < 9; i++)
        {
            if (!ItemRope.is(main[i])) continue;
            shownRope[i] = main[i].stackSize;
            main[i].stackSize = total;
        }
        ropeShown = true;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void ropeTotalAfter(RenderGameOverlayEvent.Post event)
    {
        if (!ropeShown || event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        ropeShown = false;
        ItemStack[] main = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;
        for (int i = 0; i < 9; i++)
        {
            if (shownRope[i] > 0 && ItemRope.is(main[i])) main[i].stackSize = shownRope[i];
            shownRope[i] = 0;
        }
    }

    private static int storedRope(ItemStack backpack)
    {
        if (backpack == null || backpack.stackTagCompound == null) return 0;
        NBTTagList list = backpack.stackTagCompound.getCompoundTag("backpackData").getTagList(RopeWidget.KEY, 10);
        int count = 0;
        for (int i = 0; i < list.tagCount(); i++) count += list.getCompoundTagAt(i).getInteger(RopeWidget.COUNT_KEY);
        return count;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void telescopeAtEye(RenderPlayerEvent.Specials.Post event)
    {
        if (hiddenTelescope == null) return;
        ModelRenderer head = event.renderer.modelBipedMain.bipedHead;
        float pitch = head.rotateAngleX;
        head.rotateAngleX = Math.max(-0.5236F, Math.min(1.4835F, pitch));
        GL11.glPushMatrix();
        head.postRender(0.0625F);
        head.rotateAngleX = pitch;
        GL11.glTranslatef(0F, -0.25F, 0F);
        GL11.glRotatef(180F, 0F, 1F, 0F);
        GL11.glScalef(0.625F, -0.625F, -0.625F);
        GL11.glTranslatef(2.5F / 16F, -0.0625F, -0.875F);
        RendererItemTelescope.drawModel();
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void telescopePoseEnd(RenderPlayerEvent.Post event)
    {
        if (hiddenTelescope != null)
        {
            event.entityPlayer.inventory.mainInventory[hiddenSlot] = hiddenTelescope;
            hiddenTelescope = null;
        }
        TelescopePose.end();
    }

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
