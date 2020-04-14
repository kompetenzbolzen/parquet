package re.jag.parquet.commands;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.dimension.DimensionType;

public class ListAllPlayerSaves {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		 LiteralArgumentBuilder<ServerCommandSource> savedata = literal("savedata").
	                requires(source -> source.hasPermissionLevel(4)).
	                executes((c) -> help(c.getSource())).
                	then( literal("playersave").
                		then(literal("list").
                			executes((c) -> list_local_saves(c.getSource()))
                		).
                		then(argument("UUID", StringArgumentType.word()).
                			executes((c) -> get_player_save_uuid(c.getSource(), StringArgumentType.getString(c, "UUID")))
                		)
                	);
		 
		 dispatcher.register(savedata);
	}
	
	private static int help(ServerCommandSource source) {
		source.sendFeedback(new LiteralText("Test"), false);
		return 1;
	}
	
	private static int list_local_saves(ServerCommandSource source) {
		String uuid_list[] = source.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getSaveHandler().getSavedPlayerIds();
		
		for (int i = 0; i < uuid_list.length; i++) {
			GameProfile profile = source.getMinecraftServer().getUserCache().getByUuid(UUID.fromString(uuid_list[i]));
			String name = "N.A.";
			
			if(profile != null) 
				 name = profile.getName();
			
			source.sendFeedback(new LiteralText(name + " (" + uuid_list[i] + ")"), false);
		}
		
		return 1;
	}
	
	private static int get_player_save_uuid(ServerCommandSource source, String uuid) {
		return 1;
	}

}
