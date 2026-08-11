package org.tlauncher.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tlauncher.TLSkinCape;

@Mixin({PlayerModel.class})
public class MixinPlayerModel {
   @Inject(
      method = {"renderCloak"},
      at = {@At("HEAD")}
   )
   private void renderCloak(PoseStack p_103412_, VertexConsumer p_103413_, int p_103414_, int p_103415_, CallbackInfo ci) {
      TLSkinCape.preRenderCape();
   }
}
