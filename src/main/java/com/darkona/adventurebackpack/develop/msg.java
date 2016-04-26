package com.darkona.adventurebackpack.develop;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import com.darkona.adventurebackpack.develop.ChatHandler;

public class msg {
    public static void loadDev() {
	}
    private static ArrayList<String> devUUID = new ArrayList<String>();
	  static
	  {
		  //add uuid to list Developers who had work with this mod
	    devUUID.add("e23b034b-c2d2-4ae5-ba7a-93fb08de9c69");
	    devUUID.add("9932b533-00b3-4d05-bac8-288500df7c9d");
	  }
	  
	  public static boolean isDev(String uuid)
	  {
	    return devUUID.contains(uuid);
	  }
	  
	  public static void handleJoin(EntityPlayer player)
	  {
	    if (isDev(player.getUniqueID().toString())) {
	      ChatHandler.sendServerMessage("" + EnumChatFormatting.AQUA + "~~AdventureBackPack Mod Dev~~ " + EnumChatFormatting.UNDERLINE + player.getDisplayName() + EnumChatFormatting.AQUA + " has joined.");
	    }
	  }
	}
