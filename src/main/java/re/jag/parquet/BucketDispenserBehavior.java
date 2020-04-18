package re.jag.parquet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BucketDispenserBehavior extends FallibleItemDispenserBehavior {
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) { 
		this.success = false;
		World world = pointer.getWorld();
		BlockPos block_pos = pointer.getBlockPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(block_pos);
		Block block = state.getBlock();
		if (block instanceof FluidDrainable) {
			Fluid fluid = ((FluidDrainable)block).tryDrainFluid(world, block_pos, state);
		
			//Mojang, why are you here?
			if (!(fluid instanceof net.minecraft.fluid.BaseFluid)) {
				return super.dispenseSilently(pointer, stack);
			}
			//TODO if not work, add item = ...
			fluid.getBucketItem();  
		}
		if (block instanceof CauldronBlock) {
			if(state.get(CauldronBlock.LEVEL) >= 3) {
				((CauldronBlock)block).setLevel(world, block_pos, state, 0);
				this.success = true;
				return new ItemStack(Items.WATER_BUCKET, 1);
			}
			return stack;
		}
		return super.dispenseSilently(pointer, stack);		
	}	
}
