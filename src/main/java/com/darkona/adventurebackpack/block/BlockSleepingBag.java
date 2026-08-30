package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.SleepingBagRules;
import com.darkona.adventurebackpack.common.SleepingBagSpawn;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.item.ItemComponent;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Resources;
import com.darkona.adventurebackpack.util.Utils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created on 14/10/2014
 *
 * @author Darkona
 */
public class BlockSleepingBag extends BlockDirectional
{

    public static final int[][] footBlockToHeadBlockMap = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};

    @SideOnly(Side.CLIENT)
    private IIcon[] endIcons;
    @SideOnly(Side.CLIENT)
    private IIcon[] sideIcons;
    @SideOnly(Side.CLIENT)
    private IIcon[] topIcons;

    public BlockSleepingBag()
    {
        super(Material.cloth);
        this.func_149978_e();
        setBlockName(getUnlocalizedName());
    }


    @SideOnly(Side.CLIENT)
    protected String getTextureName()
    {
        return this.textureName == null ? "MISSING_ICON_BLOCK_" + getIdFromBlock(this) + "_" + getUnlocalizedName() : this.textureName;
    }

    /**
     * Returns the unlocalized name of the block with "tile." appended to the front.
     */
    @Override
    public String getUnlocalizedName()
    {
        return "block.adventurebackpack:SleepingBag";
    }


    private void func_149978_e()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1F, 1.0F);
    }

    /**
     * Returns whether or not this bed block is the head of the bed.
     */

    public static boolean isBlockHeadOfBed(int meta)
    {
        return (meta & 8) != 0;
    }

    public static ChunkCoordinates verifyRespawnCoordinatesOnBlock(World world, ChunkCoordinates chunkCoordinates, boolean forced)
    {
        IChunkProvider ichunkprovider = world.getChunkProvider();
        ichunkprovider.loadChunk(chunkCoordinates.posX - 3 >> 4, chunkCoordinates.posZ - 3 >> 4);
        ichunkprovider.loadChunk(chunkCoordinates.posX + 3 >> 4, chunkCoordinates.posZ - 3 >> 4);
        ichunkprovider.loadChunk(chunkCoordinates.posX - 3 >> 4, chunkCoordinates.posZ + 3 >> 4);
        ichunkprovider.loadChunk(chunkCoordinates.posX + 3 >> 4, chunkCoordinates.posZ + 3 >> 4);

        if (world.getBlock(chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ).isBed(world, chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ, null))
        {
            ChunkCoordinates newChunkCoords = world.getBlock(chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ).getBedSpawnPosition(world, chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ, null);
            return newChunkCoords;
        }

        Material material = world.getBlock(chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ).getMaterial();
        Material material1 = world.getBlock(chunkCoordinates.posX, chunkCoordinates.posY + 1, chunkCoordinates.posZ).getMaterial();
        boolean flag1 = (!material.isSolid()) && (!material.isLiquid());
        boolean flag2 = (!material1.isSolid()) && (!material1.isLiquid());
        return (forced) && (flag1) && (flag2) ? chunkCoordinates : null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int id, float f1, float f2, float f3)
    {
        if (world.isRemote)
        {
            return true;
        } else
        {
            int meta = world.getBlockMetadata(x, y, z);

            if (player.isSneaking() && ownerBackpack(world, x, y, z, meta) == null)
            {
                // Sneak + right click rolls up a bag of its own into the inventory; a backpack's bag is folded from its GUI
                pickUp(world, x, y, z, meta, player);
                return true;
            }

            if (!isBlockHeadOfBed(meta))
            {
                int dir = getDirection(meta);
                x += footBlockToHeadBlockMap[dir][0];
                z += footBlockToHeadBlockMap[dir][1];

                if (world.getBlock(x, y, z) != this)
                {
                    return true;
                }

                meta = world.getBlockMetadata(x, y, z);
            }

            if (world.provider.canRespawnHere() && world.getBiomeGenForCoords(x, z) != BiomeGenBase.hell)
            {
                if (isBedOccupied(meta))
                {
                    EntityPlayer entityplayer1 = null;
                    Iterator iterator = world.playerEntities.iterator();

                    while (iterator.hasNext())
                    {
                        EntityPlayer entityplayer2 = (EntityPlayer) iterator.next();

                        if (entityplayer2.isPlayerSleeping())
                        {
                            ChunkCoordinates chunkcoordinates = entityplayer2.playerLocation;

                            if (chunkcoordinates.posX == x && chunkcoordinates.posY == y && chunkcoordinates.posZ == z)
                            {
                                entityplayer1 = entityplayer2;
                            }
                        }
                    }

                    if (entityplayer1 != null)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied", new Object[0]));
                        return true;
                    }

                    setBedOccupied(world, x, y, z, false);
                }

                EntityPlayer.EnumStatus enumstatus = player.sleepInBedAt(x, y, z);

                if (enumstatus == EntityPlayer.EnumStatus.OK)
                {
                    setBedOccupied(world, x, y, z, true);
                    if (!ConfigHandler.SLEEPINGBAG_SETS_SPAWN)
                    {
                        // Vanilla moves the spawn here on waking up; the old one is put back afterwards
                        SleepingBagSpawn.remember(player);
                    }
                    //This is so the wake up event can detect it. It fires before the player wakes up.
                    //and the bed location isn't set until then, normally.
                    player.setSpawnChunk(new ChunkCoordinates(x,y,z),true,player.dimension);
                    LogHelper.info("Looking for a campfire nearby...");
                    ChunkCoordinates campfire = Utils.findBlock3D(world, x, y, z, ModBlocks.blockCampFire, 8, 2);
                    if (campfire != null)
                    {
                        LogHelper.info("Campfire Found, saving coordinates. " + LogHelper.print3DCoords(campfire));
                        BackpackProperty.get(player).setCampFire(campfire);
                    }
                    return true;
                } else
                {
                    if (enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep", new Object[0]));
                    } else if (enumstatus == EntityPlayer.EnumStatus.NOT_SAFE)
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]));
                    }

                    return true;
                }
            } else
            {
                double d2 = (double) x + 0.5D;
                double d0 = (double) y + 0.5D;
                double d1 = (double) z + 0.5D;
                world.setBlockToAir(x, y, z);
                int k1 = getDirection(meta);
                x += footBlockToHeadBlockMap[k1][0];
                z += footBlockToHeadBlockMap[k1][1];

                if (world.getBlock(x, y, z) == this)
                {
                    world.setBlockToAir(x, y, z);
                    d2 = (d2 + (double) x + 0.5D) / 2.0D;
                    d0 = (d0 + (double) y + 0.5D) / 2.0D;
                    d1 = (d1 + (double) z + 0.5D) / 2.0D;
                }

                world.newExplosion((Entity) null, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), 5.0F, true, true);
                return true;
            }
        }
    }

    /**
     * Forge only clears the occupied bit of the vanilla bed on waking up; this clears it for the sleeping bag too, so
     * an empty bag does not look taken.
     */
    @Override
    public void setBedOccupied(IBlockAccess world, int x, int y, int z, EntityPlayer player, boolean occupied)
    {
        if (world instanceof World)
        {
            setBedOccupied((World) world, x, y, z, occupied);
        }
    }

    /** The backpack whose deployed bag this is, or null for a bag placed on its own. */
    public static TileAdventureBackpack ownerBackpack(IBlockAccess world, int x, int y, int z, int meta)
    {
        int footX = SleepingBagRules.footX(x, meta);
        int footZ = SleepingBagRules.footZ(z, meta);
        TileEntity te = world.getTileEntity(SleepingBagRules.backpackX(footX, meta), y, SleepingBagRules.backpackZ(footZ, meta));
        if (te instanceof TileAdventureBackpack && ((TileAdventureBackpack) te).ownsSleepingBagAt(footX, y, footZ))
        {
            return (TileAdventureBackpack) te;
        }
        return null;
    }

    /** A bag of its own drops the Sleeping Bag item from its foot; a backpack's bag drops nothing. */
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune)
    {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        if (SleepingBagRules.dropsItem(meta, ownerBackpack(world, x, y, z, meta) != null))
        {
            drops.add(sleepingBagItem());
        }
        return drops;
    }

    public static ItemStack sleepingBagItem()
    {
        return new ItemStack(ModItems.component, 1, ItemComponent.SLEEPING_BAG);
    }

    /** Rolls up a bag of its own straight into the player's inventory (or at their feet when it is full). */
    private void pickUp(World world, int x, int y, int z, int meta, EntityPlayer player)
    {
        int footX = SleepingBagRules.footX(x, meta);
        int footZ = SleepingBagRules.footZ(z, meta);
        int headX = footX + SleepingBagRules.headDx(meta);
        int headZ = footZ + SleepingBagRules.headDz(meta);
        if (world.getBlock(headX, y, headZ) == this && isBedOccupied(world.getBlockMetadata(headX, y, headZ)))
        {
            for (Object o : world.playerEntities)
            {
                EntityPlayer sleeper = (EntityPlayer) o;
                if (sleeper.isPlayerSleeping() && sleeper.playerLocation != null && sleeper.playerLocation.posX == headX
                        && sleeper.playerLocation.posY == y && sleeper.playerLocation.posZ == headZ)
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied"));
                    return;
                }
            }
        }
        // Foot first: a head without its foot just vanishes, while a foot without its head drops the item (a dupe here)
        if (world.getBlock(footX, y, footZ) == this) world.setBlockToAir(footX, y, footZ);
        if (world.getBlock(headX, y, headZ) == this) world.setBlockToAir(headX, y, headZ);
        world.playSoundEffect(footX + 0.5, y + 0.5, footZ + 0.5, Block.soundTypeCloth.func_150496_b(), 0.5F, 1.0F);
        ItemStack bag = sleepingBagItem();
        if (!player.inventory.addItemStackToInventory(bag))
        {
            player.dropPlayerItemWithRandomChoice(bag, false);
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    /**
     * Places a bag of its own (the Sleeping Bag item) like a bed: the foot on the clicked block, the head away from
     * the player. Server side. Returns false when there is no room, no solid floor, or a backpack would take the bag
     * as its own (it would then fold into that backpack instead of dropping the item).
     */
    public static boolean placeOnItsOwn(World world, EntityPlayer player, ItemStack stack, int x, int y, int z)
    {
        int dir = SleepingBagRules.directionFromYaw(player.rotationYaw);
        int headX = x + SleepingBagRules.headDx(dir);
        int headZ = z + SleepingBagRules.headDz(dir);
        if (!player.canPlayerEdit(x, y, z, 1, stack) || !player.canPlayerEdit(headX, y, headZ, 1, stack)) return false;
        if (!world.isAirBlock(x, y, z) || !world.isAirBlock(headX, y, headZ)) return false;
        if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) || !World.doesBlockHaveSolidTopSurface(world, headX, y - 1, headZ)) return false;
        if (ownerBackpack(world, x, y, z, dir) != null) return false;
        BlockSleepingBag bag = ModBlocks.blockSleepingBag;
        world.setBlock(x, y, z, bag, dir, 3);
        if (world.getBlock(x, y, z) != bag) return false;
        world.setBlock(headX, y, headZ, bag, dir | 8, 3);
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, Block.soundTypeCloth.func_150496_b(), 0.5F, 1.0F);
        return true;
    }

    public static void setBedOccupied(World world, int x, int y, int z, boolean flag)
    {
        int l = world.getBlockMetadata(x, y, z);

        if (flag)
        {
            l |= 4;
        } else
        {
            l &= -5;
        }

        world.setBlockMetadataWithNotify(x, y, z, l, 4);
    }

    public static boolean isBedOccupied(int meta)
    {
        return (meta & 4) != 0;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        int meta = world.getBlockMetadata(x, y, z);
        int dir = getDirection(meta);

        if (isBlockHeadOfBed(meta))
        {
            if (world.getBlock(x - footBlockToHeadBlockMap[dir][0], y, z - footBlockToHeadBlockMap[dir][1]) != this)
            {
                world.setBlockToAir(x, y, z);
            }
        } else if (world.getBlock(x + footBlockToHeadBlockMap[dir][0], y, z + footBlockToHeadBlockMap[dir][1]) != this)
        {
            world.setBlockToAir(x, y, z);

            if (!world.isRemote)
            {
                this.dropBlockAsItem(world, x, y, z, meta, 0);
            }
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        this.blockBoundsForRender();
    }

    private void blockBoundsForRender()
    {
        this.func_149978_e();
    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return null;
    }

    public static ChunkCoordinates func_149977_a(World world, int x, int y, int z, int whatever)
    {
        int meta = world.getBlockMetadata(x, y, z);
        int dir = BlockDirectional.getDirection(meta);

        for (int k1 = 0; k1 <= 1; ++k1)
        {
            int l1 = x - footBlockToHeadBlockMap[dir][0] * k1 - 1;
            int i2 = z - footBlockToHeadBlockMap[dir][1] * k1 - 1;
            int j2 = l1 + 2;
            int k2 = i2 + 2;

            for (int l2 = l1; l2 <= j2; ++l2)
            {
                for (int i3 = i2; i3 <= k2; ++i3)
                {
                    if (World.doesBlockHaveSolidTopSurface(world, l2, y - 1, i3) && !world.getBlock(l2, y, i3).getMaterial().isOpaque() && !world.getBlock(l2, y + 1, i3).getMaterial().isOpaque())
                    {
                        if (whatever <= 0)
                        {
                            return new ChunkCoordinates(l2, y, i3);
                        }

                        --whatever;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
    {
        int direction = getDirection(meta);
        if (player.capabilities.isCreativeMode && isBlockHeadOfBed(meta))
        {
            x -= footBlockToHeadBlockMap[direction][0];
            z -= footBlockToHeadBlockMap[direction][1];

            if (world.getBlock(x, y, z) == this)
            {
                world.setBlockToAir(x, y, z);
            }
        }
    }


    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion boom)
    {
        this.onBlockDestroyedByPlayer(world, x, y, z, world.getBlockMetadata(x, y, z));
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
    {
        // The backpack that deployed this bag (from either half, occupied or not) learns that it is folded again.
        // Its stored position is kept, so the drops computed after this still see the bag as the backpack's.
        TileAdventureBackpack owner = ownerBackpack(world, x, y, z, meta);
        if (owner != null)
        {
            owner.setSleepingBagDeployed(false);
        }
    }

    @Override
    public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == 0)
        {
            return Blocks.planks.getBlockTextureFromSide(side);
        } else
        {
            int k = getDirection(meta);
            int l = Direction.bedDirection[k][side];
            int isHead = isBlockHeadOfBed(meta) ? 1 : 0;
            return (isHead != 1 || l != 2) && (isHead != 0 || l != 3) ? (l != 5 && l != 4 ? this.topIcons[isHead] : this.sideIcons[isHead]) : this.endIcons[isHead];
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        this.topIcons = new IIcon[]{
                iconRegister.registerIcon(Resources.blockTextures("sleepingBag_feet_top").toString()),
                iconRegister.registerIcon(Resources.blockTextures("sleepingBag_head_top").toString())
        };

        this.endIcons = new IIcon[]{
                iconRegister.registerIcon(Resources.blockTextures("sleepingBag_feet_end").toString()),
                iconRegister.registerIcon(Resources.blockTextures("sleepingBag_head_end").toString())
        };

        this.sideIcons = new IIcon[]{
                iconRegister.registerIcon(Resources.blockTextures("sleepingBag_feet_side").toString()),
                iconRegister.registerIcon(Resources.blockTextures("sleepingBag_head_side").toString())
        };
    }

    public int getRenderType()
    {
        return 14;
    }

    @Override
    public boolean isNormalCube()
    {
        return false;
    }

    /**
     * Indicate if a material is a normal solid opaque cube
     */
    @Override
    public boolean isBlockNormalCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    @Override
    public boolean isCollidable()
    {
        return super.isCollidable();
    }
}
