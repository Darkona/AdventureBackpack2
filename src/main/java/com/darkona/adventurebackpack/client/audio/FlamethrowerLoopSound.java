package com.darkona.adventurebackpack.client.audio;

import com.darkona.adventurebackpack.common.FlamethrowerSound;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.item.ItemFlamethrower;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class FlamethrowerLoopSound extends MovingSound
{
    private final EntityPlayer player;

    public FlamethrowerLoopSound(EntityPlayer player)
    {
        super(new ResourceLocation(ModInfo.MOD_ID, FlamethrowerSound.LOOP_SOUND));
        this.player = player;
        this.volume = FlamethrowerSound.LOOP_VOLUME * FlamethrowerSound.master(ConfigHandler.RENDER_FLAMETHROWER_VOLUME);
        this.xPosF = (float) player.posX;
        this.yPosF = (float) player.posY;
        this.zPosF = (float) player.posZ;
    }

    public static boolean isFiring(EntityPlayer player)
    {
        ItemStack held = player.getHeldItem();
        return FlamethrowerSound.firing(InventoryFlamethrowerPack.isLit(Wearing.getWearingFlamethrowerPack(player)),
                player.isUsingItem(), held != null && held.getItem() instanceof ItemFlamethrower);
    }

    @Override
    public void update()
    {
        float master = FlamethrowerSound.master(ConfigHandler.RENDER_FLAMETHROWER_VOLUME);
        if (!FlamethrowerSound.loopContinues(ConfigHandler.FLAMETHROWER_SOUND, master, !player.isDead && player.getHealth() > 0.0F, isFiring(player)))
        {
            donePlaying = true;
            return;
        }
        volume = FlamethrowerSound.LOOP_VOLUME * master;
        xPosF = (float) player.posX;
        yPosF = (float) player.posY;
        zPosF = (float) player.posZ;
    }

    @Override
    public boolean isDonePlaying()
    {
        return donePlaying;
    }

    @Override
    public boolean canRepeat()
    {
        return true;
    }

    @Override
    public int getRepeatDelay()
    {
        return 0;
    }

    @Override
    public float getVolume()
    {
        return volume;
    }

    @Override
    public float getPitch()
    {
        return FlamethrowerSound.LOOP_PITCH;
    }

    @Override
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.LINEAR;
    }
}
