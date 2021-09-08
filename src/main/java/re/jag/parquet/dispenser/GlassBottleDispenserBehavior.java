package re.jag.parquet.dispenser;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class GlassBottleDispenserBehavior extends FallibleItemDispenserBehavior {
	private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

	private ItemStack tryPutFilledBottle(BlockPointer pointer, ItemStack emptyBottleStack, ItemStack filledBottleStack) {
		emptyBottleStack.decrement(1);
		if (emptyBottleStack.isEmpty()) {
			pointer.getWorld().emitGameEvent((Entity)null, GameEvent.FLUID_PICKUP, pointer.getPos());
			return filledBottleStack.copy();
		} else {
			if (((DispenserBlockEntity)pointer.getBlockEntity()).addToFirstFreeSlot(filledBottleStack.copy()) < 0) {
				this.fallbackBehavior.dispense(pointer, filledBottleStack.copy());
			}

			return emptyBottleStack;
		}
	}

	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		//this.success = false;
		World world = pointer.getWorld();
		BlockPos block_pos = pointer.getPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(block_pos);
		Block block = state.getBlock();

		// Original Behavior
		if (state.isIn(BlockTags.BEEHIVES, (bstate) -> {
			return bstate.contains(BeehiveBlock.HONEY_LEVEL);
		}) && (Integer)state.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
			((BeehiveBlock)state.getBlock()).takeHoney(world, state, block_pos, (PlayerEntity)null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
			this.setSuccess(true);
			return this.tryPutFilledBottle(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
		} else if (world.getFluidState(block_pos).isIn(FluidTags.WATER)) {
			this.setSuccess(true);
			return this.tryPutFilledBottle(pointer, stack, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
		}

		// New behavior
		if (block instanceof LeveledCauldronBlock) {
			/* TODO Check for Water */
			LeveledCauldronBlock.decrementFluidLevel(state, world, block_pos);
			this.setSuccess(true);
			return tryPutFilledBottle(pointer, stack, PotionUtil.setPotion( new ItemStack(Items.POTION), Potions.WATER ) );
		}

		return super.dispenseSilently(pointer, stack);
	}
}

