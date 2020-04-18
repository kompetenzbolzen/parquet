package re.jag.parquet.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.Bootstrap;

import re.jag.parquet.Parquet;

@Mixin(Bootstrap.class)
public abstract class BootstrapMixin {

	@Inject(method="initialize", at = @At("RETURN"))
	private static void onInitialize(CallbackInfo ci) {
		Parquet.registerCustomDispenserBehavior();
	}
}
