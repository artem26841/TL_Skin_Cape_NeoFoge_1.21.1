package org.tlauncher.util;

import java.lang.reflect.InvocationTargetException;
import org.tlauncher.TLSkinCape;
import org.tlauncher.connector.AbstractConnector;
import org.tlauncher.connector.FabricConnector;
import org.tlauncher.connector.ForgeConnector;

public class ServerConnector {
   private boolean flag;

   public void tryToConnect() {
      if (!this.flag) {
         this.flag = true;
         AbstractConnector connector = null;
         if (TLModCfg.isForgeDetected()) {
            connector = new ForgeConnector(TLSkinCape.getMinecraftInstance());
         } else if (TLModCfg.isFabricDetected()) {
            connector = new FabricConnector(TLSkinCape.getMinecraftInstance());
         } else {
            try {
               Class<?> aClass = Class.forName("org.tlauncher.connector.Connector");
               connector = (AbstractConnector)aClass.getConstructor(TLSkinCape.getMinecraftInstance().getClass()).newInstance(TLSkinCape.getMinecraftInstance());
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
               TLSkinCape.LOGGER.error("No suitable connector found.", e);
            }
         }

         if (connector != null) {
            connector.connectToServer();
         }
      }

   }
}
