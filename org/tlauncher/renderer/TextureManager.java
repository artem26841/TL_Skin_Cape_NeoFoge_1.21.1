package org.tlauncher.renderer;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.tlauncher.TLSkinCape;
import org.tlauncher.minecraft.GameTextureManager;
import org.tlauncher.minecraft.Resource;
import org.tlauncher.model.MinecraftProfileTextureDTO;
import org.tlauncher.model.Performance;
import org.tlauncher.model.PerformanceTextureData;
import org.tlauncher.model.PlayerName;
import org.tlauncher.model.PreparedTextureData;
import org.tlauncher.model.ProfileTexture;
import org.tlauncher.renderer.image.ImageWrap;
import org.tlauncher.renderer.texture.FramedTexture;
import org.tlauncher.renderer.texture.LightTexture;
import org.tlauncher.util.MinecraftUtils;
import org.tlauncher.util.PreparedProfileManager;

public class TextureManager {
   private static final long TIME_GUP = 2000L;
   private static int SKIN_COUNTER;
   private final Map<PlayerName, ProfileTexture> textures = new HashMap();
   private PreparedProfileManager preparedProfileManager;
   private long updateTime;
   private long nextCleanTimeMills;
   private boolean calculatedElitraPerformance;
   private boolean calculatedCapePerformance;
   private final GameTextureManager gameTextureManager;

   public TextureManager(GameTextureManager gameTextureManager) {
      this.gameTextureManager = gameTextureManager;
   }

