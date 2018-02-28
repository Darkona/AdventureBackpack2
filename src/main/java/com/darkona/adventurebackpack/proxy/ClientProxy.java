package com.darkona.adventurebackpack.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.block.TileCampfire;
import com.darkona.adventurebackpack.client.gui.GuiOverlay;
import com.darkona.adventurebackpack.client.models.ModelBackpackArmor;
import com.darkona.adventurebackpack.client.models.ModelCoalJetpack;
import com.darkona.adventurebackpack.client.models.ModelCopterPack;
import com.darkona.adventurebackpack.client.render.RenderRideableSpider;
import com.darkona.adventurebackpack.client.render.RendererAdventureBackpackBlock;
import com.darkona.adventurebackpack.client.render.RendererCampFire;
import com.darkona.adventurebackpack.client.render.RendererHose;
import com.darkona.adventurebackpack.client.render.RendererInflatableBoat;
import com.darkona.adventurebackpack.client.render.RendererItemAdventureBackpack;
import com.darkona.adventurebackpack.client.render.RendererItemAdventureHat;
import com.darkona.adventurebackpack.client.render.RendererItemClockworkCrossbow;
import com.darkona.adventurebackpack.client.render.RendererWearableEquipped;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.config.Keybindings;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.handlers.KeyInputEventHandler;
import com.darkona.adventurebackpack.handlers.RenderHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.LoadedMods;

/**
 * Created on 10/10/2014
 *
 * @author Darkona
 */
public class ClientProxy implements IProxy
{
    public static RendererWearableEquipped rendererWearableEquipped = new RendererWearableEquipped();
    public static ModelBackpackArmor modelAdventureBackpack = new ModelBackpackArmor();
    public static ModelCoalJetpack modelCoalJetpack = new ModelCoalJetpack();
    public static ModelCopterPack modelCopterPack = new ModelCopterPack();

    @Override
    public void init()
    {
        initRenderers();
        registerKeybindings();
        MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getMinecraft()));

        if (LoadedMods.NEI)
        {
            codechicken.nei.api.API.hideItem(new ItemStack(ModBlocks.blockBackpack, 1, OreDictionary.WILDCARD_VALUE));
            codechicken.nei.api.API.hideItem(new ItemStack(ModBlocks.blockSleepingBag, 1, OreDictionary.WILDCARD_VALUE));
        }
    }

    @Override
    public void initNetwork()
    {

    }

    @Override
    public void joinPlayer(EntityPlayer player)
    {

    }

    @Override
    public void synchronizePlayer(int id, NBTTagCompound properties)
    {
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(id);

        if (entity instanceof EntityPlayer && properties != null)
        {
            EntityPlayer player = (EntityPlayer) entity;

            if (BackpackProperty.get(player) == null)
                BackpackProperty.register(player);

            BackpackProperty.get(player).loadNBTData(properties);
        }
    }

    private void initRenderers()
    {
        MinecraftForge.EVENT_BUS.register(new RenderHandler());

        MinecraftForgeClient.registerItemRenderer(ModItems.adventureBackpack, new RendererItemAdventureBackpack());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.blockBackpack), new RendererItemAdventureBackpack());
        ClientRegistry.bindTileEntitySpecialRenderer(TileAdventureBackpack.class, new RendererAdventureBackpackBlock());

        MinecraftForgeClient.registerItemRenderer(ModItems.adventureHat, new RendererItemAdventureHat());

        if (!ConfigHandler.tanksOverlay)
        {
            MinecraftForgeClient.registerItemRenderer(ModItems.hose, new RendererHose());
        }

        ClientRegistry.bindTileEntitySpecialRenderer(TileCampfire.class, new RendererCampFire());

        RenderingRegistry.registerEntityRenderingHandler(EntityInflatableBoat.class, new RendererInflatableBoat());
        RenderingRegistry.registerEntityRenderingHandler(EntityFriendlySpider.class, new RenderRideableSpider());

        MinecraftForgeClient.registerItemRenderer(ModItems.cwxbow, new RendererItemClockworkCrossbow());
    }

    @Override
    public void registerKeybindings()
    {
        ClientRegistry.registerKeyBinding(Keybindings.openInventory);
        ClientRegistry.registerKeyBinding(Keybindings.toggleActions);
        FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
    }
}
