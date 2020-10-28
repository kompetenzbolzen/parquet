package re.jag.parquet.mixin;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import re.jag.parquet.CustomStats;
import re.jag.parquet.Parquet;

@Mixin(MerchantEntity.class)
public class AbstractTraderEntityMixin {
	@Shadow
	PlayerEntity customer;

	@Inject(method="trade", at = @At("RETURN"))
	private void onTrade(TradeOffer tradeOffer, CallbackInfo ci) {
		if ( Parquet.settings.stats_villager_trades && this.customer instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)customer).incrementStat( CustomStats.TRADED.getOrCreateStat( tradeOffer.getMutableSellItem().getItem() ) );
		}
	}
}
