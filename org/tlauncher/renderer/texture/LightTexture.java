package org.tlauncher.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.lang.reflect.Method;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

public class LightTexture extends AbstractTexture implements AutoCloseable {
   private static Method prepImage;
   private static Method bindTex;
   private static Method delTex;
   private static Method image_getWidth;
   private static Method image_getHeight;
   private static Method image_uploadTexture;
   private static Method image_close;
   protected int glTextureId = -1;

   public LightTexture(Object imageWrap) {
      try {
         if (prepImage == null) {
            prepImage = Class.forName("org.tlauncher.util.TextureUtils").getDeclaredMethod("prepareImage", Integer.TYPE, Integer.TYPE, Integer.TYPE);
         }

         if (bindTex == null) {
            bindTex = Class.forName("org.tlauncher.util.TextureUtils").getDeclaredMethod("bindTexture", Integer.TYPE);
         }

         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.load(imageWrap));
         } else {
            this.load(imageWrap);
         }

      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   private void load(Object imageWrap) {
      try {
         if (image_getWidth == null) {
            image_getWidth = imageWrap.getClass().getMethod("getWidth");
            image_getHeight = imageWrap.getClass().getMethod("getHeight");
            image_uploadTexture = imageWrap.getClass().getMethod("uploadTexture", Integer.TYPE);
            image_close = imageWrap.getClass().getMethod("close");
         }

         Object width = image_getWidth.invoke(imageWrap);
         Object height = image_getHeight.invoke(imageWrap);
         prepImage.invoke((Object)null, this.m_117963_(), width, height);
         bindTex.invoke((Object)null, this.m_117963_());
         image_uploadTexture.invoke(imageWrap, this.m_117963_());
         image_close.invoke(imageWrap);
      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   public int m_117963_() {
      RenderSystem.recordRenderCall(RenderSystem::isOnRenderThreadOrInit);
      if (this.f_117950_ == -1) {
         this.f_117950_ = TextureUtil.generateTextureId();
      }

      return this.f_117950_;
   }

   public void m_6704_(ResourceManager p_117955_) throws IOException {
   }

   public void close() {
      try {
         if (this.glTextureId != -1) {
            if (delTex == null) {
               delTex = Class.forName("org.tlauncher.util.TextureUtils").getDeclaredMethod("deleteTexture", Integer.TYPE);
            }

            delTex.invoke((Object)null, this.glTextureId);
            this.glTextureId = -1;
         }

      } catch (Throwable $ex) {
         throw $ex;
      }
   }
}
