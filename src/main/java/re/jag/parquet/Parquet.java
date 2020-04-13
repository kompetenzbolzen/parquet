package re.jag.parquet;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.command.ServerCommandSource;
import re.jag.parquet.commands.CameraMode;
import re.jag.parquet.commands.ListAllPlayerSaves;

public class Parquet implements ModInitializer {

	@Override
	public void onInitialize() {
	
	}
	
	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		ListAllPlayerSaves.register(dispatcher);
		CameraMode.register(dispatcher);
	}

}
