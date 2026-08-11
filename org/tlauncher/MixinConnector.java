package org.tlauncher;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
   public void connect() {
      Mixins.addConfiguration("tlskincape-mixins.json");
   }
}
