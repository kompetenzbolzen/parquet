package re.jag.parquet.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.dimension.DimensionType;

public class ListAllPlayerSaves {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		 LiteralArgumentBuilder<ServerCommandSource> listplayersaves = literal("listplayersaves").
	                requires(source -> source.hasPermissionLevel(4)).
	                executes((c) -> execute(c.getSource()));
		 
		 dispatcher.register(listplayersaves);
	}
	
	private static int execute(ServerCommandSource source) {
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

}
