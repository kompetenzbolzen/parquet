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

## Features

### Dispenser Behavior

#### Cauldron
A dispenser pointing into a cauldron can:

* add one fill level with a water bottle
* add up to 3 fill levels with a water bucket
* get a water bottle for one fill level
* get a water bucket if the cauldron is full
* remove the color of a shulker box

#### ShulkerBoxes

A dispenser pointing into an undyed Shulker Box can apply a color.

## Fixes

### [MC-111534](https://bugs.mojang.com/browse/MC-111534)

Stat `minecraft.used:minecraft.firework_rocket` is only incremented, when the rocket is used on the ground, not during elytra flight.
