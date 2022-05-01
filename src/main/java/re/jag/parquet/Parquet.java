package re.jag.parquet;

import net.minecraft.item.MusicDiscItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.DyeColor;
import re.jag.parquet.commands.*;

import re.jag.parquet.dispenser.*;

public class Parquet implements ModInitializer {
	public static final Logger LOG = LogManager.getLogger();

	public static final ParquetSettings settings = new ParquetSettings("parquet.properties");

	@Override
	public void onInitialize() {

	}
	
	public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		if(settings.command_stats) Stats.register(dispatcher);
		if(settings.command_cameramode) CameraMode.register(dispatcher);
		if(settings.command_calculator) Calculator.register(dispatcher);
		if(settings.command_timediff) TimeDIff.register(dispatcher);
		if(settings.command_rename) Rename.register(dispatcher);

		LOG.info("[PQ] Registered commands");
	}
	
	public static void onBootstrap() {
		//settings = new ParquetSettings("parquet.properties");

		// CustomStats.register_custom_stats();

		if (settings.dispenser_custom_behavior) register_dispenser_behavior();
	}

	private static void register_dispenser_behavior() {
		//This is a rather hacky implementation
		DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerPlacementDispenserBehavior());
		for (DyeColor dye_color : DyeColor.values()) {
			DispenserBlock.registerBehavior(ShulkerBoxBlock.get(dye_color).asItem(), new ShulkerPlacementDispenserBehavior());

			DispenserBlock.registerBehavior(DyeItem.byColor(dye_color).asItem(), new DyeItemDispenserBehavior());
		}

		// MusicDiscItem.MUSIC_DISCS Map is private.
		MusicDiscItem[] music_discs = {
				(MusicDiscItem) Items.MUSIC_DISC_13,
				(MusicDiscItem) Items.MUSIC_DISC_CAT,
				(MusicDiscItem) Items.MUSIC_DISC_BLOCKS,
				(MusicDiscItem) Items.MUSIC_DISC_CHIRP,
				(MusicDiscItem) Items.MUSIC_DISC_FAR,
				(MusicDiscItem) Items.MUSIC_DISC_MALL,
				(MusicDiscItem) Items.MUSIC_DISC_MELLOHI,
				(MusicDiscItem) Items.MUSIC_DISC_STAL,
				(MusicDiscItem) Items.MUSIC_DISC_STRAD,
				(MusicDiscItem) Items.MUSIC_DISC_WARD,
				(MusicDiscItem) Items.MUSIC_DISC_11,
				(MusicDiscItem) Items.MUSIC_DISC_WAIT,
				(MusicDiscItem) Items.MUSIC_DISC_PIGSTEP
		};
		for (MusicDiscItem item : music_discs)
			DispenserBlock.registerBehavior(item, new MusicDiscDispenserBehavior());

		DispenserBlock.registerBehavior(Items.WATER_BUCKET, new WaterBucketDispenserBehavior());
		DispenserBlock.registerBehavior(Items.BUCKET, new BucketDispenserBehavior());
		DispenserBlock.registerBehavior(Items.GLASS_BOTTLE, new GlassBottleDispenserBehavior());
		DispenserBlock.registerBehavior(Items.POTION, new PotionDispenserBehavior());

		LOG.info("[PQ] Registered Custom Dispenser behaviors");
	}

}
