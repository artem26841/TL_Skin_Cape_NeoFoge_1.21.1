package org.tlauncher.minecraft;

import org.tlauncher.renderer.texture.LightTexture;

public interface GameTextureManager {
   void loadTexture(Resource var1, LightTexture var2);

   void deleteTexture(Resource var1);

   LightTexture getTexture(Resource var1);
}
