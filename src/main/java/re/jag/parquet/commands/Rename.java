package re.jag.parquet.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Rename {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> rename = literal("rename").
				requires((s) -> can_execute(s)).
				then( argument("name", StringArgumentType.string()).
						executes((c) -> rename(c.getSource(), StringArgumentType.getString(c, "name")))
				);

		dispatcher.register(rename);
	}

	private static int rename (ServerCommandSource _source, String _name) throws CommandSyntaxException {

		ItemStack stack = _source.getPlayer().getStackInHand(Hand.MAIN_HAND);
		if (stack.isEmpty()) {
			_source.sendError(new LiteralText("Your hand is empty"));
			return 0;
		}

		stack.setCustomName(new LiteralText(_name));
		return 1;
	}

	private static boolean can_execute (ServerCommandSource _source) {
		if(_source.getWorld().getServer().getDefaultGameMode().isCreative())
			return true;

		try {
			return _source.getPlayer().interactionManager.getGameMode().isCreative();
		} catch (CommandSyntaxException e) {
			return false;
		}
	}
}
