# Configuration

Every setting of Adventure Backpack is in one file, `config/adventurebackpack.cfg`. The game writes it the first time it starts, with every key and its comment. This page lists all 271 keys.

You can also edit the file from the game: in the Mods list, select Adventure Backpack and press Config. Changes made in the file need a restart of the game. Keys marked "Needs a restart" are read when items, blocks or keys are created, so they need one even with the config screen.

## How the file is laid out

The file is a tree under `adventurebackpack`. First comes `enabled`, with one switch per system. Then each system has its own section with its own switches and numbers. The tables below drop the `adventurebackpack` prefix: `enabled.hose` is the `hose` key in the `enabled` section.

- A disabled system stays registered, so worlds and servers with different files still load. It is hidden, has no recipe and does nothing.
- Time is in ticks (20 per second) or seconds as each key says. Fluid is in millibuckets (mB): 1000 mB is one bucket. Speeds are in blocks per tick.
- Values outside the range are brought back into it. A value of the wrong type is replaced by the default.
- Use the same file on the server and on every client for the keys that say so. The `render` and `keys` sections only matter on the client.
- Keys from the older flat file (`general` section) move to the new places the first time the game starts, keeping their values. The table at the end lists them.

## Contents

- [Systems](#systems-enabled), [Backpack](#backpack-backpack), [Backpack types](#backpack-types-backpackscolour), [Abilities](#abilities-abilities), [Hose](#hose-hose), [Copter pack](#copter-pack-copterpack), [Steam jetpack](#steam-jetpack-steamjetpack)
- [Scuba gear](#scuba-gear-scuba), [Air compressor](#air-compressor-scubacompressor), [Flamethrower](#flamethrower-flamethrower), [Fuels](#fuels-fuels), [Fuel station](#fuel-station-fuelstation), [Distillery](#distillery-distillery), [Placed gear](#placed-gear-placedgear)
- [Inflatable boat](#inflatable-boat-inflatableboat), [Boat motor](#boat-motor-inflatableboatmotor)
- [Crossbow](#clockwork-crossbow-crossbow), [Crossbow Magazine](#crossbow-magazine-crossbowmagazine), [Ammunition](#ammunition-crossbowammo), [Grappling hook](#grappling-hook-crossbowgrapple), [Sniper mode](#sniper-mode-crossbowsniper), [Gatling mode](#gatling-mode-crossbowquiver), [Piston boots](#piston-boots-pistonboots), [Campfire](#campfire-cooking-campfirecooking), [Sleeping bag](#sleeping-bag-sleepingbag)
- [Display](#display-render), [Keys](#keys-keys), [Tool swapping keys](#tool-swapping-keys-keystoolcycle)
- [Backpacks in the world](#backpacks-in-the-world-worldgen), [Extra recipes](#extra-recipes-recipes), [Small tweaks](#small-tweaks-tweaks), [Tool slots](#tool-slots-tweakstoolcycle), [Grave mods](#grave-mods-compatgraves), [Baubles](#baubles-compatbaubles)
- [Earlier flight values](#flight-values-before-100)

## Systems (`enabled`)

One switch per system. A disabled system stays registered, so worlds and servers still load, but it is hidden, has no recipe and does nothing.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `enabled.abilities` | boolean | `true` |  | Backpack abilities. When false every backpack is only cosmetic (the lightning transformation still works). Set it the same on server and clients. |
| `enabled.hose` | boolean | `true` |  | The Backpack Hose. |
| `enabled.copterPack` | boolean | `true` |  | The Copter Pack. It works when some loaded mod adds a fluid of fuels.list (BuildCraft, Forestry, Railcraft, GregTech...); with none of them it stays in the creative tab but cannot be crafted and does nothing. |
| `enabled.steamJetpack` | boolean | `true` |  | The Steam Jetpack. |
| `enabled.scuba` | boolean | `true` |  | The Scuba Gear, its Air Tanks and Diving Mask, and the Air Compressor. |
| `enabled.inflatableBoat` | boolean | `true` |  | The Inflatable Boat and the Motorized Inflatable Boat. |
| `enabled.crossbow` | boolean | `true` |  | The Clockwork Crossbow, its Crossbow Magazines and the Crossbow Bolts. |
| `enabled.sleepingBag` | boolean | `true` |  | The sleeping bag: deploying it from a placed backpack, and placing the Sleeping Bag item on its own (sleepingbag.placeable). Bags already on the ground still work and can be picked up. |
| `enabled.pistonBoots` | boolean | `true` |  | The Piston Boots: step assist, sprint speed and fall protection. |
| `enabled.campfire` | boolean | `true` |  | Cooking on the Campfire (see campfire.cooking). The campfire block, its recipe and the respawn point stay either way. |
| `enabled.quiver` | boolean | `true` |  | The quiver of the Skeleton Backpack (`backpacks.skeleton.quiver`): it takes the place of the right tank, so that backpack has one tank and a few slots that only take arrows and crossbow bolts. When false the Skeleton Backpack has its two tanks again; arrows already in the quiver are kept in the backpack and come back when it is turned on. Set it the same on server and clients. |
| `enabled.fuelStation` | boolean | `true` |  | The Fuel Station block. When false it has no recipe, is hidden from the creative tab, and a placed one neither takes nor gives fuel (it can still be broken, and keeps its fuel). |
| `enabled.distillery` | boolean | `true` |  | The Distillery block, which makes ethanol for the fuel users. When false it has no recipe, is hidden from the creative tab, and a placed one does nothing: no batches, no push, no fluid in or out (it can still be broken and drops its items). Set it the same on server and clients. |
| `enabled.placedGear` | boolean | `true` |  | Placing the Steam Jetpack, the Copter Pack, the Scuba Gear and the Flamethrower Pack on the ground as a block (see placedgear for each one). When false they cannot be placed; gear already placed does nothing (no window, no fluid in or out) but can still be picked up with everything it holds. Set it the same on server and clients. |
| `enabled.flamethrower` | boolean | `true` |  | The Flamethrower Pack, the Flamethrower nozzle and the Pyro Mask. When false they have no recipe, are hidden from the creative tab, and the nozzle does not fire. Set it the same on server and clients. |

## Backpack (`backpack`)

The Adventure Backpack itself, worn or placed.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `backpack.placeOnDeath` | boolean | `true` |  | Place the backpack as a block where the player dies. When false it drops as an item. |
| `backpack.tankCapacity` | int | `4000` | 1000 to 64000 | Capacity of each of the two backpack tanks, in millibuckets. Use the same value on server and clients. Lowering it does not delete fluid already stored above the new limit. Needs a restart. |
| `backpack.blastResistance` | double | `2000.0` | 0.0 to 6000000.0 | Explosion resistance of a placed backpack. 2000 is obsidian, which survives TNT and creepers; 10 is stone. Needs a restart. |
| `backpack.hardness` | double | `1.0` | 0.0 to 100.0 | How long a placed backpack takes to break by hand. Stone is 1.5. Needs a restart. |
| `backpack.deathMessage` | boolean | `true` |  | On death, tell the player in chat where the worn backpack, copter pack or jetpack went (placed, dropped or in the grave) with its coordinates. |
| `backpack.wornSyncIntervalTicks` | int | `4` | 1 to 20 | Worn gear (backpack, jetpack, copter pack...) that changes every tick, such as a boiling jetpack, a burning firebox or a Cow backpack filling a tank, tells its wearer and the players near about it at most once every this many ticks (20 per second). A single change is sent at once. 1 sends every tick. |

## Backpack types (`backpacks.<colour>`)

One category per backpack type, named after its colour in lower case: `backpacks.standard`, `backpacks.cow`, `backpacks.irongolem` and so on for all 81 types. Each one has the same three keys, with `enabled` first. `rows` and `columns` are the size of the storage grid of that backpack's window. The 3x3 crafting grid takes the bottom right corner of the grid, so a backpack has rows x columns - 9 storage slots: the default 6 rows of 8 columns give the classic 39. The tanks, the tool slots, the buttons and your inventory move with the grid, and the window background is drawn to fit.

A backpack never loses slots. A backpack that already holds more slots than its type now gives keeps its size, so a smaller size only applies to new backpacks; a bigger size applies to every backpack of that type the next time it is opened. The size is read when a backpack is opened or loaded (no restart), but set it the same on the server and on every client: the window must have the same slots on both sides. An addon ability can also ask for a bigger window for its colour (see the [API](API) page).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `backpacks.<colour>.enabled` | boolean | `true` |  | The backpack type. When false it has no crafting recipe and is hidden from the creative tab and NEI; backpacks of this type that already exist keep working. Needs a restart for the recipe. |
| `backpacks.<colour>.rows` | int | `6` | 4 to 10 | Rows of the storage grid of this backpack's window. Set it the same on server and clients. |
| `backpacks.<colour>.columns` | int | `8` | 8 to 12 | Columns of the storage grid of this backpack's window. Set it the same on server and clients. |

For example, `backpacks.diamond.rows = 7` and `backpacks.diamond.columns = 9` give the Diamond Backpack 54 storage slots.

### Skeleton quiver (`backpacks.skeleton.quiver`)

The Skeleton Backpack carries a quiver in the place of its right tank, while `enabled.quiver` is on. Its slots only take the items of the list. Bows drawn by the wearer take their arrows from the backpack, quiver first (`abilities.skeleton`). The model shows a few arrows in the quiver when it holds some.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `backpacks.skeleton.quiver.slots` | int | `4` | 1 to 6 | Slots of the quiver, each holding one stack. They fill the column of the right tank, then the column of its bucket slots. Lowering it keeps the arrows of the slots that go away in the backpack (they come back when the slots do). Set it the same on server and clients. |
| `backpacks.skeleton.quiver.items` | string list | `item:minecraft:arrow`, `item:adventurebackpack:bolt` |  | What the quiver slots take, one per line: `item:modid:name` (any damage value), `item:modid:name:meta` (that damage value only), `ore:name` (an Ore Dictionary name) or `modid:name`. The default is vanilla arrows and the Crossbow Bolts. Set it the same on server and clients. |

A Skeleton Backpack saved before the quiver keeps its fluid: what fits moves from the right tank into the left one, and the rest stays sealed in the right tank. A sealed tank takes nothing more, the hose can still empty it, and it is back as a normal tank when `enabled.quiver` is turned off.

## Abilities (`abilities`)

Special effects of the backpacks while they are worn (the Cactus and Melon also work placed). Each backpack has its own switch under its own name. Ticks are 20 per second. Use the same values on the server and on every client.

### All abilities

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.effectRefreshTicks` | int | `40` | 2 to 200 | Ticks left on a long potion effect (fire resistance, Dragon effects) under which the ability renews it. |
| `abilities.toggleMessage` | boolean | `true` |  | Tell the player in chat when the Gear On/Off key (keys.ability) or the button in the backpack window turns the worn backpack's abilities off or on. Each player's choice is kept with the player; enabled.abilities false keeps them off for everyone. |

### Bat backpack (`abilities.bat`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.bat.enabled` | boolean | `true` |  | Bat Backpack: night vision. |
| `abilities.bat.nightVisionTicks` | int | `5000` | 400 to 1000000 | Length of the night vision effect it gives. |
| `abilities.bat.nightVisionRefreshBelow` | int | `260` | 201 to 1000 | Ticks left under which night vision is renewed. Keep it above 200: vanilla makes the screen flicker in the last 200 ticks. |

### Squid backpack (`abilities.squid`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.squid.enabled` | boolean | `true` |  | Squid Backpack: water breathing and night vision while in water. |
| `abilities.squid.worksOnLand` | boolean | `false` |  | Give the water breathing and the night vision out of water too, for mods that make the player breathe water (merfolk). Both are taken away when the backpack comes off or this is turned off. |

### Pigman backpack (`abilities.pigman`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.pigman.enabled` | boolean | `true` |  | Pigman Backpack: fire resistance. |
| `abilities.pigman.fireResistanceTicks` | int | `5000` | 100 to 1000000 | Length of the fire resistance effect it gives. |

### Dragon backpack (`abilities.dragon`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.dragon.enabled` | boolean | `true` |  | Dragon Backpack: fire resistance, water breathing, night vision, strength and regeneration. |
| `abilities.dragon.effectTicks` | int | `5000` | 100 to 1000000 | Length of the strength and regeneration effects. |
| `abilities.dragon.strengthAmplifier` | int | `1` | 0 to 4 | Strength level minus one (0 = Strength I, 1 = Strength II). |
| `abilities.dragon.regenerationAmplifier` | int | `0` | 0 to 4 | Regeneration level minus one (0 = Regeneration I). |

### Cactus backpack (`abilities.cactus`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.cactus.enabled` | boolean | `true` |  | Cactus Backpack: fills its tanks with water in water or rain. |
| `abilities.cactus.wornIntervalTicks` | int | `5` | 1 to 1200 | Ticks between two fills while worn. |
| `abilities.cactus.wornWaterInWater` | int | `2` | 0 to 1000 | Millibuckets added to each tank per fill while the player is in water. |
| `abilities.cactus.wornWaterInRain` | int | `1` | 0 to 1000 | Millibuckets added to each tank per fill while the player is under rain. Adds up with the water amount. |
| `abilities.cactus.placedIntervalTicks` | int | `5` | 1 to 1200 | Ticks between two fills of a placed backpack under rain. |
| `abilities.cactus.placedRainAmount` | int | `2` | 0 to 1000 | Millibuckets added to each tank per fill of a placed backpack under rain. |

### Melon backpack (`abilities.melon`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.melon.enabled` | boolean | `true` |  | Melon Backpack: fills its tanks with melon juice in water or rain. |
| `abilities.melon.wornIntervalTicks` | int | `5` | 1 to 1200 | Ticks between two fills while worn. |
| `abilities.melon.wornJuiceInWater` | int | `2` | 0 to 1000 | Millibuckets added to each tank per fill while the player is in water. |
| `abilities.melon.wornJuiceInRain` | int | `1` | 0 to 1000 | Millibuckets added to each tank per fill while the player is under rain. Adds up with the water amount. |
| `abilities.melon.placedIntervalTicks` | int | `5` | 1 to 1200 | Ticks between two fills of a placed backpack under rain. |
| `abilities.melon.placedRainAmount` | int | `2` | 0 to 1000 | Millibuckets added to each tank per fill of a placed backpack under rain. |

### Pig backpack (`abilities.pig`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.pig.enabled` | boolean | `true` |  | Pig Backpack: oinks now and then. |
| `abilities.pig.oinkMaxSeconds` | int | `60` | 1 to 3600 | Longest wait between two oinks; each wait is random between 0 and this. |

### Slime backpack (`abilities.slime`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.slime.enabled` | boolean | `true` |  | Slime Backpack: slime trail and sound while sprinting, slippery stops. |
| `abilities.slime.trailIntervalTicks` | int | `5` | 1 to 200 | Ticks between two slime particles and sounds while sprinting. |
| `abilities.slime.fullMoonSpeed` | boolean | `true` |  | Give Speed II at night under a full moon. |

### Chicken backpack (`abilities.chicken`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.chicken.enabled` | boolean | `true` |  | Chicken Backpack: lays an egg now and then. |
| `abilities.chicken.eggMinSeconds` | int | `200` | 1 to 86400 | Shortest wait between two eggs. |
| `abilities.chicken.eggRandomSeconds` | int | `90` | 0 to 86400 | Random extra wait added to the shortest one (0 to this). |

### Creeper backpack (`abilities.creeper`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.creeper.enabled` | boolean | `true` |  | Creeper Backpack: sneak near another player to play the creeper hiss. |
| `abilities.creeper.scareCooldownSeconds` | int | `120` | 1 to 86400 | Wait after a hiss before the next one. |
| `abilities.creeper.checkIntervalTicks` | int | `20` | 1 to 1200 | Ticks between two searches for a nearby player while sneaking. |
| `abilities.creeper.scareRange` | double | `3.0` | 1.0 to 5.0 | Distance in blocks to the other player for the hiss. |
| `abilities.creeper.deathExplosion` | boolean | `true` |  | Explode when the wearer dies. The explosion does not break blocks. |
| `abilities.creeper.deathExplosionPower` | double | `4.0` | 0.0 to 10.0 | Power of the death explosion (TNT is 4). |

### Cow backpack (`abilities.cow`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.cow.enabled` | boolean | `true` |  | Cow Backpack: eats wheat from its inventory and fills a tank with milk. |
| `abilities.cow.wheatPerBatch` | int | `16` | 1 to 64 | Wheat eaten to make one batch of milk. |
| `abilities.cow.milkPerBatch` | int | `1000` | 1 to 64000 | Millibuckets of milk per batch, made one per tick. |
| `abilities.cow.eatIntervalTicks` | int | `20` | 1 to 1200 | Ticks between two wheat. |

### Mooshroom backpack (`abilities.mooshroom`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.mooshroom.enabled` | boolean | `true` |  | Mooshroom Backpack: eats wheat from its inventory and fills a tank with mushroom stew. |
| `abilities.mooshroom.wheatPerBatch` | int | `16` | 1 to 64 | Wheat eaten to make one batch of stew. |
| `abilities.mooshroom.stewPerBatch` | int | `1000` | 1 to 64000 | Millibuckets of stew per batch, made one per tick. |
| `abilities.mooshroom.eatIntervalTicks` | int | `20` | 1 to 1200 | Ticks between two wheat. |

### Wolf backpack (`abilities.wolf`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.wolf.enabled` | boolean | `true` |  | Wolf Backpack: angry wolves that target the wearer calm down. |
| `abilities.wolf.checkIntervalTicks` | int | `20` | 1 to 1200 | Ticks between two searches for wolves. |
| `abilities.wolf.range` | double | `16.0` | 1.0 to 64.0 | Horizontal search distance in blocks. |

### Ocelot backpack (`abilities.ocelot`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.ocelot.enabled` | boolean | `true` |  | Ocelot Backpack: creepers run away from the wearer. |
| `abilities.ocelot.checkIntervalTicks` | int | `20` | 1 to 1200 | Ticks between two searches for creepers. |
| `abilities.ocelot.range` | double | `16.0` | 1.0 to 64.0 | Horizontal search distance in blocks. |

### Rainbow backpack (`abilities.rainbow`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.rainbow.enabled` | boolean | `true` |  | Rainbow Backpack: eating a golden apple starts the Nyan run (speed, jump, particles). |
| `abilities.rainbow.durationSeconds` | int | `150` | 4 to 3600 | Length of the Nyan run, including the 3 second intro. The song lasts 150 seconds. |
| `abilities.rainbow.speedAmplifier` | int | `2` | 0 to 9 | Speed level minus one during the run. |
| `abilities.rainbow.jumpAmplifier` | int | `2` | 0 to 9 | Jump boost level minus one during the run. |
| `abilities.rainbow.sound` | boolean | `true` |  | Play the song (client only). |

### Sunflower backpack (`abilities.sunflower`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.sunflower.enabled` | boolean | `true` |  | Sunflower Backpack: feeds the wearer in the sun. |
| `abilities.sunflower.intervalSeconds` | int | `120` | 1 to 3600 | Seconds between two meals. |
| `abilities.sunflower.food` | int | `2` | 0 to 20 | Hunger points per meal (2 = one drumstick). |
| `abilities.sunflower.saturation` | double | `0.2` | 0.0 to 2.0 | Saturation modifier per meal. |

### Skeleton backpack (`abilities.skeleton`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.skeleton.enabled` | boolean | `true` |  | Skeleton Backpack: bows take arrows from the backpack, quiver first, and arrows leave faster (`arrowSpeedBonus`). |
| `abilities.skeleton.arrowSpeedBonus` | double | `0.3` | 0.0 to 2.0 | Extra speed of the arrows and Crossbow Bolts the wearer shoots, as a part of their normal speed (0.30 = 30 % faster). Arrows hurt in proportion to their speed, so they also hit about this much harder. 0 turns it off. |

### Iron Golem backpack (`abilities.irongolem`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.irongolem.enabled` | boolean | `true` |  | Iron Golem Backpack: no fall damage. |

### Spider backpack (`abilities.spider`)

Taming works with the Spider Backpack on. A tamed spider is kept for good, like a wolf: it does not despawn and it is not set free when its owner dies, logs out or changes dimension. The number of tame spiders of each player is saved with the world. See [Backpacks](Backpacks#taming-spiders).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.spider.enabled` | boolean | `true` |  | Spider Backpack: tame spiders and ride them. Wearing it, right click a spider with the tame item to try to tame it (like taming a wolf). A tamed spider is yours for good, like a wolf: it stays when you log out, die or change dimension, follows you (with or without the backpack) and climbs walls after you. Right click it to ride it while wearing the Spider Backpack; sneak and right click to make it sit or stand up; right click with the heal item to heal it; sneak and right click with the tame item to set it free. When false, no new spider can be tamed and nobody can ride one; tamed spiders keep following, sitting and healing. |
| `abilities.spider.tameItem` | string | `minecraft:spider_eye` |  | Item used to tame a spider, as modid:name. One is used up per try (not in creative). Sneaking and right clicking your tamed spider with it sets the spider free. |
| `abilities.spider.tameChance` | double | `0.33` | 0.0 to 1.0 | Chance that one try tames the spider (1 = always, the wolf is about 0.33). |
| `abilities.spider.maxTamed` | int | `2` | 0 to 16 | Tamed spiders a player may have at once. 0 turns taming off. |
| `abilities.spider.tamedHealth` | double | `20.0` | 1.0 to 100.0 | Max health of a tamed spider, in half hearts (a wild one has 16). Taming heals it to this. Applied again when a spider loads, so a change reaches existing spiders. |
| `abilities.spider.healItem` | string | `minecraft:rotten_flesh` |  | Item that heals your tamed spider on right click, as modid:name. One is used up per heal (not in creative); nothing happens at full health. |
| `abilities.spider.healAmount` | int | `4` | 1 to 40 | Health one heal item gives back, in half hearts. |
| `abilities.spider.followStartDistance` | double | `6.0` | 2.0 to 32.0 | A tamed spider starts walking to its owner when it is farther than this, in blocks. |
| `abilities.spider.followStopDistance` | double | `3.0` | 1.0 to 32.0 | It stops when it is this close to its owner, in blocks. |
| `abilities.spider.teleportDistance` | int | `24` | 0 to 128 | When it cannot walk to its owner and is farther than this, it jumps next to the owner like a wolf. 0 = never. |
| `abilities.spider.teleportStuckDistance` | int | `8` | 0 to 64 | When it gets no closer for twice stuckTicks and is at least this far from its owner, it jumps next to the owner. 0 = only when farther than teleportDistance. |
| `abilities.spider.teleportCooldownTicks` | int | `100` | 20 to 1200 | Ticks between two jumps next to the owner. |
| `abilities.spider.verifyLoadsChunks` | boolean | `true` |  | On login, and when the maxTamed limit blocks a tame, check that every spider counted for the player still exists, loading its chunk for a moment if needed. Counted spiders that are gone (removed by a world edit, for example) stop counting. When false, only spiders in loaded chunks are checked. |
| `abilities.spider.smartClimb` | boolean | `true` |  | A tamed spider plans its way up walls and towers to an owner above, and down cliffs to an owner below, and only climbs when it needs to (it does not hang on trees or walls while wandering). When false it climbs any wall it touches, like a wild spider, and only walks and teleports to follow. |
| `abilities.spider.climbMaxHeight` | int | `32` | 2 to 128 | Highest wall it climbs up or down to follow its owner, in blocks. |
| `abilities.spider.safeDropHeight` | int | `3` | 0 to 8 | It walks off a ledge this high or lower instead of climbing down it. |
| `abilities.spider.repathTicks` | int | `10` | 5 to 40 | Ticks between two path checks while it follows its owner. |
| `abilities.spider.stuckTicks` | int | `60` | 20 to 400 | Ticks without getting closer before it tries another way up; after twice this it jumps next to its owner (see teleportStuckDistance) or rests a while. |

### Blaze backpack (`abilities.blaze`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.blaze.enabled` | boolean | `true` |  | Blaze Backpack: fire immunity and burning punches. |
| `abilities.blaze.fireImmunity` | boolean | `true` |  | The wearer does not burn in fire or lava, like a magma cube (fire resistance, taken away when the backpack comes off). |
| `abilities.blaze.fireResistanceTicks` | int | `5000` | 100 to 1000000 | Length of the fire resistance effect it gives; it is renewed before it runs out (abilities.effectRefreshTicks). |
| `abilities.blaze.punchFireSeconds` | int | `4` | 0 to 60 | Seconds a punched mob or player burns. 0 turns the burning punch off. |
| `abilities.blaze.punchWithNonWeapons` | boolean | `true` |  | Hitting with an item that is not a weapon (blocks, food, sticks...) also counts as a punch. When false only the empty hand burns. Swords, tools, bows and items that add attack damage are weapons. |

## Hose (`hose`)

Amounts are in millibuckets (1000 is one bucket).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `hose.transferAmount` | int | `1000` | 1 to 64000 | Millibuckets moved per click between the backpack and a tank block (suck or spill mode). Fluid blocks in the world are always one bucket. |
| `hose.drinkAmount` | int | `1000` | 1 to 64000 | Millibuckets drunk per sip in drink mode. |
| `hose.drinkTicks` | int | `24` | 1 to 200 | Ticks a sip takes. |
| `hose.milkAmount` | int | `1000` | 1 to 64000 | Millibuckets of milk or stew taken from a cow or mooshroom per click. |

## Copter pack (`copterpack`)

It burns the fuels of [`fuels.list`](#fuels-fuels), so it works with any mod that adds one of them (BuildCraft, Forestry, Railcraft, GregTech...). Speeds are in blocks per tick. The defaults make flying expensive: a full tank gives roughly one to two and a half minutes of flight.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `copterpack.sound` | boolean | `true` |  | Play the rotor sound (client only; other players may still hear theirs). |
| `copterpack.tankCapacity` | int | `6000` | 1000 to 64000 | Fuel tank capacity in millibuckets. Use the same value on server and clients. Needs a restart. |
| `copterpack.fuelIntervalTicks` | int | `3` | 1 to 200 | Fuel is taken once every this many ticks: the cost of all those ticks at once. |
| `copterpack.fuelPerTick` | int | `2` | 0 to 100 | Millibuckets of fuel per tick while the engine runs, before the fuel's own multiplier in `fuels.list` (oil 1.5, fuel 1.0, creosote 1.8...). With the defaults a full tank lasts about 2.5 minutes of gliding, 75 seconds of hovering and 50 seconds of climbing while hovering. |
| `copterpack.hoverExtraFuel` | int | `2` | 0 to 100 | Extra fuel per tick while hovering (not while sinking with sneak). |
| `copterpack.climbExtraFuel` | int | `2` | 0 to 100 | Extra fuel per tick while climbing (jump held). |
| `copterpack.startFuel` | int | `100` | 0 to 64000 | Fuel the engine takes when it is turned on. It does not start with less in the tank. 0 turns it off. |
| `copterpack.liftSpeed` | double | `0.15` | 0.01 to 2.0 | Climbing speed. |
| `copterpack.descendSpeed` | double | `0.3` | 0.01 to 2.0 | Descending speed while sneaking. |
| `copterpack.glideFactor` | double | `0.6` | 0.0 to 1.0 | Falling speed is multiplied by this every tick in normal mode (lower = slower fall). |
| `copterpack.airControl` | double | `0.03` | 0.0 to 0.5 | Horizontal acceleration while flying. The top horizontal speed is about (0.02 + this value) / 0.09 blocks per tick: 0.03 gives about 11 blocks per second. |
| `copterpack.maxAltitude` | int | `90` | 1 to 255 | Height above which lift fades out. Climbing stops about liftSpeed x 100 blocks higher; hover mode keeps any height you already reached. |
| `copterpack.itemPushRange` | double | `4.0` | 0.0 to 16.0 | Distance in blocks at which the rotor blows dropped items away. 0 turns it off. |
| `copterpack.itemPushIntervalTicks` | int | `2` | 1 to 20 | The rotor looks for dropped items to blow away once every this many ticks. |

## Steam jetpack (`steamjetpack`)

The boiler needs a fluid called steam: another mod's, or the backpack's own (see ownSteamFluid). Speeds are in blocks per tick.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `steamjetpack.sound` | boolean | `true` |  | Play the boiler and thrust sounds (client only). |
| `steamjetpack.ownSteamFluid` | boolean | `true` |  | When no other mod (Railcraft, IC2...) registers a 'steam' fluid, register Adventure Backpack's own so the jetpack boils water without them. When another mod has steam, that one is used. Set it the same on server and clients. Needs a restart. |
| `steamjetpack.waterCapacity` | int | `6000` | 1000 to 64000 | Water tank capacity in millibuckets. Use the same value on server and clients. Needs a restart. |
| `steamjetpack.steamCapacity` | int | `12000` | 1000 to 64000 | Steam tank capacity in millibuckets. Use the same value on server and clients. Needs a restart. |
| `steamjetpack.steamPerTick` | int | `10` | 0 to 1000 | Steam spent per tick of thrust. With the defaults a full steam tank gives 60 seconds of thrust, and full water and steam tanks with a hot boiler about 3 minutes. |
| `steamjetpack.ignitionSteam` | int | `150` | 0 to 64000 | Steam spent at once each time the thrust starts, so taking off costs more than keeping it going. 0 turns it off. |
| `steamjetpack.thrustFuelBurn` | int | `3` | 1 to 20 | Ticks of firebox burn time used per tick of thrust (1 = as fast as idle). Coal burns 1600 ticks, so 3 makes one coal last about 27 seconds of thrust. |
| `steamjetpack.thrustHeatLossTicks` | int | `10` | 0 to 1200 | While thrusting the boiler loses one degree every this many ticks, down to the ambient temperature. With the fire lit it still heats up; without fuel a long flight cools it below boiling. 0 turns it off. |
| `steamjetpack.heatingSpeed` | double | `1.0` | 0.1 to 10.0 | How fast the firebox heats the boiler. 1 is the built-in curve (about 62 seconds from 25 to boiling at 100, then faster); 2 is twice as fast. |
| `steamjetpack.coolingSpeed` | double | `1.5` | 0.1 to 10.0 | How fast the boiler cools without fire. 1 is the old curve (from 200 to 100 in 5 minutes); 1.5 cools it in about 3 minutes and 20 seconds. |
| `steamjetpack.waterIntake` | int | `2` | 0 to 1000 | Millibuckets of water taken per tick while the wearer is in water. |
| `steamjetpack.steamPerWater` | int | `4` | 1 to 100 | Millibuckets of steam made from one millibucket of water. |
| `steamjetpack.boilingTemperature` | int | `100` | 1 to 199 | Boiler temperature at which water turns into steam (the boiler tops out at 200). |
| `steamjetpack.thrust` | double | `0.14` | 0.01 to 1.0 | Upward speed added per tick of thrust. Gravity takes 0.08 each tick, so the jetpack climbs only with more than that: 0.08 just floats. 0.14 reaches the top climbing speed in about 6 ticks. Movement is decided by each client, so use the same value on server and clients. |
| `steamjetpack.maxRiseSpeed` | double | `0.4` | 0.01 to 2.0 | Top climbing speed in blocks per tick (0.4 is 8 blocks a second). Needs thrust above 0.08 + maxRiseSpeed / 50 to be reached. |
| `steamjetpack.fallBrake` | double | `2.0` | 1.0 to 10.0 | Thrust multiplier while the wearer is falling, so holding jump stops a fall at once. 1 turns it off. |
| `steamjetpack.airControl` | double | `0.02` | 0.0 to 0.5 | Horizontal acceleration while thrusting. |
| `steamjetpack.maxAltitude` | int | `90` | 1 to 255 | Height above which thrust fades out. Thrust holds you at about maxRiseSpeed x 100 blocks higher. |

## Scuba gear (`scuba`)

The [Scuba Gear](Scuba-Gear): two air tanks and a diving mask worn on the back. Amounts are in millibuckets (1000 is one bucket), time in ticks (20 per second). Use the same values on the server and on every client for the keys that say so.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `scuba.tankCapacity` | int | `3000` | 500 to 64000 | Capacity of each of the two air tanks, in millibuckets. Use the same value on server and clients. Needs a restart. |
| `scuba.airPerSecond` | int | `10` | 0 to 1000 | Millibuckets of air spent per second while it keeps the wearer breathing. With the defaults two full tanks last 10 minutes under water. 0 makes the air free. |
| `scuba.airIntervalTicks` | int | `20` | 1 to 200 | The air is taken once every this many ticks: the cost of all those ticks at once. |
| `scuba.lowAirWarning` | int | `600` | 0 to 64000 | When the air left goes below this many millibuckets the wearer gets a chat warning, once per dive (600 is one minute with the defaults). 0 turns the warning off. |
| `scuba.warningSound` | boolean | `true` |  | Play a click with the low air warning. |
| `scuba.breathableFluids` | string list | `air, oxygen` |  | Fluids the tanks and the compressor take, by registered name (air of GregTech or Adventure Backpack, oxygen of GregTech, Galacticraft or Mekanism...). When the 'air' fluid exists every one of them is stored as air. Set it the same on server and clients. Needs a restart. |
| `scuba.ownAirFluid` | boolean | `true` |  | When no other mod (GregTech) registers an 'air' fluid, register Adventure Backpack's own so the hand pump and the furnace of the compressor can make air. When another mod has air, that one is used. Set it the same on server and clients. Needs a restart. |
| `scuba.airItems` | string list | `ic2:airCell=1000>ic2:cell` |  | Items that hold air without being fluid containers (IC2's Compressed Air Cell), for the container slots of the Scuba Gear and the compressor. One per line: &lt;item&gt;=&lt;millibuckets&gt;, optionally followed by &gt;&lt;empty item&gt; for the item it leaves and that the compressor fills again. Items as in recipes.boat.motors: ic2:&lt;IC2 item name&gt;, item:&lt;modid&gt;:&lt;item name&gt;[:&lt;damage&gt;], ore:&lt;Ore Dictionary name&gt;, stack:&lt;modid&gt;:&lt;registered stack name&gt;. GregTech's air cells are fluid containers and need no entry. Needs a restart. |

## Air compressor (`scuba.compressor`)

The Air Compressor block, which fills the Scuba Gear. It is switched by `enabled.scuba`.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `scuba.compressor.tankCapacity` | int | `16000` | 1000 to 64000 | Capacity of the compressor's air tank, in millibuckets. Use the same value on server and clients. Needs a restart. |
| `scuba.compressor.handPumpAmount` | int | `100` | 0 to 16000 | Millibuckets of air one right click with an empty hand pumps in (60 clicks fill a Scuba Gear with the defaults). 0 turns the hand pump off. |
| `scuba.compressor.handPumpCooldownTicks` | int | `4` | 0 to 200 | Ticks between two strokes of the hand pump. |
| `scuba.compressor.airPerFuelTick` | int | `10` | 0 to 1000 | Millibuckets of air the furnace makes per tick of burning fuel: one coal (1600 ticks) makes 16000 with the defaults. 0 turns the furnace off. |
| `scuba.compressor.fillPerTick` | int | `50` | 1 to 64000 | Millibuckets per tick moved into the Scuba Gear in the compressor's slot (an empty gear fills in 6 seconds with the defaults). |
| `scuba.compressor.intervalTicks` | int | `10` | 1 to 200 | The compressor works once every this many ticks, doing the work of all those ticks at once. |
| `scuba.compressor.needsOpenAir` | boolean | `true` |  | The hand pump and the furnace make air only when the block above the compressor is air, so it is not an air source buried or under water. Air from pipes is taken anyway. |

## Flamethrower (`flamethrower`)

The [Flamethrower](Flamethrower): the Flamethrower Pack worn on the back and the nozzle held in hand. Amounts are in millibuckets, time in ticks (20 per second), ranges in blocks.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `flamethrower.tankCapacity` | int | `4000` | 500 to 64000 | Capacity of each of the two fuel tanks of the pack, in millibuckets. Use the same value on server and clients. Needs a restart. |
| `flamethrower.fuelPerBurst` | int | `20` | 0 to 10000 | Millibuckets of fuel one burst burns, times the fuel's multiplier in fuels.list (oil 1.5 makes it 30). With the defaults two full tanks of fuel make 400 bursts, 80 seconds of continuous fire. 0 makes the fuel free. |
| `flamethrower.fireIntervalTicks` | int | `4` | 1 to 40 | Cooldown between two bursts while right click is held, in ticks. Each burst burns fuelPerBurst and deals its damage once. |
| `flamethrower.range` | double | `6.0` | 1.0 to 16.0 | Reach of the flame in blocks, from the wearer's eyes. |
| `flamethrower.coneDegrees` | double | `25.0` | 5.0 to 90.0 | Opening of the cone of flame in degrees, around the direction the wearer looks. |
| `flamethrower.damage` | double | `2.0` | 0.0 to 40.0 | Fire damage of one burst to each target in the cone (2 is one heart). Vanilla's half second of invulnerability after a hit means not every burst lands. Fire-immune mobs take none. |
| `flamethrower.fireSeconds` | int | `5` | 0 to 60 | Seconds every target in the cone keeps burning after a burst. 0 does not set targets on fire. |
| `flamethrower.pvp` | boolean | `false` |  | Let the flame hurt and burn other players. Even when true, the server's pvp setting and scoreboard teams still apply. Animals the wearer owns are never hurt. |
| `flamethrower.ignitesBlocks` | boolean | `false` |  | Let each burst set fire to the side of the block it hits, like flint and steel. The fire is vanilla fire, so it spreads and burns as usual. It respects spawn protection, adventure mode and protection mods that cancel block placing. |
| `flamethrower.pilotFuelPerMinute` | int | `0` | 0 to 6000 | Millibuckets of fuel the lit pilot burns per minute while the pack is worn, times the fuel's multiplier. 0 (the default) makes the pilot free. |
| `flamethrower.sound` | boolean | `true` |  | Play the vanilla ignite sound when the pilot is lit (the server sends it) and the vanilla fire sound while the nozzle fires (each client plays it). Set it the same on server and clients. |

## Fuels (`fuels`)

One list of liquid fuels for everything that burns them: the Copter Pack, the Flamethrower Pack, the boat motor and the Fuel Station. See [Fluids and Hose](Fluids-and-Hose#fuels).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `fuels.list` | string list | `fuel=1.0, oil=1.5, bioethanol=1.2, biofuel=1.0, creosote=1.8, coal=0.8, biomass=1.5` |  | Liquid fuels as fluidName=multiplier. The fuel cost of each user (`copterpack.fuelPerTick`...) is multiplied by it: higher burns faster. Only these fluids go into fuel tanks. Use the fluid names the mods register (BuildCraft `oil` and `fuel`, Forestry `bioethanol` and `biomass`, Railcraft `creosote`, Thermal Foundation `coal`, GregTech `fuel` (diesel), `bioethanol` and `creosote`); add `lava=4.0`, for example, to burn lava. Moved here from `copterpack.fuels`, keeping its value. Set it the same on server and clients. Needs a restart. |

## Fuel station (`fuelstation`)

The [Fuel Station](Fuel-Station). The block is switched by `enabled.fuelStation`.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `fuelstation.tankCapacity` | int | `16000` | 1000 to 64000 | Capacity of the station's tank in millibuckets. Use the same value on server and clients. Lowering it does not delete fuel already stored above the new limit. Needs a restart. |

## Distillery (`distillery`)

The [Distillery](Distillery) makes ethanol from crops for the fuel users. The block is switched by `enabled.distillery`. Items in the lists are written as in `recipes.boat.motors`: `item:<modid>:<item name>[:<damage>]` (without a damage value it takes every damage value), `ore:<Ore Dictionary name>`, `stack:<modid>:<registered stack name>` or `ic2:<IC2 item name>`.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `distillery.outputFluid` | string | `bioethanol` |  | Registered name of the fluid the Distillery makes. bioethanol is the name Forestry, Railcraft and GregTech use: when one of them is loaded the Distillery makes their ethanol. Any registered fluid name works; a name no loaded mod registers falls back to bioethanol. Set it the same on server and clients. Needs a restart. |
| `distillery.ownEthanolFluid` | boolean | `true` |  | When no other mod registers a 'bioethanol' fluid, register Adventure Backpack's own (shown as Ethanol), only while enabled.distillery is on. It has no block and no bucket: it lives in tanks, pipes and fuel packs. When another mod has bioethanol, that one is used. Set it the same on server and clients. Needs a restart. |
| `distillery.fuelMultiplier` | double | `1.2` | 0.1 to 10.0 | Burn rate of the output fluid when fuels.list does not list it (fuels.list wins when it does; its default has bioethanol=1.2). Higher burns faster: 1.2 makes ethanol last a little less than fuel (1.0) and more than oil (1.5). Needs a restart. |
| `distillery.outputCapacity` | int | `8000` | 1000 to 64000 | Capacity of the ethanol tank in millibuckets (8000 is eight buckets). Use the same value on server and clients. Needs a restart. |
| `distillery.waterCapacity` | int | `8000` | 1000 to 64000 | Capacity of the water tank in millibuckets. Use the same value on server and clients. Needs a restart. |
| `distillery.waterPerBatch` | int | `1000` | 0 to 64000 | Millibuckets of water each batch uses up. 0 makes water free (and not needed). |
| `distillery.ticksPerBatch` | int | `400` | 1 to 72000 | Ticks of burning flame one batch takes (400 is 20 seconds). With the defaults one coal (3200 ticks) runs eight batches. |
| `distillery.intervalTicks` | int | `10` | 1 to 200 | The Distillery works once every this many ticks, doing the work of all those ticks at once. An idle Distillery (no flame and nothing to do) does no work at all until something changes. |
| `distillery.organics` | string list | `item:minecraft:wheat=100, item:minecraft:potato=150, item:minecraft:carrot=120, item:minecraft:reeds=150, item:minecraft:apple=150, item:minecraft:melon=40, item:minecraft:pumpkin=250, item:minecraft:poisonous_potato=100, item:minecraft:wheat_seeds=20, ore:cropCorn=150, ore:cropRice=100, ore:cropBarley=100, ore:cropRye=100, ore:cropSugarbeet=150` |  | Organic items the grid takes and the ethanol each one makes, one per line: <item>=<millibuckets>. Every batch takes one item from each slot of the grid, so nine different crops make the most at once. Entries no loaded mod provides are ignored (the ore: names cover crops of Pam's HarvestCraft and other farming mods). Set it the same on server and clients. Needs a restart. |
| `distillery.catalysts` | string list | `item:minecraft:sugar` |  | Items the catalyst slot takes, one per line, without amounts. Each batch uses catalystPerBatch of them. The window shows the first one in the empty slot. Set it the same on server and clients. Needs a restart. |
| `distillery.catalystPerBatch` | int | `1` | 0 to 64 | Catalyst items each batch uses up. 0 makes the catalyst free (and not needed). |
| `distillery.solidFuels` | string list | `item:minecraft:coal=3200, item:minecraft:coal_block=32000` |  | Solid fuels the fuel slot takes and how long each one burns, one per line: <item>=<ticks>. The coal entry has no damage value, so it covers coal and charcoal. These win over furnaceFuelMultiplier. Set it the same on server and clients. Needs a restart. |
| `distillery.furnaceFuelMultiplier` | double | `1.0` | 0.0 to 100.0 | Any other furnace fuel (planks, sticks, blaze rods, lava buckets, fuels of other mods) burns for its furnace time times this. 0 takes only the fuels of solidFuels. |
| `distillery.pushIntervalTicks` | int | `20` | 1 to 1200 | Ticks between two pushes of ethanol into the neighbours. It only pushes while there is ethanol and some neighbour takes fluid. |
| `distillery.pushAmount` | int | `1000` | 0 to 64000 | Millibuckets offered to the neighbours in each push, shared in turn between the four sides. 0 turns the push off: then pipes have to pull the ethanol out. |
| `distillery.waterPerSource` | int | `50` | 0 to 1000 | Millibuckets of water per second each water source block next to the Distillery (its four sides and below) gives while the water tank is not full. With the defaults two sources keep it running without buckets or pipes. 0 turns it off. |


## Placed gear (`placedgear`)

The Steam Jetpack, the Copter Pack, the Scuba Gear and the Flamethrower Pack can be put on the ground as a block, like a backpack (see [Gear](Gear#placing-gear-on-the-ground)). `enabled.placedGear` switches it all; each gear has its own switch here. A placed gear does not run and never ticks: the jetpack's boiler stays as it was, and the copter's engine and the pilot flame are off.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `placedgear.jetpack` | boolean | `true` |  | The Steam Jetpack can be placed. Set it the same on server and clients. |
| `placedgear.copter` | boolean | `true` |  | The Copter Pack can be placed. Set it the same on server and clients. |
| `placedgear.scuba` | boolean | `true` |  | The Scuba Gear can be placed. Set it the same on server and clients. |
| `placedgear.flamethrower` | boolean | `true` |  | The Flamethrower Pack can be placed. Set it the same on server and clients. |
| `placedgear.syncIntervalTicks` | int | `10` | 1 to 200 | A placed gear filled or emptied by pipes tells the nearby players about its new content at most once every this many ticks (20 per second). Its window updates at once. |


## Inflatable boat (`inflatableboat`)

The plain boat. The motor of the motorized one has its own section below.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `inflatableboat.maxSpeed` | double | `0.35` | 0.07 to 2.0 | Top speed in blocks per tick (the vanilla boat uses 0.35). The motorized boat without fuel, or with its motor off, uses it too. |

### Boat motor (`inflatableboat.motor`)

The outboard motor of the Motorized Inflatable Boat. It burns the fuels of `fuels.list`. Speeds are in blocks per tick. See [Gear](Gear#motorized-inflatable-boat).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `inflatableboat.motor.enabled` | boolean | `true` |  | The motor of the Motorized Inflatable Boat. False: the motor is only a look, and the boat moves like the plain one. |
| `inflatableboat.motor.mode` | string | `toggle` | toggle, hold | toggle: the Change Mode key (keys.mode, G by default) turns the motor on and off, and the boat goes ahead on its own while the pilot steers; holding back still brakes. hold: the motor pushes while the pilot holds the forward key. Either way it stops without fuel, when the pilot gets off and out of the water. Read on the server. |
| `inflatableboat.motor.maxSpeed` | double | `0.6` | 0.05 to 2.0 | Top speed while the motor runs, in blocks per tick (0.6 = 12 blocks per second; the boat without motor uses inflatableboat.maxSpeed). Use the same value on server and clients. |
| `inflatableboat.motor.acceleration` | double | `2.0` | 1.0 to 10.0 | How much faster than the plain boat the boat speeds up while the motor runs: 1.0 = as fast as the plain boat, 2.0 = twice as fast. Use the same value on server and clients. |
| `inflatableboat.motor.fuelPerTick` | int | `1` | 0 to 100 | Millibuckets of fuel per tick while the motor runs, before the fuel's own multiplier (fuels.list). With the defaults a full tank of fuel lasts 5 minutes. 0 makes the motor free (it still needs some fuel in the tank). |
| `inflatableboat.motor.fuelIntervalTicks` | int | `10` | 1 to 200 | Fuel is taken once every this many ticks of running: the cost of all those ticks at once. |
| `inflatableboat.motor.tankCapacity` | int | `6000` | 1000 to 64000 | Capacity of the motor's fuel tank in millibuckets. Use the same value on server and clients. Lowering it drops the fuel above the new limit when a boat is loaded or placed. Needs a restart. |
| `inflatableboat.motor.outOfWaterTicks` | int | `10` | 1 to 200 | The motor stops after the boat has been out of the water for this many ticks in a row (on land, or in the air over a wave or a drop). |
| `inflatableboat.motor.sound` | boolean | `true` |  | Sound of the boat motor, on the client: a looping putt-putt while the motor runs, a cord pull when it starts and a cough when it stops. It replaces the chat messages as the motor's notice. False: the motor is silent. |
| `inflatableboat.motor.soundVolume` | double | `0.6` | 0.0 to 1.0 | Volume of the boat motor's sounds, from 0.0 (silent) to 1.0, on the client. The running loop is a little quieter at low speed and louder at full speed. |
| `inflatableboat.motor.messages` | boolean | `false` |  | Chat messages about the boat motor (on, off, out of fuel, not on water), read on the server. Off by default: the sound is the notice. Turn it on for players who cannot hear. |

## Clockwork crossbow (`crossbow`)

A repeating crossbow fed with reusable Crossbow Magazines. The Change State key (`keys.state`, V by default) changes its mode; the other two modes have their own sections below. In repetition mode, hold right click to fire the magazine one bolt after another; when it is empty the next click puts in the first loaded magazine of your inventory, hotbar first, and the empty one comes back to the inventory, or drops at your feet when it is full. Sneak + right click swaps a partly used magazine, which comes back with the charges it has left. Ticks are 20 per second.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `crossbow.shotDelayTicks` | int | `4` | 1 to 40 | Pause between two bolts. Minecraft repeats a held right click every 4 ticks, so the real pause while holding is this value rounded up to a multiple of 4 (4 = 5 bolts a second, 5 to 8 = 2.5 a second). |
| `crossbow.reloadTicks` | int | `30` | 0 to 200 | Time to put a magazine into the crossbow, during which the crossbow does not fire. |
| `crossbow.boltVelocity` | double | `3.0` | 0.5 to 6.0 | Bolt speed in blocks per tick when it leaves the crossbow (a fully drawn bow shoots at 3). Faster bolts fly flatter and further. |
| `crossbow.boltDamage` | double | `4.0` | 0.0 to 100.0 | Damage of a bolt fired from a magazine of arrows as it leaves the crossbow, in half hearts (a fully drawn bow does 6 to 10). Crossbow Bolts multiply it by `crossbow.ammo.boltDamageMultiplier`. It drops a little with distance as the bolt slows down. |
| `crossbow.ignoreHurtCooldown` | boolean | `true` |  | Bolts ignore the short invulnerability a mob gets after a hit, so every bolt of a burst lands. When false about half of a fast burst bounces off the same target. |
| `crossbow.boltsRecoverable` | boolean | `false` |  | Bolts stuck in a block can be picked up as arrows. Off by default: a charge of the magazine is spent ammunition. |
| `crossbow.durability` | int | `0` | 0 to 100000 | Bolts the crossbow fires before it breaks. 0 means it never breaks. |

## Crossbow Magazine (`crossbow.magazine`)

The Crossbow Magazine is reusable: you load it by hand with ammunition and the crossbow gives it back empty.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `crossbow.magazine.capacity` | int | `8` | 1 to 24 | Charges a full Crossbow Magazine holds, and bolts the crossbow fires from it. |
| `crossbow.magazine.loadTicksPerCharge` | int | `4` | 0 to 100 | Pause between two charges while loading a magazine with a held right click (a held click repeats every 4 ticks). 0 loads the whole magazine with one click. |
| `crossbow.magazine.reloadRecipe` | boolean | `true` |  | Shapeless recipe: a magazine and up to its free room of ammunition of one kind (the kind it already holds) make the magazine loaded with them. Needs a restart. |
| `crossbow.magazine.legacyRecipe` | boolean | `true` |  | Keep the quick recipe (6 arrows, an iron ingot, 2 sticks): it makes a magazine already loaded with 6 arrows. The empty magazine has its own recipe (an iron ingot, 2 sticks and a string). Needs a restart. |

## Ammunition (`crossbow.ammo`)

What the Crossbow Magazine is loaded with. Bolts fired from a magazine of Crossbow Bolts hit harder than those from a magazine of arrows.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `crossbow.ammo.list` | string list | `adventurebackpack:bolt`, `minecraft:arrow` |  | Items a magazine can be loaded with, as modid:itemName (any damage value). An empty magazine takes the first ammunition found in the inventory (hotbar first); a loaded one only its own kind. Unknown names are ignored. Same on server and clients. |
| `crossbow.ammo.boltDamageMultiplier` | double | `1.5` | 0.1 to 10.0 | Damage of bolts fired from a magazine of Crossbow Bolts, as a multiple of `crossbow.boltDamage` and `crossbow.sniper.boltDamage`. Other ammunition does the base damage. |

## Grappling hook (`crossbow.grapple`)

The second mode of the crossbow: right click shoots a hook that catches a block and pulls the player to it. Speeds are in blocks per tick (20 ticks a second).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `crossbow.grapple.enabled` | boolean | `true` |  | The grappling hook mode. When false the mode is skipped and a crossbow left in it shoots bolts. |
| `crossbow.grapple.maxDistance` | double | `32.0` | 4.0 to 64.0 | Range of the hook in blocks. A hook that reaches it without catching comes back. |
| `crossbow.grapple.hookSpeed` | double | `2.5` | 0.5 to 5.0 | Speed of the flying hook, in blocks per tick. It flies straight, without gravity. |
| `crossbow.grapple.pullSpeed` | double | `0.8` | 0.1 to 3.0 | Speed at which the hook pulls the player, in blocks per tick (sprinting is about 0.28). |
| `crossbow.grapple.releaseDistance` | double | `1.5` | 0.5 to 4.0 | The hook lets go when the player's body is this close to the anchor, in blocks. |
| `crossbow.grapple.maxPullTicks` | int | `60` | 10 to 200 | Longest pull; the hook lets go after it (for example when the player is stuck under a ledge). On a server with allow-flight=false keep it under 80, or a long upward pull can kick the player for flying. |
| `crossbow.grapple.cooldownTicks` | int | `20` | 0 to 200 | Wait after firing the hook before the crossbow can fire or reload again. |
| `crossbow.grapple.chargesPerShot` | int | `1` | 0 to 24 | Bolts from the magazine a hook shot takes. 0 makes it free; more than a full magazine counts as a full magazine. |
| `crossbow.grapple.noFallDamage` | boolean | `true` |  | The pull never causes fall damage: the fall distance is reset while pulling and when the hook lets go. A fall after letting go counts from there. |

## Sniper mode (`crossbow.sniper`)

The third mode: with the bipod down (sneaking, standing still on the ground) the crossbow fires one strong, straight bolt. With the bipod folded it fires normal bolts.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `crossbow.sniper.enabled` | boolean | `true` |  | The sniper mode. When false the mode is skipped and a crossbow left in it shoots normal bolts. |
| `crossbow.sniper.boltDamage` | double | `12.0` | 0.0 to 100.0 | Damage of a sniper bolt as it leaves the crossbow, in half hearts. It drops a little with distance as the bolt slows down. |
| `crossbow.sniper.boltVelocity` | double | `5.0` | 0.5 to 8.0 | Speed of a sniper bolt in blocks per tick. Faster bolts fly flatter and further. |
| `crossbow.sniper.shotDelayTicks` | int | `30` | 4 to 200 | Pause after a sniper shot. A held right click repeats every 4 ticks, so the real pause is this value rounded up to a multiple of 4. |
| `crossbow.sniper.stillTolerance` | double | `0.02` | 0.001 to 0.2 | Horizontal movement in blocks per tick under which the player counts as still (sneak walking is about 0.065). |
| `crossbow.sniper.zoom` | double | `0.35` | 0.1 to 1.0 | Client only: field of view multiplier while the bipod is down. 1 = no zoom, 0.35 = about three times closer. |

## Gatling mode (`crossbow.quiver`)

While you wear a Skeleton Backpack with arrows or Crossbow Bolts in its quiver (`enabled.quiver`) and its abilities are on, the crossbow needs no magazine: hold right click and it fires one item of the quiver per shot, without stopping, until the quiver runs out. Then it goes back to the magazine inside and to the loaded magazines of your inventory. It takes the items in the order of `crossbow.ammo.list` (Crossbow Bolts first, then arrows). The grappling hook takes its charges from the quiver, and the sniper with the bipod down fires single bolts from it at its own rate. Turn the backpack's abilities off with the Gear On/Off key to use magazines while you wear it.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `crossbow.quiver.enabled` | boolean | `true` |  | The crossbow fires from the quiver of a worn Skeleton Backpack (gatling mode). When false it always uses magazines. |
| `crossbow.quiver.fireIntervalTicks` | int | `3` | 1 to 40 | Ticks between two bolts while the trigger is held. 3 = between 6 and 7 bolts a second (the magazine fires 5). Any value works, not only multiples of 4. |
| `crossbow.quiver.triggerHoldTicks` | int | `6` | 5 to 20 | Ticks the trigger counts as held after the last right click the server got. Minecraft repeats a held click every 4 ticks; the margin covers a late packet. Higher values keep a laggy burst going but fire a little longer after letting go. |
| `crossbow.quiver.flushEveryShots` | int | `8` | 1 to 64 | The server counts the spent items in memory and takes them out of the backpack every this many shots, and when the burst ends. Lower values update the quiver in the backpack window and on the model sooner and send the backpack to its wearer more often. |

## Piston boots (`pistonboots`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `pistonboots.onlyWhileSprinting` | boolean | `true` |  | Step up full blocks only while sprinting, so walking up to a block does not climb it. False steps up at any pace while the boots are on. The sprint speed is only given while sprinting either way. |
| `pistonboots.stepHeight` | double | `1.001` | 0.5 to 3.0 | Step height while the boots step up (vanilla is 0.5). |
| `pistonboots.sprintSpeed` | boolean | `true` |  | Give Speed I while sprinting. |
| `pistonboots.safeFallDistance` | int | `8` | 0 to 256 | Falls shorter than this many blocks do no damage. 0 turns it off. |
| `pistonboots.leap.enabled` | boolean | `true` |  | A running jump in the boots carries more speed forward: the jump multiplies the horizontal speed you had, once, when your feet leave the ground. No flight and no extra fall protection. |
| `pistonboots.leap.boost` | double | `1.4` | 1.0 to 3.0 | How much the jump multiplies your horizontal speed. 1.0 adds nothing. |
| `pistonboots.leap.maxHorizontalSpeed` | double | `0.7` | 0.1 to 2.0 | The leap never takes your horizontal speed past this, in blocks per tick (vanilla sprinting is about 0.28, a vanilla sprint jump about 0.36). If something else already made you faster, you keep that speed: the leap never slows you down. |
| `pistonboots.leap.requiresSprint` | boolean | `true` |  | Leap only from a sprint. False leaps from any moving jump in the boots. |

## Campfire cooking (`campfire.cooking`)

Right click the campfire with a raw item to cook it. The campfire block, its recipe and its use as a respawn point do not depend on these keys; `enabled.campfire` only turns the cooking on or off. Ticks are 20 per second.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `campfire.cooking.enabled` | boolean | `true` |  | Cook items by right clicking the campfire with them. |
| `campfire.cooking.ticksPerItem` | int | `200` | 1 to 12000 | Time to cook one item (the furnace takes 200). |
| `campfire.cooking.onlyFood` | boolean | `true` |  | Only cook items whose furnace result is food. False takes any furnace recipe (ores, sand, logs...). |
| `campfire.cooking.maxPerUse` | int | `8` | 1 to 64 | Most items the campfire holds at once; a right click puts on as many as fit, the rest stays in the hand. |
| `campfire.cooking.returnRange` | double | `4.0` | 0.0 to 16.0 | Distance in blocks within which the cooked items go straight into the inventory of the player who put them on the fire. Farther away, or with 0, they pop out on top of the campfire. |

## Sleeping bag (`sleepingbag`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `sleepingbag.placeable` | boolean | `true` |  | Right click the top of a block with the Sleeping Bag item to lay it on the ground as a bed, without a backpack. Breaking it gives the item back; sneak + right click rolls it up into the inventory. |
| `sleepingbag.setsSpawn` | boolean | `true` |  | Sleeping in a sleeping bag (placed on its own or from a backpack) moves the player's spawn point there, like a bed. When false the player keeps the spawn point they had. |

## Display (`render`)

Client only: how the mod draws things. The server ignores this section.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `render.backpackOnPlayer` | boolean | `true` |  | Draw the worn backpack on the player. Turn it off with mods that already draw the player's back, like Smart Moving. |
| `render.tankRenderType` | int | `3` | 1 to 3 | How fluids are drawn in the tanks of the backpack window: style 1, 2 or 3. |
| `render.tankHoverText` | boolean | `false` |  | Show the tank contents as hovering text instead of under the tanks. |
| `render.statusOverlay` | boolean | `true` |  | Show the player's potion effects on screen. |
| `render.scubaBubbleTicks` | int | `10` | 1 to 200 | Ticks between two puffs of bubbles from the mask of a player breathing from the Scuba Gear. |
| `render.flamethrowerParticles` | int | `6` | 0 to 30 | Flame particles drawn along the cone for each burst of a Flamethrower (smoke comes with every second burst). 0 draws none. |
| `render.wearableOverlay` | boolean | `true` |  | Show the overlays of the worn backpack, copter pack or jetpack on screen, and the fuel tank of the motorized boat the player rides. |
| `render.boatSmokeTicks` | int | `8` | 0 to 200 | The running motor of the motorized boat puffs smoke once every this many ticks. 0 = no smoke. |
| `render.quiverArrows` | boolean | `true` |  | Draw arrows sticking out of the Skeleton Backpack's quiver (worn, placed and as an item): none when it is empty, more as it fills up (1 to 8, 9 to 32, more than 32 items). |
| `render.tooltipWidth` | int | `200` | 100 to 400 | Widest line of the mod's tooltips, in GUI pixels; longer lines wrap. The crossbow's controls show only while Shift is held. |

## Keys (`keys`)

Client only. The four keys of the mod and their default codes, as LWJGL key codes (B is 48, N is 49, G is 34, V is 47; see the LWJGL Keyboard class). They are only defaults: once you change a key in Options > Controls, Minecraft keeps your choice. What each key does in each case is in [Controls](Controls). The eight keys of older versions (`openBackpack`, `toggle`, `crossbowMode`, `toggleAbilities`, `boatMotorToggle` and the three `toolcycle` keys) are removed from the file.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `keys.gear` | int | `48` | 0 to 255 | Default key of Open Gear: opens the window of the worn backpack, copter pack, jetpack, scuba gear or flamethrower pack (48 = B). Needs a restart. |
| `keys.ability` | int | `49` | 0 to 255 | Default key of Gear On/Off: turns the worn gear on and off. Backpack: its abilities, for this player only. Copter pack: the engine. Steam Jetpack: the firebox. Flamethrower Pack: the pilot. The Scuba Gear always works (49 = N). Needs a restart. |
| `keys.mode` | int | `34` | 0 to 255 | Default key of Change Mode, first match wins: riding the Motorized Inflatable Boat, the motor on and off (inflatableboat.motor.mode toggle); wearing the copter pack, normal or hover; a hose in the hand, the left or right tank (34 = G). Needs a restart. |
| `keys.state` | int | `47` | 0 to 255 | Default key of Change State, first match wins: the Clockwork Crossbow in the hand, its firing mode; a hose in the hand, its mode (suck, spill, drink); wearing a backpack, swap the held tool with its tool slots. Sneak + the key goes backwards (47 = V). Needs a restart. |

## Tool swapping keys (`keys.toolcycle`)

Client only. Swapping the held tool with the backpack's tool slots, and changing the hose mode, with the Change State key or Sneak + the mouse wheel. The key itself is changed in Options > Controls.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `keys.toolcycle.enabled` | boolean | `true` |  | Swap the held tool with the backpack's tool slots, and change the hose mode, with the Change State key or Sneak + the mouse wheel. False turns both off, so the wheel always scrolls the hotbar (the crossbow mode still changes with the key). |
| `keys.toolcycle.mode` | string | `both` | wheel, keys, both | wheel: hold Sneak and turn the mouse wheel. keys: only the Change State key. both: either one. |
| `keys.toolcycle.invertWheel` | boolean | `false` |  | Turn the mouse wheel the other way to cycle. |

## Backpacks in the world (`worldgen`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `worldgen.bonusChestBackpack` | boolean | `false` |  | Put a Standard Adventure Backpack in the bonus chest. |
| `worldgen.pigmanBackpacks` | boolean | `false` |  | Let Pigman Backpacks appear in dungeon loot and villager trades. |

## Extra recipes (`recipes`)

The saddle recipe, and the recipes that change with the mods that are loaded: the Inflatable Boat, the Fuel Station, the Flamethrower Pack and the Flamethrower. The Diving Mask and the Pyro Mask follow the rubber setting of the boat, `recipes.boat.mode` and `recipes.boat.rubber`. The list entries are read once when the game starts, after every mod has registered its items. See [Gear](Gear#inflatable-boat), [Scuba Gear](Scuba-Gear), [Flamethrower](Flamethrower) and [Fuel Station](Fuel-Station).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `recipes.saddle` | boolean | `true` |  | Add a crafting recipe for the saddle. |
| `recipes.boat.mode` | string | `auto` |  | vanilla: only the vanilla recipes. auto: the vanilla recipes, plus a rubber or motor variant when a loaded mod provides rubber or an electric motor. technical: the rubber or motor variant replaces the vanilla recipe of each part a loaded mod provides; a part no loaded mod provides keeps its vanilla recipe. Values: auto, vanilla, technical. Needs a restart. |
| `recipes.boat.rubber` | string list | `itemRubber, plateRubber, ingotRubber` |  | Ore Dictionary names of rubber (IC2 itemRubber, GregTech plateRubber and ingotRubber...). Each one some loaded mod provides gets its own chamber recipe (three rubber, two chambers). Needs a restart. |
| `recipes.boat.motors` | string list | `ic2:elemotor, item:gregtech:gt.metaitem.01:32600, ore:craftingElectricMotor, stack:ThermalExpansion:powerCoilGold` |  | Electric motors for the motorized boat, each one found gets its own recipe (boat + motor + Hydro Blades). Entries: ic2:&lt;IC2 item name&gt; (IC2 API), item:&lt;modid&gt;:&lt;item name&gt;[:&lt;damage&gt;], ore:&lt;Ore Dictionary name&gt;, stack:&lt;modid&gt;:&lt;registered stack name&gt;. Needs a restart. |
| `recipes.fuelstation.mode` | string | `auto` |  | vanilla: only the iron ingot recipe. auto: the iron ingot recipe, plus one with plates for each plate a loaded mod provides. technical: the plate recipes replace the iron ingot one when a loaded mod provides a plate; without plates the iron ingot recipe stays. Values: auto, vanilla, technical. Needs a restart. |
| `recipes.fuelstation.plates` | string list | `plateSteel, plateIron` |  | Ore Dictionary names of the metal plates for the technical recipe (GregTech, IC2, Thermal Foundation, Railcraft plateSteel and plateIron...). Each one some loaded mod provides gets its own recipe. Needs a restart. |
| `recipes.flamethrower.mode` | string | `auto` |  | vanilla: only the iron ingot recipes. auto: the iron ingot recipes, plus ones with plates for each plate a loaded mod provides. technical: the plate recipes replace the iron ingot ones when a loaded mod provides a plate; without plates the iron ingot recipes stay. Values: auto, vanilla, technical. Needs a restart. |
| `recipes.flamethrower.plates` | string list | `plateSteel, plateIron` |  | Ore Dictionary names of the metal plates for the technical recipes of the pack and the nozzle (GregTech, IC2, Thermal Foundation, Railcraft plateSteel and plateIron...). Each one some loaded mod provides gets its own recipe. Needs a restart. |

## Small tweaks (`tweaks`)

Fixes and behaviours that are not tied to one item.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `tweaks.fixVanillaLead` | boolean | `true` |  | Mobs on a lead held by a player take no fall damage unless they fall fast. |
| `tweaks.namedHorsesFollowOwner` | boolean | `true` |  | Tamed horses with a name tag follow their owner. |

## Tool slots (`tweaks.toolcycle`)

Which items may go into the two tool slots of the backpack, by hand or with the tool swap. Set it the same on the server and on every client.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `tweaks.toolcycle.mode` | string | `none` | none, whitelist, blacklist | Which items may go into the backpack's two tool slots, by hand or with the tool cycle. none: the built-in rules (vanilla tools, shears, hoes, fishing rods, flint and steel, and tools of Tinkers' Construct, BuildCraft, IndustrialCraft and Thermal Expansion). whitelist: only the items on the list. blacklist: the built-in rules minus the items on the list. Stackable items, hoses and backpacks never go in. Set it the same on server and clients. |
| `tweaks.toolcycle.list` | string list | empty |  | Items for the whitelist or blacklist mode, one per line, as registry names: modid:name (minecraft:shears, TConstruct:pickaxe; the names /give takes), or modid:* for every item of a mod. A name without a mod id means minecraft:. Case does not matter. A blacklisted bow also stops the Skeleton Backpack from cycling it. |

## Grave mods (`compat.graves`)

Acts only when OpenBlocks or GraveStone is loaded.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `compat.graves.enabled` | boolean | `true` |  | Send the worn wearable to the grave of OpenBlocks or GraveStone when one of them is loaded. When false, backpack.placeOnDeath decides as if no grave mod were there. |

## Baubles (`compat.baubles`)

Acts only when Baubles or Baubles Expanded is loaded.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `compat.baubles.enabled` | boolean | `true` |  | Let the Adventure Backpack go into a bauble slot and work from there. When false the backpack does not fit a bauble slot and one already there does nothing. The Baubles Expanded back slot exists anyway (so client and server always match); its layout can be changed in the Baubles Expanded config. |


## Checks the mod makes

Two combinations are fixed when the file is read: the Bat night vision length must be longer than its refresh threshold (if not, it is set to the threshold plus 100 ticks), and the steam spent per tick of thrust cannot be more than the steam tank holds.

## Flight values before 1.0.0

Flying costs more in 1.0.0. These are the earlier values, for a pack that wants the old balance. With them the copter pack and the jetpack are still dearer than before in some modes: the climb is charged now, and the cost is paid for the whole interval.

| Key | 1.0.0 default | Earlier value |
|---|---|---|
| `copterpack.fuelPerTick` | 2 | 1 |
| `copterpack.startFuel` | 100 | 0 |
| `copterpack.liftSpeed` | 0.15 | 0.18 |
| `copterpack.airControl` | 0.03 | 0.05 |
| `copterpack.maxAltitude` | 90 | 100 |
| `steamjetpack.steamPerTick` | 20 | 13 |
| `steamjetpack.ignitionSteam` | 150 | 0 |
| `steamjetpack.thrustFuelBurn` | 3 | 1 |
| `steamjetpack.thrustHeatLossTicks` | 10 | 0 |
| `steamjetpack.coolingSpeed` | 1.5 | 1.0 |
| `steamjetpack.thrust` | 0.14 | 0.1 |
| `steamjetpack.maxRiseSpeed` | 0.4 | 0.32 |
| `steamjetpack.fallBrake` | 2.0 | 1.0 |
| `steamjetpack.maxAltitude` | 90 | 100 |

## Keys moved from the older file

| Old key (`general`) | New key |
|---|---|
| `BackpackAbilities` | `enabled.abilities` |
| `BackpackDeathPlace` | `backpack.placeOnDeath` |
| `CopterPackSound` | `copterpack.sound` |
| `TankRenderType` | `render.tankRenderType` |
| `StatusOverlay` | `render.statusOverlay` |
| `BackpackOverlay` | `render.wearableOverlay` |
| `HoveringText` | `render.tankHoverText` |
| `RenderBackpackOnPlayer` | `render.backpackOnPlayer` |
| `BonusBackpack` | `worldgen.bonusChestBackpack` |
| `PigmanBackpacks` | `worldgen.pigmanBackpacks` |
| `SaddleRecipe` | `recipes.saddle` |
| `FixVanillaLead` | `tweaks.fixVanillaLead` |

## Not in the file

These are fixed on purpose: how much a backpack holds and how its slots are laid out (changing it would break saved backpacks), the boiler heat curve of the jetpack beyond its boiling point, the physics of the Inflatable Boat other than its top speed and its motor, the push strength of the copter rotor, the death placement search (12 blocks around, 6 up and down) and the reach of the sleeping bag button (8 blocks).

## See also

[Backpacks](Backpacks), [Jetpack and Copter Pack](Jetpack-and-Copter-Pack), [Crossbow](Crossbow), [Compatibility](Compatibility)
