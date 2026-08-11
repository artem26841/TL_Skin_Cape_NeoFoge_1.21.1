@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractPlayer {

    @Shadow @Nullable protected abstract PlayerInfo getPlayerInfo();

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void onGetSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerInfo playerInfo = this.getPlayerInfo();
        if (playerInfo != null) {
            PlayerSkin original = cir.getReturnValue();
            
            // Создаем новый объект PlayerSkin, подставляя туда данные из TLauncher
            PlayerSkin customSkin = new PlayerSkin(
                TLSkinCape.getLocationSkin(playerInfo.getProfile()),
                TLSkinCape.getLocationCape(playerInfo.getProfile()),
                TLSkinCape.getLocationElytra(playerInfo.getProfile()),
                original.capeTexture(), // если плащ не нужен, можно оставить оригинал
                original.model(),       // или перевести строку TLauncher в PlayerSkin.Model
                original.secure()
            );
            
            cir.setReturnValue(customSkin);
        }
    }
}
