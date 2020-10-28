package re.jag.parquet.mixin;

import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import re.jag.parquet.Parquet;

@Mixin(FireBlock.class)
public class FireBlockMixin {

	@Inject(method="registerDefaultFlammables", at=@At("HEAD"), cancellable = true)
	private static void onRegisterDefaultFlammables(CallbackInfo ci) {
		if (Parquet.settings.better_no_fire_tick)
			ci.cancel();
	}
}
