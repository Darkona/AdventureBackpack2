package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.common.MagazineLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * The Crossbow Magazine (registry name {@code boltCartridge}, kept from the 1.0 Bolt Cartridge so worlds and the
 * recipe still work). It is reusable: the player loads it with ammunition from the inventory (right click, held), the
 * crossbow takes it loaded and gives it back empty. Rules in {@link MagazineLogic}.
 * <p>
 * NBT: {@value #TAG_CHARGES} (int, 0 to {@code crossbow.magazine.capacity}) and {@value #TAG_AMMO} (string, registry
 * name of the ammunition, only while it holds charges). Every empty magazine carries exactly {@code {Charges:0}}, so
 * empty ones stack together and loaded ones stack when they hold the same. A stack without {@value #TAG_CHARGES} is a
 * 1.0/1.1 cartridge: full of arrows; the first tick it spends in an inventory writes that down.
 */
public class ItemBoltCartridge extends ItemAB
{
    public static final String TAG_CHARGES = "Charges";
    public static final String TAG_AMMO = "AmmoItem";

    /** Player entity data (not saved on the magazine, so it keeps stacking): world tick of the next charge. */
    private static final String PLAYER_READY_AT = ModInfo.MOD_ID + ":magazineReadyAt";
    /** Player entity data: last "no ammunition" message. */
    private static final String PLAYER_LAST_DRY = ModInfo.MOD_ID + ":magazineLastDry";
    private static final int MESSAGE_QUIET_TICKS = 20;

    @SideOnly(Side.CLIENT)
    private IIcon emptyIcon;

    public ItemBoltCartridge()
    {
        super();
        setUnlocalizedName("boltCartridge");
        setMaxDamage(0);
        setMaxStackSize(16);
    }

    // ----------------------------------------------------------------------------------------------- stacks and NBT

    /** A magazine with {@code charges} of {@code ammo}; with 0 charges, the plain empty magazine. */
    public static ItemStack create(int charges, String ammo)
    {
        ItemStack stack = new ItemStack(ModItems.boltCartridge);
        write(stack, charges, ammo);
        return stack;
    }

    private static void write(ItemStack stack, int charges, String ammo)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(TAG_CHARGES, Math.max(0, charges));
        if (charges > 0) tag.setString(TAG_AMMO, MagazineLogic.ammoType(ammo));
        stack.setTagCompound(tag);
    }

    public static int getCharges(ItemStack stack, int capacity)
    {
        NBTTagCompound tag = stack.getTagCompound();
        boolean tagged = tag != null && tag.hasKey(TAG_CHARGES);
        return MagazineLogic.charges(tagged, tagged ? tag.getInteger(TAG_CHARGES) : 0, capacity);
    }

    /** Registry name of the ammunition in a loaded magazine (arrows for an old cartridge). */
    public static String getAmmo(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        return MagazineLogic.ammoType(tag != null ? tag.getString(TAG_AMMO) : null);
    }

    /** First magazine with charges in the inventory, hotbar first, or -1. */
    public static int findLoaded(EntityPlayer player)
    {
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        ItemStack[] inventory = player.inventory.mainInventory;
        for (int i = 0; i < inventory.length; i++)
        {
            ItemStack s = inventory[i];
            if (s != null && s.getItem() == ModItems.boltCartridge && getCharges(s, capacity) > 0) return i;
        }
        return -1;
    }

    /** Gives a magazine back to the player, or drops it at the feet when the inventory is full. */
    public static void giveBack(EntityPlayer player, ItemStack magazine)
    {
        player.inventory.addItemStackToInventory(magazine);
        if (magazine.stackSize > 0) player.dropPlayerItemWithRandomChoice(magazine, false);
    }

    // ------------------------------------------------------------------------------------------------- ammunition

    private static String[] ammoSource;
    private static Item[] ammoItems = new Item[0];
    private static String[] ammoNames = new String[0];

    /** Resolves crossbow.ammo.list once per config change (Tier 2: only on a right click or a recipe check). */
    private static void resolveAmmo()
    {
        String[] list = ConfigHandler.CROSSBOW_AMMO_LIST;
        if (list == ammoSource) return;
        List<Item> items = new ArrayList<Item>();
        List<String> names = new ArrayList<String>();
        for (String raw : list)
        {
            String name = raw == null ? "" : raw.trim();
            if (name.isEmpty()) continue;
            Object found = Item.itemRegistry.getObject(name);
            if (found instanceof Item)
            {
                items.add((Item) found);
                names.add(Item.itemRegistry.getNameForObject(found));
            } else
            {
                LogHelper.warn("crossbow.ammo.list: no item named " + name + ", ignored.");
            }
        }
        ammoItems = items.toArray(new Item[items.size()]);
        ammoNames = names.toArray(new String[names.size()]);
        ammoSource = list;
    }

    /** Registry name of {@code stack} when it is ammunition of crossbow.ammo.list, otherwise null. */
    public static String ammoName(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return null;
        resolveAmmo();
        for (int i = 0; i < ammoItems.length; i++)
        {
            if (ammoItems[i] == stack.getItem()) return ammoNames[i];
        }
        return null;
    }

    /** The first ammunition of the list, for creative loading without ammunition. */
    private static String defaultAmmo()
    {
        resolveAmmo();
        return ammoNames.length > 0 ? ammoNames[0] : MagazineLogic.LEGACY_AMMO;
    }

    // ---------------------------------------------------------------------------------------------- loading by hand

    /**
     * Holding right click loads the magazine one charge at a time (every {@code crossbow.magazine.loadTicksPerCharge}),
     * taking ammunition from the inventory, hotbar first. Server side only; the pause is a world-tick timestamp on the
     * player, so nothing runs between clicks. A held stack of several magazines keeps one in the hand and moves the
     * rest to the inventory, so the clicks fill one magazine at a time.
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote || !ConfigHandler.CROSSBOW_ENABLED) return stack;
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        int charges = getCharges(stack, capacity);
        if (charges >= capacity) return stack;

        NBTTagCompound data = player.getEntityData();
        long now = world.getTotalWorldTime();
        int ticks = ConfigHandler.MAGAZINE_LOAD_TICKS;
        if (MagazineLogic.isLoading(now, data.getLong(PLAYER_READY_AT), ticks)) return stack;

        String type = charges > 0 ? getAmmo(stack) : null;
        boolean creative = player.capabilities.isCreativeMode;
        ItemStack[] inventory = player.inventory.mainInventory;
        int available = 0;
        String found = null;
        for (ItemStack s : inventory)
        {
            String name = ammoName(s);
            if (name == null || (type != null && !type.equals(name)) || (found != null && !found.equals(name))) continue;
            found = name;
            available += s.stackSize;
        }
        if (creative)
        {
            available = Integer.MAX_VALUE;
            if (found == null) found = type != null ? type : defaultAmmo();
        }
        int load = MagazineLogic.chargesToLoad(charges, capacity, available, ticks);
        if (load <= 0)
        {
            world.playSoundAtEntity(player, "random.click", 0.4F, 1.4F);
            if (CrossbowLogic.shouldWarnEmpty(now, data.getLong(PLAYER_LAST_DRY), MESSAGE_QUIET_TICKS))
            {
                player.addChatComponentMessage(type == null
                        ? new ChatComponentTranslation("adventurebackpack:messages.magazine.noammo")
                        : new ChatComponentTranslation("adventurebackpack:messages.magazine.noammo.kind", ammoComponent(type)));
            }
            data.setLong(PLAYER_LAST_DRY, now);
            return stack;
        }

        if (!creative) take(inventory, found, load);
        if (stack.stackSize > 1)
        {
            ItemStack rest = stack.copy();
            rest.stackSize = stack.stackSize - 1;
            stack.stackSize = 1;
            write(stack, charges + load, found);
            giveBack(player, rest);
        } else
        {
            write(stack, charges + load, found);
        }
        data.setLong(PLAYER_READY_AT, now + ticks);
        world.playSoundAtEntity(player, "random.click", 0.6F, 1.0F + 0.05F * (charges + load));
        player.inventory.markDirty();
        return stack;
    }

    /** Takes {@code count} items of the ammunition {@code name} from the inventory, hotbar first. */
    private static void take(ItemStack[] inventory, String name, int count)
    {
        for (int i = 0; i < inventory.length && count > 0; i++)
        {
            if (!name.equals(ammoName(inventory[i]))) continue;
            int n = Math.min(count, inventory[i].stackSize);
            inventory[i].stackSize -= n;
            count -= n;
            if (inventory[i].stackSize <= 0) inventory[i] = null;
        }
    }

    /** The ammunition's name for a chat message, translated by each client. */
    private static Object ammoComponent(String ammo)
    {
        Object item = Item.itemRegistry.getObject(ammo);
        return item instanceof Item ? new ChatComponentTranslation(new ItemStack((Item) item).getUnlocalizedName() + ".name") : ammo;
    }

    private static String displayName(String ammo)
    {
        Object item = Item.itemRegistry.getObject(ammo);
        return item instanceof Item ? new ItemStack((Item) item).getDisplayName() : ammo;
    }

    // ---------------------------------------------------------------------------------------------------- legacy

    /**
     * Migration of the 1.0/1.1 cartridges: the 1.0 elemental metadata goes back to 0, and a cartridge without a
     * charge count gets its full charges of arrows written down so it stacks with new full magazines. One int compare
     * and one null check per tick; a write only on an old stack.
     */
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
    {
        if (stack.getItemDamage() != 0) stack.setItemDamage(0);
        if (!world.isRemote && (stack.getTagCompound() == null || !stack.getTagCompound().hasKey(TAG_CHARGES)))
        {
            write(stack, getCharges(stack, ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE), MagazineLogic.LEGACY_AMMO);
        }
    }

    // ------------------------------------------------------------------------------------------------------ client

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(create(0, null));
        list.add(create(ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE, MagazineLogic.BOLT_AMMO));
        list.add(create(ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE, MagazineLogic.LEGACY_AMMO));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        emptyIcon = iconRegister.registerIcon(ModInfo.MOD_ID.toLowerCase() + ":boltCartridgeEmpty");
    }

    /** Empty or loaded icon: one int read from the stack's tag, like the durability bar. */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack)
    {
        return isEmpty(stack) ? emptyIcon : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return getIconIndex(stack);
    }

    private static boolean isEmpty(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey(TAG_CHARGES) && tag.getInteger(TAG_CHARGES) <= 0;
    }

    private static String tooltipAmmo;
    private static String tooltipAmmoName;

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
    {
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        int charges = getCharges(stack, capacity);
        tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.magazine.charges", charges, capacity));
        if (charges > 0)
        {
            String ammo = getAmmo(stack);
            if (!ammo.equals(tooltipAmmo))
            {
                // The display name is looked up once per kind, not on every frame the tooltip is drawn.
                tooltipAmmoName = displayName(ammo);
                tooltipAmmo = ammo;
            }
            tooltip.add(StatCollector.translateToLocalFormatted("adventurebackpack:tooltip.magazine.ammo", tooltipAmmoName));
        }
        if (charges < capacity) tooltip.add(StatCollector.translateToLocal("adventurebackpack:tooltip.magazine.load"));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return getCharges(stack, ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE) < ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        int capacity = ConfigHandler.CROSSBOW_SHOTS_PER_MAGAZINE;
        return MagazineLogic.emptyFraction(getCharges(stack, capacity), capacity);
    }
}
