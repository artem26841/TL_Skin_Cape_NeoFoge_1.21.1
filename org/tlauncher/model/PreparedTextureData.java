package org.tlauncher.model;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tlauncher.renderer.image.ImageWrap;

public class PreparedTextureData {
   private PlayerName name;
   private volatile Map<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> profileTextureDTO = new HashMap();
   private Map<MinecraftProfileTexture.Type, ImageWrap> images = Collections.synchronizedMap(new HashMap());
   private List<ImageWrap> capeFrames = new ArrayList();
   private ImageWrap skin;
   private int preparedIndexFrame;
   private long maxTimeLoad;
   private long initTime = System.currentTimeMillis();
   private boolean hasElytra;
   private boolean hasAnimatedElytra;

   public boolean hasFrame() {
      return this.preparedIndexFrame < this.capeFrames.size();
   }

   public int getPreparedIndexFrameAndIncrease() {
      return this.preparedIndexFrame++;
   }

   public void setMaxTimeLoad(long maxTimeLoad) {
      if (this.maxTimeLoad < maxTimeLoad) {
         this.maxTimeLoad = maxTimeLoad;
      }

   }

   public PlayerName getName() {
      return this.name;
   }

   public Map<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> getProfileTextureDTO() {
      return this.profileTextureDTO;
   }

   public Map<MinecraftProfileTexture.Type, ImageWrap> getImages() {
      return this.images;
   }

   public List<ImageWrap> getCapeFrames() {
      return this.capeFrames;
   }

   public ImageWrap getSkin() {
      return this.skin;
   }

   public int getPreparedIndexFrame() {
      return this.preparedIndexFrame;
   }

   public long getMaxTimeLoad() {
      return this.maxTimeLoad;
   }

   public long getInitTime() {
      return this.initTime;
   }

   public boolean isHasElytra() {
      return this.hasElytra;
   }

   public boolean isHasAnimatedElytra() {
      return this.hasAnimatedElytra;
   }

   public void setName(PlayerName name) {
      this.name = name;
   }

   public void setProfileTextureDTO(Map<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> profileTextureDTO) {
      this.profileTextureDTO = profileTextureDTO;
   }

   public void setImages(Map<MinecraftProfileTexture.Type, ImageWrap> images) {
      this.images = images;
   }

   public void setCapeFrames(List<ImageWrap> capeFrames) {
      this.capeFrames = capeFrames;
   }

   public void setSkin(ImageWrap skin) {
      this.skin = skin;
   }

   public void setPreparedIndexFrame(int preparedIndexFrame) {
      this.preparedIndexFrame = preparedIndexFrame;
   }

   public void setInitTime(long initTime) {
      this.initTime = initTime;
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
      } else if (!(o instanceof PreparedTextureData)) {
         return false;
      } else {
         PreparedTextureData other = (PreparedTextureData)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.getPreparedIndexFrame() != other.getPreparedIndexFrame()) {
            return false;
         } else if (this.getMaxTimeLoad() != other.getMaxTimeLoad()) {
            return false;
         } else if (this.getInitTime() != other.getInitTime()) {
            return false;
         } else if (this.isHasElytra() != other.isHasElytra()) {
            return false;
         } else if (this.isHasAnimatedElytra() != other.isHasAnimatedElytra()) {
            return false;
         } else {
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
               if (other$name != null) {
                  return false;
               }
            } else if (!this$name.equals(other$name)) {
               return false;
            }

            Object this$profileTextureDTO = this.getProfileTextureDTO();
            Object other$profileTextureDTO = other.getProfileTextureDTO();
            if (this$profileTextureDTO == null) {
               if (other$profileTextureDTO != null) {
                  return false;
               }
            } else if (!this$profileTextureDTO.equals(other$profileTextureDTO)) {
               return false;
            }

            Object this$images = this.getImages();
            Object other$images = other.getImages();
            if (this$images == null) {
               if (other$images != null) {
                  return false;
               }
            } else if (!this$images.equals(other$images)) {
               return false;
            }

            Object this$capeFrames = this.getCapeFrames();
            Object other$capeFrames = other.getCapeFrames();
            if (this$capeFrames == null) {
               if (other$capeFrames != null) {
                  return false;
               }
            } else if (!this$capeFrames.equals(other$capeFrames)) {
               return false;
            }

            Object this$skin = this.getSkin();
            Object other$skin = other.getSkin();
            if (this$skin == null) {
               if (other$skin != null) {
                  return false;
               }
            } else if (!this$skin.equals(other$skin)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof PreparedTextureData;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getPreparedIndexFrame();
      long $maxTimeLoad = this.getMaxTimeLoad();
      result = result * 59 + (int)($maxTimeLoad >>> 32 ^ $maxTimeLoad);
      long $initTime = this.getInitTime();
      result = result * 59 + (int)($initTime >>> 32 ^ $initTime);
      result = result * 59 + (this.isHasElytra() ? 79 : 97);
      result = result * 59 + (this.isHasAnimatedElytra() ? 79 : 97);
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $profileTextureDTO = this.getProfileTextureDTO();
      result = result * 59 + ($profileTextureDTO == null ? 43 : $profileTextureDTO.hashCode());
      Object $images = this.getImages();
      result = result * 59 + ($images == null ? 43 : $images.hashCode());
      Object $capeFrames = this.getCapeFrames();
      result = result * 59 + ($capeFrames == null ? 43 : $capeFrames.hashCode());
      Object $skin = this.getSkin();
      result = result * 59 + ($skin == null ? 43 : $skin.hashCode());
      return result;
   }

   public String toString() {
      PlayerName var10000 = this.getName();
      return "PreparedTextureData(name=" + var10000 + ", profileTextureDTO=" + this.getProfileTextureDTO() + ", images=" + this.getImages() + ", capeFrames=" + this.getCapeFrames() + ", skin=" + this.getSkin() + ", preparedIndexFrame=" + this.getPreparedIndexFrame() + ", maxTimeLoad=" + this.getMaxTimeLoad() + ", initTime=" + this.getInitTime() + ", hasElytra=" + this.isHasElytra() + ", hasAnimatedElytra=" + this.isHasAnimatedElytra() + ")";
   }
}
