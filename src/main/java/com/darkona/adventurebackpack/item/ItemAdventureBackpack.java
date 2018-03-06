package com.darkona.adventurebackpack.item;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.events.WearableEvent;
import com.darkona.adventurebackpack.util.TipUtils;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.GUIPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.CoordsUtils;
import com.darkona.adventurebackpack.util.EnchUtils;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Utils;

import static com.darkona.adventurebackpack.common.Constants.BASIC_TANK_CAPACITY;
import static com.darkona.adventurebackpack.common.Constants.TAG_DISABLE_CYCLING;
import static com.darkona.adventurebackpack.common.Constants.TAG_DISABLE_NVISION;
import static com.darkona.adventurebackpack.common.Constants.TAG_INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.TAG_LEFT_TANK;
import static com.darkona.adventurebackpack.common.Constants.TAG_RIGHT_TANK;
import static com.darkona.adventurebackpack.common.Constants.TAG_TYPE;
import static com.darkona.adventurebackpack.util.TipUtils.l10n;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class ItemAdventureBackpack extends ItemAdventure
{
    public ItemAdventureBackpack()
    {
        super();
        setUnlocalizedName("adventureBackpack");
    }

    @Override
    @SuppressWarnings({"unchecked"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List subItems)
    {
        for (BackpackTypes type : BackpackTypes.values())
        {
            if (type == BackpackTypes.UNKNOWN)
                continue;

            subItems.add(BackpackUtils.createBackpackStack(type));
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean advanced)
    {
        NBTTagCompound backpackTag = BackpackUtils.getWearableCompound(stack);

        BackpackTypes type = BackpackTypes.getType(backpackTag.getByte(TAG_TYPE));
        tooltips.add(Utils.getColoredSkinName(type));

        FluidTank tank = new FluidTank(BASIC_TANK_CAPACITY);

        if (GuiScreen.isShiftKeyDown())
        {
            NBTTagList itemList = backpackTag.getTagList(TAG_INVENTORY, NBT.TAG_COMPOUND);
            tooltips.add(l10n("backpack.slots.used") + ": " + TipUtils.inventoryTooltip(itemList));

            tank.readFromNBT(backpackTag.getCompoundTag(TAG_LEFT_TANK));
            tooltips.add(l10n("backpack.tank.left") + ": " + TipUtils.tankTooltip(tank));

            tank.readFromNBT(backpackTag.getCompoundTag(TAG_RIGHT_TANK));
            tooltips.add(l10n("backpack.tank.right") + ": " + TipUtils.tankTooltip(tank));

            TipUtils.shiftFooter(tooltips);
        }
        else if (!GuiScreen.isCtrlKeyDown())
        {
            tooltips.add(TipUtils.holdShift());
        }

        if (GuiScreen.isCtrlKeyDown())
        {
            boolean cycling = !backpackTag.getBoolean(TAG_DISABLE_CYCLING);
            tooltips.add(l10n("backpack.cycling") + ": " + TipUtils.switchTooltip(cycling, true));
            tooltips.add(TipUtils.pressKeyFormat(TipUtils.actionKeyFormat()) + l10n("backpack.cycling.key1"));
            tooltips.add(l10n("backpack.cycling.key2") + " " + TipUtils.switchTooltip(!cycling, false));

            if (BackpackTypes.isNightVision(type))
            {
                boolean vision = !backpackTag.getBoolean(TAG_DISABLE_NVISION);
                tooltips.add(l10n("backpack.vision") + ": " + TipUtils.switchTooltip(vision, true));
                tooltips.add(TipUtils.pressShiftKeyFormat(TipUtils.actionKeyFormat()) + l10n("backpack.vision.key1"));
                tooltips.add(l10n("backpack.vision.key2") + " " + TipUtils.switchTooltip(!vision, false));
            }
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
        super.onCreated(stack, world, player);
        BackpackUtils.setBackpackType(stack, BackpackTypes.getType(stack.getItemDamage()));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return player.canPlayerEdit(x, y, z, side, stack) && placeBackpack(stack, player, world, x, y, z, side, true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
        if (mop == null || mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
        {
            if (world.isRemote)
            {
                ModNetwork.net.sendToServer(new GUIPacket.GUImessage(GUIPacket.BACKPACK_GUI, GUIPacket.FROM_HOLDING));
            }
        }
        return stack;
    }

    @Override
    public void onPlayerDeath(World world, EntityPlayer player, ItemStack stack)
    {
        if (world.isRemote || !ConfigHandler.backpackDeathPlace || EnchUtils.isSoulBounded(stack)
                || player.getEntityWorld().getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            return;
        }

        if (!tryPlace(world, player, stack))
        {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }

        BackpackProperty.get(player).setWearable(null);
    }

    private boolean tryPlace(World world, EntityPlayer player, ItemStack backpack) //TODO extract behavior to CoordsUtils
    {
        int X = (int) player.posX;
        if (player.posX < 0) X--;
        int Z = (int) player.posZ;
        if (player.posZ < 0) Z--;
        int Y = (int) player.posY;
        if (Y < 1) Y = 1;

        int positions[] = {0, -1, 1, -2, 2, -3, 3, -4, 4, -5, 5, -6, 6};

        for (int shiftY : positions)
        {
            if (Y + shiftY >= 1)
            {
                ChunkCoordinates spawn = CoordsUtils.getNearestEmptyChunkCoordinatesSpiral(world, X, Z, X, Y + shiftY, Z, 6, true, 1, (byte) 0, false);
                if (spawn != null)
                {
                    return placeBackpack(backpack, player, world, spawn.posX, spawn.posY, spawn.posZ, ForgeDirection.UP.ordinal(), false);
                }
            }
        }
        return false;
    }

    private boolean placeBackpack(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, boolean from)
    {
        if (stack.stackSize == 0 || !player.canPlayerEdit(x, y, z, side, stack)) return false;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.stackTagCompound.hasKey(TAG_TYPE))
        {
            stack.stackTagCompound.setByte(TAG_TYPE, BackpackTypes.getMeta(BackpackTypes.STANDARD));
        }

        // world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
        BlockAdventureBackpack backpack = ModBlocks.blockBackpack;

        if (y <= 0 || y >= world.getHeight())
        {
            return false;
        }
        if (backpack.canPlaceBlockOnSide(world, x, y, z, side))
        {
            if (world.getBlock(x, y, z).getMaterial().isSolid())
            {
                switch (side)
                {
                    case 0:
                        --y;
                        break;
                    case 1:
                        ++y;
                        break;
                    case 2:
                        --z;
                        break;
                    case 3:
                        ++z;
                        break;
                    case 4:
                        --x;
                        break;
                    case 5:
                        ++x;
                        break;
                }
            }
            if (y <= 0 || y >= world.getHeight())
            {
                return false;
            }
            if (backpack.canPlaceBlockAt(world, x, y, z))
            {
                if (world.setBlock(x, y, z, ModBlocks.blockBackpack))
                {
                    backpack.onBlockPlacedBy(world, x, y, z, player, stack);
                    world.playSoundAtEntity(player, BlockAdventureBackpack.soundTypeCloth.getStepResourcePath(), 0.5f, 1.0f);
                    ((TileAdventureBackpack) world.getTileEntity(x, y, z)).loadFromNBT(stack.stackTagCompound);
                    if (from)
                    {
                        stack.stackSize--;
                    }
                    else
                    {
                        BackpackProperty.get(player).setWearable(null);
                    }
                    WearableEvent event = new WearableEvent(player, stack);
                    MinecraftForge.EVENT_BUS.post(event);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onEquipped(World world, EntityPlayer player, ItemStack stack)
    {

    }

    @Override
    public void onEquippedUpdate(World world, EntityPlayer player, ItemStack stack)
    {
        if (!ConfigHandler.backpackAbilities || world == null || player == null || stack == null)
            return;

        if (BackpackTypes.isSpecial(BackpackTypes.getType(stack)))
        {
            BackpackAbilities.backpackAbilities.executeAbility(player, world, stack);
        }
    }

    @Override
    public void onUnequipped(World world, EntityPlayer player, ItemStack stack)
    {
        if (BackpackTypes.hasProperty(BackpackTypes.getType(stack), BackpackTypes.Props.REMOVAL))
        {
            BackpackAbilities.backpackAbilities.executeRemoval(player, world, stack);
        }
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (double) getItemCount(stack) / Constants.INVENTORY_MAIN_SIZE;
    }

    private int getItemCount(ItemStack backpack)
    {
        NBTTagList itemList = BackpackUtils.getWearableInventory(backpack);
        int itemCount = itemList.tagCount();
        for (int i = itemCount - 1; i >= 0; i--)
        {
            int slotAtI = itemList.getCompoundTagAt(i).getInteger(Constants.TAG_SLOT);
            if (slotAtI < Constants.INVENTORY_MAIN_SIZE)
                break;
            itemCount--;
        }
        return itemCount;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return ConfigHandler.enableFullnessBar && getItemCount(stack) > 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getWearableModel(ItemStack wearable)
    {
        return ClientProxy.modelAdventureBackpack.setWearable(wearable);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearableTexture(ItemStack wearable)
    {
        return Resources.getBackpackTexture(BackpackTypes.getType(wearable));
    }
}
