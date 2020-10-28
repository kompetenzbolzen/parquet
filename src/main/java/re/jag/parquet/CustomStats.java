package re.jag.parquet;

import net.minecraft.item.Item;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CustomStats {
	public static StatType<Item> TRADED;

	public static void register_custom_stats() {
		if(Parquet.settings.stats_villager_trades) TRADED = register_type("traded", Registry.ITEM);
	}

	private static Identifier register(String _identifier, StatFormatter _formatter) {
		Identifier identifier = new Identifier(_identifier);
		Registry.register(Registry.CUSTOM_STAT, _identifier, identifier);
		Stats.CUSTOM.getOrCreateStat(identifier, _formatter);
		return identifier;
	}

	private static StatType register_type(String _identifier, Registry _registry) {
		return Registry.register(Registry.STAT_TYPE, new Identifier(_identifier), new StatType(_registry));
	}
}
