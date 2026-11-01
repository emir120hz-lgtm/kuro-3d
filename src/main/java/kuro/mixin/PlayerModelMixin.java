package kuro.mixin;

import kuro.Config;
import kuro.Kuro;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Yoii: baska oyunculari (kendimiz haric) sadece bizim ekranimizda "chibi" oranli (buyuk kafa,
 * kucuk govde/kol/bacak) gosterir. Sunucuya hicbir paket gitmez, baska hicbir oyuncu bundan
 * etkilenmez -- yalnizca bu istemciyi calistiran kisi gorur.
 *
 * Bilincli olarak gercek bir "Yes Steve Model / GeckoLib" tarzi ozel geometri+doku ice aktarma
 * sistemi DEGIL: vanilla biped modelinin kendi parcalarini (head/body/arm/leg ModelPart) yeniden
 * olcekleyip hafifce yeniden konumlandiriyoruz. Boylece hicbir disaridan model/doku dosyasi
 * gerekmez, telif/izin sorunu olmaz ve API'si 1.21.4 icin dogrulanmis, sabit alanlar uzerinden
 * calisir (bkz. ModelPart.xScale/yScale/zScale/pivotY).
 *
 * "required: false" / "defaultRequire: 0" olan kuro.extra.mixins.json icinde kayitli: bu mixin
 * herhangi bir sebeple uygulanamazsa oyun yine de normal acilir, sadece Yoii gorsel efekti pasif
 * kalir.
 */
@Mixin(PlayerEntityModel.class)
public abstract class PlayerModelMixin {
   @Shadow
   private ModelPart head;
   @Shadow
   private ModelPart body;
   @Shadow
   private ModelPart leftArm;
   @Shadow
   private ModelPart rightArm;
   @Shadow
   private ModelPart leftLeg;
   @Shadow
   private ModelPart rightLeg;

   @Inject(method = "setAngles", at = @At("TAIL"), require = 0)
   private void kuro$yoii(PlayerEntityRenderState state, CallbackInfo ci) {
      if (!Kuro.on[Kuro.YOII]) {
         return;
      }

      MinecraftClient mc = Kuro.mc;
      if (mc.player != null && state.id == mc.player.getId()) {
         return; // kendimizi degil, sadece digerlerini degistiriyoruz
      }

      if (Config.yoiiChibi < 0.5F) {
         return;
      }

      float headScale = 1.55F;
      head.xScale = headScale;
      head.yScale = headScale;
      head.zScale = headScale;
      head.pivotY -= 1.6F;

      float bodyXz = 0.82F;
      body.xScale = bodyXz;
      body.yScale = 0.78F;
      body.zScale = bodyXz;

      float armS = 0.80F;
      leftArm.xScale = armS;
      leftArm.yScale = 0.80F;
      leftArm.zScale = armS;
      rightArm.xScale = armS;
      rightArm.yScale = 0.80F;
      rightArm.zScale = armS;

      float legXz = 0.78F;
      leftLeg.xScale = legXz;
      leftLeg.yScale = 0.66F;
      leftLeg.zScale = legXz;
      leftLeg.pivotY -= 2.0F;
      rightLeg.xScale = legXz;
      rightLeg.yScale = 0.66F;
      rightLeg.zScale = legXz;
      rightLeg.pivotY -= 2.0F;
   }
}
