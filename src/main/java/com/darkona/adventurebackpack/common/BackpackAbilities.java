package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.ai.EntityAIAvoidPlayerWithBackpack;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.TickThrottle;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 * @see com.darkona.adventurebackpack.block.TileAdventureBackpack
 * @see com.darkona.adventurebackpack.item.ItemAdventureBackpack
 * @see com.darkona.adventurebackpack.block.BlockAdventureBackpack
 */
public class BackpackAbilities
{

    public static BackpackAbilities backpackAbilities = new BackpackAbilities();
    public static BackpackRemovals backpackRemovals = new BackpackRemovals();

    /** Root key of the stack: world time when the Pig, Chicken or Creeper ability may fire next. */
    public static final String NEXT_ABILITY = "abilityNext";
    /** Root key of the stack: world time when the Rainbow's Nyan run ends. */
    public static final String RAINBOW_END = "rainbowEnd";

    /**
     *
     * @param colorName
     * @return
     */
    public static boolean hasAbility(String colorName)
    {
        for (String valid : validWearingBackpacks)
        {
            if (valid.equals(colorName))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRemoval(String colorName)
    {
        for (String valid : validRemovalBackpacks)
        {
            if (valid.equals(colorName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes the ability of any given backpack, be it on the ground or be it on a player.
     *
     * @param player   An entity player, can be null in the case of the tile entity.
     * @param world    This is necessary, so get it from wherever you can inside the class you're calling this.
     * @param backpack An object representing a backpack, either in its ItemStack form or its TileEntity form.
     */
    /** Ability methods by backpack color name, built once: read-only afterwards, so the client and server threads can share them. */
    private static final Map<String, Method> ITEM_ABILITIES = abilityTable("item", EntityPlayer.class, World.class, ItemStack.class);
    private static final Map<String, Method> TILE_ABILITIES = abilityTable("tile", World.class, TileAdventureBackpack.class);

    private static Map<String, Method> abilityTable(String prefix, Class<?>... params)
    {
        Map<String, Method> table = new HashMap<String, Method>();
        for (Method m : BackpackAbilities.class.getMethods())
        {
            if (m.getName().startsWith(prefix) && m.getName().length() > prefix.length()
                    && java.util.Arrays.equals(m.getParameterTypes(), params))
            {
                table.put(m.getName().substring(prefix.length()), m);
            }
        }
        return table;
    }

    public void executeAbility(EntityPlayer player, World world, Object backpack)
    {
        if (!ConfigHandler.BACKPACK_ABILITIES) return;
        if (backpack instanceof ItemStack)
        {
            String colorName = BackpackNames.getBackpackColorName((ItemStack)backpack);
            try
            {
                // item<Color>(player, world, stack), looked up once in a table instead of getMethod every tick
                Method ability = ITEM_ABILITIES.get(colorName);
                if (ability != null) ability.invoke(backpackAbilities, player, world, backpack);
            } catch (Exception oops)
            {
                LogHelper.debug("Backpack ability " + colorName + " failed: " + oops);
            }
        }

        if (backpack instanceof TileAdventureBackpack)
        {
            String colorName = ((TileAdventureBackpack) backpack).getColorName();
            try
            {
                    /*
                        This is witchery, witchery I say!
                        But seriously, if you want to know how this works just pay very close attention:
                        invoke will execute any method of a given class, okay? so this should be obvious.
                        Look at the names of the methods in this class and you'll figure it out.
                        You have to indicate exactly the classes that the method should use as parameters so
                        be very careful with "getMethod".
                     */
                Method ability = TILE_ABILITIES.get(colorName);
                if (ability != null) ability.invoke(backpackAbilities, world, backpack);
            } catch (Exception oops)
            {
                LogHelper.debug("Placed backpack ability " + colorName + " failed: " + oops);
            }
        }

    }

    public void executeRemoval(EntityPlayer player, World world,ItemStack backpack )
    {

        String colorName = BackpackNames.getBackpackColorName(backpack);
        try
        {
            //This is black magic and shouldn't be attempted by the faint of heart.
            backpackRemovals.getClass()
                    .getMethod("item" + colorName, EntityPlayer.class, World.class, ItemStack.class).
                    invoke(backpackRemovals, player, world, backpack);
        } catch (Exception oops)
        {
            LogHelper.error("---Something bad happened when removing a backpack---");
            oops.printStackTrace();
        }
    }
    /**
     * These are the colorNames of the backpacks that have abilities when being worn.
     */
    private static String[] validWearingBackpacks = {
            "Bat", "Squid", "Pigman", "Cactus", "Cow", "Pig", "Dragon", "Slime", "Chicken", "Wolf", "Ocelot", "Creeper", "Rainbow", "Melon", "Sunflower","Mooshroom"};

    private static String[] validRemovalBackpacks = {
            "Bat", "Squid", "Pigman", "Dragon", "Rainbow", "Slime"
    };
    /**
     * These are the colorNames of the backpacks that have abilities while being blocks. Note that not all the
     * backpacks that have particularities while in block form necessarily have abilities.
     *
     * @see com.darkona.adventurebackpack.block.BlockAdventureBackpack
     */
    private static String[] validTileBackpacks = {"Cactus","Melon"};

    /**
     * Detects if a player is under the rain. For detecting when it is Under The Sea (maybe to sing a nice Disney tune)
     * it won't work, there's a different method for that, isInWater
     *
     * @param player The player
     * @return True if the player is outside and it's raining.
     */
    private boolean isUnderRain(EntityPlayer player)
    {
        return player.worldObj.canLightningStrikeAt(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ))
                || player.worldObj.canLightningStrikeAt(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY + player.height),
                MathHelper.floor_double(player.posZ));
    }

    /**
     * This backpack will feed you while you stay in the sun, slowly. At the very least you shouldn't starve.
     * @param player
     * @param world
     * @param backpack
     */
    public void itemSunflower(EntityPlayer player, World world, ItemStack backpack)
    {
        // Food is server state (EntityPlayerMP sends the new food level to its client). The beat comes from the
        // world clock: a countdown saved in the stack changed its NBT every tick, which re-synced the wearable to
        // every nearby player every tick.
        if (!ConfigHandler.SUNFLOWER_ENABLED || world.isRemote) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), Utils.secondsToTicks(ConfigHandler.SUNFLOWER_INTERVAL_SECONDS))) return;
        if (world.isDaytime()
                && world.canBlockSeeTheSky(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY + 1), MathHelper.floor_double(player.posZ)))
        {
            player.getFoodStats().addStats(ConfigHandler.SUNFLOWER_FOOD, ConfigHandler.SUNFLOWER_SATURATION);
        }
    }

