package org.tlauncher.util;

import java.io.PrintStream;
import org.slf4j.LoggerFactory;

public class Logger {
   private static final org.slf4j.Logger log = LoggerFactory.getLogger(Logger.class);

   public void warn(String s, Object... objects) {
      int objectsLength = objects == null ? 0 : objects.length;
      PrintStream var10000 = System.out;
      String var10001 = this.processArgs(s, objectsLength, objects);
      var10000.println("[WARN] " + var10001);
   }

   public void debug(String s, Object... objects) {
      int objectsLength = objects == null ? 0 : objects.length;
      PrintStream var10000 = System.out;
      String var10001 = this.processArgs(s, objectsLength, objects);
      var10000.println("[DEBUG]" + var10001);
   }

   public void info(String s, Object... objects) {
      int objectsLength = objects == null ? 0 : objects.length;
      PrintStream var10000 = System.out;
      String var10001 = this.processArgs(s, objectsLength, objects);
      var10000.println("[INFO] " + var10001);
   }

   public void trace(String s, Object... objects) {
      int objectsLength = objects == null ? 0 : objects.length;
      PrintStream var10000 = System.out;
      String var10001 = this.processArgs(s, objectsLength, objects);
      var10000.println("[TRACE] " + var10001);
   }

   public <E extends Exception> void error(String s, E exception) {
      PrintStream var10000 = System.out;
      String var10001 = this.processArgs(s, 0);
      var10000.println("[ERROR] " + var10001);
      exception.printStackTrace();
   }

   public void error(String s, Object... objects) {
      int objectsLength = objects == null ? 0 : objects.length;
      Exception exception = null;
      if (objects != null && objects.length > 0 && objects[objects.length - 1] instanceof Exception) {
         exception = (Exception)objects[objects.length - 1];
         --objectsLength;
      }

      PrintStream var10000 = System.out;
      String var10001 = this.processArgs(s, objectsLength, objects);
      var10000.println("[ERROR] " + var10001);
      if (exception != null) {
         exception.printStackTrace();
      }

   }

   private String processArgs(String message, int argsLength, Object... args) {
      if (args != null && argsLength != 0) {
         String result = message;
         int index = 0;
         int i = 1;

         while(true) {
            if (i < result.length()) {
               char firstChar = result.charAt(i - 1);
               char secChar = result.charAt(i);
               if (firstChar != '{' || secChar != '}') {
                  ++i;
                  continue;
               }

               String arg = args[index].toString();
               result = result.substring(0, i - 1) + arg + result.substring(i + 1);
               i += arg.length() - 1;
            }

            ++index;
            if (index >= argsLength) {
               return result;
            }
         }
      } else {
         int index = message.indexOf("{}");
         if (index != -1) {
            String var10002 = message.substring(Math.max(0, index - 10), Math.min(message.length(), index + 10));
            throw new RuntimeException("No message args at " + var10002);
         } else {
            return message;
         }
      }
   }
}
