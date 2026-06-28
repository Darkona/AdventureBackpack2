package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.util.Wearing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * Server check of the client packets that the server sends on to the nearby players (particles and sounds at an
 * entity). A client packet is a request (performance contract, rule 4): it is relayed only when the type is one the
 * sender's worn wearable really makes, the entity exists in the sender's world and is the sender or what the sender
 * rides (or is ridden by), it is near the sender, and the sender did not relay another one a moment ago. Anything
 * else is dropped, so a modified client cannot make other players hear or see effects at will.
 */
public final class RelayGuard
{
    /** Shortest wait between two relayed packets of one player, in ticks. */
    public static final int MIN_INTERVAL_TICKS = 4;
    /** Farthest the entity of a relayed packet may be from its sender (squared blocks). */
    public static final double MAX_DISTANCE_SQ = 8 * 8;

    private RelayGuard() {}

    /** Pure decision: every check passed and the per-player wait is over (or the clock went back). */
    public static boolean allowed(boolean typeAllowed, boolean related, double distanceSq, long now, long lastRelay)
    {
        if (!typeAllowed || !related || distanceSq > MAX_DISTANCE_SQ) return false;
        return now < lastRelay || now - lastRelay >= MIN_INTERVAL_TICKS;
    }

    /** Checks a particle packet from {@code sender} and records the relay when it passes. */
    public static boolean mayRelayParticle(EntityPlayerMP sender, int entityId, byte particle)
    {
        return mayRelay(sender, entityId, particleMatchesWearable(sender, particle));
    }

    /** Checks a sound packet from {@code sender} and records the relay when it passes. */
    public static boolean mayRelaySound(EntityPlayerMP sender, int entityId, byte sound)
    {
        // The only sound a client sends is the jetpack's ignition; the others come from the server itself
        return mayRelay(sender, entityId, sound == EntitySoundPacket.JETPACK_FIZZ && jetpackOn(sender));
    }

    /**
     * Rate limit alone, for what a client action makes the server send to the nearby players or back to the sender
     * (copter start sound, a requested properties sync). Records the relay when it passes.
     */
    public static boolean mayRelayFrom(EntityPlayerMP sender)
    {
        return sender != null && mayRelay(sender, sender.getEntityId(), true);
    }

    private static boolean mayRelay(EntityPlayerMP sender, int entityId, boolean typeAllowed)
    {
        if (sender == null || sender.worldObj == null) return false;
        Entity entity = sender.worldObj.getEntityByID(entityId);
        boolean related = entity != null && (entity == sender || entity == sender.ridingEntity || entity == sender.riddenByEntity);
        double distanceSq = entity != null ? entity.getDistanceSqToEntity(sender) : Double.MAX_VALUE;
        long now = sender.worldObj.getTotalWorldTime();
        BackpackProperty props = BackpackProperty.get(sender);
        if (props == null || !allowed(typeAllowed, related, distanceSq, now, props.getLastRelayTick())) return false;
        props.setLastRelayTick(now);
        return true;
    }

    private static boolean particleMatchesWearable(EntityPlayerMP sender, byte particle)
    {
        switch (particle)
        {
            case EntityParticlePacket.COPTER_PARTICLE:
                return ItemCopterPack.isUsable() && Wearing.isWearingCopter(sender)
                        && Wearing.getWearingCopter(sender).hasTagCompound()
                        && Wearing.getWearingCopter(sender).stackTagCompound.getByte("status") != ItemCopterPack.OFF_MODE;
            case EntityParticlePacket.JETPACK_PARTICLE:
                return jetpackOn(sender) && new InventorySteamJetpack(Wearing.getWearingSteam(sender)).isInUse();
            case EntityParticlePacket.SLIME_PARTICLE:
                return Wearing.isWearingTheRightBackpack(sender, "Slime");
            case EntityParticlePacket.NYAN_PARTICLE:
                if (!Wearing.isWearingTheRightBackpack(sender, "Rainbow")) return false;
                ItemStack backpack = Wearing.getWearingBackpack(sender);
                return backpack != null && BackpackAbilities.isRainbowRunning(sender.worldObj, backpack);
            default:
                return false;
        }
    }

    private static boolean jetpackOn(EntityPlayerMP sender)
    {
        return Wearing.isWearingSteam(sender) && new InventorySteamJetpack(Wearing.getWearingSteam(sender)).getStatus();
    }
}
