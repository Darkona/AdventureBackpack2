package com.darkona.adventurebackpack.develop;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
public class ChatHandler
{
	  public static void sendServerMessage(String string)
	  {
	    ChatComponentTranslation translation = new ChatComponentTranslation(string, new Object[0]);
	    MinecraftServer.getServer().getConfigurationManager().sendChatMsg(translation);
	  }
  }
