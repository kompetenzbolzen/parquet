package re.jag.parquet.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;

public class CameraMode {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		 LiteralArgumentBuilder<ServerCommandSource> camermode = literal("c").
				executes((c) -> camera( c.getSource(), c.getSource().getPlayer())).
					then(argument("player", EntityArgumentType.player()).
						executes( (c) -> camera(c.getSource(), EntityArgumentType.getPlayer(c, "player"))));
		 LiteralArgumentBuilder<ServerCommandSource> survivalmode = literal("s").
				executes((c) -> survival( c.getSource(), c.getSource().getPlayer())).
					then(argument("player", EntityArgumentType.player()).
						executes( (c) -> survival(c.getSource(), EntityArgumentType.getPlayer(c, "player"))));
		 
		 dispatcher.register(camermode);
		 dispatcher.register(survivalmode);
	}
	
	private static int camera(ServerCommandSource source, ServerPlayerEntity player) {
		if (! ((CameraModeData)player).saveCameraPosition() ) {
			source.sendFeedback(new LiteralText("Gamemode is already camera"), false);
			return 0;
		}
		
		player.setGameMode(GameMode.SPECTATOR);
        player.addVelocity(0,0.1,0);
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 999999, 0, false, false));
        return 1;
	}
	
	private static int survival(ServerCommandSource source, ServerPlayerEntity player) {
		if (! ((CameraModeData)player).restoreCameraPosition() ) {
			source.sendFeedback(new LiteralText("Gamemode is already survival"), false);
			return 0;
		}
		
		player.setGameMode(GameMode.SURVIVAL);
        player.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(player.getEntityId(), StatusEffects.NIGHT_VISION));
        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        player.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(player.getEntityId(), StatusEffects.CONDUIT_POWER));
        player.removeStatusEffect(StatusEffects.CONDUIT_POWER);
		return 1;
	}
}
