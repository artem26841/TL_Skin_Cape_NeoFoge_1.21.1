package org.tlauncher.model;

import java.util.Map;

public class MinecraftProfileTextureDTO {
   private String url;
   private Map<String, String> metadata;
   private Boolean animatedCape;
   private Boolean animatedElytra;
   private Integer capeHeight;
   private Integer capeWidth;
   private Integer fps;

   public MinecraftProfileTextureDTO() {
      this.animatedCape = Boolean.FALSE;
      this.animatedElytra = Boolean.FALSE;
   }

   public String getUrl() {
      return this.url;
   }

   public Map<String, String> getMetadata() {
      return this.metadata;
   }

   public Boolean getAnimatedCape() {
      return this.animatedCape;
   }

   public Boolean getAnimatedElytra() {
      return this.animatedElytra;
   }

   public Integer getCapeHeight() {
      return this.capeHeight;
   }

   public Integer getCapeWidth() {
      return this.capeWidth;
   }

   public Integer getFps() {
      return this.fps;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
   }

   public void setAnimatedCape(Boolean animatedCape) {
      this.animatedCape = animatedCape;
   }

   public void setAnimatedElytra(Boolean animatedElytra) {
      this.animatedElytra = animatedElytra;
   }

   public void setCapeHeight(Integer capeHeight) {
      this.capeHeight = capeHeight;
   }

   public void setCapeWidth(Integer capeWidth) {
      this.capeWidth = capeWidth;
   }

   public void setFps(Integer fps) {
      this.fps = fps;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MinecraftProfileTextureDTO)) {
         return false;
      } else {
         MinecraftProfileTextureDTO other = (MinecraftProfileTextureDTO)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$animatedCape = this.getAnimatedCape();
            Object other$animatedCape = other.getAnimatedCape();
            if (this$animatedCape == null) {
               if (other$animatedCape != null) {
                  return false;
               }
            } else if (!this$animatedCape.equals(other$animatedCape)) {
               return false;
            }

            Object this$animatedElytra = this.getAnimatedElytra();
            Object other$animatedElytra = other.getAnimatedElytra();
            if (this$animatedElytra == null) {
               if (other$animatedElytra != null) {
                  return false;
               }
            } else if (!this$animatedElytra.equals(other$animatedElytra)) {
               return false;
            }

            Object this$capeHeight = this.getCapeHeight();
            Object other$capeHeight = other.getCapeHeight();
            if (this$capeHeight == null) {
               if (other$capeHeight != null) {
                  return false;
               }
            } else if (!this$capeHeight.equals(other$capeHeight)) {
               return false;
            }

            Object this$capeWidth = this.getCapeWidth();
            Object other$capeWidth = other.getCapeWidth();
            if (this$capeWidth == null) {
               if (other$capeWidth != null) {
                  return false;
               }
            } else if (!this$capeWidth.equals(other$capeWidth)) {
               return false;
            }

            Object this$fps = this.getFps();
            Object other$fps = other.getFps();
            if (this$fps == null) {
               if (other$fps != null) {
                  return false;
               }
            } else if (!this$fps.equals(other$fps)) {
               return false;
            }

            Object this$url = this.getUrl();
            Object other$url = other.getUrl();
            if (this$url == null) {
               if (other$url != null) {
                  return false;
               }
            } else if (!this$url.equals(other$url)) {
               return false;
            }

            Object this$metadata = this.getMetadata();
            Object other$metadata = other.getMetadata();
            if (this$metadata == null) {
               if (other$metadata != null) {
                  return false;
               }
            } else if (!this$metadata.equals(other$metadata)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof MinecraftProfileTextureDTO;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $animatedCape = this.getAnimatedCape();
      result = result * 59 + ($animatedCape == null ? 43 : $animatedCape.hashCode());
      Object $animatedElytra = this.getAnimatedElytra();
      result = result * 59 + ($animatedElytra == null ? 43 : $animatedElytra.hashCode());
      Object $capeHeight = this.getCapeHeight();
      result = result * 59 + ($capeHeight == null ? 43 : $capeHeight.hashCode());
      Object $capeWidth = this.getCapeWidth();
      result = result * 59 + ($capeWidth == null ? 43 : $capeWidth.hashCode());
      Object $fps = this.getFps();
      result = result * 59 + ($fps == null ? 43 : $fps.hashCode());
      Object $url = this.getUrl();
      result = result * 59 + ($url == null ? 43 : $url.hashCode());
      Object $metadata = this.getMetadata();
      result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getUrl();
      return "MinecraftProfileTextureDTO(url=" + var10000 + ", metadata=" + this.getMetadata() + ", animatedCape=" + this.getAnimatedCape() + ", animatedElytra=" + this.getAnimatedElytra() + ", capeHeight=" + this.getCapeHeight() + ", capeWidth=" + this.getCapeWidth() + ", fps=" + this.getFps() + ")";
   }
}
