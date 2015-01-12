package com.darkona.adventurebackpack.proxy;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.block.TileCampfire;
import com.darkona.adventurebackpack.client.gui.GuiOverlayBackpack;
import com.darkona.adventurebackpack.client.render.*;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.handlers.KeybindHandler;
import com.darkona.adventurebackpack.handlers.RenderHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created on 10/10/2014
 *
 * @author Darkona
 */
public class ClientProxy implements IProxy
{

    public static HashMap<UUID,MovingSound> soundPoolCopters = new HashMap<UUID,MovingSound>();
    public static RendererItemAdventureBackpack rendererItemAdventureBackpack;
    public static RendererItemAdventureHat rendererItemAdventureHat;
    public static RendererHose rendererHose;
    public static RendererWearableEquipped rendererWearableEquipped;
    public static RenderHandler renderHandler;
    public static RendererInflatableBoat renderInflatableBoat;
    public static RenderRideableSpider renderRideableSpider;
    public static RendererItemClockworkCrossbow renderCrossbow;

    public static void putCopterSound(EntityPlayer player, MovingSound sound)
    {
        UUID key = player.getUniqueID();
        if(!soundPoolCopters.containsKey(key))
        {
            soundPoolCopters.put(key, sound);
        }
    }

    public static MovingSound getCopterSound(EntityPlayer player)
    {
        return soundPoolCopters.get(player.getUniqueID());
    }




    public void init()
    {
        initRenderers();
        registerKeybindings();
        MinecraftForge.EVENT_BUS.register(new GuiOverlayBackpack(Minecraft.getMinecraft()));
    }

    public void initNetwork()
    {

    }

    @Override
    public void joinPlayer(EntityPlayer player)
    {
        soundPoolCopters.remove(getCopterSound(player));
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

        rendererItemAdventureHat = new RendererItemAdventureHat();
        MinecraftForgeClient.registerItemRenderer(ModItems.adventureHat, rendererItemAdventureHat);

        rendererHose = new RendererHose();
        MinecraftForgeClient.registerItemRenderer(ModItems.hose, rendererHose);

        ClientRegistry.bindTileEntitySpecialRenderer(TileCampfire.class, new RendererCampFire());

        renderInflatableBoat = new RendererInflatableBoat();
        RenderingRegistry.registerEntityRenderingHandler(EntityInflatableBoat.class, renderInflatableBoat);
        renderRideableSpider = new RenderRideableSpider();
        RenderingRegistry.registerEntityRenderingHandler(EntityFriendlySpider.class, renderRideableSpider);

        renderCrossbow = new RendererItemClockworkCrossbow();
        MinecraftForgeClient.registerItemRenderer(ModItems.cwxbow, renderCrossbow);
    }

    public void registerKeybindings()
    {
        ClientRegistry.registerKeyBinding(Keybindings.openBackpack);
        ClientRegistry.registerKeyBinding(Keybindings.toggleHose);
        FMLCommonHandler.instance().bus().register(new KeybindHandler());

    }

}
