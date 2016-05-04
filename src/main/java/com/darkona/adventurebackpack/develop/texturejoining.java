package com.darkona.adventurebackpack.develop;

import net.minecraft.entity.player.EntityPlayerMP;
import com.darkona.adventurebackpack.develop.texturemsg;

public class texturejoining
{
  private EntityPlayerMP player;

  public void onPlayerLogin()
  {
      texturemsg.handleJoin(player);
    }
  }
