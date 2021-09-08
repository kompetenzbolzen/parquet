package re.jag.parquet.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.literal;

public class TimeDIff {
	private static int last_server_time = 0;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> dt = literal("dt").executes( (c) -> dt(c.getSource()) );

		dispatcher.register(dt);
	}

	private static int dt(ServerCommandSource _source) {
		int current = _source.getWorld().getServer().getTicks();
		_source.sendFeedback( new LiteralText( String.valueOf(current - last_server_time)), false) ;
		last_server_time = current;
		return 1;
	}
}
