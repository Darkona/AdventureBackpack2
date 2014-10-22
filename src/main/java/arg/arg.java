package arg;

import java.util.Map;
import java.util.logging.Logger;

import com.darkona.adventurebackpack.init.recipes.AbstractBackpackRecipe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeFireworks;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.item.crafting.RecipesMapCloning;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = arg.NAME, name = arg.NAME, version = arg.VERSION)
public class arg
{
    public static final String NAME = "Advanced-Recipe-Generator";
    public static final String VERSION = "${version}";

    @Instance("Advanced-Recipe-Generator")
    public static arg instance;

    public static Logger argLog = Logger.getLogger(NAME);

    public static int[] mapLoaded = {0, 0};
    public static boolean mapGenerated = false;

    @EventHandler
    public void load(FMLInitializationEvent evt)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void createRecipeImages(TextureStitchEvent.Post evt)
    {
        mapLoaded[evt.map.getTextureType()]++;

        if (mapLoaded[0] > 0 && mapLoaded[0] == mapLoaded[1])
        {
            if (mapGenerated)
            {
                return;
            }
            mapGenerated = true;

            argLog.info("Generating Recipes ...");

            TextureManager tm = Minecraft.getMinecraft().getTextureManager();

            // save since we get a ConcurrentModificationException in TextureManager.func_110549_a otherwise

            Map mapTextureObjects = ObfuscationReflectionHelper.getPrivateValue(TextureManager.class, tm, "mapTextureObjects", "field_110585_a");

            Map new_mapTextureObjects = Maps.newHashMap();
            new_mapTextureObjects.putAll(mapTextureObjects);
            ObfuscationReflectionHelper.setPrivateValue(TextureManager.class, tm, new_mapTextureObjects, "mapTextureObjects", "field_110585_a");

            for (Object orecipe : CraftingManager.getInstance().getRecipeList())
            {
                IRecipe irecipe = (IRecipe) orecipe;

                if ((irecipe instanceof RecipesArmorDyes) || (irecipe instanceof RecipeFireworks) || (irecipe instanceof RecipesMapCloning))
                {
                    continue;
                }

                if (irecipe.getRecipeOutput() == null)
                {
                    System.out.println("Skip recipe without output: " + irecipe.getClass().getSimpleName());
                    continue;
                }


//                if(irecipe instanceof AbstractBackpackRecipe){
//                    AbstractBackpackRecipe abs = new AbstractBackpackRecipe();
//                    ItemStack backpack = abs.getRecipeOutput();
//                    for (Map.Entry<String, ItemStack[]> recipe : abs.recipes.entrySet()){
//                        RenderRecipe render = new RenderRecipe(recipe.getKey());
//                        try {
//                            for (int i = 0; i < recipe.getValue().length; ++i)
//                                if(i == 4){
//                                    render.getCraftingContainer().craftMatrix.setInventorySlotContents(i, backpack);
//                                }else
//                                {
//                                    render.getCraftingContainer().craftMatrix.setInventorySlotContents(i, recipe.getValue()[i]);
//                                }
//                            render.getCraftingContainer().craftResult.setInventorySlotContents(0, abs.getCraftingResult(render.getCraftingContainer().craftMatrix));
//                            render.draw("AdventureBackpack");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }else{
//                    continue;
//                }

                RenderRecipe render = new RenderRecipe(irecipe.getRecipeOutput().getDisplayName());

                ItemStack[] recipeInput = null;
                try
                {
                    recipeInput = RecipeHelper.getRecipeArray(irecipe);
                    if (recipeInput == null)
                    {
                        continue;
                    }
                } catch (Exception e)
                {
                    //e.printStackTrace();
                }

                // Determine mod of this recipe.
                UniqueIdentifier identifier = null;
                identifier = getUniqueIdentifier(irecipe.getRecipeOutput());
                int recipe = 0;
                while (identifier == null && recipeInput != null && recipe < recipeInput.length)
                {
                    ItemStack input = recipeInput[recipe];
                    identifier = getUniqueIdentifier(input);
                    recipe++;
                }
                String subFolder = "vanilla";
                if (identifier != null)
                {
                    subFolder = identifier.modId;
                }

                try
                {
                    for (int i = 0; i < recipeInput.length - 1; ++i)
                        render.getCraftingContainer().craftMatrix.setInventorySlotContents(i, recipeInput[i + 1]);

                    render.getCraftingContainer().craftResult.setInventorySlotContents(0, recipeInput[0]);
                    render.draw(subFolder);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            // restore map since we get a ConcurrentModificationException in TextureManager.func_110549_a otherwise
            ObfuscationReflectionHelper.setPrivateValue(TextureManager.class, tm, mapTextureObjects, "mapTextureObjects", "field_110585_a");

            argLog.info("Finished Generation of Recipes in " + Minecraft.getMinecraft().mcDataDir + "/recipes/");
        }
    }

    private UniqueIdentifier getUniqueIdentifier(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.getItem() == null)
        {
            return null;
        }
        if (itemStack.getItem() instanceof ItemBlock)
        {
            Block block = Block.getBlockFromItem(((ItemBlock) itemStack.getItem()));
            return GameRegistry.findUniqueIdentifierFor(block);
        } else
        {
            return GameRegistry.findUniqueIdentifierFor(itemStack.getItem());
        }
    }
}