package com.darkona.adventurebackpack.reference;

import java.util.Arrays;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.darkona.adventurebackpack.util.BackpackUtils;

import static com.darkona.adventurebackpack.common.Constants.TAG_TYPE;
import static com.darkona.adventurebackpack.reference.BackpackTypes.Props.NIGHT_VISION;
import static com.darkona.adventurebackpack.reference.BackpackTypes.Props.REMOVAL;
import static com.darkona.adventurebackpack.reference.BackpackTypes.Props.SPECIAL;
import static com.darkona.adventurebackpack.reference.BackpackTypes.Props.TILE;

/**
 * Created on 15.08.2017
 *
 * @author Ugachaga
 */
@SuppressWarnings("unused")
public enum BackpackTypes
{
    // @formatter:off
    STANDARD        (  0),

    BAT             (  2, SPECIAL, REMOVAL, NIGHT_VISION),
    BLACK           (  3),
    BLAZE           (  4),
    BLUE            ( 14),
    BOOKSHELF       ( 15),
    BROWN           ( 16),
    BROWN_MUSHROOM  ( 43),
    CACTUS          ( 17, SPECIAL, TILE),
    CAKE            ( 18),
    CARROT          (  5),
    CHEST           ( 19),
    CHICKEN         ( 28, SPECIAL),
    COAL            (  6),
    COOKIE          ( 20),
    COW             (  1, SPECIAL),
    CREEPER         ( 64, SPECIAL),
    CYAN            ( 21),
    DELUXE          ( 25),
    DIAMOND         (  7),
    DRAGON          ( 22, SPECIAL, REMOVAL, NIGHT_VISION),
    EGG             ( 23),
    ELECTRIC        ( 24),
    EMERALD         (  8),
    END             ( 27),
    ENDERMAN        ( 26),
    GHAST           ( 30),
    GLOWSTONE       ( 37),
    GOLD            (  9),
    GRAY            ( 31),
    GREEN           ( 32),
    HAYBALE         ( 33),
    HORSE           ( 34),
    IRON            ( 10),
    IRON_GOLEM      ( 11), //TODO has other ability, need some prop
    LAPIS           ( 12),
    LEATHER         ( 35),
    LIGHT_BLUE      ( 36),
    LIGHT_GRAY      ( 38),
    LIME            ( 39),
    MAGENTA         ( 40),
    MAGMA_CUBE      ( 41),
    MELON           ( 42, SPECIAL, TILE),
    MODDED_NETWORK  ( 76),
    MOOSHROOM       ( 45, SPECIAL),
    NETHER          ( 46),
    OBSIDIAN        ( 48),
    OCELOT          ( 29, SPECIAL),
    ORANGE          ( 49),
    OVERWORLD       ( 50),
    PIG             ( 53, SPECIAL),
    PIGMAN          ( 51, SPECIAL, REMOVAL),
    PINK            ( 52),
    PUMPKIN         ( 54),
    PURPLE          ( 55),
    QUARTZ          ( 56),
    RAINBOW         ( 57, SPECIAL, REMOVAL),
    RED             ( 58),
    RED_MUSHROOM    ( 44),
    REDSTONE        ( 13),
    SANDSTONE       ( 59),
    SHEEP           ( 60),
    SILVERFISH      ( 61),
    SKELETON        ( 65),
    SLIME           ( 67, SPECIAL),
    SNOW            ( 68),
    SPIDER          ( 69),
    SPONGE          ( 70),
    SQUID           ( 62, SPECIAL, REMOVAL, NIGHT_VISION),
    SUNFLOWER       ( 63, SPECIAL),
    VILLAGER        ( 71),
    WHITE           ( 72),
    WITHER          ( 47),
    WITHER_SKELETON ( 66),
    WOLF            ( 73, SPECIAL),
    YELLOW          ( 74),
    ZOMBIE          ( 75),

    UNKNOWN         (127, "UNKNOWN"), // null object
    ;
    // @formatter:on

    public static final ImmutableBiMap<Byte, BackpackTypes> BY_META;

    private final byte meta;
    private final String skinName;
    private final ImmutableSet<Props> props;

