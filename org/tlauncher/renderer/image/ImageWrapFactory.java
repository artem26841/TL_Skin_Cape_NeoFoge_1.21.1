package org.tlauncher.renderer.image;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import org.tlauncher.util.TLModCfg;

public final class ImageWrapFactory {
   public static ImageWrap create(BufferedImage bufferedImage) {
      return (ImageWrap)(!TLModCfg.isNativeImageSupported() ? new BufferedImageWrap(bufferedImage) : new NativeImageWrap(bufferedImage));
   }

   public static ImageWrap create(InputStream inputStream) {
      try {
         return (ImageWrap)(!TLModCfg.isNativeImageSupported() ? new BufferedImageWrap(inputStream) : new NativeImageWrap(inputStream));
      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   public static ImageWrap create(int width, int height) {
      return (ImageWrap)(!TLModCfg.isNativeImageSupported() ? new BufferedImageWrap(width, height) : new NativeImageWrap(width, height));
   }

   private ImageWrapFactory() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
