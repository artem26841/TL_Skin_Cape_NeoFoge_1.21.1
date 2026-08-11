package org.tlauncher;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import org.tlauncher.connector.Connector;
import org.tlauncher.injection.mapping.MappingManager;
import org.tlauncher.injection.mapping.ObfClass;
import org.tlauncher.minecraft.GameTextureManager;
import org.tlauncher.minecraft.GameTextureManagerImpl;
import org.tlauncher.model.PlayerName;
import org.tlauncher.model.ProfileTexture;
import org.tlauncher.renderer.TextureManager;
import org.tlauncher.renderer.texture.FramedTexture;
import org.tlauncher.tweaker.Tweaker;
import org.tlauncher.util.PreparedProfileManager;
import org.tlauncher.util.ServerConnector;
import org.tlauncher.util.TypeLocator;
import org.tlauncher.util.reflection.ReflectionUtils;

public class TLSkinCape implements TypeLocator {
   public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
   private static final TLSkinCape INSTANCE = new TLSkinCape();
   public static Minecraft MINECRAFT_INSTANCE;
   private final PreparedProfileManager preparedProfileManager = new PreparedProfileManager();
   private long nextTimeMills;
   private final GameTextureManager gameTextureManager = new GameTextureManagerImpl(() -> MINECRAFT_INSTANCE);
   private final TextureManager textureManager;
   private final ServerConnector serverConnector = new ServerConnector();

   public TLSkinCape() {
      this.textureManager = new TextureManager(this.gameTextureManager);
      this.textureManager.setPreparedProfileManager(this.preparedProfileManager);
      if (Objects.nonNull(System.getProperty("memoryLeakTest"))) {
         this.testMemoryLeak();
      }

   }

   public static String getSkinType(GameProfile gameProfile) {
      PlayerName p = new PlayerName(gameProfile);
      ProfileTexture profileTexture = INSTANCE.getTextureManager().get(p);
      if (Objects.nonNull(profileTexture)) {
         String skinType = profileTexture.getSkinType();
         if ("slim".equals(profileTexture.getSkinType())) {
            return skinType;
         }
      }

      return "default";
   }

   public static ResourceLocation getLocationCape(GameProfile gameProfile) {
      PlayerName p = new PlayerName(gameProfile);
      if (INSTANCE.getTextureManager().isInit(p, Type.CAPE)) {
         FramedTexture framedTexture = INSTANCE.getTextureManager().get(p).getCape();
         if (Objects.nonNull(framedTexture)) {
            return ReflectionUtils.isElytraEquipped(MINECRAFT_INSTANCE.getClass().getClassLoader()) ? null : framedTexture.getFrame();
         }
      } else {
         INSTANCE.createTexture(gameProfile, p);
      }

      return null;
   }

   public static ResourceLocation getLocationElytra(GameProfile gameProfile) {
      PlayerName p = new PlayerName(gameProfile);
      if (INSTANCE.getTextureManager().isInit(p, Type.CAPE)) {
         ProfileTexture profileTexture = INSTANCE.getTextureManager().get(p);
         if (profileTexture.isHasElytra() && Objects.nonNull(profileTexture.getCape())) {
            if (profileTexture.isHasAnimatedElytra()) {
               return profileTexture.getCape().getFrame();
            }

            return profileTexture.getCape().getFirstFrame();
         }
      } else {
         INSTANCE.createTexture(gameProfile, p);
      }

      return null;
   }

   public static ResourceLocation getLocationSkin(GameProfile gameProfile) {
      if (MINECRAFT_INSTANCE == null) {
         MINECRAFT_INSTANCE = Minecraft.m_91087_();
      }

      PlayerName p = new PlayerName(gameProfile);
      if (INSTANCE.getTextureManager().isInit(p, Type.SKIN)) {
         ResourceLocation resourceLocation = INSTANCE.getTextureManager().get(p).getSkin();
         if (Objects.nonNull(resourceLocation)) {
            return resourceLocation;
         }
      } else {
         INSTANCE.createTexture(gameProfile, p);
      }

      return DefaultPlayerSkin.m_118626_();
   }

   public static <T extends Enum<?>> boolean isWearing(boolean isWearing, T part) {
      return isWearing && isCapePart(part);
   }

   public static <T extends Enum<?>> boolean isCapePart(T part) {
      return part.name().equals("CAPE") && !ReflectionUtils.isElytraEquipped(part.getClass().getClassLoader());
   }

