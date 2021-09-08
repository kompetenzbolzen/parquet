package re.jag.parquet.dispenser;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BucketDispenserBehavior extends FallibleItemDispenserBehavior {
	private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		World world = pointer.getWorld();
		FluidModificationItem bucket_item = (FluidModificationItem) stack.getItem();
		Item item;
		BlockPos block_pos = pointer.getPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(block_pos);
		Block block = state.getBlock();

		//original
		if (block instanceof FluidDrainable) {
			ItemStack itemStack = ((FluidDrainable)block).tryDrainFluid(world, block_pos, state);
			if (itemStack.isEmpty()) {
				return super.dispenseSilently(pointer, stack);
			} else {
				world.emitGameEvent((Entity)null, GameEvent.FLUID_PICKUP, block_pos);
				Item item2 = itemStack.getItem();
				stack.decrement(1);
				if (stack.isEmpty()) {
					return new ItemStack(item2);
				} else {
					if (((DispenserBlockEntity)pointer.getBlockEntity()).addToFirstFreeSlot(new ItemStack(item2)) < 0) {
						this.fallbackBehavior.dispense(pointer, new ItemStack(item2));
					}

					return stack;
				}
			}
		}

		// New
		else if (block instanceof LeveledCauldronBlock) {
			if(state.get(LeveledCauldronBlock.LEVEL) >= 3) {
				world.setBlockState(block_pos, Blocks.CAULDRON.getDefaultState());
				this.setSuccess(true);
				item = Items.WATER_BUCKET;
			} else {
				return stack;
			}
		} else {
			return this.fallbackBehavior.dispense(pointer, stack);
		}
		
		stack.decrement(1); 
		if (stack.isEmpty())
			return new ItemStack(item);  
		if (((DispenserBlockEntity)pointer.getBlockEntity()).addToFirstFreeSlot(new ItemStack(item)) < 0)
			this.fallbackBehavior.dispense(pointer, new ItemStack(item));
		return stack;
	}	
}
