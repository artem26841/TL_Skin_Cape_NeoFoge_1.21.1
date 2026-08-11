package org.tlauncher.connector;

import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.tlauncher.injection.mapping.MappingManager;
import org.tlauncher.injection.mapping.ObfClass;
import org.tlauncher.util.TLModCfg;

public class Connector extends AbstractConnector {
   private Minecraft client;
   private Map<ServerData, ExtendedServerListData> serverDataTag;

   public Connector(Minecraft minecraft) {
      this.client = minecraft;
   }

   public void showGuiScreen(@Nullable Object clientGuiElement) {
      Screen gui = (Screen)clientGuiElement;

      try {
         Method method = this.client.getClass().getMethod(MappingManager.instance().getMappings().getClass("Minecraft").getMethod("displayGuiScreen").getObfName(), Screen.class);
         method.invoke(this.client, gui);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
         ((ReflectiveOperationException)e).printStackTrace();
      }

   }

   public void setupServerList() {
      this.serverDataTag = Collections.synchronizedMap(Maps.newHashMap());
   }

   public void connectToServer(String host, int port) {
      this.setupServerList();
      ServerData serverData = null;
      String command_line = "Command Line";
      String serverPath = host + ":" + port;

      try {
         ServerData.class.getConstructor(String.class, String.class, Boolean.TYPE);
         serverData = new ServerData(command_line, serverPath, false);
      } catch (NoSuchMethodException var9) {
         try {
            serverData = (ServerData)ServerData.class.getConstructor(String.class, String.class).newInstance(command_line, serverPath);
         } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException instantiationException) {
            ((ReflectiveOperationException)instantiationException).printStackTrace();
         }
      }

      if (serverData != null) {
         this.connectToServer((Screen)null, serverData);
      }

   }

   public void connectToServer(Screen guiMultiplayer, ServerData serverEntry) {
      ExtendedServerListData extendedData = (ExtendedServerListData)this.serverDataTag.get(serverEntry);
      if (extendedData != null && extendedData.isBlocked) {
         this.showGuiScreen((Object)null);
      } else {
         ServerAddress serverAddress = new ServerAddress(serverEntry.f_105363_.split(":")[0], Integer.parseInt(serverEntry.f_105363_.split(":")[1]));
         if (TLModCfg.getMinecraftVersion().equals("1.20")) {
            try {
               ObfClass classMappings = MappingManager.instance().getMappings().getClass("ConnectScreen");
               String methodName = classMappings.getMethod("startConnecting").getObfName();
               Method method = ConnectScreen.class.getDeclaredMethod(methodName, Screen.class, Minecraft.class, ServerAddress.class, ServerData.class, Boolean.TYPE);
               method.invoke((Object)null, new TitleScreen(), this.client, serverAddress, serverEntry, false);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
               throw new RuntimeException(e);
            }
         } else {
            ConnectScreen.m_169267_(new TitleScreen(), this.client, serverAddress, (ServerData)null);
         }
      }

   }
}
