package com.darkona.adventurebackpack.develop;

import net.minecraft.entity.player.EntityPlayerMP;
import com.darkona.adventurebackpack.develop.msg;

public class DeveloperJoining
{
  private EntityPlayerMP player;

  public void onPlayerLogin()
  {
      msg.handleJoin(player);
    }
  }
