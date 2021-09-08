package re.jag.parquet.dispenser;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MusicDiscDispenserBehavior extends FallibleItemDispenserBehavior {

	protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		World world = pointer.getWorld();
		Direction direction = (Direction)pointer.getBlockState().get(DispenserBlock.FACING);
		BlockPos block_pos = pointer.getPos().offset(direction);
		BlockState state = world.getBlockState(block_pos);

		if (state.isOf(Blocks.JUKEBOX)) {
			JukeboxBlock block = (JukeboxBlock)state.getBlock();
			ItemStack disc_stack = stack.split(1);

			if (state.get(JukeboxBlock.HAS_RECORD)) // Remove record if present
				block.onUse(state, world, block_pos, null, null, null);

			block.setRecord(world, block_pos, state, disc_stack);
			world.syncWorldEvent((PlayerEntity)null, 1010, block_pos, Item.getRawId(disc_stack.getItem()));

			return stack;
		}

		return super.dispenseSilently(pointer, stack);
	}
}
