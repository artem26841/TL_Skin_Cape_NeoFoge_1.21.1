package org.tlauncher.model;

import org.tlauncher.minecraft.Resource;
import org.tlauncher.renderer.texture.FramedTexture;

public class ProfileTexture {
   private static final long REMOVAL_TIME_MILLS = 85000L;
   private Resource skin;
   private FramedTexture cape;
   private boolean capeReady;
   private long time;
   private String skinType;
   private boolean hasElytra;
   private boolean hasAnimatedElytra;

   public void updateTime() {
      this.setTime(System.currentTimeMillis() + 85000L);
   }

   public Resource getSkin() {
      return this.skin;
   }

   public FramedTexture getCape() {
      return this.cape;
   }

   public boolean isCapeReady() {
      return this.capeReady;
   }

   public long getTime() {
      return this.time;
   }

   public String getSkinType() {
      return this.skinType;
   }

   public boolean isHasElytra() {
      return this.hasElytra;
   }

   public boolean isHasAnimatedElytra() {
      return this.hasAnimatedElytra;
   }

   public void setSkin(Resource skin) {
      this.skin = skin;
   }

   public void setCape(FramedTexture cape) {
      this.cape = cape;
   }

   public void setCapeReady(boolean capeReady) {
      this.capeReady = capeReady;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public void setSkinType(String skinType) {
      this.skinType = skinType;
   }

   public void setHasElytra(boolean hasElytra) {
      this.hasElytra = hasElytra;
   }

   public void setHasAnimatedElytra(boolean hasAnimatedElytra) {
      this.hasAnimatedElytra = hasAnimatedElytra;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ProfileTexture)) {
         return false;
      } else {
         ProfileTexture other = (ProfileTexture)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.isCapeReady() != other.isCapeReady()) {
            return false;
         } else if (this.getTime() != other.getTime()) {
            return false;
         } else if (this.isHasElytra() != other.isHasElytra()) {
            return false;
         } else if (this.isHasAnimatedElytra() != other.isHasAnimatedElytra()) {
            return false;
         } else {
            Object this$skin = this.getSkin();
            Object other$skin = other.getSkin();
            if (this$skin == null) {
               if (other$skin != null) {
                  return false;
               }
            } else if (!this$skin.equals(other$skin)) {
               return false;
            }

            Object this$cape = this.getCape();
            Object other$cape = other.getCape();
            if (this$cape == null) {
               if (other$cape != null) {
                  return false;
               }
            } else if (!this$cape.equals(other$cape)) {
               return false;
            }

            Object this$skinType = this.getSkinType();
            Object other$skinType = other.getSkinType();
            if (this$skinType == null) {
               if (other$skinType != null) {
                  return false;
               }
            } else if (!this$skinType.equals(other$skinType)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof ProfileTexture;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isCapeReady() ? 79 : 97);
      long $time = this.getTime();
      result = result * 59 + (int)($time >>> 32 ^ $time);
      result = result * 59 + (this.isHasElytra() ? 79 : 97);
      result = result * 59 + (this.isHasAnimatedElytra() ? 79 : 97);
      Object $skin = this.getSkin();
      result = result * 59 + ($skin == null ? 43 : $skin.hashCode());
      Object $cape = this.getCape();
      result = result * 59 + ($cape == null ? 43 : $cape.hashCode());
      Object $skinType = this.getSkinType();
      result = result * 59 + ($skinType == null ? 43 : $skinType.hashCode());
      return result;
   }

   public String toString() {
      Resource var10000 = this.getSkin();
      return "ProfileTexture(skin=" + var10000 + ", cape=" + this.getCape() + ", capeReady=" + this.isCapeReady() + ", time=" + this.getTime() + ", skinType=" + this.getSkinType() + ", hasElytra=" + this.isHasElytra() + ", hasAnimatedElytra=" + this.isHasAnimatedElytra() + ")";
   }
}
