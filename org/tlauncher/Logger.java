package org.tlauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private BufferedWriter writer;

   public Logger() {
      this.writer = null;
   }

   public Logger(String logFile) {
      this(new File(logFile));
   }

   public Logger(File logFile) {
      this.writer = null;

      try {
         if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
         }

         if (!logFile.exists()) {
            logFile.createNewFile();
         }

         this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8"));
         System.out.println("Log Path: " + logFile.getAbsolutePath());
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void close() {
      if (this.writer != null) {
         try {
            this.writer.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

   }

   public void log(Level level, String msg) {
      if (level.display() || this.writer != null) {
         String sb = String.format("[%s %s] %s", Thread.currentThread().getName(), level.getName(), msg);
         if (level.display()) {
            System.out.println(sb);
         }

         if (this.writer != null) {
            try {
               String sb2 = String.format("[%s] %s\r\n", DATE_FORMAT.format(new Date()), sb);
               this.writer.write(sb2);
               this.writer.flush();
            } catch (Exception e) {
               e.printStackTrace();
            }

         }
      }
   }

   public void debug(String msg) {
      this.log(Logger.Level.DEBUG, msg);
   }

   public void debug(String format, Object... objs) {
      this.debug(String.format(format, objs));
   }

   public void info(String msg) {
      this.log(Logger.Level.INFO, msg);
   }

   public void info(String format, Object... objs) {
      this.info(String.format(format, objs));
   }

   public void warning(String msg) {
      this.log(Logger.Level.WARNING, msg);
   }

   public void warning(String format, Object... objs) {
      this.warning(String.format(format, objs));
   }

   public void warning(Throwable e) {
      this.log(Logger.Level.WARNING, "Exception: " + e.toString());
      StackTraceElement[] stes = e.getStackTrace();

      for(StackTraceElement ste : stes) {
         this.log(Logger.Level.WARNING, ste.toString());
      }

   }

   public static enum Level {
      DEBUG("DEBUG", false),
      INFO("INFO", true),
      WARNING("WARNING", true);

      String name;
      boolean display;

      private Level(String name, boolean display) {
         this.name = name;
         this.display = display;
      }

      public String getName() {
         return this.name;
      }

      public boolean display() {
         return this.display;
      }

      // $FF: synthetic method
      private static Level[] $values() {
         return new Level[]{DEBUG, INFO, WARNING};
      }
   }
}
