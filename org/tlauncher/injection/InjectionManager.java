package org.tlauncher.injection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InjectionManager {
   private static final List<Injection> EMPTY_INJECTION_LIST = new ArrayList();
   private final Map<String, List<Injection>> injectionMap = new HashMap();
   private final Map<String, InjectionHelper> injectionHelperMap = new HashMap();

   public void addInjection(String targetClassName, Injection injection) {
      this.injectionMap.putIfAbsent(targetClassName, new ArrayList());
      ((List)this.injectionMap.get(targetClassName)).add(injection);
      this.injectionHelperMap.putIfAbsent(targetClassName, new InjectionHelper());
   }

   public InjectionHelper getHelper(String targetClassName) {
      return (InjectionHelper)this.injectionHelperMap.get(targetClassName);
   }

   public List<Injection> getInjections(String targetClassName) {
      return (List)this.injectionMap.getOrDefault(targetClassName, EMPTY_INJECTION_LIST);
   }
}