   private void cleanOldTextures() {
      if (this.nextCleanTimeMills < System.currentTimeMillis()) {
         long l = System.currentTimeMillis();
         List<PlayerName> removed = (List)this.textures.entrySet().stream().filter((e) -> l > ((ProfileTexture)e.getValue()).getTime()).map(Map.Entry::getKey).collect(Collectors.toList());
         PlayerName n = new PlayerName(MinecraftUtils.getSessionUsername());
         removed.remove(n);

         for(PlayerName s : removed) {
            ProfileTexture profileTexture = (ProfileTexture)this.textures.get(s);
            if (Objects.nonNull(profileTexture.getSkin())) {
               LightTexture texture = this.gameTextureManager.getTexture(profileTexture.getSkin());
               if (texture != null) {
                  texture.close();
               }

               this.gameTextureManager.deleteTexture(profileTexture.getSkin());
            }

            if (Objects.nonNull(profileTexture.getCape())) {
               for(Resource resourceLocation : ((ProfileTexture)this.textures.get(s)).getCape().getFrames()) {
                  LightTexture texture = this.gameTextureManager.getTexture(resourceLocation);
                  if (texture != null) {
                     texture.close();
                  }

                  this.gameTextureManager.deleteTexture(resourceLocation);
               }
            }
         }

         Map var10001 = this.textures;
         Objects.requireNonNull(var10001);
         removed.forEach(var10001::remove);
         TLSkinCape.LOGGER.trace(String.format("removed %s , during %s", removed.stream().map(PlayerName::getName).collect(Collectors.joining(",")), System.currentTimeMillis() - l));
         this.nextCleanTimeMills = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15L);
      }

   }

   public boolean isInit(PlayerName n, MinecraftProfileTexture.Type type) {
      ProfileTexture p = (ProfileTexture)this.textures.get(n);
      if (Objects.isNull(p)) {
         return false;
      } else {
         this.updateCache(n);
         switch (type) {
            case SKIN -> {
               return true;
            }
            case CAPE -> {
               return p.isCapeReady();
            }
            default -> {
               return false;
            }
         }
      }
   }

   private void updateCache(PlayerName p) {
      if (this.updateTime < System.currentTimeMillis()) {
         this.updateTime = System.currentTimeMillis() + 2000L;
         ProfileTexture profileTexture = (ProfileTexture)this.textures.get(p);
         if (Objects.nonNull(profileTexture)) {
            TLSkinCape.LOGGER.debug("updated cache value " + p);
            profileTexture.updateTime();
         }
      }

   }

   public void createFramedTextures() {
      long l = System.currentTimeMillis();
      this.cleanOldTextures();
      PreparedTextureData preparedTextureData = this.preparedProfileManager.peek();
      if (!Objects.isNull(preparedTextureData)) {
         PlayerName playerName = preparedTextureData.getName();
         ProfileTexture profile = (ProfileTexture)this.textures.get(playerName);

         try {
            ImageWrap imageWrap = (ImageWrap)preparedTextureData.getImages().get(Type.CAPE);
            if (Objects.isNull(profile)) {
               profile = new ProfileTexture();
               profile.updateTime();
               this.textures.put(playerName, profile);
               ImageWrap skin = (ImageWrap)preparedTextureData.getImages().get(Type.SKIN);
               Resource resourceLocation = null;
               if (Objects.nonNull(skin)) {
                  resourceLocation = new Resource(String.format("dynamic/skin_%s", SKIN_COUNTER++));
                  LightTexture textureObject = new LightTexture(skin);
                  this.gameTextureManager.loadTexture(resourceLocation, textureObject);
                  Map<String, String> map = ((MinecraftProfileTextureDTO)preparedTextureData.getProfileTextureDTO().get(Type.SKIN)).getMetadata();
                  if (Objects.nonNull(map)) {
                     profile.setSkinType((String)map.get("model"));
                  }
               }

               profile.setSkin(resourceLocation);
               if (Objects.nonNull(imageWrap)) {
                  return;
               }
            }

            MinecraftProfileTextureDTO p = (MinecraftProfileTextureDTO)preparedTextureData.getProfileTextureDTO().get(Type.CAPE);
            if (Objects.nonNull(p) && !preparedTextureData.getCapeFrames().isEmpty()) {
               if (Objects.equals(p.getAnimatedCape(), true)) {
                  FramedTexture cape = profile.getCape();
                  if (Objects.isNull(cape)) {
                     int fps = p.getFps() > 0 ? p.getFps() : 1;
                     cape = new FramedTexture(p.getCapeHeight(), 1000000000L / (long)fps, true);
                     profile.setCape(cape);
                  }

                  if (!this.fillCape(preparedTextureData, cape)) {
                     return;
                  }
               } else {
                  profile.setCape(FramedTexture.createOneFramedTexture(this.gameTextureManager, (ImageWrap)preparedTextureData.getCapeFrames().get(0)));
               }
            }
         } catch (Throwable e) {
            TLSkinCape.LOGGER.error("error", e);
            e.printStackTrace();
         }

         if (profile.getSkinType() == null) {
            profile.setSkinType("undefined");
         }

         profile.setHasElytra(preparedTextureData.isHasElytra());
         profile.setHasAnimatedElytra(preparedTextureData.isHasAnimatedElytra());
         preparedTextureData.setMaxTimeLoad(System.currentTimeMillis() - l);
         this.setReady(playerName, profile);
         this.preparedProfileManager.poll();
         String log1 = String.format("textures '%s' was added, skin: %s,cape: %s, max waiting: %s ,during : %s ", playerName.getDisplayName(), Objects.nonNull(profile.getSkin()), Objects.nonNull(profile.getCape()), preparedTextureData.getMaxTimeLoad(), System.currentTimeMillis() - preparedTextureData.getInitTime());
         if (preparedTextureData.getMaxTimeLoad() > 15L) {
            TLSkinCape.LOGGER.info(log1);
         } else {
            TLSkinCape.LOGGER.debug(log1);
         }

         this.defineProperPerformance(preparedTextureData);
      }
   }

   private void defineProperPerformance(PreparedTextureData t) {
      if (!this.calculatedCapePerformance && t instanceof PerformanceTextureData t1 && t.getName().getName().equals(PreparedProfileManager.CAPE_TEST_272)) {
         double time = t1.getMiddleInitFrameTime();
         if (time > PreparedProfileManager.MAX_TIME_LOAD_FRAMES) {
            this.preparedProfileManager.setAnimatedCapePerformance(Performance.TEXTURE_136);
         }

         TLSkinCape.LOGGER.info("calculated cape performance:{}, middle time: {}", this.preparedProfileManager.getAnimatedCapePerformance(), time);
         this.calculatedCapePerformance = true;
      } else if (!this.calculatedElitraPerformance && t instanceof PerformanceTextureData t1 && t.getName().getName().equals(PreparedProfileManager.ELITRA_TEST_272)) {
         double time = t1.getMiddleInitFrameTime();
         if (time > PreparedProfileManager.MAX_TIME_LOAD_FRAMES) {
            this.preparedProfileManager.setAnimatedElitraPerformance(Performance.TEXTURE_136);
         }

         TLSkinCape.LOGGER.info("can use texture size:{}, middle time: {}", this.preparedProfileManager.getAnimatedElitraPerformance(), time);
         this.calculatedElitraPerformance = true;
      }

   }

   private boolean fillCape(PreparedTextureData preparedTextureData, FramedTexture cape) {
      int size = preparedTextureData.getCapeFrames().size();
      long current = System.currentTimeMillis();

      for(int i = 1; i > 0 && cape.getFrames().size() < size; --i) {
         int index = cape.getFrames().size();
         cape.initByOneImage(this.gameTextureManager, (ImageWrap)preparedTextureData.getCapeFrames().get(index));
      }

      long c = System.currentTimeMillis() - current;
      preparedTextureData.setMaxTimeLoad(c);
      if ((!this.calculatedCapePerformance || !this.calculatedElitraPerformance) && preparedTextureData instanceof PerformanceTextureData) {
         ((PerformanceTextureData)preparedTextureData).getInitFrameTime().add(c);
      }

      return size == cape.getFrames().size();
   }

   private void setReady(PlayerName playerName, ProfileTexture profile) {
      profile.setCapeReady(true);
      this.preparedProfileManager.removeByName(playerName);
   }

   public void setPreparedProfileManager(PreparedProfileManager preparedProfileManager) {
      this.preparedProfileManager = preparedProfileManager;
   }

   public ProfileTexture get(PlayerName username) {
      return (ProfileTexture)this.textures.get(username);
   }
}
