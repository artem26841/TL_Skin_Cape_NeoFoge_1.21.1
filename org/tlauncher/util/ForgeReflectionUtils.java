package org.tlauncher.util;

import java.lang.reflect.Field;
import java.util.AbstractList;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

public final class ForgeReflectionUtils {
   private static AbstractList<ItemStack> armorInventoryList;

   public static boolean isElytraEquipped(int slotIndex, boolean forceUpdate) {
      try {
         if (armorInventoryList == null || forceUpdate) {
            try {
               return ((ItemStack)Minecraft.m_91087_().f_91074_.m_150109_().f_35975_.get(slotIndex)).m_41720_() instanceof ElytraItem;
            } catch (NoSuchFieldError var17) {
               label65:
               for(Field declaredField : Minecraft.class.getDeclaredFields()) {
                  if (Entity.class.isAssignableFrom(declaredField.getType())) {
                     Class<?> playerClass = declaredField.getType();

                     for(Field declaredField1 : playerClass.getSuperclass().getSuperclass().getDeclaredFields()) {
                        if (Container.class.isAssignableFrom(declaredField1.getType())) {
                           for(Field declaredField2 : declaredField1.getType().getDeclaredFields()) {
                              if (AbstractList.class.isAssignableFrom(declaredField2.getType())) {
                                 AbstractList<ItemStack> list = (AbstractList)declaredField2.get(declaredField1.get(declaredField.get(Minecraft.m_91087_())));
                                 if (list.size() == 4) {
                                    armorInventoryList = list;
                                    break label65;
                                 }
                              }
                           }
                           break;
                        }
                     }
                     break;
                  }
               }
            }
         }

         if (armorInventoryList == null) {
            return false;
         } else {
            String name = ((ItemStack)armorInventoryList.get(slotIndex)).m_41720_().getClass().getName();
            return name.endsWith("ItemElytra") || name.endsWith("ElytraItem");
         }
      } catch (Throwable $ex) {
         throw $ex;
      }
   }

   private ForgeReflectionUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
