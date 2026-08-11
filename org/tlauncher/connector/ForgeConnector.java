package org.tlauncher.connector;

import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.tlauncher.util.TypeLocator;

public class ForgeConnector extends AbstractConnector implements TypeLocator {
   private final Minecraft client;
   private Map<ServerData, ExtendedServerListData> serverDataTag;

   public void showGuiScreen(@Nullable Object clientGuiElement) {
      try {
         Method method = this.client.getClass().getMethod("m_91152_", TitleScreen.class);
         method.invoke(this.client, clientGuiElement);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
         ((ReflectiveOperationException)e).printStackTrace();
      }

   }

   public void setupServerList() {
      this.serverDataTag = Collections.synchronizedMap(Maps.newHashMap());
   }

   public void connectToServer(String host, int port) {
      this.setupServerList();
      ServerData serverData = new ServerData("Command Line", host + ":" + port, false);
      this.connectToServer((Object)null, serverData);
   }

   public void connectToServer(Object guiMultiplayer, ServerData serverEntry) {
      ExtendedServerListData extendedData = (ExtendedServerListData)this.serverDataTag.get(serverEntry);
      if (extendedData != null && extendedData.isBlocked) {
         this.showGuiScreen((Object)null);
      } else {
         Object clientGuiElement = this.findConstructor(new TypeLocator.ClassNames(new String[]{"net.minecraft.client.gui.screens.ConnectScreen"}), new TypeLocator.ParamsData(new Class[]{TitleScreen.class, Minecraft.class, ServerData.class})).newInstance(guiMultiplayer, this.client, serverEntry);
         this.showGuiScreen(clientGuiElement);
      }

   }

   public ForgeConnector(Minecraft client) {
      this.client = client;
   }
}
