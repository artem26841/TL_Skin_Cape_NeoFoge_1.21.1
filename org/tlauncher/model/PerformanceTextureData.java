package org.tlauncher.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PerformanceTextureData extends PreparedTextureData {
   private List<Long> initFrameTime = new ArrayList();

   public double getMiddleInitFrameTime() {
      return (Double)this.initFrameTime.stream().collect(Collectors.averagingLong(Long::longValue));
   }

   public List<Long> getInitFrameTime() {
      return this.initFrameTime;
   }

   public void setInitFrameTime(List<Long> initFrameTime) {
      this.initFrameTime = initFrameTime;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PerformanceTextureData)) {
         return false;
      } else {
         PerformanceTextureData other = (PerformanceTextureData)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$initFrameTime = this.getInitFrameTime();
            Object other$initFrameTime = other.getInitFrameTime();
            if (this$initFrameTime == null) {
               if (other$initFrameTime != null) {
                  return false;
               }
            } else if (!this$initFrameTime.equals(other$initFrameTime)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof PerformanceTextureData;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $initFrameTime = this.getInitFrameTime();
      result = result * 59 + ($initFrameTime == null ? 43 : $initFrameTime.hashCode());
      return result;
   }

   public String toString() {
      return "PerformanceTextureData(initFrameTime=" + this.getInitFrameTime() + ")";
   }
}
