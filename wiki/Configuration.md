# Configuration

Every setting of Adventure Backpack is in one file, `config/adventurebackpack.cfg`. The game writes it the first time it starts, with every key and its comment. This page lists all 188 keys.

You can also edit the file from the game: in the Mods list, select Adventure Backpack and press Config. Changes made in the file need a restart of the game. Keys marked "Needs a restart" are read when items, blocks or keys are created, so they need one even with the config screen.

## How the file is laid out

The file is a tree under `adventurebackpack`. First comes `enabled`, with one switch per system. Then each system has its own section with its own switches and numbers. The tables below drop the `adventurebackpack` prefix: `enabled.hose` is the `hose` key in the `enabled` section.

- A disabled system stays registered, so worlds and servers with different files still load. It is hidden, has no recipe and does nothing.
- Time is in ticks (20 per second) or seconds as each key says. Fluid is in millibuckets (mB): 1000 mB is one bucket. Speeds are in blocks per tick.
- Values outside the range are brought back into it. A value of the wrong type is replaced by the default.
- Use the same file on the server and on every client for the keys that say so. The `render` and `keys` sections only matter on the client.
- Keys from the older flat file (`general` section) move to the new places the first time the game starts, keeping their values. The table at the end lists them.

## Contents

- [Systems](#systems-enabled), [Backpack](#backpack-backpack), [Abilities](#abilities-abilities), [Hose](#hose-hose), [Copter pack](#copter-pack-copterpack), [Steam jetpack](#steam-jetpack-steamjetpack), [Inflatable boat](#inflatable-boat-inflatableboat)
- [Crossbow](#clockwork-crossbow-crossbow), [Grappling hook](#grappling-hook-crossbowgrapple), [Sniper mode](#sniper-mode-crossbowsniper), [Piston boots](#piston-boots-pistonboots), [Campfire](#campfire-cooking-campfirecooking), [Sleeping bag](#sleeping-bag-sleepingbag)
- [Display](#display-render), [Keys](#keys-keys), [Tool swapping keys](#tool-swapping-keys-keystoolcycle)
- [Backpacks in the world](#backpacks-in-the-world-worldgen), [Extra recipes](#extra-recipes-recipes), [Small tweaks](#small-tweaks-tweaks), [Tool slots](#tool-slots-tweakstoolcycle), [Grave mods](#grave-mods-compatgraves), [Baubles](#baubles-compatbaubles)
- [Earlier flight values](#flight-values-before-100)

## Systems (`enabled`)

One switch per system. A disabled system stays registered, so worlds and servers still load, but it is hidden, has no recipe and does nothing.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `enabled.abilities` | boolean | `true` |  | Backpack abilities. When false every backpack is only cosmetic (the lightning transformation still works). Set it the same on server and clients. |
| `enabled.hose` | boolean | `true` |  | The Backpack Hose. |
| `enabled.copterPack` | boolean | `true` |  | The Copter Pack. It is always registered, but it only works (and shows in the creative tab) when BuildCraft is loaded. |
| `enabled.steamJetpack` | boolean | `true` |  | The Steam Jetpack. |
| `enabled.inflatableBoat` | boolean | `true` |  | The Inflatable Boat and the Motorized Inflatable Boat. |
| `enabled.crossbow` | boolean | `true` |  | The Clockwork Crossbow and its Bolt Cartridges. |
| `enabled.sleepingBag` | boolean | `true` |  | The sleeping bag: deploying it from a placed backpack, and placing the Sleeping Bag item on its own (sleepingbag.placeable). Bags already on the ground still work and can be picked up. |
| `enabled.pistonBoots` | boolean | `true` |  | The Piston Boots: step assist, sprint speed and fall protection. |
| `enabled.campfire` | boolean | `true` |  | Cooking on the Campfire (see campfire.cooking). The campfire block, its recipe and the respawn point stay either way. |

## Backpack (`backpack`)

The Adventure Backpack itself, worn or placed.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `backpack.placeOnDeath` | boolean | `true` |  | Place the backpack as a block where the player dies. When false it drops as an item. |
| `backpack.tankCapacity` | int | `4000` | 1000 to 64000 | Capacity of each of the two backpack tanks, in millibuckets. Use the same value on server and clients. Lowering it does not delete fluid already stored above the new limit. Needs a restart. |
| `backpack.blastResistance` | double | `2000.0` | 0.0 to 6000000.0 | Explosion resistance of a placed backpack. 2000 is obsidian, which survives TNT and creepers; 10 is stone. Needs a restart. |
| `backpack.hardness` | double | `1.0` | 0.0 to 100.0 | How long a placed backpack takes to break by hand. Stone is 1.5. Needs a restart. |
| `backpack.deathMessage` | boolean | `true` |  | On death, tell the player in chat where the worn backpack, copter pack or jetpack went (placed, dropped or in the grave) with its coordinates. |

## Abilities (`abilities`)

Special effects of the backpacks while they are worn (the Cactus and Melon also work placed). Each backpack has its own switch under its own name. Ticks are 20 per second. Use the same values on the server and on every client.

### All abilities

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.effectRefreshTicks` | int | `40` | 2 to 200 | Ticks left on a long potion effect (fire resistance, Dragon effects) under which the ability renews it. |
| `abilities.toggleMessage` | boolean | `true` |  | Tell the player in chat when the abilities key (keys.toggleAbilities) turns the worn backpack's abilities off or on. Each player's choice is kept with the player; enabled.abilities false keeps them off for everyone. |

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
| `abilities.skeleton.enabled` | boolean | `true` |  | Skeleton Backpack: bows take arrows from the backpack. |

### Iron Golem backpack (`abilities.irongolem`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.irongolem.enabled` | boolean | `true` |  | Iron Golem Backpack: no fall damage. |

### Spider backpack (`abilities.spider`)

Taming works with the Spider Backpack on. Tamed spiders are kept in memory and released when their owner dies, logs out or changes dimension.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `abilities.spider.enabled` | boolean | `true` |  | Spider Backpack: tame spiders and ride them. Wearing it, right click a spider with the tame item to try to tame it (like taming a wolf). A tamed spider follows its owner and only its owner rides it, both only while the owner wears the Spider Backpack. It is released (back to a normal spider) when its owner dies, logs out or changes dimension, or sneaks and right clicks it. When false, tamed spiders neither follow nor carry anyone, and no new spider can be tamed. |
| `abilities.spider.tameItem` | string | `minecraft:spider_eye` |  | Item used to tame a spider, as modid:name. One is used up per try (not in creative). |
| `abilities.spider.tameChance` | double | `0.33` | 0.0 to 1.0 | Chance that one try tames the spider (1 = always, the wolf is about 0.33). |
| `abilities.spider.maxTamed` | int | `2` | 0 to 16 | Tamed spiders a player may have at once. 0 turns taming off. |
| `abilities.spider.followStartDistance` | double | `6.0` | 2.0 to 32.0 | A tamed spider starts walking to its owner when it is farther than this, in blocks. |
| `abilities.spider.followStopDistance` | double | `3.0` | 1.0 to 32.0 | It stops when it is this close to its owner, in blocks. |
| `abilities.spider.teleportDistance` | int | `24` | 0 to 128 | When it cannot walk to its owner and is farther than this, it jumps next to the owner like a wolf. 0 = never. |

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

Works only with BuildCraft loaded. Speeds are in blocks per tick. The defaults make flying expensive: a full tank gives roughly one to two and a half minutes of flight.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `copterpack.sound` | boolean | `true` |  | Play the rotor sound (client only; other players may still hear theirs). |
| `copterpack.tankCapacity` | int | `6000` | 1000 to 64000 | Fuel tank capacity in millibuckets. Use the same value on server and clients. Needs a restart. |
| `copterpack.fuelIntervalTicks` | int | `3` | 1 to 200 | Fuel is taken once every this many ticks: the cost of all those ticks at once. |
| `copterpack.fuelPerTick` | int | `2` | 0 to 100 | Millibuckets of fuel per tick while the engine runs, before the fuel's own multiplier. With the defaults a full tank lasts about 2.5 minutes of gliding, 75 seconds of hovering and 50 seconds of climbing while hovering. |
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
| `copterpack.fuels` | string list | `fuel=1.0, oil=1.5, bioethanol=1.2, biofuel=1.0, creosote=1.8, coal=0.8` |  | Fluids the copter pack burns, as name=multiplier: the fuel cost is multiplied by it (higher burns faster). Fluid names as registered by the mods (BuildCraft fuel and oil, Railcraft creosote...). Needs a restart. |

## Steam jetpack (`steamjetpack`)

The boiler needs a fluid called steam: another mod's, or the backpack's own (see ownSteamFluid). Speeds are in blocks per tick.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `steamjetpack.sound` | boolean | `true` |  | Play the boiler and thrust sounds (client only). |
| `steamjetpack.ownSteamFluid` | boolean | `true` |  | When no other mod (Railcraft, IC2...) registers a 'steam' fluid, register Adventure Backpack's own so the jetpack boils water without them. When another mod has steam, that one is used. Set it the same on server and clients. Needs a restart. |
| `steamjetpack.waterCapacity` | int | `6000` | 1000 to 64000 | Water tank capacity in millibuckets. Use the same value on server and clients. Needs a restart. |
| `steamjetpack.steamCapacity` | int | `12000` | 1000 to 64000 | Steam tank capacity in millibuckets. Use the same value on server and clients. Needs a restart. |
| `steamjetpack.steamPerTick` | int | `20` | 0 to 1000 | Steam spent per tick of thrust. With the defaults a full steam tank gives 30 seconds of thrust, and full water and steam tanks with a hot boiler about 90 seconds. |
| `steamjetpack.ignitionSteam` | int | `150` | 0 to 64000 | Steam spent at once each time the thrust starts, so taking off costs more than keeping it going. 0 turns it off. |
| `steamjetpack.thrustFuelBurn` | int | `3` | 1 to 20 | Ticks of firebox burn time used per tick of thrust (1 = as fast as idle). Coal burns 1600 ticks, so 3 makes one coal last about 27 seconds of thrust. |
| `steamjetpack.thrustHeatLossTicks` | int | `10` | 0 to 1200 | While thrusting the boiler loses one degree every this many ticks, down to the ambient temperature. With the fire lit it still heats up; without fuel a long flight cools it below boiling. 0 turns it off. |
| `steamjetpack.heatingSpeed` | double | `1.0` | 0.1 to 10.0 | How fast the firebox heats the boiler. 1 is the built-in curve (about 62 seconds from 25 to boiling at 100, then faster); 2 is twice as fast. |
| `steamjetpack.coolingSpeed` | double | `1.5` | 0.1 to 10.0 | How fast the boiler cools without fire. 1 is the old curve (from 200 to 100 in 5 minutes); 1.5 cools it in about 3 minutes and 20 seconds. |
| `steamjetpack.waterIntake` | int | `2` | 0 to 1000 | Millibuckets of water taken per tick while the wearer is in water. |
| `steamjetpack.steamPerWater` | int | `4` | 1 to 100 | Millibuckets of steam made from one millibucket of water. |
| `steamjetpack.boilingTemperature` | int | `100` | 1 to 199 | Boiler temperature at which water turns into steam (the boiler tops out at 200). |
| `steamjetpack.thrust` | double | `0.08` | 0.01 to 1.0 | Upward acceleration per tick. |
| `steamjetpack.maxRiseSpeed` | double | `0.28` | 0.01 to 2.0 | Top climbing speed. |
| `steamjetpack.airControl` | double | `0.02` | 0.0 to 0.5 | Horizontal acceleration while thrusting. |
| `steamjetpack.maxAltitude` | int | `90` | 1 to 255 | Height above which thrust fades out. Thrust holds you at about maxRiseSpeed x 100 blocks higher. |

## Inflatable boat (`inflatableboat`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `inflatableboat.maxSpeed` | double | `0.35` | 0.07 to 2.0 | Top speed in blocks per tick (the vanilla boat uses 0.35). |

## Clockwork crossbow (`crossbow`)

A repeating crossbow fed with Bolt Cartridges. The key `keys.crossbowMode` changes its mode; the other two modes have their own sections below. In repetition mode, hold right click to fire the magazine one bolt after another; when it is empty the next click loads the first cartridge in your inventory, hotbar first. Sneak + right click swaps a partly used magazine, which comes back as a partly used cartridge. Ticks are 20 per second.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `crossbow.shotsPerMagazine` | int | `6` | 1 to 24 | Bolts in a full magazine, and in a full Bolt Cartridge (the recipe still takes 6 arrows). |
| `crossbow.shotDelayTicks` | int | `4` | 1 to 40 | Pause between two bolts. Minecraft repeats a held right click every 4 ticks, so the real pause while holding is this value rounded up to a multiple of 4 (4 = 5 bolts a second, 5 to 8 = 2.5 a second). |
| `crossbow.reloadTicks` | int | `30` | 0 to 200 | Time to load a cartridge, during which the crossbow does not fire. |
| `crossbow.boltVelocity` | double | `3.0` | 0.5 to 6.0 | Bolt speed in blocks per tick when it leaves the crossbow (a fully drawn bow shoots at 3). Faster bolts fly flatter and further. |
| `crossbow.boltDamage` | double | `4.0` | 0.0 to 100.0 | Damage of a bolt as it leaves the crossbow, in half hearts (a fully drawn bow does 6 to 10). It drops a little with distance as the bolt slows down. |
| `crossbow.ignoreHurtCooldown` | boolean | `true` |  | Bolts ignore the short invulnerability a mob gets after a hit, so every bolt of a burst lands. When false about half of a fast burst bounces off the same target. |
| `crossbow.boltsRecoverable` | boolean | `false` |  | Bolts stuck in a block can be picked up as arrows. Off by default: a cartridge is spent ammunition. |
| `crossbow.durability` | int | `0` | 0 to 100000 | Bolts the crossbow fires before it breaks. 0 means it never breaks. |

## Grappling hook (`crossbow.grapple`)

The second mode of the crossbow: right click shoots a hook that catches a block and pulls the player to it. Speeds are in blocks per tick (20 ticks a second).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `crossbow.grapple.enabled` | boolean | `true` |  | The grappling hook mode. When false the mode is skipped and a crossbow left in it shoots bolts. |
| `crossbow.grapple.maxDistance` | double | `32.0` | 4.0 to 64.0 | Range of the hook in blocks. A hook that reaches it without catching comes back. |
| `crossbow.grapple.hookSpeed` | double | `2.5` | 0.5 to 5.0 | Speed of the flying hook, in blocks per tick. It flies straight, without gravity. |
| `crossbow.grapple.pullSpeed` | double | `0.8` | 0.1 to 3.0 | Speed at which the hook pulls the player, in blocks per tick (sprinting is about 0.28). |
| `crossbow.grapple.releaseDistance` | double | `1.5` | 0.5 to 4.0 | The hook lets go when the player's body is this close to the anchor, in blocks. |
| `crossbow.grapple.maxPullTicks` | int | `60` | 10 to 200 | Longest pull; the hook lets go after it. On a server with allow-flight=false keep it under 80, or a long upward pull can kick the player for flying. |
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

## Piston boots (`pistonboots`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `pistonboots.onlyWhileSprinting` | boolean | `true` |  | Step up full blocks only while sprinting, so walking up to a block does not climb it. False steps up at any pace while the boots are on. The sprint speed is only given while sprinting either way. |
| `pistonboots.stepHeight` | double | `1.001` | 0.5 to 3.0 | Step height while the boots step up (vanilla is 0.5). |
| `pistonboots.sprintSpeed` | boolean | `true` |  | Give Speed I while sprinting. |
| `pistonboots.safeFallDistance` | int | `8` | 0 to 256 | Falls shorter than this many blocks do no damage. 0 turns it off. |

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
| `render.wearableOverlay` | boolean | `true` |  | Show the overlays of the worn backpack, copter pack or jetpack on screen. |

## Keys (`keys`)

Client only. Default keys, as LWJGL key codes (B is 48, N is 49; see the LWJGL Keyboard class). They are only defaults: once you change a key in Options > Controls, Minecraft keeps your choice.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `keys.openBackpack` | int | `48` | 0 to 255 | Default key that opens the worn backpack, copter pack or jetpack (48 is B). Once you change the key in Options > Controls, Minecraft keeps your choice. Needs a restart. |
| `keys.toggle` | int | `49` | 0 to 255 | Default key that switches the hose tank and toggles the copter pack or jetpack (49 is N). Sneak + this key turns the copter pack or jetpack on and off. Needs a restart. |
| `keys.crossbowMode` | int | `47` | 0 to 255 | Default key that changes the firing mode of the held Clockwork Crossbow (47 = V). Needs a restart. |
| `keys.toggleAbilities` | int | `0` | 0 to 255 | Default key that turns the abilities of the worn backpack off and on, for this player only (night vision, water breathing, fire resistance, milk...). 0 = not bound: pick one in Options > Controls. Needs a restart. |

## Tool swapping keys (`keys.toolcycle`)

Client only. Swapping the held tool with the backpack's tool slots, and changing the hose mode. The keys themselves are changed in Options > Controls.

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `keys.toolcycle.enabled` | boolean | `true` |  | Swap the held tool with the backpack's tool slots, and change the hose mode, from the keyboard or mouse wheel. False turns both off, so the wheel always scrolls the hotbar. |
| `keys.toolcycle.mode` | string | `both` | wheel, keys, both | wheel: hold the modifier key and turn the mouse wheel. keys: only the next and previous tool keys. both: either one. |
| `keys.toolcycle.invertWheel` | boolean | `false` |  | Turn the mouse wheel the other way to cycle. |
| `keys.toolcycle.modifierKey` | int | `0` | 0 to 255 | Default key to hold while turning the mouse wheel. 0 means the sneak key (Left Shift by default), because a separate binding on the same key as sneak would stop one of the two from working in 1.7.10. Needs a restart. |
| `keys.toolcycle.nextToolKey` | int | `0` | 0 to 255 | Default key for the next tool. 0 means not bound. Needs a restart. |
| `keys.toolcycle.previousToolKey` | int | `0` | 0 to 255 | Default key for the previous tool. 0 means not bound. Needs a restart. |

## Backpacks in the world (`worldgen`)

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `worldgen.bonusChestBackpack` | boolean | `false` |  | Put a Standard Adventure Backpack in the bonus chest. |
| `worldgen.pigmanBackpacks` | boolean | `false` |  | Let Pigman Backpacks appear in dungeon loot and villager trades. |

## Extra recipes (`recipes`)

The saddle recipe, and the recipes of the Inflatable Boat, which change with the mods that are loaded. The list entries of the boat are read once when the game starts, after every mod has registered its items. See [Gear](Gear#inflatable-boat).

| Key | Type | Default | Range | What it does |
|---|---|---|---|---|
| `recipes.saddle` | boolean | `true` |  | Add a crafting recipe for the saddle. |
| `recipes.boat.mode` | string | `auto` | auto, vanilla, technical | vanilla: only the vanilla recipes. auto: the vanilla recipes, plus a rubber or motor variant when a loaded mod provides rubber or an electric motor. technical: the rubber or motor variant replaces the vanilla recipe of each part a loaded mod provides; a part no loaded mod provides keeps its vanilla recipe. Needs a restart. |
| `recipes.boat.rubber` | string list | `itemRubber, plateRubber, ingotRubber` |  | Ore Dictionary names of rubber (IC2 itemRubber, GregTech plateRubber and ingotRubber...). Each one some loaded mod provides gets its own chamber recipe (three rubber, two chambers). Needs a restart. |
| `recipes.boat.motors` | string list | `ic2:elemotor, item:gregtech:gt.metaitem.01:32600, ore:craftingElectricMotor, stack:ThermalExpansion:powerCoilGold` |  | Electric motors for the motorized boat, each one found gets its own recipe (boat + motor + Hydro Blades). Entries: ic2:&lt;IC2 item name&gt; (IC2 API), item:&lt;modid&gt;:&lt;item name&gt;[:&lt;damage&gt;], ore:&lt;Ore Dictionary name&gt;, stack:&lt;modid&gt;:&lt;registered stack name&gt;. Needs a restart. |

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
| `compat.graves.enabled` | boolean | `true` |  | Send the worn backpack, copter pack or jetpack to the grave of OpenBlocks or GraveStone when one of them is loaded. When false, `backpack.placeOnDeath` decides as if no grave mod were there. |

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
| `steamjetpack.thrust` | 0.08 | 0.1 |
| `steamjetpack.maxRiseSpeed` | 0.28 | 0.32 |
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

The old single-shot crossbow keys `crossbow.arrowVelocity` and `crossbow.arrowDamage` are removed from the file and not moved. Use `crossbow.boltVelocity` and `crossbow.boltDamage`. `crossbow.reloadTicks` now means the time to load a cartridge.

The section `crossbow.elemental` of 1.0.0 (`enabled`, `freezingTicks`, `freezingAmplifier`, `poisonTicks`, `poisonAmplifier`, `stunningTicks`, `stunningAmplifier`) is removed from the file and not moved: the elemental bolts no longer exist. Cartridges and loaded crossbows from 1.0.0 that held them load as normal ones.

## Not in the file

These are fixed on purpose: how much a backpack holds and how its slots are laid out (changing it would break saved backpacks), the boiler heat curve of the jetpack beyond its boiling point, the physics of the Inflatable Boat other than its top speed, the push strength of the copter rotor, the death placement search (12 blocks around, 6 up and down) and the reach of the sleeping bag button (8 blocks).

## See also

[Backpacks](Backpacks), [Jetpack and Copter Pack](Jetpack-and-Copter-Pack), [Crossbow](Crossbow), [Compatibility](Compatibility)