    BackpackTypes(int meta, String skin, Props... props)
    {
        Validate.inclusiveBetween(0, (int) Byte.MAX_VALUE, meta, "wrong meta value: %s (%s)", meta, this);

        this.meta = (byte) meta;
        this.skinName = skin.isEmpty() ? generateSkinName() : skin;
        this.props = Sets.immutableEnumSet(Arrays.asList(props));
    }

    BackpackTypes(int meta, Props... props)
    {
        this(meta, StringUtils.EMPTY, props);
    }

    private String generateSkinName()
    {
        return WordUtils
                .capitalize(this.name().toLowerCase(), '_')
                .replaceAll("_", "");
    }

    static
    {
        BiMap<BackpackTypes, Byte> byMeta = EnumHashBiMap.create(BackpackTypes.class);

        for (BackpackTypes type : BackpackTypes.values())
            if (byMeta.put(type, type.meta) != null)
                throw new IllegalArgumentException("duplicate meta: " + type.meta);

        BY_META = ImmutableBiMap.copyOf(byMeta.inverse());
    }

    public static String getSkinName(BackpackTypes type)
    {
        return type.skinName;
    }

    public static String getSkinName(int meta)
    {
        return getType(meta).skinName;
    }

    public static String getSkinName(ItemStack backpack)
    {
        return getSkinName(getType(backpack));
    }

    public static String getLocalizedName(BackpackTypes type)
    {
        return StatCollector.translateToLocal("adventurebackpack:skin.name." + type.name().toLowerCase());
    }

    public static byte getMeta(BackpackTypes type)
    {
        return type.meta;
    }

    public static BackpackTypes getType(int meta)
    {
        Validate.inclusiveBetween(0, (int) Byte.MAX_VALUE, meta, "wrong meta value: %s", meta);
        BackpackTypes type = BY_META.get((byte) meta);
        return type != null ? type : UNKNOWN;
    }

    public static BackpackTypes getType(byte meta)
    {
        Validate.inclusiveBetween((byte) 0, Byte.MAX_VALUE, meta, "wrong meta value: %s", meta);
        BackpackTypes type = BY_META.get(meta);
        return type != null ? type : UNKNOWN;
    }

    public static BackpackTypes getType(String skinName)
    {
        for (BackpackTypes type : BackpackTypes.values())
        {
            if (type.skinName.equals(skinName))
                return type;
        }
        return UNKNOWN;
    }

    public static BackpackTypes getType(ItemStack backpack)
    {
        if (backpack == null) // well... Wearing.getWearingBackpack() may return null... //TODO solve this damn null
            return null;

        NBTTagCompound backpackTag = BackpackUtils.getWearableCompound(backpack);
        if (backpackTag.getByte(TAG_TYPE) == UNKNOWN.meta) //TODO remove? are we rly need to normalize it?
        {
            backpackTag.setByte(TAG_TYPE, STANDARD.meta);
        }
        return getType(backpackTag.getByte(TAG_TYPE));
    }

    public static int getLowestUnusedMeta()
    {
        for (byte b = 0; b < Byte.MAX_VALUE; b++)
        {
            if (BY_META.get(b) == null)
                return b;
        }
        return -1;
    }

    public static boolean isNightVision(BackpackTypes type)
    {
        return hasProperty(type, NIGHT_VISION);
    }

    public static boolean isSpecial(BackpackTypes type)
    {
        return hasProperty(type, SPECIAL);
    }

    public static boolean hasProperty(BackpackTypes type, Props prop)
    {
        return type.props.contains(prop);
    }

    public static boolean hasProperties(BackpackTypes type, ImmutableSet<Props> props)
    {
        return type.props.containsAll(props);
    }

    public enum Props
    {
        SPECIAL,
        REMOVAL,
        TILE,
        NIGHT_VISION,
        //HOLIDAY,
        //OTHER_ABILITY, // creeper or skeleton etc
        ;

        public static final ImmutableSet<Props> POTION_EFFECT = Sets.immutableEnumSet(SPECIAL, REMOVAL);
    }

}
