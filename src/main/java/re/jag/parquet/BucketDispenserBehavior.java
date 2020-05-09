package re.jag.parquet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BucketDispenserBehavior extends FallibleItemDispenserBehavior {
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) { 
		//this.success = false;
		World world = pointer.getWorld();
		Item item;
		BlockPos block_pos = pointer.getBlockPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(block_pos);
		Block block = state.getBlock();
		if (block instanceof FluidDrainable) {
			Fluid fluid = ((FluidDrainable)block).tryDrainFluid(world, block_pos, state);
		
			if (!(fluid instanceof net.minecraft.fluid.FlowableFluid/*BaseFluid*/)) {
				return super.dispenseSilently(pointer, stack);
			}
			
			item = fluid.getBucketItem();  
		} else if (block instanceof CauldronBlock) {
			if(state.get(CauldronBlock.LEVEL) >= 3) {
				((CauldronBlock)block).setLevel(world, block_pos, state, 0);
				//this.success = true;
				this.setSuccess(true);
				item = Items.WATER_BUCKET;
			} else {
				return stack;
			}
		} else {
			return super.dispenseSilently(pointer, stack);
		}
		
		stack.decrement(1); 
		if (stack.isEmpty())
			return new ItemStack(item);  
		if (((DispenserBlockEntity)pointer.getBlockEntity()).addToFirstFreeSlot(new ItemStack(item)) < 0)
			this.dispense(pointer, new ItemStack(item));  
		return stack;
	}	
}
