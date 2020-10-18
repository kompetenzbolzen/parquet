package re.jag.parquet.mixin;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "getServerModName", at = @At("RETURN"), cancellable = true)
	protected void onGetServerModName(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue("vanilla++™");
	}
}
