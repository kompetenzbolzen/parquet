package re.jag.parquet.dispenser;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GlassBottleDispenserBehavior extends FallibleItemDispenserBehavior {

	//TODO Port over to bucket?
	private ItemStack insert_first_free_or_drop(BlockPointer blockPointer, ItemStack emptyBottleStack, ItemStack filledBottleStack) { 
		emptyBottleStack.decrement(1);
		if (emptyBottleStack.isEmpty()) {
			return filledBottleStack.copy();
		}
		if (((DispenserBlockEntity)blockPointer.getBlockEntity()).addToFirstFreeSlot(filledBottleStack.copy()) < 0) {
			this.dispense(blockPointer, filledBottleStack.copy());
		}
		return emptyBottleStack; 
	}
	
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		//this.success = false;
		World world = pointer.getWorld();
		BlockPos block_pos = pointer.getBlockPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(block_pos);
		Block block = state.getBlock();
		//if (block.matches(BlockTags.BEEHIVES) && ...
		if (state.method_27851(BlockTags.BEEHIVES, (abstractBlockState) -> { return abstractBlockState.contains(BeehiveBlock.HONEY_LEVEL); })
				&& ((Integer)state.get(BeehiveBlock.HONEY_LEVEL)).intValue() >= 5) {
			((BeehiveBlock)state.getBlock()).takeHoney(world, state, block_pos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
			//this.success = true;
			this.setSuccess(true);
			return insert_first_free_or_drop(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
		}
		if (block instanceof CauldronBlock) {
			int fill_level = state.get(CauldronBlock.LEVEL);
			if(fill_level > 0) {
				((CauldronBlock)block).setLevel(world, block_pos, state, fill_level - 1);
				//this.success = true;
				this.setSuccess(true);
				return insert_first_free_or_drop(pointer, stack, PotionUtil.setPotion( new ItemStack(Items.POTION), Potions.WATER ) );
			}
			return stack;
		}
		if (world.getFluidState(block_pos).isIn(FluidTags.WATER)) {
			//this.success = true;
			this.setSuccess(true);
			return insert_first_free_or_drop(pointer, stack, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
		} 
		return super.dispenseSilently(pointer, stack);
	}
}

