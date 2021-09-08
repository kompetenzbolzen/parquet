package re.jag.parquet.dispenser;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WaterBucketDispenserBehavior extends FallibleItemDispenserBehavior{
	
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) { 
		//this.success = false;
		
		BucketItem bucket_item = (BucketItem)stack.getItem(); 
		BlockPos block_pos = pointer.getPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		World world = pointer.getWorld(); 
		if (bucket_item.placeFluid(null, world, block_pos, null)) { 
			bucket_item.onEmptied((PlayerEntity)null, world, stack, block_pos);
			this.setSuccess(true);
			return new ItemStack(Items.BUCKET);
		}
		
		BlockState state = pointer.getWorld().getBlockState(block_pos);
		Block facing_block = state.getBlock();
		
		if ( facing_block instanceof CauldronBlock) {
			world.setBlockState(block_pos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
			world.syncWorldEvent(1047, block_pos, 0);

			this.setSuccess(true);
			return new ItemStack(Items.BUCKET, 1);
		}

		this.setSuccess(true);
		return this.dispense(pointer, stack); 
	}
}