   public static void onMainMenuRender() {
      ReflectionUtils.forceUpdate();
      INSTANCE.serverConnector.tryToConnect();
   }

   public static String[] processMainArgs(String[] args) {
      List<String> list = new LinkedList(Arrays.asList(args));
      Iterator<String> iterator = list.iterator();

      while(iterator.hasNext()) {
         switch ((String)iterator.next()) {
            case "--server":
               iterator.remove();
               Tweaker.serverName = (String)iterator.next();
               iterator.remove();
               break;
            case "--port":
               iterator.remove();
               Tweaker.serverPort = Integer.parseInt((String)iterator.next());
               iterator.remove();
               break;
            case "--disable_tl_skin_cape":
               iterator.remove();
               Tweaker.isTLSkinCapeEnabled = false;
               LOGGER.info("TLSkinCape disabled.");
               break;
            case "--tl_old_wearing":
               iterator.remove();
               Tweaker.useOldWearing = true;
               LOGGER.info("TLSkinCape uses the old wearing hook.");
               break;
            case "--tl_disable_connector":
               iterator.remove();
               Tweaker.isConnectorEnabled = false;
         }
      }

      String[] ss = new String[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         ss[i] = (String)list.get(i);
      }

      return ss;
   }

   public static void startConnector(Minecraft minecraft) {
      if (!Tweaker.isConnectorStarted && Tweaker.isConnectorEnabled && Tweaker.serverName != null) {
         Tweaker.isConnectorStarted = true;
         System.out.println("[Connector] Init connector...");
         ObfClass minecraftClass = MappingManager.instance().getMappings().getClass("Minecraft");
         String debugRendererObjName = minecraftClass.getField("debugRenderer").getObfName();
         String queueName = minecraftClass.getField("progressTasks").getObfName();
         String taskMethodName = minecraftClass.getMethod("addScheduledTask").getObfName();
         CompletableFuture.runAsync(() -> {
            while(true) {
               try {
                  Field field = Minecraft.class.getDeclaredField(debugRendererObjName);
                  field.setAccessible(true);
                  Object object = field.get(minecraft);
                  if (object != null && Variables.isLoaded) {
                     Queue queue = null;

                     try {
                        Field asd = minecraft.getClass().getDeclaredField(queueName);
                        asd.setAccessible(true);
                        queue = (Queue)asd.get(minecraft);
                     } catch (IllegalAccessException | NoSuchFieldException e) {
                        ((ReflectiveOperationException)e).printStackTrace();
                     }

                     if (queue != null) {
                        System.out.println("[Connector] Found a queue. Running task...");
                        Connector connector = new Connector(minecraft);
                        connector.setupServerList();
                        Runnable runnable = () -> {
                           System.out.println("[Connector] Trying to connect...");
                           connector.connectToServer(Tweaker.serverName, Tweaker.serverPort);
                        };
                        queue.add(runnable);
                     } else {
                        System.out.println("[Connector] Queue is null!");
                     }

                     return;
                  }
               } catch (IllegalAccessException | NoSuchFieldException e) {
                  ((ReflectiveOperationException)e).printStackTrace();
               }
            }
         });
         System.out.println("[Connector] Inited.");
      }

   }

   public static void preRenderCape() {
      GL11.glEnable(3042);
   }

   private void createTexture(GameProfile p, PlayerName name) {
      if (System.currentTimeMillis() >= this.nextTimeMills) {
         this.preparedProfileManager.addNewName(p, name);
         this.textureManager.createFramedTextures();
         this.nextTimeMills = System.currentTimeMillis() + 40L;
      }

   }

   private void testMemoryLeak() {
      int name = 0;
      int i = 100;
      LOGGER.info("started testing memory leak");

      for(int j = 0; j < 20; ++j) {
         for(int k = 0; k < i; ++k) {
            int var10003 = name++;
            this.preparedProfileManager.addNewName(new PlayerName("test" + var10003));

            for(int l = 0; l < 100; ++l) {
               try {
                  Thread.sleep(10L);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }

               this.textureManager.createFramedTextures();
            }
         }

         LOGGER.info("finished cycle: " + j);
      }

      LOGGER.info("finished testing memory leak");
   }

   public static Minecraft getMinecraftInstance() {
      if (MINECRAFT_INSTANCE == null) {
         MINECRAFT_INSTANCE = Minecraft.m_91087_();
      }

      return MINECRAFT_INSTANCE;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }
}
