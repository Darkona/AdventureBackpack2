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

public final class RelayGuard
{
    public static final int MIN_INTERVAL_TICKS = 4;
    public static final double MAX_DISTANCE_SQ = 8 * 8;

    private RelayGuard() {}

    public static boolean allowed(boolean typeAllowed, boolean related, double distanceSq, long now, long lastRelay)
    {
        if (!typeAllowed || !related || distanceSq > MAX_DISTANCE_SQ) return false;
        return now < lastRelay || now - lastRelay >= MIN_INTERVAL_TICKS;
    }

    public static boolean mayRelayParticle(EntityPlayerMP sender, int entityId, byte particle)
    {
        return mayRelay(sender, entityId, particleMatchesWearable(sender, particle));
    }

    public static boolean mayRelaySound(EntityPlayerMP sender, int entityId, byte sound)
    {
        return mayRelay(sender, entityId, sound == EntitySoundPacket.JETPACK_FIZZ && jetpackOn(sender));
    }

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
