package re.jag.parquet.dispenser;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DyeItemDispenserBehavior extends FallibleItemDispenserBehavior{
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		//this.success = false;
		World world = pointer.getWorld();
		BlockPos block_pos = pointer.getBlockPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(block_pos);
		Block block = state.getBlock();
		Item item = stack.getItem();
		
		if (block instanceof ShulkerBoxBlock && item instanceof DyeItem && world instanceof ServerWorld) {
			if ( ((ShulkerBoxBlock)block).getColor() != null )
				return stack;
			
			BlockEntity block_entity = world.getBlockEntity(block_pos);
			List<ItemStack> dropped_stacks = ShulkerBoxBlock.getDroppedStacks(state, (ServerWorld)world, block_pos, block_entity);
			
			if (dropped_stacks.size() == 1) {
				ItemStack dropped_stack = dropped_stacks.get(0);
				ItemStack new_stack = new ItemStack(ShulkerBoxBlock.get(((DyeItem)item).getColor()));
				
				if (dropped_stack.hasTag()) {
					new_stack.setTag(dropped_stack.getTag().copy());
				}
				
				world.setBlockState(block_pos, Blocks.AIR.getDefaultState());
				
				Direction direction = (Direction)pointer.getBlockState().get(DispenserBlock.FACING);
				Direction direction2 = pointer.getWorld().isAir(block_pos.down()) ? direction : Direction.UP;
				//this.success = (((BlockItem)new_stack.getItem()).place(new AutomaticItemPlacementContext(pointer.getWorld(), block_pos, direction, new_stack, direction2)) == ActionResult.SUCCESS);
				this.setSuccess((((BlockItem)new_stack.getItem()).place(new AutomaticItemPlacementContext(pointer.getWorld(), block_pos, direction, new_stack, direction2)) == ActionResult.SUCCESS));
				stack.decrement(1);
			}
			return stack;
		}
		
		//this.success = true;
		this.setSuccess(true);
		return super.dispenseSilently(pointer, stack);
	}

}
