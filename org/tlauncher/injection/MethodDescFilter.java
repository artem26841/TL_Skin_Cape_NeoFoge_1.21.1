package org.tlauncher.injection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MethodDescFilter {
   private final String startsWith;
   private final String endsWith;
   private final List<String> containsList;
   private final String desc;

   public boolean test(String desc) {
      boolean b = true;
      if (this.startsWith != null) {
         b = desc.startsWith(this.startsWith);
      }

      if (this.endsWith != null) {
         b = desc.endsWith(this.endsWith);
      }

      if (this.desc != null) {
         b = this.desc.equals(desc);
      }

      if (this.containsList != null) {
         for(String s : this.containsList) {
            if (b) {
               b = desc.contains(s);
            }
         }
      }

      return b;
   }

   public static MethodDescFilter empty() {
      return new MethodDescFilter((String)null, (String)null, (List)null, (String)null);
   }

   MethodDescFilter(String startsWith, String endsWith, List<String> containsList, String desc) {
      this.startsWith = startsWith;
      this.endsWith = endsWith;
      this.containsList = containsList;
      this.desc = desc;
   }

   public static MethodDescFilterBuilder builder() {
      return new MethodDescFilterBuilder();
   }

   public static class MethodDescFilterBuilder {
      private String startsWith;
      private String endsWith;
      private ArrayList<String> containsList;
      private String desc;

      MethodDescFilterBuilder() {
      }

      public MethodDescFilterBuilder startsWith(String startsWith) {
         this.startsWith = startsWith;
         return this;
      }

      public MethodDescFilterBuilder endsWith(String endsWith) {
         this.endsWith = endsWith;
         return this;
      }

      public MethodDescFilterBuilder contains(String contains) {
         if (this.containsList == null) {
            this.containsList = new ArrayList();
         }

         this.containsList.add(contains);
         return this;
      }

      public MethodDescFilterBuilder containsList(Collection<? extends String> containsList) {
         if (containsList == null) {
            throw new NullPointerException("containsList cannot be null");
         } else {
            if (this.containsList == null) {
               this.containsList = new ArrayList();
            }

            this.containsList.addAll(containsList);
            return this;
         }
      }

      public MethodDescFilterBuilder clearContainsList() {
         if (this.containsList != null) {
            this.containsList.clear();
         }

         return this;
      }

      public MethodDescFilterBuilder desc(String desc) {
         this.desc = desc;
         return this;
      }

      public MethodDescFilter build() {
         List<String> containsList;
         switch (this.containsList == null ? 0 : this.containsList.size()) {
            case 0 -> containsList = Collections.emptyList();
            case 1 -> containsList = Collections.singletonList((String)this.containsList.get(0));
            default -> containsList = Collections.unmodifiableList(new ArrayList(this.containsList));
         }

         return new MethodDescFilter(this.startsWith, this.endsWith, containsList, this.desc);
      }

      public String toString() {
         return "MethodDescFilter.MethodDescFilterBuilder(startsWith=" + this.startsWith + ", endsWith=" + this.endsWith + ", containsList=" + this.containsList + ", desc=" + this.desc + ")";
      }
   }
}
