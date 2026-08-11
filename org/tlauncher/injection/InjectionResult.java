package org.tlauncher.injection;

public enum InjectionResult {
   SUCCESS,
   TARGET_METHOD_NOT_FOUND,
   TARGET_CLASS_NOT_FOUND,
   FAILURE;

   // $FF: synthetic method
   private static InjectionResult[] $values() {
      return new InjectionResult[]{SUCCESS, TARGET_METHOD_NOT_FOUND, TARGET_CLASS_NOT_FOUND, FAILURE};
   }
}
