package com.darkona.adventurebackpack.reference;

import java.util.EnumSet;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;

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
    COOKIE          ( 20, SPECIAL),
    COW             (  1),
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
    IRON_GOLEM      ( 11),
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
    public static final ImmutableSet<BackpackTypes> SPECIAL_TYPES;
    public static final ImmutableSet<BackpackTypes> REMOVAL_TYPES;
    public static final ImmutableSet<BackpackTypes> NIGHT_VISION_TYPES;

    private final byte meta;
    private final String skinName; // equivalent to current colorName
    private final Props[] props;

    //TODO step1: replace BackpackNames by this , make it work
    //TODO step2: add support for recipes (see BackpackRecipes[List]). new field 'Object[]'?
    //TODO step3: rework NBT for wearable packs. unificate and simplify structure.
    //TODO step4: remove all internal interactions by colorName (skinName), replace by enum. maybe remove NBT field too cuz we need only meta

    BackpackTypes(int meta, String skin, Props... props)
    {
        Validate.inclusiveBetween(0, 127, meta, "wrong meta value: %s (%s)", meta, this);

        this.meta = (byte) meta;
        this.props = props;
        this.skinName = skin;
    }

    BackpackTypes(int meta, Props... props)
    {
        Validate.inclusiveBetween(0, 127, meta, "wrong meta value: %s (%s)", meta, this);

        this.meta = (byte) meta;
        this.props = props;
        this.skinName = generateSkinName();
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
        EnumSet<BackpackTypes> specials = EnumSet.noneOf(BackpackTypes.class);
        EnumSet<BackpackTypes> removals = EnumSet.noneOf(BackpackTypes.class);
        EnumSet<BackpackTypes> nightVisions = EnumSet.noneOf(BackpackTypes.class);

        for (BackpackTypes type : BackpackTypes.values())
        {
            if (byMeta.put(type, type.meta) != null)
                throw new IllegalArgumentException("duplicate meta: " + type.meta);

            for (Props property : type.props)
            {
                if (property == SPECIAL)
                    specials.add(type);
                if (property == REMOVAL)
                    removals.add(type);
                if (property == NIGHT_VISION)
                    nightVisions.add(type);
            }
        }

        BY_META = ImmutableBiMap.copyOf(byMeta.inverse());
        SPECIAL_TYPES = ImmutableSet.copyOf(specials);
        REMOVAL_TYPES = ImmutableSet.copyOf(removals);
        NIGHT_VISION_TYPES = ImmutableSet.copyOf(nightVisions);

        //ImmutableSet.<BackpackTypes>builder().addAll(SPECIAL).addAll(TILE).addAll(OTHER).build();
    }

    public static String getSkinName(BackpackTypes type)
    {
        return type.skinName;
    }

    public static byte getMeta(BackpackTypes type)
    {
        return type.meta;
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

    public static BackpackTypes getType(int meta)
    {
        Validate.inclusiveBetween(0, 127, meta, "wrong meta value: %s", meta);
        BackpackTypes type = BY_META.get((byte) meta);
        return type != null ? type : UNKNOWN;
    }

    public static BackpackTypes getType(byte meta)
    {
        Validate.inclusiveBetween((byte) 0, (byte) 127, meta, "wrong meta value: %s", meta);
        BackpackTypes type = BY_META.get(meta);
        return type != null ? type : UNKNOWN;
    }

    public static int getLowestUnusedMeta()
    {
        for (byte i = 0; i <= 126; i++)
        {
            if (BY_META.get(i) == null)
                return i;
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
        for (Props p : type.props)
        {
            if (p == prop)
                return true;
        }
        return false;
    }

    public enum Props
    {
        SPECIAL,
        REMOVAL,
        TILE,
        NIGHT_VISION,
        //HOLIDAY,
        //OTHER_ABILITY, // creeper or skeleton etc
    }
}
