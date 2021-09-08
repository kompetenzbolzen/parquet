package re.jag.parquet.dispenser;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PotionDispenserBehavior extends FallibleItemDispenserBehavior {
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) { 
		World world = pointer.getWorld();
		BlockPos block_pos = pointer.getPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(block_pos);
		Block block = state.getBlock();
		
		
		if (PotionUtil.getPotion(stack) == Potions.WATER) {	
			if (block instanceof CauldronBlock) {
				world.setBlockState(block_pos, Blocks.WATER_CAULDRON.getDefaultState());
				world.syncWorldEvent(1047, block_pos, 0);
				world.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, block_pos);

				return new ItemStack(Items.GLASS_BOTTLE, 1);

			} else if (block instanceof LeveledCauldronBlock) {
				if (((LeveledCauldronBlock) block).isFull(state))
					return stack;

				world.setBlockState(block_pos, (BlockState)state.with(LeveledCauldronBlock.LEVEL, (Integer)state.get(LeveledCauldronBlock.LEVEL) + 1));
				world.syncWorldEvent(1047, block_pos, 0);
				this.setSuccess(true);
			}
			return stack;
		}

		return super.dispenseSilently(pointer, stack);
	}
}
