package re.jag.parquet.commands;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ObjectiveCriteriaArgumentType;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.block.Block;

public class Savedata {
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
                	).
	                then(literal("stats").
	                		then(argument("player", EntityArgumentType.player()).
			                		then(argument("criteria", ObjectiveCriteriaArgumentType.objectiveCriteria()).
			                				executes((c)->print_player_stat(c.getSource(), EntityArgumentType.getPlayer(c, "player"), ObjectiveCriteriaArgumentType.getCriteria(c, "criteria"))).
			                				then(literal("import").then(argument("score", ObjectiveArgumentType.objective()).
			                						executes((c) -> import_stat_to_scoreboard(c.getSource(), EntityArgumentType.getPlayer(c, "player"), ObjectiveCriteriaArgumentType.getCriteria(c, "criteria"), 
			                								ObjectiveArgumentType.getWritableObjective(c, "score"), 1)).
			                						then(argument("multiplier", FloatArgumentType.floatArg()).
			                								executes((c) ->  import_stat_to_scoreboard(c.getSource(), EntityArgumentType.getPlayer(c, "player"), ObjectiveCriteriaArgumentType.getCriteria(c, "criteria"), 
			                										ObjectiveArgumentType.getWritableObjective(c, "score"), FloatArgumentType.getFloat(c, "multiplier")))))
		            						))
		            				)
                		)
                	;
		 
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
	
	private static int import_stat_to_scoreboard(ServerCommandSource source, ServerPlayerEntity player,ScoreboardCriterion stat, ScoreboardObjective objective, float multiplier) {
		int score = get_player_stat(source, player, stat);
		if (score < 0)
			return 0;
		
		ServerScoreboard server_scoreboard = source.getMinecraftServer().getScoreboard();
		ScoreboardPlayerScore player_score = server_scoreboard.getPlayerScore(player.getEntityName(), objective);
		
		int mod_score = multiplier >= 0 ? (int)((float)score * multiplier) : (int)((float)score / multiplier * (-1));
		
		player_score.setScore(mod_score);
		
		return 1;
	}
	
	private static int get_player_save_uuid(ServerCommandSource source, String uuid) {
		return 1;
	}

	private static int print_player_stat(ServerCommandSource source, ServerPlayerEntity player, ScoreboardCriterion stat) {
		Integer stat_value = get_player_stat(source, player, stat);
		
		if (stat_value >= 0)
		{
			source.sendFeedback(new LiteralText(stat_value.toString()), false);
			return 1;
		}
		return 0;
	}
	
	private static int get_player_stat(ServerCommandSource source, ServerPlayerEntity player, ScoreboardCriterion stat) {
		
		String criteria_name = stat.getName();
		int criteria_name_seperator = criteria_name.indexOf(':');
		
		String registry_name="";
		String stat_name="";
		
		if (criteria_name_seperator < 0) {
			source.sendError(new LiteralText("Invalid Argument"));
			return -1;
		} else {
			registry_name = criteria_name.substring(0,criteria_name_seperator);
			stat_name = criteria_name.substring(criteria_name_seperator + 1, criteria_name.length() );
		}
		
		//Not good but whatever
		int returned_stat = -1;
		try {
			switch(registry_name) {
			case "minecraft.mined":
				//Block
				returned_stat = get_stat_block(player, registry_name, stat_name);
				break;
			case "minecraft.crafted":
			case "minecraft.used":
			case "minecraft.broken":
			case "minecraft.picked_up":
			case "minecraft.dropped":
				returned_stat = get_stat_item(player, registry_name, stat_name);
				break;
			case "minecraft.custom":
				returned_stat = get_stat_custom(player, stat_name);
				//Custom
				break;
			default:
				source.sendError(new LiteralText("Unknown Statistic"));
				return -1;
			};
		} catch (CommandSyntaxException e) {
			source.sendError(new LiteralText("Failed: " + e.getContext()));
			return -1;
		}
	
		return returned_stat;
	}
	
	private static int get_stat_block(ServerPlayerEntity player, String registry_name, String stat_name) throws CommandSyntaxException {
		@SuppressWarnings("unchecked")
		StatType<Block> stat_type = (StatType<Block>) Registry.STAT_TYPE.get(Identifier.splitOn(registry_name, '.'));
		Block block = Registry.BLOCK.get(Identifier.splitOn(stat_name, '.'));
		Stat<Block> stat = stat_type.getOrCreateStat(block);	
		return player.getStatHandler().getStat(stat);
	}

	private static int get_stat_item(ServerPlayerEntity player, String registry_name, String stat_name) throws CommandSyntaxException {
		@SuppressWarnings("unchecked")
		StatType<Item> stat_type = (StatType<Item>) Registry.STAT_TYPE.get(Identifier.splitOn(registry_name, '.'));
		Item item = Registry.ITEM.get(Identifier.splitOn(stat_name, '.'));
		Stat<Item> stat = stat_type.getOrCreateStat(item);	
		return player.getStatHandler().getStat(stat);
	}
	
	private static int get_stat_custom(ServerPlayerEntity player, String stat_name) throws CommandSyntaxException {
		Stat<Identifier> stat = Stats.CUSTOM.getOrCreateStat(Identifier.splitOn(stat_name, '.'));
		return player.getStatHandler().getStat(stat);
		
	}
}
