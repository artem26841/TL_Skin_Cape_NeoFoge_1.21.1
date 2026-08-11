package org.tlauncher.connector;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import org.tlauncher.util.TypeLocator;

public class FabricConnector extends AbstractConnector implements TypeLocator {
   private final Minecraft client;

   public void showGuiScreen(@Nullable Object clientGuiElement) {
      this.client.m_91152_((Screen)clientGuiElement);
   }

   public void connectToServer(String host, int port) {
      ServerData serverData = new ServerData("Command Line", host + ":" + port, false);
      this.connectToServer((Object)null, serverData);
   }

   public void connectToServer(Object guiMultiplayer, ServerData serverEntry) {
   }

   public FabricConnector(Minecraft client) {
      this.client = client;
   }
}
