package org.tlauncher.launch;

import org.tlauncher.TLSkinCape;

public class Launch {
   public static void main(String[] args) {
      args = TLSkinCape.processMainArgs(args);
      net.minecraft.launchwrapper.Launch.main(args);
   }
}
