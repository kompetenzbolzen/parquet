package re.jag.parquet.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.stat.Stats;


@Mixin(FireworkRocketItem.class)
public class FireWorkItemMixin extends Item{
	
	public FireWorkItemMixin(Settings settings) {
		super(settings);
	}
	
	/*
	 * minecraft.used:minecraft.firework_rocket only counts rockets fired on ground, not the ones used during flight.
	 * GG Mojang!
	 */
	@Inject(method="use", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	protected void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci) {
		user.incrementStat(Stats.USED.getOrCreateStat(this));
	}
}
