package org.tlauncher.util;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;

public class MinecraftUtils {
   public static String getSessionUsername() {
      return Minecraft.m_91087_().m_91094_().m_92546_();
   }

   public static NativeImage readNativeImage(InputStream inputStream) {
      try {
         return NativeImage.m_85058_(inputStream);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
