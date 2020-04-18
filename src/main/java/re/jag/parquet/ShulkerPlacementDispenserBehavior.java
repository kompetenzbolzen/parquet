package re.jag.parquet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ShulkerPlacementDispenserBehavior extends BlockPlacementDispenserBehavior{
	protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		this.success = false;
		
		Item item = stack.getItem();
		
		if (item instanceof BlockItem) {
			Direction direction = (Direction)pointer.getBlockState().get(DispenserBlock.FACING);
			BlockPos block_pos = pointer.getBlockPos().offset(direction);	
			Block block = ((BlockItem)item).getBlock();
			
			if (block instanceof ShulkerBoxBlock && !pointer.getWorld().isClient()) {
				BlockState state = pointer.getWorld().getBlockState(block_pos);
				if (state.getBlock() instanceof CauldronBlock) {
					int fill_level = state.get(CauldronBlock.LEVEL);
					if (fill_level > 0) {
						ItemStack itemStack5 = new ItemStack(Blocks.SHULKER_BOX, 1);
						if (stack.hasTag()) {
							itemStack5.setTag(stack.getTag().copy());
						}
						((CauldronBlock)state.getBlock()).setLevel(pointer.getWorld(), block_pos, state, fill_level - 1);
						
						this.success = true;
						return itemStack5;
					}
					
					//fail if cauldron empty
					return stack;
				}	
			} 
		} 
     
	return super.dispenseSilently(pointer, stack);
	}
}
