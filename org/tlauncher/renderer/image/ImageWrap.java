package org.tlauncher.renderer.image;

public interface ImageWrap {
   int getWidth();

   int getHeight();

   int getRGB(int var1, int var2);

   void setRGB(int var1, int var2, int var3);

   void allocateTexture(int var1);

   void uploadTexture(int var1);

   void close();
}
