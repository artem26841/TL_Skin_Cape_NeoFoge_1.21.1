package org.tlauncher.util;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.tlauncher.TLSkinCape;
import org.tlauncher.model.MinecraftProfileTextureDTO;
import org.tlauncher.model.MinecraftTexturesPayload1;
import org.tlauncher.model.Performance;
import org.tlauncher.model.PerformanceTextureData;
import org.tlauncher.model.PlayerName;
import org.tlauncher.model.PreparedTextureData;
import org.tlauncher.renderer.image.ImageWrap;
import org.tlauncher.renderer.image.ImageWrapFactory;

public class PreparedProfileManager {
   public static String ELITRA_TEST_272 = "elitra_test_272";
   public static String CAPE_TEST_272 = "cape_test_272";
   private final ExecutorService pool = Executors.newFixedThreadPool(1, (new ThreadFactoryBuilder()).setDaemon(true).build());
   private final Set<PlayerName> addedNames = new HashSet();
   private final LinkedBlockingQueue<PreparedTextureData> preparedData = new LinkedBlockingQueue();
   public static double MAX_TIME_LOAD_FRAMES = (double)10.0F;
   private Performance animatedElitraPerformance;
   private Performance animatedCapePerformance;

   public PreparedProfileManager() {
      this.animatedElitraPerformance = Performance.TEXTURE_272;
      this.animatedCapePerformance = Performance.TEXTURE_272;
      this.addPerformanceTexture();
   }

   private void addPerformanceTexture() {
      try {
         this.addTestTexture(false, CAPE_TEST_272);
         this.addTestTexture(true, ELITRA_TEST_272);
      } catch (Exception t) {
         TLSkinCape.LOGGER.error("couldn't calculate size of the cape", t);
      }

   }

   private void addTestTexture(boolean elitraCape, String name) {
      PreparedTextureData texture = new PerformanceTextureData();
      final MinecraftProfileTextureDTO test = new MinecraftProfileTextureDTO();
      texture.setName(new PlayerName(name));
      texture.setHasElytra(elitraCape);
      texture.setHasAnimatedElytra(elitraCape);
      test.setAnimatedElytra(elitraCape);
      test.setCapeHeight(272);
      test.setAnimatedCape(true);
      test.setFps(10);
      final ImageWrap nativeImage = ImageWrapFactory.create(PreparedProfileManager.class.getResourceAsStream(name + ".png"));
      texture.setImages(new HashMap<MinecraftProfileTexture.Type, ImageWrap>() {
         {
            this.put(Type.CAPE, nativeImage);
         }
      });
      texture.setProfileTextureDTO(new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO>() {
         {
            this.put(Type.CAPE, test);
         }
      });
      this.preparedFramesForCape(texture);
      this.preparedData.add(texture);
   }

   private void fillGameProfileData(PlayerName name, GameProfile profile) {
      Property textureProperty = (Property)Iterables.getFirst(profile.getProperties().get("textures"), (Object)null);
      if (textureProperty != null) {
         try {
            String json = new String(Base64.decodeBase64(textureProperty.getValue()), StandardCharsets.UTF_8);
            Gson g = new Gson();
            MinecraftTexturesPayload1 result = (MinecraftTexturesPayload1)g.fromJson(json, MinecraftTexturesPayload1.class);
            name.fill(result);
         } catch (JsonParseException e) {
            TLSkinCape.LOGGER.warn("Could not decode textures payload", e);
         }

      }
   }

   public void addNewName(PlayerName p) {
      if (!this.addedNames.contains(p)) {
         this.addedNames.add(p);
         this.loadByName(p);
      }

   }

   private void loadByName(PlayerName p) {
      CompletableFuture.runAsync(() -> {
         PreparedTextureData texture = new PreparedTextureData();
         texture.setName(p);

         try {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> profile = this.loadProfileData0(p);
            this.fillIfEmptyWithNativeGameProfileTextures(profile, p);
            texture.setProfileTextureDTO(profile);
            this.downloadAndValidateImage(profile, texture);
            this.preparedFramesForCape(texture);
            this.preparedFrameForSkin(texture);
            org.apache.logging.log4j.Logger var10000 = TLSkinCape.LOGGER;
            String var10001 = p.getName();
            var10000.debug("finished downloading data " + var10001 + " " + p.getDisplayName());
         } catch (Exception io) {
            TLSkinCape.LOGGER.warn("error " + p.toString(), io);
         }

         this.preparedData.add(texture);
      }, this.pool);
   }

   private void fillIfEmptyWithNativeGameProfileTextures(Map<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> profile, PlayerName p) {
      if (profile.isEmpty() && Objects.nonNull(p.getNativeTextures())) {
         p.getNativeTextures().forEach((k, v) -> {
            MinecraftProfileTextureDTO m = new MinecraftProfileTextureDTO();
            m.setUrl(v.getUrl());
            profile.put(k, m);
         });
      }

   }

