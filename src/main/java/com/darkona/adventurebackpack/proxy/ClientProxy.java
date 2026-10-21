package com.darkona.adventurebackpack.proxy;

import com.darkona.adventurebackpack.handlers.ClientConnectionHandler;
import com.darkona.adventurebackpack.client.gui.widget.classic.ClassicSkins;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.block.TileCampfire;
import com.darkona.adventurebackpack.client.gui.GuiOverlay;
import com.darkona.adventurebackpack.client.models.ModelBackpackArmor;
import com.darkona.adventurebackpack.client.models.ModelCopterPack;
import com.darkona.adventurebackpack.client.models.ModelScubaGear;
import com.darkona.adventurebackpack.client.models.ModelFlamethrowerPack;
import com.darkona.adventurebackpack.client.models.ModelSteamJetpack;
import com.darkona.adventurebackpack.client.render.*;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.entity.EntityBolt;
import com.darkona.adventurebackpack.entity.EntityGrapple;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.handlers.CrossbowRenderHandler;
import com.darkona.adventurebackpack.handlers.KeybindHandler;
import com.darkona.adventurebackpack.handlers.RenderHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy
{
    public static RendererItemAdventureBackpack rendererItemAdventureBackpack;
    public static RendererItemAdventureHat rendererItemAdventureHat;
    public static RendererHose rendererHose;
    public static RendererWearableEquipped rendererWearableEquipped;
    public static RenderHandler renderHandler;
    public static RendererInflatableBoat renderInflatableBoat;
    public static RenderRideableSpider renderRideableSpider;
    public static RendererItemClockworkCrossbow renderCrossbow;
    public static ModelSteamJetpack modelSteamJetpack = new ModelSteamJetpack();
    public static ModelBackpackArmor modelAdventureBackpack = new ModelBackpackArmor();
    public static ModelCopterPack modelCopterPack = new ModelCopterPack();
    public static ModelScubaGear modelScubaGear = new ModelScubaGear();
    public static ModelFlamethrowerPack modelFlamethrowerPack = new ModelFlamethrowerPack();

    public void init()
    {
        initRenderers();
        registerKeybindings();
        MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getMinecraft()));
    }

    public void initNetwork()
    {
    }

    @Override
    public void joinPlayer(EntityPlayer player)
    {
        LogHelper.info("Joined Player in client");
    }

    @Override
    public void synchronizePlayer(int id, NBTTagCompound properties)
    {
        if (Minecraft.getMinecraft().theWorld == null) return;
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(id);
        if(Utils.notNullAndInstanceOf(entity, EntityPlayer.class)&& properties != null)
        {
            EntityPlayer player = (EntityPlayer)entity;
            if(BackpackProperty.get(player) == null) BackpackProperty.register(player);
            BackpackProperty.get(player).loadNBTData(properties);
        }
    }

    public void initRenderers()
    {
        renderHandler = new RenderHandler();
        MinecraftForge.EVENT_BUS.register(renderHandler);
        rendererWearableEquipped = new RendererWearableEquipped();

        rendererItemAdventureBackpack = new RendererItemAdventureBackpack();
        MinecraftForgeClient.registerItemRenderer(ModItems.adventureBackpack, rendererItemAdventureBackpack);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.blockBackpack), rendererItemAdventureBackpack);
        ClientRegistry.bindTileEntitySpecialRenderer(TileAdventureBackpack.class, new RendererAdventureBackpackBlock());
        ClientRegistry.bindTileEntitySpecialRenderer(com.darkona.adventurebackpack.block.TileGear.class, new com.darkona.adventurebackpack.client.render.RendererGearBlock());

        rendererItemAdventureHat = new RendererItemAdventureHat();
        MinecraftForgeClient.registerItemRenderer(ModItems.adventureHat, rendererItemAdventureHat);

        if(!ConfigHandler.TANKS_OVERLAY)
        {
            rendererHose = new RendererHose();
            MinecraftForgeClient.registerItemRenderer(ModItems.hose, rendererHose);
        }

        ClientRegistry.bindTileEntitySpecialRenderer(TileCampfire.class, new RendererCampFire());

        renderInflatableBoat = new RendererInflatableBoat();
        RenderingRegistry.registerEntityRenderingHandler(EntityInflatableBoat.class, renderInflatableBoat);
        renderRideableSpider = new RenderRideableSpider();
        RenderingRegistry.registerEntityRenderingHandler(EntityFriendlySpider.class, renderRideableSpider);

        renderCrossbow = new RendererItemClockworkCrossbow();
        MinecraftForgeClient.registerItemRenderer(ModItems.cwxbow, renderCrossbow);
        MinecraftForgeClient.registerItemRenderer(ModItems.telescope, new com.darkona.adventurebackpack.client.render.RendererItemTelescope());
        RenderingRegistry.registerEntityRenderingHandler(EntityBolt.class, new RenderBolt());
        RenderingRegistry.registerEntityRenderingHandler(EntityGrapple.class, new RenderGrapple());
        CrossbowRenderHandler crossbowRenderHandler = new CrossbowRenderHandler();
        MinecraftForge.EVENT_BUS.register(crossbowRenderHandler);
        FMLCommonHandler.instance().bus().register(crossbowRenderHandler);
    }

    public void registerKeybindings()
    {
        Keybindings.init();
        ClientRegistry.registerKeyBinding(Keybindings.gear);
        ClientRegistry.registerKeyBinding(Keybindings.ability);
        ClientRegistry.registerKeyBinding(Keybindings.mode);
        ClientRegistry.registerKeyBinding(Keybindings.state);
        FMLCommonHandler.instance().bus().register(new KeybindHandler());
        FMLCommonHandler.instance().bus().register(new ClientConnectionHandler());
        ClassicSkins.install();
    }
}
