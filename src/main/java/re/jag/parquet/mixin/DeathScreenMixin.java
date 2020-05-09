package re.jag.parquet.mixin;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin extends Screen {
    @Shadow
    private int ticksSinceDeath;

    protected DeathScreenMixin(Text title) {
        super(title);
    }

    @Inject(method="init", at=@At("RETURN"))
    protected void onInit(CallbackInfo ci) {
        this.ticksSinceDeath = 19;
    }
}
