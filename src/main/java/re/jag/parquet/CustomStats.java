package re.jag.parquet;

import net.minecraft.item.Item;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CustomStats {
	public static final StatType<Item> TRADED;

	static {
		TRADED = (StatType<Item>) Registry.STAT_TYPE.get(new Identifier("traded"));
	}
}