   private void preparedFrameForSkin(PreparedTextureData texture) {
      ImageWrap imageWrap = (ImageWrap)texture.getImages().get(Type.SKIN);
      if (Objects.nonNull(imageWrap)) {
         texture.setSkin(imageWrap);
      }

   }

   private void preparedFramesForCape(PreparedTextureData texture) {
      MinecraftProfileTextureDTO p = (MinecraftProfileTextureDTO)texture.getProfileTextureDTO().get(Type.CAPE);
      if (!Objects.isNull(p) && !Objects.isNull(texture.getImages().get(Type.CAPE))) {
         if (!Objects.equals(p.getAnimatedCape(), true)) {
            ImageWrap imageWrap = (ImageWrap)texture.getImages().get(Type.CAPE);
            p.setCapeWidth(imageWrap.getWidth());
            p.setCapeHeight(imageWrap.getHeight());
            texture.setHasElytra(this.hasElytra(texture, p));
            texture.getCapeFrames().add(imageWrap);
         } else {
            for(ImageWrap imageWrap : this.cutAnimatedCape(texture, p)) {
               texture.getCapeFrames().add(imageWrap);
            }

         }
      }
   }

   private List<ImageWrap> cutAnimatedCape(PreparedTextureData texture, MinecraftProfileTextureDTO p) {
      ImageWrap imageWrap = (ImageWrap)texture.getImages().get(Type.CAPE);

      try {
         boolean animatedElytra = Objects.equals(p.getAnimatedElytra(), true);
         int frameHeight = animatedElytra ? p.getCapeHeight() / 17 * 32 : p.getCapeHeight();
         int frameWidth = animatedElytra ? p.getCapeHeight() / 17 * 64 : p.getCapeHeight() / 17 * 22;
         p.setCapeWidth(frameWidth);
         int xFrames = imageWrap.getWidth() / frameWidth;
         int yFrames = imageWrap.getHeight() / frameHeight;
         texture.setHasElytra(animatedElytra);
         texture.setHasAnimatedElytra(animatedElytra);
         List<ImageWrap> list = new ArrayList();

         for(int j = 0; j < xFrames; ++j) {
            for(int i = 0; i < yFrames; ++i) {
               ImageWrap imageWrap1;
               if (animatedElytra) {
                  imageWrap1 = ImageWrapFactory.create(frameWidth, frameHeight);
               } else {
                  imageWrap1 = ImageWrapFactory.create(frameWidth / 22 * 64, frameHeight / 17 * 32);
               }

               boolean flag = false;

               for(int y = 0; y < frameHeight; ++y) {
                  for(int x = 0; x < frameWidth; ++x) {
                     int color = -1;

                     try {
                        color = imageWrap.getRGB(j * frameWidth + x, i * frameHeight + y);
                        if (!flag) {
                           int alpha = color >>> 24 & 255;
                           if (alpha != 0) {
                              flag = true;
                           }
                        }

                        imageWrap1.setRGB(x, y, color);
                     } catch (ArrayIndexOutOfBoundsException e) {
                        TLSkinCape.LOGGER.error("Coordinates: x " + x + " y " + y + " color " + color, e);
                     }
                  }
               }

               if (!flag) {
                  Object var24 = list;
                  return (List<ImageWrap>)var24;
               }

               list.add(imageWrap1);
            }
         }

         Object var23 = list;
         return (List<ImageWrap>)var23;
      } finally {
         imageWrap.close();
      }
   }

