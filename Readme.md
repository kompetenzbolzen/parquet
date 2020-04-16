# Parquet

Minecraft Java Mod for Fabric Loader

## Commands

### Cameramode `/x` `/a` `/d`

* `x` Enter
* `a` Survival
* `d` Server default
	
Invocation Location is saved and restored on exit to make it less exploitable

Not using `/c` and `/s` to be carpet compatible

### `/savedata`

#### `playersave list`

List all player savefiles, UUIDs are resolved to names if in usercache

#### `stats`

Query user statistics and import them to the scoreboard. 

`/savedata stats PLAYER CRITERIA [import OBJECTIVE [MULTIPLIER]]`

If `MULTIPLIER < 0` the score is modified with `1/MULTIPLIER`, since FloatArgumentType does not support exponentials like 1.3e-4