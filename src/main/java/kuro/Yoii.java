package kuro;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

/**
 * Yoii skin havuzu: dahili (uretilmis, ozgun) anime kiz dokulari + oyun klasorundeki
 * "kuro_yoii" klasorune birakilan kendi PNG skinlerin (64x64, 4D/5D katmanli dahil).
 * Sadece yerel ekranda gorunur, sunucuya hicbir sey gitmez.
 */
public final class Yoii {
   public static final String[] BUILTIN = {
      "chibi_default", "sakura_pink", "sailor_blue", "silver_witch", "crimson_maid",
      "sunny_blonde", "mint_idol", "night_kimono", "snow_white"
   };
   private static final List<Identifier> POOL = new ArrayList<>();
   private static boolean loaded;

   private static void load() {
      loaded = true;
      POOL.clear();
      for (String n : BUILTIN) {
         POOL.add(Identifier.of("kuro", "textures/yoii/" + n + ".png"));
      }

      try {
         Path dir = FabricLoader.getInstance().getGameDir().resolve("kuro_yoii");
         Files.createDirectories(dir);
         try (var st = Files.list(dir)) {
            int i = 0;
            for (Path p : (Iterable<Path>)st.filter(x -> x.toString().toLowerCase().endsWith(".png")).sorted()::iterator) {
               try (InputStream in = Files.newInputStream(p)) {
                  NativeImage img = NativeImage.read(in);
                  Identifier id = Identifier.of("kuro", "yoii_custom/" + (i++));
                  Kuro.mc.getTextureManager().registerTexture(id, new NativeImageBackedTexture(img));
                  POOL.add(id);
               } catch (Throwable ignored) {
               }
            }
         }
      } catch (Throwable ignored) {
      }
   }

   /** PNG'leri klasorden yeniden okur (menudeki "Yenile" ayari). */
   public static void reload() {
      load();
   }

   public static int count() {
      if (!loaded) {
         load();
      }

      return POOL.size();
   }

   /** mode 0: secili skin, 1: oyuncu basina rastgele (sabit), 2: sirayla hepsi (entity id'ye gore). */
   public static Identifier pick(int entityId) {
      if (!loaded) {
         load();
      }

      int n = POOL.size();
      int mode = (int)Config.yoiiMode;
      int idx;
      if (mode == 0) {
         idx = Math.floorMod((int)Config.yoiiSkin, n);
      } else if (mode == 1) {
         idx = Math.floorMod(entityId * 0x9E3779B1, n);
      } else {
         idx = Math.floorMod(entityId, n);
      }

      return POOL.get(idx);
   }
}
