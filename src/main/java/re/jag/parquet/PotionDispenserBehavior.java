package re.jag.parquet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PotionDispenserBehavior extends FallibleItemDispenserBehavior {
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) { 
		this.success = false;
		World world = pointer.getWorld();
		BlockPos block_pos = pointer.getBlockPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(block_pos);
		Block block = state.getBlock();
		
		
		if (PotionUtil.getPotion(stack) == Potions.WATER) {	
			if (block instanceof CauldronBlock) {
				int fill_level = state.get(CauldronBlock.LEVEL);
				if(fill_level < 3) {
					((CauldronBlock)block).setLevel(world, block_pos, state, fill_level + 1);
					this.success = true;
					return new ItemStack(Items.GLASS_BOTTLE, 1);
				}
				return stack;
			}
		}
		return super.dispenseSilently(pointer, stack);		
	}
}
