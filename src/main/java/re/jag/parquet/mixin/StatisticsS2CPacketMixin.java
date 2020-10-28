package re.jag.parquet.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.stat.Stat;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import re.jag.parquet.CustomStats;
import re.jag.parquet.Parquet;

@Mixin(StatisticsS2CPacket.class)
public class StatisticsS2CPacketMixin {
	@Shadow
	private Object2IntMap<Stat<?>> stats;

	@Shadow
	private <T> int getStatId(Stat<T> stat) { return 0; }

	/*
	 * Opening the statistics menu on client causes crash with custom stats enabled.
	 * This new write() removes all custom stats from the list sent to the client.
	 */
	@Inject(method="write", at=@At("HEAD"), cancellable = true)
	protected void onWrite(PacketByteBuf buf, CallbackInfo ci){
		if (Parquet.settings.stats_send_to_client)
			return;

		Object2IntMap<Stat<?>> copy = ((Object2IntOpenHashMap)stats).clone();

		ObjectIterator iter = copy.object2IntEntrySet().iterator();

		while (iter.hasNext()) {
			Object2IntMap.Entry<Stat<?>> entry = (Object2IntMap.Entry)iter.next();
			Stat<?> stat = (Stat)entry.getKey();

			if( Parquet.settings.stats_villager_trades && stat.getType() == CustomStats.TRADED )
				copy.removeInt(stat);
		}

		iter = copy.object2IntEntrySet().iterator();
		buf.writeVarInt(copy.size());

		while(iter.hasNext()) {
			Object2IntMap.Entry<Stat<?>> entry = (Object2IntMap.Entry)iter.next();
			Stat<?> stat = (Stat)entry.getKey();

			buf.writeVarInt(Registry.STAT_TYPE.getRawId(stat.getType()));
			buf.writeVarInt(this.getStatId(stat));
			buf.writeVarInt(entry.getIntValue());
		}

		ci.cancel();
	}
}
