package re.jag.parquet.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import re.jag.parquet.Parquet;
import re.jag.parquet.interfaces.CameraModeData;

public class CameraMode {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		/*
		 * If not used with fabric-carpet, /s and /c can be used.
		 */
		LiteralArgumentBuilder<ServerCommandSource> camermode = literal("x").
				executes((c) -> camera( c.getSource(), c.getSource().getPlayer())).
					then(argument("player", EntityArgumentType.player()).
						executes( (c) -> camera(c.getSource(), EntityArgumentType.getPlayer(c, "player"))));
		LiteralArgumentBuilder<ServerCommandSource> survivalmode = literal("a").
				executes((c) -> survival( c.getSource(), c.getSource().getPlayer())).
					then(argument("player", EntityArgumentType.player()).
						executes( (c) -> survival(c.getSource(), EntityArgumentType.getPlayer(c, "player"))));
		LiteralArgumentBuilder<ServerCommandSource> defaultmode = literal("d").
				executes((c) -> defmode( c.getSource(), c.getSource().getPlayer())).
					then(argument("player", EntityArgumentType.player()).
						executes( (c) -> defmode(c.getSource(), EntityArgumentType.getPlayer(c, "player"))));
		 
		dispatcher.register(camermode);
		dispatcher.register(survivalmode);
		dispatcher.register(defaultmode);
	}
	
	private static void add_status_effects(ServerPlayerEntity player) {
		
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 999999, 0, false, false));
	}
	
	private static void remove_status_effects(ServerPlayerEntity player) {
		player.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(player.getId(), StatusEffects.NIGHT_VISION));
        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        player.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(player.getId(), StatusEffects.CONDUIT_POWER));
        player.removeStatusEffect(StatusEffects.CONDUIT_POWER);
	}
	
	private static int camera(ServerCommandSource source, ServerPlayerEntity player) {
		if (! ((CameraModeData)player).saveCameraPosition() ) {
			//source.sendFeedback(new LiteralText("Reset location already exists"), false);
			Parquet.LOG.debug("x: Reset location already exists for " + player.getName());
		} else {
			add_status_effects(player);
		}

		player.changeGameMode(GameMode.SPECTATOR);
        player.addVelocity(0,0.1,0);
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
        
        return 1;
	}
	
	private static int survival(ServerCommandSource source, ServerPlayerEntity player) {
		if (! ((CameraModeData)player).restoreCameraPosition() ) {
			//source.sendFeedback(new LiteralText("No reset location stored"), false);
			Parquet.LOG.debug("a: No reset location for " + player.getName());
		} else {
			remove_status_effects(player);
		}
		
		player.changeGameMode(GameMode.SURVIVAL);
        
		return 1;
	}
	
	//Reset to server default gamemode
	private static int defmode(ServerCommandSource source, ServerPlayerEntity player) {
		if (! ((CameraModeData)player).restoreCameraPosition() ) {
			//source.sendFeedback(new LiteralText("No reset location stored"), false);
			Parquet.LOG.debug("d: No reset location for " + player.getName());
		} else {
			remove_status_effects(player);
		}
		
		player.changeGameMode( player.getServer().getDefaultGameMode() );

		return 1;
	}
}
