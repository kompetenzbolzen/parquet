package re.jag.parquet.dispenser;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ShulkerPlacementDispenserBehavior extends BlockPlacementDispenserBehavior{
	protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		Item item = stack.getItem();
		
		if (item instanceof BlockItem) {
			Direction direction = (Direction)pointer.getBlockState().get(DispenserBlock.FACING);
			BlockPos block_pos = pointer.getPos().offset(direction);
			Block block = ((BlockItem)item).getBlock();
			
			if (block instanceof ShulkerBoxBlock && !pointer.getWorld().isClient()) {
				BlockState state = pointer.getWorld().getBlockState(block_pos);
				Block facing_block = state.getBlock();

				if (facing_block instanceof LeveledCauldronBlock) {
					/* TODO Check for WATER!!!!! */

					ItemStack itemStack5 = new ItemStack(Blocks.SHULKER_BOX, 1);

					if (stack.hasNbt()) {
						itemStack5.setNbt(stack.getNbt().copy());
					}

					LeveledCauldronBlock.decrementFluidLevel(state, pointer.getWorld(), block_pos);

					this.setSuccess(true);
					return itemStack5;
				}
			} 
		} 
     
		return super.dispenseSilently(pointer, stack);
	}
}
