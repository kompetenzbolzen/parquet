package re.jag.parquet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ParquetSettings {
	Properties properties = new Properties();

	public final boolean dispenser_custom_behavior;

	public final boolean stats_villager_trades;
	public final boolean stats_send_to_client;

	public final boolean command_cameramode;
	public final boolean command_stats;
	public final boolean command_calculator;
	public final boolean command_rename;
	public final boolean command_timediff;

	public final boolean better_no_fire_tick;

	private boolean changed_config = false;

	public ParquetSettings(String _properties_file) {
		Path path = FileSystems.getDefault().getPath(_properties_file);
		load(path);

		this.dispenser_custom_behavior = get_boolean("dispenser-custom-behavior", true);

		this.stats_villager_trades = get_boolean("stats-villager-trades", true);
		this.stats_send_to_client = get_boolean("stats-send-to-client", false);

		this.command_cameramode = get_boolean("command-cameramode", true);
		this.command_stats = get_boolean("command-stats", true);
		this.command_calculator = get_boolean("command-calculator", true);
		this.command_rename = get_boolean("command-rename", true);
		this.command_timediff = get_boolean("command-timediff", true);

		this.better_no_fire_tick = get_boolean("better-no-fire-tick", false);

		if(changed_config)
			save(path);
	}

	private void load(Path _path) {
		try {
			InputStream inp = Files.newInputStream(_path);
			properties.load(inp);
		} catch (IOException e) {
			Parquet.LOG.warn("Failed to read from " + _path.getFileName());
		}
	}

	private void save(Path _path) {
		try {
			OutputStream out = Files.newOutputStream(_path);
			properties.store(out, "Parquet mod settings");
		} catch (IOException e) {
			Parquet.LOG.error("Failed to write to " + _path.getFileName());
		}
	}

	private String get(String _name, String _default) {
		String val = properties.getProperty(_name);
		if (val == null) {
			properties.setProperty(_name, _default);
			changed_config = true;
			return _default;
		}
		return val;
	}

	private boolean get_boolean(String _name, boolean _default) {
		return Boolean.parseBoolean(get(_name, String.valueOf(_default)));
	}

	private String get_string(String _name, String _default) {
		return get(_name, _default);
	}

	private int get_int(String _name, int _default) {
		return Integer.parseInt(get(_name, String.valueOf(_default)));
	}
}
