package org.tlauncher.injection;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.tlauncher.injection.mapping.MappingManager;
import org.tlauncher.injection.mapping.Mappings;
import org.tlauncher.injection.mapping.ObfClass;
import org.tlauncher.tweaker.Tweaker;
import org.tlauncher.util.TLModCfg;

public class Injections {
   void registerInjections(InjectionManager injectionManager) {
      Mappings mappings = MappingManager.instance().getMappings();
      ObfClass abstractClientPlayer = mappings.getClass("AbstractClientPlayer");
      ObfClass entityPlayer = mappings.getClass("Player");
      String playerClassName = entityPlayer.getObfName();
      String isModelPartShownMethodName = entityPlayer.getMethod("isModelPartShown").getObfName();
      String playerModelPartClassName = mappings.getClass("PlayerModelPart").getObfName();
      String isModelPartShownMethodDesc = String.format("(%s)Z;", playerModelPartClassName);
      String resourceLocation = mappings.getClass("ResourceLocation").getObfName();
      String locCapeMethodName = abstractClientPlayer.getMethod("getCloakTextureLocation").getObfName();
      String abstrPlayerClassName = abstractClientPlayer.getObfName();
      String locSkinMethodName = abstractClientPlayer.getMethod("getSkinTextureLocation").getObfName();
      String skinTypeMethodName = abstractClientPlayer.getMethod("getModelName").getObfName();
      String locElytraMethodName = abstractClientPlayer.getMethod("getElytraTextureLocation").getObfName();
      String minecraftClassName = mappings.getClass("Minecraft").getObfName();
      String gameProfileClassName = entityPlayer.getMethod("getGameProfile").getObfName();
      String resourceLocationDesc = String.format("()L%s;", resourceLocation);
      ObfClass guiMainMenu = mappings.getClass("TitleScreen");
      injectionManager.addInjection(guiMainMenu.getObfName(), (bytes, injectionHelper) -> {
         String descriptor;
         if (TLModCfg.getMinecraftVersion().equals("1.20")) {
            descriptor = String.format("(L%s;IIF)V", mappings.getClass("GuiGraphics").getObfName());
         } else {
            descriptor = String.format("(L%s;IIF)V", mappings.getClass("PoseStack").getObfName());
         }

         return (InjectionResult)injectionHelper.init(bytes).findMethod(guiMainMenu.getMethod("render").getObfName(), descriptor).thenApply((methodNodeWrap) -> {
            InsnList insnList = new InsnList();
            insnList.add(new InsnNode(4));
            insnList.add(new FieldInsnNode(179, "org/tlauncher/Variables", "isLoaded", "Z"));
            insnList.add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "onMainMenuRender", "()V", false));
            methodNodeWrap.getInstructions().insert(methodNodeWrap.getInstructions().getFirst(), insnList);
            return InjectionResult.SUCCESS;
         });
      });
      if (!TLModCfg.isOptiFineDetected()) {
         String gameMainClass = "net.minecraft.client.main.Main";
         injectionManager.addInjection("net.minecraft.client.main.Main", (bytes, injectionHelper) -> (InjectionResult)injectionHelper.init(bytes).findMethod("main", "([Ljava/lang/String;)V").thenApply((methodNodeWrap) -> {
               methodNodeWrap.findInstruction().filter((abstractInsnNode) -> abstractInsnNode instanceof MethodInsnNode).map((abstractInsnNode) -> (MethodInsnNode)abstractInsnNode).filter((methodInsnNode) -> methodInsnNode.owner.equals("joptsimple/OptionParser")).filter((methodInsnNode) -> methodInsnNode.name.equals("<init>")).findFirst().ifPresent((methodInsnNode) -> {
                  methodInsnNode.desc = "(Z)V";
                  methodNodeWrap.getInstructions().insertBefore(methodInsnNode, new InsnNode(3));
               });
               InsnList list = new InsnList();
               list.add(new VarInsnNode(25, 0));
               list.add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "processMainArgs", "([Ljava/lang/String;)[Ljava/lang/String;", false));
               list.add(new VarInsnNode(58, 0));
               methodNodeWrap.getInstructions().insertBefore(methodNodeWrap.getInstructions().getFirst(), list);
               return InjectionResult.SUCCESS;
            }));
         injectionManager.addInjection(minecraftClassName, (bytes, injectionHelper) -> {
            injectionHelper.init(bytes).findMethod("<init>").forEach((objectMethodNodeContainer) -> objectMethodNodeContainer.thenAccept((methodNodeWrap) -> {
                  AbstractInsnNode instruction = methodNodeWrap.findInstruction((node) -> node instanceof MethodInsnNode && ((MethodInsnNode)node).name.equals("<init>"));
                  InsnList list = new InsnList();
                  list.add(new VarInsnNode(25, 0));
                  list.add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "startConnector", String.format("(L%s;)V", minecraftClassName), false));
                  methodNodeWrap.getInstructions().insert(instruction, list);
               }));
            return InjectionResult.SUCCESS;
         });
      } else {
         String tutorialClass = mappings.getClass("Tutorial").getObfName();
         injectionManager.addInjection(tutorialClass, (bytes, injectionHelper) -> (InjectionResult)((InjectionHelper.MethodNodeContainer)injectionHelper.init(bytes).findMethod("<init>").get(0)).thenApply((methodNodeWrap) -> {
               AbstractInsnNode instruction = methodNodeWrap.findInstruction((node) -> node instanceof FieldInsnNode && ((FieldInsnNode)node).desc.equals("L" + minecraftClassName + ";"));
               InsnList list = new InsnList();
               list.add(new VarInsnNode(25, 1));
               list.add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "startConnector", String.format("(L%s;)V", minecraftClassName), false));
               methodNodeWrap.getInstructions().insert(instruction, list);
               return InjectionResult.SUCCESS;
            }));
      }

      injectionManager.addInjection(abstrPlayerClassName, (bytes, injectionHelper) -> {
         List<InjectionResult> injectionResults = new ArrayList();
         injectionHelper.init(bytes).findMethod(locCapeMethodName, resourceLocationDesc).thenAccept((methodNodeWrap) -> {
            if (Tweaker.isTLSkinCapeEnabled) {
               methodNodeWrap.getInstructions().clear();
               methodNodeWrap.getInstructions().add(new VarInsnNode(25, 0));
               methodNodeWrap.getInstructions().add(new MethodInsnNode(182, abstrPlayerClassName, gameProfileClassName, "()Lcom/mojang/authlib/GameProfile;", false));
               methodNodeWrap.getInstructions().add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "getLocationCape", String.format("(Lcom/mojang/authlib/GameProfile;)L%s;", resourceLocation), false));
               methodNodeWrap.getInstructions().add(new InsnNode(176));
               injectionResults.add(InjectionResult.SUCCESS);
            }
         }).findMethod(locSkinMethodName, resourceLocationDesc).thenAccept((methodNodeWrap) -> {
            if (Tweaker.isTLSkinCapeEnabled) {
               methodNodeWrap.getInstructions().clear();
               methodNodeWrap.getInstructions().add(new VarInsnNode(25, 0));
               methodNodeWrap.getInstructions().add(new MethodInsnNode(182, abstrPlayerClassName, gameProfileClassName, "()Lcom/mojang/authlib/GameProfile;", false));
               methodNodeWrap.getInstructions().add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "getLocationSkin", String.format("(Lcom/mojang/authlib/GameProfile;)L%s;", resourceLocation), false));
               methodNodeWrap.getInstructions().add(new InsnNode(176));
               injectionResults.add(InjectionResult.SUCCESS);
            }
         }).findMethod(locElytraMethodName, resourceLocationDesc).nonNull().thenAccept((methodNodeWrap) -> {
            if (Tweaker.isTLSkinCapeEnabled) {
               methodNodeWrap.getInstructions().clear();
               methodNodeWrap.getInstructions().add(new VarInsnNode(25, 0));
               methodNodeWrap.getInstructions().add(new MethodInsnNode(182, abstrPlayerClassName, gameProfileClassName, "()Lcom/mojang/authlib/GameProfile;", false));
               methodNodeWrap.getInstructions().add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "getLocationElytra", String.format("(Lcom/mojang/authlib/GameProfile;)L%s;", resourceLocation), false));
               methodNodeWrap.getInstructions().add(new InsnNode(176));
               injectionResults.add(InjectionResult.SUCCESS);
            }
         }).findMethod(skinTypeMethodName, "()Ljava/lang/String;").thenAccept((methodNodeWrap) -> {
            if (Tweaker.isTLSkinCapeEnabled) {
               methodNodeWrap.getInstructions().clear();
               methodNodeWrap.getInstructions().add(new VarInsnNode(25, 0));
               methodNodeWrap.getInstructions().add(new MethodInsnNode(182, abstrPlayerClassName, gameProfileClassName, "()Lcom/mojang/authlib/GameProfile;", false));
               methodNodeWrap.getInstructions().add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "getSkinType", "(Lcom/mojang/authlib/GameProfile;)Ljava/lang/String;", false));
               methodNodeWrap.getInstructions().add(new InsnNode(176));
               injectionResults.add(InjectionResult.SUCCESS);
            }
         });

         for(InjectionResult injectionResult : injectionResults) {
            if (injectionResult != InjectionResult.SUCCESS) {
               return InjectionResult.FAILURE;
            }
         }

         return InjectionResult.SUCCESS;
      });
      ObfClass playerModel = mappings.getClass("PlayerModel");
      injectionManager.addInjection(playerModel.getObfName(), (bytes, injectionHelper) -> (InjectionResult)injectionHelper.init(bytes).findMethod(playerModel.getMethod("renderCloak").getObfName(), MethodDescFilter.builder().endsWith(";II)V").build()).thenApply((methodNodeWrap) -> {
            InsnList list = new InsnList();
            list.add(new MethodInsnNode(184, "org/tlauncher/TLSkinCape", "preRenderCape", "()V", false));
            methodNodeWrap.getInstructions().insertBefore(methodNodeWrap.getInstructions().getFirst(), list);
            return InjectionResult.SUCCESS;
         }));
   }
}