    /**
     * Nana nana nana nana Bat - Batpack! See in the dark!
     * @param player
     * @param world
     * @param backpack
     */
    public void itemBat(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.BAT_ENABLED) giveNightVision(player);
    }

    private void giveNightVision(EntityPlayer player)
    {
        //Shameless rip-off from Machinemuse. Thanks Claire, I don't have to reinvent the wheel thanks to you.
        //I will use a different potion id to avoid conflicting with her modular suits
        PotionEffect nightVision = player.getActivePotionEffect(Potion.nightVision);
        if (NightVisionRefresh.needsRefresh(nightVision != null,
                nightVision != null ? nightVision.getDuration() : 0,
                nightVision != null ? nightVision.getAmplifier() : 0, -4))
        {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, ConfigHandler.BAT_NIGHT_VISION_TICKS, -4));
        }
    }

    public void itemSquid(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.SQUID_ENABLED) giveWaterBreathing(player, world, backpack);
    }

    private void giveWaterBreathing(EntityPlayer player, World world, ItemStack backpack)
    {
        // The server owns the effects and syncs them; the client would only add a local copy the server overwrites
        if (world.isRemote) return;
        if (AbilityEffects.squidActive(player.isInWater(), ConfigHandler.SQUID_WORKS_ON_LAND))
        {
            PotionEffect breathing = player.getActivePotionEffect(Potion.waterBreathing);
            if (AbilityEffects.needsShortRefresh(breathing != null,
                    breathing != null ? breathing.getDuration() : 0,
                    breathing != null ? breathing.getAmplifier() : 0, AbilityEffects.SQUID_WATER_BREATHING_MARKER))
            {
                player.addPotionEffect(new PotionEffect(Potion.waterBreathing.getId(),
                        AbilityEffects.SHORT_EFFECT_TICKS, AbilityEffects.SQUID_WATER_BREATHING_MARKER));
            }
            giveNightVision(player);
        }else{
            backpackRemovals.itemSquid(player,world, backpack);
        }
    }

    public void itemIronGolem(EntityPlayer player, World world, ItemStack backpack)
    {

    }

    public void itemPigman(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.PIGMAN_ENABLED) giveFireResistance(player);
    }

    private void giveFireResistance(EntityPlayer player)
    {
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.fireResistance.id)) {
            potion = player.getActivePotionEffect(Potion.fireResistance);
        }
        // Refreshed before it runs out: letting it expire leaves a tick without it, and lava hurts in that tick (#105)
        if (potion == null || AbilityEffects.needsRefresh(potion.getDuration()))
        {
            player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, ConfigHandler.PIGMAN_FIRE_RESISTANCE_TICKS, -5));
        }
    }

    /**
     * Mirroring real life cactii, the Cactus Backpack fills with water slowly or rapidly depending where is the player.
     * If it's raining it will fill 1milibucket of water each tick.
     * If the player is in water it will fill 2milibuckets of water each tick.
     * The quantities can be combined.
     *
     * @param player   The player. No, seriously.
     * @param world    The world the player is in.
     * @param backpack The backpack the player is wearing. This should be rechecked like 20 times by now, so
     *                 I'm not checking.
     */
    public void itemCactus(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.CACTUS_ENABLED) fillWhenWet(player, world, backpack, FluidRegistry.WATER,
                ConfigHandler.CACTUS_WORN_INTERVAL_TICKS, ConfigHandler.CACTUS_WATER_IN_WATER, ConfigHandler.CACTUS_WATER_IN_RAIN);
    }

    /**
     * Cactus and Melon: on a world-time beat of {@code interval} ticks, adds the wet amounts to both tanks. The stack
     * only changes when fluid really goes in (the old countdown in the stack changed it every tick).
     */
    private void fillWhenWet(EntityPlayer player, World world, ItemStack backpack, Fluid fluid, int interval, int inWater, int inRain)
    {
        if (world.isRemote || fluid == null) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), interval)) return;
        int drops = 0;
        if (player.isInWater()) drops += inWater;
        if (isUnderRain(player)) drops += inRain;
        if (drops <= 0) return;
        InventoryBackpack inv = new InventoryBackpack(backpack);
        FluidStack raindrop = new FluidStack(fluid, drops);
        int filled = inv.getLeftTank().fill(raindrop, true) + inv.getRightTank().fill(raindrop, true);
        if (filled > 0) inv.dirtyTanks();
    }

    /**
     * The Pig Backpack will annoy you and your friends! This beautiful design by 豚, will do as the pigs do when they
     * are frolicking around in the green pastures and terrifying slaughterhouses of the Minecraft world, after a random
     * number of seconds. It's not so frequent as I'd like.
     * Translation for pigs: Oink oink oink Oink! squee oink oink Minecraft Oink oink. "Oink" oink oink.
     *
     * @param player   The player
     * @param world    The world object
     * @param backpack The backpack the player is wearing.
     */
    public void itemPig(EntityPlayer player, World world, ItemStack backpack)
    {
        // The oink is a server sound every nearby player hears; the wait is in whole seconds, checked once a second.
        if (!ConfigHandler.PIG_ENABLED || world.isRemote) return;
        long now = world.getTotalWorldTime();
        if (!TickThrottle.every(now, player.getEntityId(), AbilityTimer.SECOND_CHECK_TICKS)) return;
        long next = BackpackUtils.getTimeMark(backpack, NEXT_ABILITY);
        if (next >= 0 && !AbilityTimer.due(now, next, Utils.secondsToTicks(ConfigHandler.PIG_OINK_MAX_SECONDS))) return;
        // A backpack without a mark (new, or from an older version) only starts its wait
        if (next >= 0) world.playSoundAtEntity(player, "mob.pig.say", 0.8f, 1f);
        BackpackUtils.setTimeMark(backpack, NEXT_ABILITY, now + Utils.secondsToTicks(world.rand.nextInt(ConfigHandler.PIG_OINK_MAX_SECONDS + 1)));
    }

    /**
     * Squishy! The Slime Backpack has an incredibly useless "ability". Makes the player leave a slimy trail of
     * particles whenever he or she is running, and make that splishy splashy squishy sound on each step as well!.
     *
     * @param player
     * @param world
     * @param backpack
     */
    public void itemSlime(EntityPlayer player, World world, ItemStack backpack)
    {
        //0 is Full Moon, 1 is Waning Gibbous, 2 is Last Quarter, 3 is Waning Crescent,
        // 4 is New Moon, 5 is Waxing Crescent, 6 is First Quarter and 7 is Waxing Gibbous
        if (!ConfigHandler.SLIME_ENABLED) return;
        if (!world.isRemote)
        {
            // Server only: it owns the effect and syncs it, and it knows the real time of day
            if (ConfigHandler.SLIME_FULL_MOON_SPEED && world.getMoonPhase() == 0 && !world.isDaytime())
            {
                giveFullMoonSpeed(player);
            }
            else
            {
                backpackRemovals.itemSlime(player, world, backpack);
            }
        }
        if (player.onGround)
        {

            if ((player.moveForward == 0 && player.moveStrafing == 0) && (Math.abs(player.moveForward) < 3 && Math.abs(player.moveStrafing)<3) )
            {
                player.addVelocity(player.motionX *= 0.828, 0, player.motionZ *= 0.828);
            }
            // Trail on a world-time beat: the server sends the particles and the sound to every nearby player
            if (player.isSprinting() && !world.isRemote
                    && TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.SLIME_TRAIL_INTERVAL_TICKS))
            {
                ModNetwork.sendToNearby(new EntityParticlePacket.Message(EntityParticlePacket.SLIME_PARTICLE, player), player);
                world.playSoundAtEntity(player, "mob.slime.small", 0.6F, (world.rand.nextFloat() - world.rand.nextFloat()) * 1F);
            }
        }
    }

    private void giveFullMoonSpeed(EntityPlayer player)
    {
        PotionEffect speed = player.getActivePotionEffect(Potion.moveSpeed);
        if (AbilityEffects.needsShortRefresh(speed != null,
                speed != null ? speed.getDuration() : 0,
                speed != null ? speed.getAmplifier() : 0, AbilityEffects.SLIME_SPEED_AMPLIFIER))
        {
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(),
                    AbilityEffects.SHORT_EFFECT_TICKS, AbilityEffects.SLIME_SPEED_AMPLIFIER));
        }
    }

    /**
     * The Chicken Backpack will go and *plop* an egg for you randomly each so many seconds. It's very rare though.
     *
     * @param player
     * @param world
     * @param backpack
     */
    public void itemChicken(EntityPlayer player, World world, ItemStack backpack)
    {
        // The egg and its sound come from the server; the wait is in whole seconds, checked once a second.
        if (!ConfigHandler.CHICKEN_ENABLED || world.isRemote) return;
        long now = world.getTotalWorldTime();
        if (!TickThrottle.every(now, player.getEntityId(), AbilityTimer.SECOND_CHECK_TICKS)) return;
        long next = BackpackUtils.getTimeMark(backpack, NEXT_ABILITY);
        int longest = Utils.secondsToTicks(ConfigHandler.CHICKEN_EGG_MIN_SECONDS + ConfigHandler.CHICKEN_EGG_RANDOM_SECONDS);
        if (next >= 0 && !AbilityTimer.due(now, next, longest)) return;
        // A backpack without a mark (new, placed and picked up, or from an older version) waits a full time first,
        // so placing and taking it again does not lay an egg every time.
        if (next >= 0)
        {
            world.playSoundAtEntity(player, "mob.chicken.plop", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F + 1.0F);
            player.dropItem(Items.egg, 1);
        }
        BackpackUtils.setTimeMark(backpack, NEXT_ABILITY, now + Utils.secondsToTicks(ConfigHandler.CHICKEN_EGG_MIN_SECONDS + world.rand.nextInt(ConfigHandler.CHICKEN_EGG_RANDOM_SECONDS + 1)));
    }

    /**
     * The Melon Backpack, like his cousin the Cactus Backpack, will fill itself, but with delicious
     * and refreshing Melon Juice, if the backpack is wet in any way.
     *
     * @param player
     * @param world
     * @param backpack
     */
    public void itemMelon(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.MELON_ENABLED) fillWhenWet(player, world, backpack, ModFluids.melonJuice,
                ConfigHandler.MELON_WORN_INTERVAL_TICKS, ConfigHandler.MELON_JUICE_IN_WATER, ConfigHandler.MELON_JUICE_IN_RAIN);
    }

    /**
     * The Dragon Backpack does something awesome.
     *
     * @param player
     * @param world
     * @param backpack
     */
    public void itemDragon(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.DRAGON_ENABLED) return;
        giveFireResistance(player);
        giveWaterBreathing(player, world, backpack);
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.regeneration.id)) {
            potion = player.getActivePotionEffect(Potion.regeneration);
        }
        if (potion == null || AbilityEffects.needsRefresh(potion.getDuration()))
        {
            player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), ConfigHandler.DRAGON_EFFECT_TICKS, ConfigHandler.DRAGON_REGENERATION_AMPLIFIER));
        }
        potion = null;
        if (player.isPotionActive(Potion.damageBoost.id)) {
            potion = player.getActivePotionEffect(Potion.damageBoost);
        }
        if (potion == null || AbilityEffects.needsRefresh(potion.getDuration())) {
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(), ConfigHandler.DRAGON_EFFECT_TICKS, ConfigHandler.DRAGON_STRENGTH_AMPLIFIER));
        }
    }

    /**
     * Sneaky! Scare your friends! Or your enemies!
     * Sneak on another player to make them jump in confusion as they think one of those green bastards is behind him/her.
     * You can only do it once every so often. A couple of minutes. Remember, you must be sneaking.
     *
     * @param player
     * @param world
     * @param backpack
     * @see com.darkona.adventurebackpack.handlers.PlayerEventHandler
     */
    @SuppressWarnings("unchecked")
    public void itemCreeper(EntityPlayer player, World world, ItemStack backpack)
    {
        // Searched on a world-time beat while sneaking; the cooldown mark is written only when it hisses.
        if (!ConfigHandler.CREEPER_ENABLED || world.isRemote || !player.isSneaking()) return;
        long now = world.getTotalWorldTime();
        if (!TickThrottle.every(now, player.getEntityId(), ConfigHandler.CREEPER_CHECK_INTERVAL_TICKS)) return;
        int cooldown = Utils.secondsToTicks(ConfigHandler.CREEPER_SCARE_COOLDOWN_SECONDS);
        if (!AbilityTimer.due(now, BackpackUtils.getTimeMark(backpack, NEXT_ABILITY), cooldown)) return;
        List<Entity> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,
                AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                        player.posX + 1.0D, player.posY + 1.0D,
                        player.posZ + 1.0D).expand(5.0D, 1.0D, 5.0D));
        for (Entity entity : entities)
        {
            if (entity instanceof EntityPlayer && player.getDistanceToEntity(entity) <= ConfigHandler.CREEPER_SCARE_RANGE)
            {
                world.playSoundAtEntity(player, "creeper.primed", 1.2F, 0.5F);
                BackpackUtils.setTimeMark(backpack, NEXT_ABILITY, now + cooldown);
                return;
            }
        }
    }

    private FluidStack milkStack = new FluidStack(FluidRegistry.getFluid("milk"), 1);
    private FluidStack soupStack = new FluidStack(FluidRegistry.getFluid("mushroomstew"), 1);
    private FluidStack lavaStack = new FluidStack(FluidRegistry.LAVA, 1);

    /**
     * The Cow Backpack fills itself with milk when there is wheat in the backpack's inventory, but it will do so slowly
     * and will eat the wheat. It's like having a cow in your backpack. Each 16 wheat makes a bucket. It only happens
     * when it is being worn. For not-player related milk generation go get a cow. Moo!
     *
     * @param player
     * @param world
     * @param backpack
     */
    public void itemCow(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.COW_ENABLED) makeFromWheat(player, world, backpack, milkStack,
                ConfigHandler.COW_EAT_INTERVAL_TICKS, ConfigHandler.COW_WHEAT_PER_BATCH, ConfigHandler.COW_MILK_PER_BATCH);
    }

    /**
     * Cow and Mooshroom: eat one wheat on a world-time beat of {@code eatInterval} ticks; every {@code wheatPerBatch}
     * wheat start a batch that adds one millibucket per tick until {@code perBatch} are made. Without wheat and without
     * a batch running nothing is read or written (the old eat countdown in the stack changed it every tick).
     */
    private void makeFromWheat(EntityPlayer player, World world, ItemStack backpack, FluidStack drop, int eatInterval, int wheatPerBatch, int perBatch)
    {
        if (world.isRemote || drop.getFluid() == null) return;
        boolean eatBeat = TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), eatInterval);
        if (!eatBeat && storedBatchTicks(backpack) <= 0) return;

        InventoryBackpack inv = new InventoryBackpack(backpack);
        if (inv.getLeftTank().fill(drop, false) <= 0 && inv.getRightTank().fill(drop, false) <= 0)
        {
            return;
        }
        int wheatConsumed = 0;
        int milkTime = -1;
        if (inv.getExtendedProperties() != null && inv.extendedProperties.hasKey("wheatConsumed"))
        {
            wheatConsumed = inv.extendedProperties.getInteger("wheatConsumed");
            milkTime = inv.extendedProperties.getInteger("milkTime") - 1;
        }

        if (eatBeat && milkTime <= 0 && inv.hasItem(Items.wheat))
        {
            inv.consumeInventoryItem(Items.wheat);
            wheatConsumed++;
            inv.dirtyInventory();
        }

        if (wheatConsumed >= wheatPerBatch)
        {
            wheatConsumed = 0;
            milkTime = perBatch - 1;
            world.playSoundAtEntity(player, "mob.cow.say", 1f, 1f);
        }

        if (milkTime >= 0)
        {
            if (inv.getLeftTank().fill(drop, true) <= 0)
            {
                inv.getRightTank().fill(drop, true);
            }
            inv.dirtyTanks();
        }
        if (milkTime < -1) milkTime = -1;
        inv.extendedProperties.setInteger("wheatConsumed", wheatConsumed);
        inv.extendedProperties.setInteger("milkTime", milkTime);
        inv.dirtyExtended();
    }

    /** Ticks of milk or stew still to make, read straight from the stack (no inventory built). */
    private static int storedBatchTicks(ItemStack backpack)
    {
        NBTTagCompound data = BackpackUtils.getBackpackData(backpack);
        return data == null ? 0 : data.getCompoundTag("extendedProperties").getInteger("milkTime");
    }

    public void itemMooshroom(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.MOOSHROOM_ENABLED) makeFromWheat(player, world, backpack, soupStack,
                ConfigHandler.MOOSHROOM_EAT_INTERVAL_TICKS, ConfigHandler.MOOSHROOM_WHEAT_PER_BATCH, ConfigHandler.MOOSHROOM_STEW_PER_BATCH);
    }

    /**
     * The Wolf Backpack is a handy one if you're out in the wild. It checks around for any wolves that may lurk around.
     * If any of them gets mad at you, it will smell the scent of it's kin on you and promptly forget about the whole
     * deal. Smelling like dog is awesome.
     *
     * @param player   the player
     * @param world    the world
     * @param backpack the backpack
     */
    @SuppressWarnings("unchecked")
    public void itemWolf(EntityPlayer player, World world, ItemStack backpack)
    {
        // Wolf AI is server state; the search runs on a world-time beat instead of a countdown in the stack
        if (!ConfigHandler.WOLF_ENABLED || world.isRemote) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.WOLF_CHECK_INTERVAL_TICKS)) return;
        List<EntityWolf> wolves = world.getEntitiesWithinAABB(
                EntityWolf.class,
                AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                        player.posX + 1.0D, player.posY + 1.0D,
                        player.posZ + 1.0D).expand(ConfigHandler.WOLF_RANGE, 4.0D, ConfigHandler.WOLF_RANGE));
        for (EntityWolf wolf : wolves)
        {
            if (wolf.isAngry() && wolf.getAttackTarget() == player)
            {
                wolf.setAngry(false);
                wolf.setAttackTarget(null);
                wolf.setRevengeTarget(null);
                Iterator<?> i2 = wolf.targetTasks.taskEntries.iterator();
                while (i2.hasNext())
                {
                    ((EntityAIBase) i2.next()).resetTask();
                }
            }
        }
    }

    /**
     * The Blaze Backpack will make you inmune to fire and lava and burning and heat and... not really. You're supposed
     * to die a fiery death if you are not careful, but this backpack will protect you against those burning fire
     * elemental inhabitants of the Nether. Any blast of fire directed your way will be stopped, deflected or whatever.
     *
     * @param player
     * @param world
     * @param backpack
     */
    public void itemBlaze(EntityPlayer player, World world, ItemStack backpack)
    {

    }

    /**
     * Like actual Ocelots and Cats, the Ocelot Backpack will scare the hell out of Creepers, so they won't creep on you
     * while you're busy doing something else, paying no attention whatsoever at your surroundings like a mindless chicken.
     *
     * @param player
     * @param world
     * @param backpack
     */
    @SuppressWarnings("unchecked")
    public void itemOcelot(EntityPlayer player, World world, ItemStack backpack)
    {
        // Creeper AI is server state; the search runs on a world-time beat instead of a countdown in the stack
        if (!ConfigHandler.OCELOT_ENABLED || world.isRemote) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.OCELOT_CHECK_INTERVAL_TICKS)) return;
        List<EntityCreeper> creepers = player.worldObj.getEntitiesWithinAABB(
                EntityCreeper.class,
                AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                        player.posX + 1.0D, player.posY + 1.0D,
                        player.posZ + 1.0D).expand(ConfigHandler.OCELOT_RANGE, 4.0D, ConfigHandler.OCELOT_RANGE));

        for (EntityCreeper creeper : creepers)
        {
            boolean set = true;
            for (Object entry : creeper.tasks.taskEntries)
            {
                if (((EntityAITasks.EntityAITaskEntry) entry).action instanceof EntityAIAvoidPlayerWithBackpack)
                {
                    set = false;
                    break;
                }
            }
            if (set)
            {
                creeper.tasks.addTask(3, new EntityAIAvoidPlayerWithBackpack(creeper, EntityPlayer.class, 10.0F, 1.0, 1.3, "Ocelot"));
            }
        }
    }


    public void itemRainbow(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.RAINBOW_ENABLED) return;
        // The run is a world-time end mark set once when the golden apple is eaten (GeneralEventHandler): both sides
        // read it, nothing is written while it plays.
        int length = Utils.secondsToTicks(ConfigHandler.RAINBOW_DURATION_SECONDS);
        long left = AbilityTimer.remaining(world.getTotalWorldTime(), BackpackUtils.getTimeMark(backpack, RAINBOW_END), length);
        long noteTime = left - 1;
        if (left > 0 && noteTime < Utils.secondsToTicks(ConfigHandler.RAINBOW_DURATION_SECONDS - 3))
        {
            player.setSprinting(true);
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), AbilityEffects.RAINBOW_EFFECT_TICKS, ConfigHandler.RAINBOW_SPEED_AMPLIFIER));
            player.addPotionEffect(new PotionEffect(Potion.jump.getId(), AbilityEffects.RAINBOW_EFFECT_TICKS, ConfigHandler.RAINBOW_JUMP_AMPLIFIER));
            if (noteTime % 2 == 0 && !world.isRemote)
            {
                ModNetwork.sendToNearby(new EntityParticlePacket.Message(EntityParticlePacket.NYAN_PARTICLE, player), player);
            }
        }
        // Older versions kept a 5000-tick Speed with the -5 marker on the wearer; Speed scales with (amplifier + 1),
        // so that was -80 % walking speed between runs. It is no longer given; a leftover one is removed here.
        PotionEffect stale = player.getActivePotionEffect(Potion.moveSpeed);
        if (stale != null && AbilityEffects.isStaleRainbowMarker(stale.getAmplifier()))
        {
            if (world.isRemote) player.removePotionEffectClient(Potion.moveSpeed.id);
            else player.removePotionEffect(Potion.moveSpeed.id);
        }
    }

    /** True while the Nyan run of this Rainbow backpack plays. */
    public static boolean isRainbowRunning(World world, ItemStack backpack)
    {
        return AbilityTimer.remaining(world.getTotalWorldTime(), BackpackUtils.getTimeMark(backpack, RAINBOW_END),
                Utils.secondsToTicks(ConfigHandler.RAINBOW_DURATION_SECONDS)) > 0;
    }

    /** Starts the Nyan run: one write of its end mark. */
    public static void startRainbowRun(World world, ItemStack backpack)
    {
        BackpackUtils.setTimeMark(backpack, RAINBOW_END, world.getTotalWorldTime() + Utils.secondsToTicks(ConfigHandler.RAINBOW_DURATION_SECONDS));
    }
    /* ==================================== TILE ABILITIES ==========================================*/

    private void fillWithRain(World world, TileAdventureBackpack backpack, FluidStack fluid, int time)
    {
        if (world.isRaining() && world.canBlockSeeTheSky(backpack.xCoord, backpack.yCoord, backpack.zCoord))
        {
            int dropTime = backpack.getLastTime() - 1;
            if (dropTime <= 0)
            {
                backpack.getRightTank().fill(fluid, true);
                backpack.getLeftTank().fill(fluid, true);
                dropTime = time;
                backpack.markDirty();
            }
            backpack.setLastTime(dropTime);
        }
    }

    /**
     * Like real life cactii, this backpack will fill slowly while it's raining with refreshing water.
     *
     * @param world
     * @param backpack
     */
    public void tileCactus(World world, TileAdventureBackpack backpack)
    {
        if (ConfigHandler.CACTUS_ENABLED) fillWithRain(world, backpack, new FluidStack(FluidRegistry.WATER, ConfigHandler.CACTUS_PLACED_RAIN_AMOUNT), ConfigHandler.CACTUS_PLACED_INTERVAL_TICKS);
    }


    public void tileMelon(World world, TileAdventureBackpack backpack)
    {
        if (ConfigHandler.MELON_ENABLED) fillWithRain(world, backpack, new FluidStack(ModFluids.melonJuice, ConfigHandler.MELON_PLACED_RAIN_AMOUNT), ConfigHandler.MELON_PLACED_INTERVAL_TICKS);
    }

}
