package re.jag.parquet.commands;

import java.io.File;
import java.util.*;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.Registry;
import re.jag.parquet.Parquet;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;

public class Stats {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> savedata = literal("stats").
				requires(source -> source.hasPermissionLevel(4)).
				executes((c) -> help(c.getSource())).
				then(argument("player", StringArgumentType.word()).
						suggests((c, b) -> suggestMatching(c.getSource().getPlayerNames(), b)).
						then(literal("get").
								then(argument("criteria", StringArgumentType.greedyString()).
										suggests((c, b) -> suggestMatching(get_stat_list(), b)).
										executes((c) -> print_player_stat(
												c.getSource(),
												StringArgumentType.getString(c, "player"),
												StringArgumentType.getString(c, "criteria")
										))
								)).
						then(literal("import").then(argument("score", ScoreboardObjectiveArgumentType.scoreboardObjective()).
								executes((c) -> import_stat_to_scoreboard(
										c.getSource(),
										StringArgumentType.getString(c, "player"),
										ScoreboardObjectiveArgumentType.getWritableObjective(c, "score"), 1)).
								then(argument("multiplier", FloatArgumentType.floatArg()).
										executes((c) -> import_stat_to_scoreboard(
												c.getSource(),
												StringArgumentType.getString(c, "player"),
												ScoreboardObjectiveArgumentType.getWritableObjective(c, "score"),
												FloatArgumentType.getFloat(c, "multiplier"))
										)
								)
						))
				);
		dispatcher.register(savedata);
	}
	
	private static int help(ServerCommandSource source) {
		//TODO Help Text
		source.sendFeedback(new LiteralText("Hölp goes here"), false);
		return 1;
	}

	private static int test(ServerCommandSource source,  String string) {
		source.sendFeedback(new LiteralText(string),false);
		return 1;
	}

	private static List<String> get_stat_list() {
		List<String> ret = Lists.newArrayList();
		Iterator stat_type_iter = Registry.STAT_TYPE.iterator();

		while(stat_type_iter.hasNext()) {
			StatType<Object> statType = (StatType)stat_type_iter.next();
			Iterator stat_iter = statType.getRegistry().iterator();

			while(stat_iter.hasNext()) {
				Object object = stat_iter.next();
				String stat_name = Stat.getName(statType, object);
				ret.add(stat_name);
			}
		}

		return ret;
	}

	private static Stat<?> get_stat_from_string(String argument) {

		int criteria_name_seperator = argument.indexOf(':');

		String registry_name="";
		String stat_name="";

		if (criteria_name_seperator < 0) {
			return null;
		} else {
			registry_name = argument.substring(0,criteria_name_seperator);
			stat_name = argument.substring(criteria_name_seperator + 1, argument.length() );
		}

		StatType<Object> stat_type = (StatType<Object>) Registry.STAT_TYPE.get(Identifier.splitOn(registry_name, '.'));
		Object stat_obj = stat_type.getRegistry().get(Identifier.splitOn(stat_name, '.'));

		Stat stat = stat_type.getOrCreateStat(stat_obj);

		return stat;
	}

	private static int import_stat_to_scoreboard(ServerCommandSource source, String player, ScoreboardObjective objective, float multiplier) {
		int score = get_player_stat(source, player, objective.getCriterion().getName());
		if (score < 0)
			return 0;
		
		ServerScoreboard server_scoreboard = source.getWorld().getServer().getScoreboard();
		ScoreboardPlayerScore player_score = server_scoreboard.getPlayerScore(player, objective);


		int mod_score = multiplier >= 0 ? (int)((float)score * multiplier) : (int)((float)score / multiplier * (-1));
		
		player_score.setScore(mod_score);
		
		return 1;
	}
	
	private static ServerStatHandler get_player_stat_handler(ServerCommandSource source, String player_name) {
		ServerPlayerEntity player = source.getWorld().getServer().getPlayerManager().getPlayer(player_name);
		if (player != null) {
			return player.getStatHandler();
		}

		// [ ] TODO 1.16 fix test
		Optional<GameProfile> opt_profile = source.getWorld().getServer().getUserCache().findByName(player_name);
		if (opt_profile.isEmpty()) {
			Parquet.LOG.debug("Savedata: User not in Usercache");
			return null;
		}

		GameProfile profile = opt_profile.get();

		UUID player_uuid = profile.getId();

		File file = source.getWorld().getServer().getSavePath(WorldSavePath.STATS).toFile();
		File file2 = new File(file, player_uuid + ".json");

		if (!file2.exists()) {
			Parquet.LOG.debug("Savedata: user stat file not found");
			return null;
		}

		return new ServerStatHandler(source.getWorld().getServer(), file2);
	}

	private static int print_player_stat(ServerCommandSource source, String player, String stat) {
		Integer stat_value = get_player_stat(source, player, stat);
		
		if (stat_value >= 0)
		{
			source.sendFeedback(new LiteralText(stat_value.toString()), false);
			return 1;
		}
		return 0;
	}
	
	private static int get_player_stat(ServerCommandSource source, String player_name, String stat_name) {
		ServerStatHandler stat_handler = get_player_stat_handler(source, player_name);
		
		if (stat_handler == null) {
			source.sendError(new LiteralText("Failed to get Statistics for Player " + player_name));
			return -1;
		}

		Stat stat = get_stat_from_string(stat_name);

		if (stat == null) {
			source.sendError(new LiteralText("Invalid stat: " + stat_name));
			return -1;
		}

		return stat_handler.getStat(stat);
	}
}
