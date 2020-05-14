package re.jag.parquet.mixin;

import net.minecraft.item.Item;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Has to be a Mixin for the game to load on startup.
 * */

@Mixin(Stats.class)
public class StatsMixin {
	@Shadow
	private static StatType<Item> registerType(String string, Registry<Item> registry){ return null; };

	@Shadow
	private static Identifier register(String string, StatFormatter statFormatter) { return  null; }

	private static final StatType<Item> TRADED;

	static {
		TRADED = registerType("traded", Registry.ITEM);
	}
}
