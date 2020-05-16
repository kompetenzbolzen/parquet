# Parquet

Minecraft Java 1.16-pre Mod for [Fabric](https://fabricmc.net/) mod loader.

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

Query user statistics and import them to the scoreboard. This work for both online and offline players, if they are in
usercache.

`/savedata stats PLAYER STATISTIC [import OBJECTIVE [MULTIPLIER]]`

If `MULTIPLIER < 0` the score is modified with `1/MULTIPLIER`, since FloatArgumentType does not support exponentials
like 1.3e-4

## Features

### Dispenser Behavior

#### Water Bottle

Water Bottles can be filled with a Dispenser from a water source, just like Buckets.

#### Cauldron
A dispenser pointing into a cauldron can:

* add one fill level with a water bottle
* add up to 3 fill levels with a water bucket
* get a water bottle for one fill level
* get a water bucket if the cauldron is full
* remove the color of a shulker box

#### ShulkerBoxes

A dispenser pointing into an undyed Shulker Box can apply a color.

### Advanced Villager Trade tracking

`minecraft.custom:minecraft.traded_with_villager` only counts the amount of interactions with a Villager.
Parquet provides more granular tracking of trades by counting every item purchased from Villagers under
`minecraft.traded:<item>`.

**Note:** Since `/scoreboard` command completion is handled client-side, they might not show up and have to be entered
manually.

## Fixes

### [MC-111534](https://bugs.mojang.com/browse/MC-111534)

Stat `minecraft.used:minecraft.firework_rocket` is only incremented, when the rocket is used on the ground, not during
elytra flight.

## "Fixes"

### [MC-126244](https://bugs.mojang.com/browse/MC-126244)

Cartographer map trade replaced with dummy map around (0,0) to mitigate server crash in large worlds due to poor
implementation of locateStructure.
