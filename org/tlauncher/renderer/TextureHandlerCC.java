package org.tlauncher.renderer;

import com.mojang.blaze3d.platform.TextureUtil;
import java.lang.reflect.Method;
import org.lwjgl.opengl.GL11;
import org.tlauncher.util.TLModCfg;
import org.tlauncher.util.TypeLocator;

public class TextureHandlerCC implements ITextureHandler, TypeLocator {
   private Method allocateTexture;

   public void bindTex(int textureId) {
      GL11.glBindTexture(3553, textureId);
   }

   public void delTex(int textureId) {
      GL11.glDeleteTextures(textureId);
   }

   public void prepImage(int textureId, int width, int height) {
      try {
         if (TLModCfg.isForgeDetected()) {
            TextureUtil.prepareImage(textureId, width, height);
         } else if (TLModCfg.isFabricDetected()) {
            TextureUtil.prepareImage(textureId, width, height);
         } else {
            TextureUtil.prepareImage(textureId, width, height);
         }

      } catch (Throwable $ex) {
         throw $ex;
      }
   }
}