   private boolean hasElytra(PreparedTextureData preparedTextureData, MinecraftProfileTextureDTO profileTextureDTO) {
      if (Objects.equals(profileTextureDTO.getAnimatedElytra(), true)) {
         return true;
      } else {
         ImageWrap cape = (ImageWrap)preparedTextureData.getImages().get(Type.CAPE);

         for(int y = 0; y < cape.getHeight(); ++y) {
            for(int x = cape.getWidth() / 2; x < cape.getWidth(); ++x) {
               int color = cape.getRGB(x, y);
               int alpha = color >>> 24 & 255;
               if (alpha != 0) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void downloadAndValidateImage(Map<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> profile, PreparedTextureData texture) throws IOException {
      for(Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> entry : profile.entrySet()) {
         BufferedImage image = this.getImage(entry);
         if (((MinecraftProfileTexture.Type)entry.getKey()).equals(Type.SKIN)) {
            image = TextureUtils.getRightSkin(image);
         }

         if (image != null) {
            boolean isCape = ((MinecraftProfileTexture.Type)entry.getKey()).equals(Type.CAPE);
            if (!isCape || texture.isHasElytra() || image.getWidth() % 22 == 0 && image.getHeight() % 17 == 0 || image.getWidth() % 64 == 0 && image.getHeight() % 32 == 0) {
               if (isCape && (((MinecraftProfileTextureDTO)entry.getValue()).getAnimatedElytra() && this.animatedElitraPerformance.equals(Performance.TEXTURE_136) || ((MinecraftProfileTextureDTO)entry.getValue()).getAnimatedCape() && this.animatedCapePerformance.equals(Performance.TEXTURE_136))) {
                  MinecraftProfileTextureDTO texture1 = (MinecraftProfileTextureDTO)profile.get(Type.CAPE);
                  if (Objects.equals(texture1.getAnimatedCape(), true) && texture1.getCapeHeight() > 200) {
                     int resizeValue = 2;
                     texture1.setCapeHeight(texture1.getCapeHeight() / resizeValue);
                     image = TextureUtils.resize(image, image.getWidth() / resizeValue, image.getHeight() / resizeValue);
                     TLSkinCape.LOGGER.debug("decreased animated texture {}", texture.getName());
                  }
               }

               ImageWrap iImageWrap = ImageWrapFactory.create(image);
               texture.getImages().put((MinecraftProfileTexture.Type)entry.getKey(), iImageWrap);
            } else {
               TLSkinCape.LOGGER.warn("not proper cape should multiple 22x17 or 64x32, w={}, h={}", image.getWidth(), image.getHeight());
            }
         }
      }

   }

   private BufferedImage getImage(Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> entry) throws IOException {
      IOException exception = new IOException("can't load image");

      for(int i = 0; i < 3; ++i) {
         HttpURLConnection c = null;

         try {
            c = (HttpURLConnection)(new URL(((MinecraftProfileTextureDTO)entry.getValue()).getUrl())).openConnection();
            c.setConnectTimeout(60000);
            c.setReadTimeout(60000);
            BufferedImage var5 = ImageIO.read(c.getInputStream());
            return var5;
         } catch (IOException io) {
            if (Objects.nonNull(c) && (c.getResponseCode() == 503 || c.getResponseCode() == 404)) {
               throw new IOException(String.format("code %s", c.getResponseCode()));
            }

            exception = io;

            try {
               Thread.sleep(2000L);
            } catch (InterruptedException var11) {
            }
         } finally {
            if (Objects.nonNull(c)) {
               IOUtils.closeQuietly(c.getInputStream());
            }

         }
      }

      throw exception;
   }

   private Map<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> loadProfileData0(PlayerName playerName) throws IOException {
      IOException exception = new IOException("can't download model");

      for(int i = 0; i < 3; ++i) {
         try {
            return this.loadProfileData(playerName.getName());
         } catch (IOException e) {
            exception = e;

            try {
               Thread.sleep(2000L);
            } catch (InterruptedException var6) {
            }
         }
      }

      throw exception;
   }

   public void addNewName(GameProfile p, PlayerName name) {
      if (!this.addedNames.contains(name)) {
         this.addedNames.add(name);
         this.fillGameProfileData(name, p);
         this.loadByName(name);
      }

   }

   private Map<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO> loadProfileData(String playerName) throws IOException {
      Gson gson = new Gson();
      if (Objects.nonNull(System.getProperty("memoryLeakTest"))) {
         playerName = "rob6";
      }

      playerName = URLEncoder.encode(playerName, "UTF-8");
      URL url = new URL(String.format("http://auth.tlauncher.org/skin/profile/texture/login/%s", playerName));
      URLConnection urlConnection = url.openConnection();
      urlConnection.setConnectTimeout(60000);
      urlConnection.setReadTimeout(60000);
      StringWriter writer = new StringWriter();
      InputStream inputStream = urlConnection.getInputStream();

      try {
         IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
      } catch (Throwable var10) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      String response = writer.toString();
      return (Map)gson.fromJson(response, (new TypeToken<HashMap<MinecraftProfileTexture.Type, MinecraftProfileTextureDTO>>() {
      }).getType());
   }

   public PreparedTextureData peek() {
      return (PreparedTextureData)this.preparedData.peek();
   }

   public void poll() {
      this.preparedData.poll();
   }

   public void removeByName(PlayerName name) {
      this.addedNames.remove(name);
   }

   public Performance getAnimatedElitraPerformance() {
      return this.animatedElitraPerformance;
   }

   public void setAnimatedElitraPerformance(Performance animatedElitraPerformance) {
      this.animatedElitraPerformance = animatedElitraPerformance;
   }

   public Performance getAnimatedCapePerformance() {
      return this.animatedCapePerformance;
   }

   public void setAnimatedCapePerformance(Performance animatedCapePerformance) {
      this.animatedCapePerformance = animatedCapePerformance;
   }
}
