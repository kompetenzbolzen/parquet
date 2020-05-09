package re.jag.parquet.commands;

import java.io.File;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ObjectiveCriteriaArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import re.jag.parquet.Parquet;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.block.Block;

public class Savedata {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		 LiteralArgumentBuilder<ServerCommandSource> savedata = literal("savedata").
	                requires(source -> source.hasPermissionLevel(4)).
	                executes((c) -> help(c.getSource())).
	                then( literal("playersave").
                			then(literal("list").
                				executes((c) -> list_local_saves(c.getSource()))
                		)
                	).
	                then(literal("stats").
	                		then(argument("player", StringArgumentType.word()).
	                				suggests( (c, b) -> suggestMatching(c.getSource().getPlayerNames() , b)).
			                		then(argument("criteria", ObjectiveCriteriaArgumentType.objectiveCriteria()).
			                				executes((c) -> print_player_stat (
			                						c.getSource(),
			                						StringArgumentType.getString(c, "player"),
			                						ObjectiveCriteriaArgumentType.getCriteria(c, "criteria") 
			                				)).
			                				then(literal("import").then(argument("score", ObjectiveArgumentType.objective()).
			                						executes((c) -> import_stat_to_scoreboard (
			                								c.getSource(), 
			                								StringArgumentType.getString(c, "player"), 
			                								ObjectiveCriteriaArgumentType.getCriteria(c, "criteria"), 
			                								ObjectiveArgumentType.getWritableObjective(c, "score"), 1)).
			                						then(argument("multiplier", FloatArgumentType.floatArg()).
			                								executes((c) ->  import_stat_to_scoreboard(
			                										c.getSource(), 
			                										StringArgumentType.getString(c, "player"), 
			                										ObjectiveCriteriaArgumentType.getCriteria(c, "criteria"), 
			                										ObjectiveArgumentType.getWritableObjective(c, "score"), 
			                										FloatArgumentType.getFloat(c, "multiplier"))
			                								)
			                						)
			                				))
			                		))
                	);
		 dispatcher.register(savedata);
	}
	
	private static int help(ServerCommandSource source) {
		//TODO Help Text
		source.sendFeedback(new LiteralText("Hölp goes here"), false);
		return 1;
	}
	
	private static int list_local_saves(ServerCommandSource source) {
		//TODO 1.16 fix

		/*
		String uuid_list[] = source.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getSaveHandler().getSavedPlayerIds();
		
		for (int i = 0; i < uuid_list.length; i++) {
			GameProfile profile = source.getMinecraftServer().getUserCache().getByUuid(UUID.fromString(uuid_list[i]));
			String name = "N.A.";
			
			if(profile != null) 
				 name = profile.getName();
			
			source.sendFeedback(new LiteralText(name + " (" + uuid_list[i] + ")"), false);
		}
		*/

		source.sendError(new LiteralText("1.16 Incompatability"));
		return 1;
	}
	
	private static int import_stat_to_scoreboard(ServerCommandSource source, String player,ScoreboardCriterion stat, ScoreboardObjective objective, float multiplier) {
		int score = get_player_stat(source, player, stat);
		if (score < 0)
			return 0;
		
		ServerScoreboard server_scoreboard = source.getMinecraftServer().getScoreboard();
		ScoreboardPlayerScore player_score = server_scoreboard.getPlayerScore(player, objective);
		
		int mod_score = multiplier >= 0 ? (int)((float)score * multiplier) : (int)((float)score / multiplier * (-1));
		
		player_score.setScore(mod_score);
		
		return 1;
	}
	
	public static ServerStatHandler get_player_stat_handler(ServerCommandSource source, String player_name) {
		ServerPlayerEntity player = source.getMinecraftServer().getPlayerManager().getPlayer(player_name);
		if (player != null) {
			return player.getStatHandler();
		}

		return null;

		//TODO 1.16 fix

		//WorldSaveHandler reworked

		/*
		GameProfile profile = source.getMinecraftServer().getUserCache().findByName(player_name);
		if (profile == null) {
			Parquet.LOG.debug("Savedata: User not in Usercache");
			return null;
		}
	
		UUID player_uuid = profile.getId();
		
		File file = new File(source.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDir(), "stats");
		File file2 = new File(file, player_uuid + ".json");
	
		if (!file2.exists()) {
			Parquet.LOG.debug("Savedata: user stat file not found");
			return null;
		}

		return new ServerStatHandler(source.getMinecraftServer(), file2);
		*/
	}

	private static int print_player_stat(ServerCommandSource source, String player, ScoreboardCriterion stat) {
		Integer stat_value = get_player_stat(source, player, stat);
		
		if (stat_value >= 0)
		{
			source.sendFeedback(new LiteralText(stat_value.toString()), false);
			return 1;
		}
		return 0;
	}
	
	private static int get_player_stat(ServerCommandSource source, String player_name, ScoreboardCriterion criteria) {
		ServerStatHandler stat_handler = get_player_stat_handler(source, player_name);
		
		if (stat_handler == null) {
			source.sendError(new LiteralText("Failed to get Statistics for Player " + player_name));
			return -1;
		}
		
		String criteria_name = criteria.getName();
		int criteria_name_seperator = criteria_name.indexOf(':');
		
		String registry_name="";
		String stat_name="";
		
		if (criteria_name_seperator < 0) {
			source.sendError(new LiteralText("Expected valid registry and stat ID"));
			return -1;
		} else {
			registry_name = criteria_name.substring(0,criteria_name_seperator);
			stat_name = criteria_name.substring(criteria_name_seperator + 1, criteria_name.length() );
		}
		
		StatType<?> stat_type = Registry.STAT_TYPE.get(Identifier.splitOn(registry_name, '.'));
		Object stat_obj = stat_type.getRegistry().get(Identifier.splitOn(stat_name, '.'));
		
		if (stat_obj instanceof Block) {
			@SuppressWarnings("unchecked")
			StatType<Block> stat_type_block = (StatType<Block>) stat_type;
			Stat<Block> stat = stat_type_block.getOrCreateStat((Block) stat_obj);
			return stat_handler.getStat(stat);
		} else if (stat_obj instanceof Item) {
			@SuppressWarnings("unchecked")
			StatType<Item> stat_type_item = (StatType<Item>) stat_type;
			Stat<Item> stat = stat_type_item.getOrCreateStat((Item) stat_obj);
			return stat_handler.getStat(stat);	
		} else if (stat_obj instanceof Identifier) {
			@SuppressWarnings("unchecked")
			StatType<Identifier> stat_type_ident = (StatType<Identifier>) stat_type;
			Stat<Identifier> stat = stat_type_ident.getOrCreateStat((Identifier) stat_obj);
			return stat_handler.getStat(stat);
		} else if (stat_obj instanceof EntityType<?>) {
			@SuppressWarnings("unchecked")
			StatType<EntityType<?>> stat_type_ident = (StatType<EntityType<?>>) stat_type;
			Stat<EntityType<?>> stat = stat_type_ident.getOrCreateStat((EntityType<?>) stat_obj);
			return stat_handler.getStat(stat);
		} else {
			source.sendError(new LiteralText("Unknown Object " + stat_obj.getClass().getName()));
			return -1;
		}
	}
}
