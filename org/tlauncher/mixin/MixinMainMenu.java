package org.tlauncher.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tlauncher.TLSkinCape;

@Mixin({TitleScreen.class})
public class MixinMainMenu {
   @Inject(
      method = {"m_88315_"},
      at = {@At("TAIL")}
   )
   private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      this.render();
   }

   private void render() {
      TLSkinCape.onMainMenuRender();
   }
}
