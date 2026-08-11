package org.tlauncher.minecraft;

import java.lang.reflect.Method;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.tlauncher.injection.mapping.MappingManager;
import org.tlauncher.injection.mapping.ObfClass;
import org.tlauncher.renderer.texture.LightTexture;
import org.tlauncher.util.TLModCfg;
import org.tlauncher.util.TypeLocator;

public class GameTextureManagerImpl implements GameTextureManager, TypeLocator {
   private final Supplier<Minecraft> getMinecraft;
   private final Method loadTextureMethod;
   private final Method deleteTextureMethod;

   public GameTextureManagerImpl(Supplier<Minecraft> getMinecraft) {
      this.getMinecraft = getMinecraft;
      Class<TextureManager> textureManagerClass = TextureManager.class;
      ObfClass classMappings = MappingManager.instance().getMappings().getClass("TextureManager");
      String loadTextureMethodName = TLModCfg.isForgeDetected() ? "m_118515_" : (TLModCfg.isFabricDetected() ? "method_24303" : classMappings.getMethod("loadTexture").getObfName());
      String safeCloseMethodName = TLModCfg.isForgeDetected() ? "m_118508_" : (TLModCfg.isFabricDetected() ? "method_30299" : classMappings.getMethod("safeClose").getObfName());
      this.loadTextureMethod = this.findMethod(textureManagerClass, new TypeLocator.MethodData[]{new TypeLocator.MethodData(true, loadTextureMethodName, new Class[]{ResourceLocation.class, AbstractTexture.class})});
      this.deleteTextureMethod = this.findMethod(textureManagerClass, new TypeLocator.MethodData[]{new TypeLocator.MethodData(true, safeCloseMethodName, new Class[]{ResourceLocation.class, AbstractTexture.class})});
      this.loadTextureMethod.setAccessible(true);
      this.deleteTextureMethod.setAccessible(true);
   }

   public void loadTexture(Resource resource, LightTexture lightTexture) {
      try {
         ((Minecraft)this.getMinecraft.get()).m_91097_().m_118495_(resource, lightTexture);
      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   public void deleteTexture(Resource resource) {
      try {
         AbstractTexture texture = ((Minecraft)this.getMinecraft.get()).m_91097_().m_118506_(resource);
         this.deleteTextureMethod.invoke(((Minecraft)this.getMinecraft.get()).m_91097_(), resource, texture);
      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   public LightTexture getTexture(Resource resource) {
      return (LightTexture)((Minecraft)this.getMinecraft.get()).m_91097_().m_118506_(resource);
   }
}
