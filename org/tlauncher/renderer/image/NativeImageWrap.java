package org.tlauncher.renderer.image;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import org.tlauncher.util.TextureUtils;
import org.tlauncher.util.TypeLocator;

public class NativeImageWrap implements ImageWrap, TypeLocator {
   private static Method readNativeImage;
   private final NativeImage nativeImage;

   public NativeImageWrap(InputStream inputStream) {
      this.nativeImage = this.readNativeImage(inputStream);
   }

   public NativeImageWrap(int width, int height) {
      this.nativeImage = new NativeImage(width, height, true);
   }

   public NativeImageWrap(BufferedImage bufferedImage) {
      try {
         this.nativeImage = this.getNativeImage(bufferedImage);
      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   private NativeImage getNativeImage(BufferedImage bufferedImage) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      return this.readNativeImage(byteArrayInputStream);
   }

   private NativeImage readNativeImage(InputStream inputStream) {
      try {
         if (readNativeImage == null) {
            readNativeImage = this.findMethod(NativeImage.class, new TypeLocator.MethodData[]{new TypeLocator.MethodData(true, "a", new Class[]{InputStream.class}), new TypeLocator.MethodData(true, "m_85058_", new Class[]{InputStream.class}), new TypeLocator.MethodData(true, "method_4309", new Class[]{InputStream.class})});
         }

         return (NativeImage)readNativeImage.invoke((Object)null, inputStream);
      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   public int getWidth() {
      return this.nativeImage.m_84982_();
   }

   public int getHeight() {
      return this.nativeImage.m_85084_();
   }

   public int getRGB(int x, int y) {
      return this.nativeImage.m_84985_(x, y);
   }

   public void setRGB(int x, int y, int color) {
      this.nativeImage.m_84988_(x, y, color);
   }

   public void allocateTexture(int textureId) {
      TextureUtils.prepareImage(textureId, this.getWidth(), this.getHeight());
   }

   public void uploadTexture(int textureId) {
      this.nativeImage.m_85040_(0, 0, 0, false);
   }

   public void close() {
      this.nativeImage.close();
   }
}
