package org.tlauncher.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public class MixinPlayer {
   @Inject(
      method = {"isModelPartShown"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void isModelPartShown(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
      this.isWearing0(part, cir);
   }

   private void isWearing0(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
   }
}
