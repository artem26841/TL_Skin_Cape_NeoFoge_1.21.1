package org.tlauncher.injection.mapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.tlauncher.util.TLModCfg;

public class MappingManager {
   private static final MappingManager instance = loadMappings();
   private final Map<String, Mappings> versions = new HashMap();

   public Mappings getMappings() {
      return (Mappings)this.versions.get(TLModCfg.getMinecraftVersion());
   }

   private static MappingManager loadMappings() {
      StringBuilder json = new StringBuilder();
      int i = 0;

      while(true) {
         InputStream inputStream = MappingManager.class.getResourceAsStream("mappings" + i);
         if (inputStream == null) {
            Gson gson = (new GsonBuilder()).create();
            return (MappingManager)gson.fromJson(new String(Base64.getDecoder().decode(json.toString())), MappingManager.class);
         }

         try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            try {
               while((line = bufferedReader.readLine()) != null) {
                  json.append(line);
               }
            } catch (Throwable var7) {
               try {
                  bufferedReader.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            bufferedReader.close();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }

         ++i;
      }
   }

   public static MappingManager instance() {
      return instance;
   }

   protected Map<String, Mappings> getVersions() {
      return this.versions;
   }
}
