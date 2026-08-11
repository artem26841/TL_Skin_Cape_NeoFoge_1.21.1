package org.tlauncher.mixin;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.tlauncher.TLSkinCape;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinAbstractPlayer {
   @Shadow
   @Nullable
   protected abstract PlayerInfo m_108558_();

   @Inject(
      method = {"m_108560_"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void m_108560_(CallbackInfoReturnable<ResourceLocation> cir) {
      PlayerInfo playerInfo = this.m_108558_();
      if (playerInfo != null) {
         cir.setReturnValue(TLSkinCape.getLocationSkin(playerInfo.m_105312_()));
      }

   }

   @Inject(
      method = {"m_108561_"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void m_108561_(CallbackInfoReturnable<ResourceLocation> cir) {
      PlayerInfo playerInfo = this.m_108558_();
      if (playerInfo != null) {
         cir.setReturnValue(TLSkinCape.getLocationCape(playerInfo.m_105312_()));
      }

   }

   @Inject(
      method = {"m_108563_"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void m_108563_(CallbackInfoReturnable<ResourceLocation> cir) {
      PlayerInfo playerInfo = this.m_108558_();
      if (playerInfo != null) {
         cir.setReturnValue(TLSkinCape.getLocationElytra(playerInfo.m_105312_()));
      }

   }

   @Inject(
      method = {"m_108564_"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void m_108564_(CallbackInfoReturnable<String> cir) {
      PlayerInfo playerInfo = this.m_108558_();
      if (playerInfo != null) {
         cir.setReturnValue(TLSkinCape.getSkinType(playerInfo.m_105312_()));
      }

   }
}
