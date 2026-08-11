package org.tlauncher.util.reflection;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import org.tlauncher.util.TLModCfg;

public final class ReflectionUtils {
   private static final int SLOT_INDEX = 2;
   private static Method method;
   private static boolean isInited = true;

   public static boolean isElytraEquipped(@Nonnull ClassLoader classLoader) {
      try {
         if (!TLModCfg.isElytraSupported()) {
            return false;
         } else if (TLModCfg.isForgeDetected()) {
            if (method == null) {
               method = Class.forName("org.tlauncher.util.ForgeReflectionUtils").getMethod("isElytraEquipped", Integer.TYPE, Boolean.TYPE);
            }

            boolean invoke = (Boolean)method.invoke((Object)null, 2, !isInited);
            isInited = true;
            return invoke;
         } else if (TLModCfg.isFabricDetected()) {
            if (method == null) {
               method = Class.forName("org.tlauncher.util.FabricReflectionUtils").getMethod("isElytraEquipped", Integer.TYPE, ClassLoader.class, Boolean.TYPE);
            }

            boolean invoke = (Boolean)method.invoke((Object)null, 2, classLoader, !isInited);
            isInited = true;
            return invoke;
         } else {
            if (method == null) {
               method = Class.forName("org.tlauncher.util.reflection.VanillaReflectionUtils").getMethod("isElytraEquipped", Integer.TYPE, ClassLoader.class, Boolean.TYPE);
            }

            boolean invoke = (Boolean)method.invoke((Object)null, 2, classLoader, !isInited);
            isInited = true;
            return invoke;
         }
      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   public static void forceUpdate() {
      isInited = false;
   }

   private ReflectionUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
