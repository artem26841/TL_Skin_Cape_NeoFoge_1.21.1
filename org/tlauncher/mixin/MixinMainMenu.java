package org.tlauncher.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tlauncher.TLSkinCape;

@Mixin(TitleScreen.class)
public class MixinMainMenu {
    
    @Inject(
        method = "render", // Заменили m_88315_ на человеческое название
        at = @At("TAIL")
    )
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Вызываем логику инициализации TLauncher напрямую
        TLSkinCape.onMainMenuRender();
    }
}
