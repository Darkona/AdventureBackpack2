# Changelog

## 1.1.0 (unreleased)

For Minecraft 1.7.10 with Forge.

The Clockwork Crossbow gets two new modes, reusable magazines and a new bolt, and loses its elemental bolts, spiders become pets you tame, the Blaze backpack gets its ability, the sleeping bag and the Inflatable Boat can be crafted and used on their own, and the campfire cooks. There is dive equipment, a flamethrower, a fuel station and an outboard motor for the boat, and the Copter Pack no longer needs BuildCraft. Numbers in brackets are issues in the GitHub repository.

### Added

- **Crossbow modes.** A key, V by default, changes the mode of the crossbow in your hand: repetition, grappling hook and sniper. See [Crossbow](Crossbow).
- **Crossbow Bolt.** New ammunition for the crossbow, made of an iron ingot, a stick and a feather, four at a time. A magazine loaded with Crossbow Bolts fires bolts that hit half as much again as arrows. Settings: `crossbow.ammo`. See [Crossbow](Crossbow#ammunition).
- **Loading magazines by hand.** Hold right click with a Crossbow Magazine in your hand to load it with arrows or Crossbow Bolts from your inventory, one charge at a time. A shapeless recipe also fills a magazine in the crafting grid. Settings: `crossbow.magazine`. See [Crossbow](Crossbow#the-crossbow-magazine).
- **Two-handed crossbow.** In third person the player holds the crossbow with both hands and aims along it, and the reload shows the spent magazine falling out to the right and the new one going in through the slot.
- **Grappling hook.** Right click shoots a hook that catches the first solid block within 32 blocks and pulls you to it. You let go by sneaking or jumping, and you take no fall damage from the pull. Settings: `crossbow.grapple`.
- **Sniper mode.** Sneak and stand still on the ground to put a bipod down and zoom in; right click fires one strong, straight bolt. Settings: `crossbow.sniper`.
- **Taming spiders.** With the Spider backpack on, right click a spider with a spider eye to try to tame it. A tame spider follows you and only you ride it, while you wear the backpack. Settings: `abilities.spider`. See [Backpacks](Backpacks#taming-spiders).
- **Blaze backpack ability.** You do not burn in fire or lava, and your punches set what you hit on fire. Settings: `abilities.blaze`.
- **Sleeping Bag on its own.** Right click the top of a block with the Sleeping Bag item to lay it on the ground as a bed, with no backpack. Break it or sneak and right click it to get the item back. Settings: `sleepingbag`. See [Gear](Gear#campfire-and-sleeping-bag).
- **Craftable Inflatable Boat.** New parts: Inflatable Chamber and a recipe for the Hydro Blades. Rubber and electric motors from IndustrialCraft 2, GregTech and Thermal Expansion give extra recipes. Settings: `recipes.boat`. See [Gear](Gear#inflatable-boat).
- **Cooking on the campfire.** Right click the campfire with raw food to cook it. Settings: `campfire.cooking`. See [Gear](Gear#cooking-on-the-campfire).
- **Abilities key.** A key, not bound by default, turns the abilities of the backpack you wear off and on, only for you (#57). Setting: `abilities.toggleMessage`. See [Controls](Controls#the-abilities-key).
- **Tool slot list.** A whitelist or a blacklist decides which items go in the tool slots of the backpack and into the tool swap (#49). Settings: `tweaks.toolcycle`.
- **Squid backpack on land.** `abilities.squid.worksOnLand` gives the Squid water breathing and night vision out of water too, for mods that make the player breathe water (#151).
- **Piston Boots at any pace.** `pistonboots.onlyWhileSprinting` set to false makes the boots step up blocks while walking (#126).
- **Scuba Gear.** Two air tanks of 3000 mB and a diving mask, worn on your back. Under water they keep your air bar full and spend 10 mB of air per second, so full tanks last 10 minutes. You get a warning at 600 mB. Press B to open its window. Settings: `scuba`, `enabled.scuba`. See [Scuba Gear](Scuba-Gear).
- **Air Compressor.** A block that fills the Scuba Gear in its slot, or the one you wear or hold with Sneak and right click. It makes air with a hand pump (right click, empty hand) or by burning any furnace fuel, needs open air above it, and takes air or oxygen from pipes, GregTech Air Cells and the IndustrialCraft 2 Compressed Air Cell. Settings: `scuba.compressor`. See [Scuba Gear](Scuba-Gear#air-compressor).
- **Flamethrower.** The Flamethrower Pack goes on your back with two fuel tanks of 4000 mB and a pyro mask. Sneak + N lights the pilot, and holding right click with the Flamethrower nozzle throws flame in a cone of 6 blocks and 25 degrees: 2.0 damage and 5 seconds of fire per burst, 20 mB of fuel per burst. It does not hurt players or light blocks unless a server allows it. Settings: `flamethrower`, `enabled.flamethrower`. See [Flamethrower](Flamethrower).
- **Fuel Station.** A block with a tank of 16000 mB that takes the fuels of the fuel list from buckets, cans, cells and pipes. It fills the Flamethrower Pack and the Copter Pack in its slot, or worn or held with Sneak and right click, and the Motorized Inflatable Boat with a right click while you ride it. Hoppers move the containers and comparators read the tank. Settings: `fuelstation`, `enabled.fuelStation`. See [Fuel Station](Fuel-Station).
- **Boat motor.** The Motorized Inflatable Boat gets an outboard motor with a tank of 6000 mB, about 5 minutes of running. It turns on and off with a key, Boat Motor On/Off, not bound by default, and the boat goes up to 12 blocks per second and speeds up twice as fast. It stops without fuel, without a pilot and out of the water, and the fuel stays in the boat when you pick it up. Settings: `inflatableboat.motor`, `keys.boatMotorToggle`. See [Gear](Gear#motorized-inflatable-boat).
- **Air and fuel from other mods.** Fuels and air are found by fluid name, so BuildCraft, Forestry, Railcraft, Thermal Foundation, GregTech, Galacticraft and Mekanism work without being needed. Settings: `fuels.list`, `scuba.breathableFluids`, `scuba.airItems`. See [Compatibility](Compatibility).
- The config file has 239 keys now. See [Configuration](Configuration).

### Changed

- **Spiders are not friends on their own any more.** In 1.0.0 a spider you right clicked with the Spider backpack on became a spider to ride. Now you tame it with a spider eye, you can have two, and they go free when you die, log out or change dimension. Friendly spiders saved by 1.0.0 have no owner and turn back into normal spiders.
- **Blaze backpack.** It had no ability before.
- **Sleeping Bag.** The item can be placed on its own, and sleeping in a bag can be set not to move your spawn point (`sleepingbag.setsSpawn`).
- **Inflatable Boat.** It was only in the creative tab. The two boats, the Inflatable Chamber and the Hydro Blades now have crafting recipes.
- **Crossbow Magazine.** The Bolt Cartridge is now the Crossbow Magazine, and it is reusable. It starts empty, you load it with arrows or Crossbow Bolts, and when the crossbow takes a new magazine the spent one comes back to your inventory to be loaded again. Sneak and right click swaps a magazine before it is empty. The 1.0 recipe of six arrows, an iron ingot and two sticks makes a magazine loaded with six arrows, and the empty one is made of an iron ingot, two sticks and a string. Cartridges from 1.0 are full magazines of arrows, and crossbows loaded with them keep their bolts.
- **Crossbow settings.** `crossbow.shotsPerMagazine` is now `crossbow.magazine.capacity`, and its value moves on the first start.
- **Copter Pack without BuildCraft.** It burns any fuel of the new list, not only BuildCraft's, and works with Forestry, Railcraft, Thermal Foundation or GregTech fuels. With no fuel mod it has no recipe and does nothing, as before.
- **Fuel list.** The setting `copterpack.fuels` is now `fuels.list`, shared by the Copter Pack, the Flamethrower, the boat motor and the Fuel Station. The value moves on the first start: a list you never changed gets the new default, which adds Forestry biomass, and one you changed stays. See [Fluids and Hose](Fluids-and-Hose#fuels).
- **Motorized Inflatable Boat.** Its motor was only a look; it now runs on fuel. The boat is the same as the plain one when the motor is off or out of fuel.
- **One thing on your back.** The Scuba Gear and the Flamethrower Pack take the same place as the backpack, the Copter Pack and the Jetpack. They drop where you die and go into a grave, like the jetpack.
- **Screen overlay.** `render.wearableOverlay` also shows the tanks of the new packs, and the fuel tank of the motorized boat you ride.

### Removed

- **Elemental bolts.** The Blazing, Freezing, Poison and Stunning cartridges and bolts are gone, with their recipes and the `crossbow.elemental` settings, which leave the file on the first start. Cartridges and crossbows loaded with them work as normal ones.

### Fixed

- Tamed spiders no longer pile up in the world. They never despawned and had no owner.
- The sleeping bag no longer stays marked as occupied after you wake up.
- The crossbow no longer looks crossed over the player in third person, and its reload no longer shows a block floating above the drum.

## 1.0.0 (2026-10-15)

For Minecraft 1.7.10 with Forge.

This is a review of the whole mod: a new weapon, new compatibilities, a config file that covers everything, flying that costs, and a long list of crashes, lost items and display problems fixed. Numbers in brackets are issues in the GitHub repository.

### New

- **Clockwork Crossbow.** A repeating crossbow with a magazine of six bolts, loaded from Bolt Cartridges. There are four elemental cartridges: Blazing, Freezing, Poison and Stunning. See [Crossbow](Crossbow).
- **Baubles and Baubles Expanded.** The Adventure Backpack goes in a bauble slot: the belt with Baubles, a new Back slot with Baubles Expanded. See [Compatibility](Compatibility).
- **Grave mods.** With OpenBlocks or GraveStone, the backpack, copter pack or jetpack you wear goes into your grave.
- **Own steam.** The Steam Jetpack no longer needs Railcraft. If no mod adds steam, the backpack adds its own. The jetpack is always registered.
- **Tool swapping with its own keys.** The modifier for the mouse wheel is a key you can change, and there are optional Next and Previous Backpack Tool keys. You can also use only the keys, or turn the feature off. See [Controls](Controls).
- **A chat message when you die** tells you where the backpack, copter pack or jetpack went, with coordinates.
- **Config for everything.** The file is now one tree with a switch per system and 150 keys: abilities, hose, flying packs, crossbow, boots, display, keys, world and compatibility. Old keys move to their new place on the first start. See [Configuration](Configuration).
- **Placed backpack settings.** Explosion resistance and hardness of a placed backpack, and the size of the backpack tanks, are settings now.

### Changed

- **Flying costs.** A full tank of the copter pack or a full jetpack gives about one to two minutes. The copter pack takes 100 mB of fuel to start, and charges climbing, which was free before. The jetpack spends more steam, burns its fuel faster while thrusting, loses heat while thrusting and cools faster. Both are slower and have a lower ceiling, Y 90. The old values are in [Configuration](Configuration#flight-values-before-100).
- **Copter pack fuels** are a setting, `copterpack.fuels`.
- **The Copter Pack** is always registered and works only with BuildCraft. Its recipe accepts any planks, not only oak.
- **Pig and Chicken backpacks** wait a whole interval before the first oink or egg. Placing and picking up a Chicken backpack no longer lays an egg every time.
- **Dragon backpack.** It now gives real Strength II and Regeneration I, and renews its fire resistance before it ends.
- **Rainbow backpack.** It no longer slows you down between runs, and it no longer removes other Speed and Jump effects.
- **Hose.** It sucks only source blocks, and only when the whole bucket fits. It follows the same spawn protection and build checks as a bucket.
- **Mushroom stew.** There is one fluid id now. Tanks saved with the old name keep their stew. Milk, melon juice and mushroom stew from another mod are reused if they are already there.
- **Crafting a backpack into another type** keeps its contents and its fluids.

### Fixed

Crashes and disconnects

- Drinking from the hose on a server disconnected you (#144, #109), and water healed only sometimes (#152).
- Invalid UUID crash with horses tamed in old versions (#99).
- Crash while joining a world or changing dimension (#121).
- Crash drawing the status effects of a potion that is not registered on the client (#108).
- Crash when equipping with the sneak key bound to a mouse button (#115).
- Crash when taking off a copter pack without data, with the jetpack boiler when no mod has steam, and with a backpack component of damage 0.

Lost and duplicated items

- Two ways to duplicate items were closed: slots that kept stale items when a backpack reloaded, and a backpack window that stayed open after its item moved (#96). A placed backpack no longer duplicates its bucket slots when you pick it up.
- A backpack lost when placing was cancelled in a protected area (#86).
- A copter pack or jetpack could be moved with the number keys while its window was open.
- Hose: sucking a flowing block gave a full bucket and so made fluid from nothing.
- The worn backpack is now part of the drops, so grave and soulbound mods see it, and a death another mod cancels drops nothing (#117, #128).
- Placing the backpack at death finds spots on snow and grass, and works with negative coordinates.
- Tool swapping uses the item that is in the slot, and checks it again on the server (#56, #67, #78).

Display

- Fluid tanks in the window are drawn faster and no longer overflow the graphics stack (#41, #136, #110).
- The cape is hidden only while you wear a backpack (#89). A new setting hides the worn backpack when another mod draws it (#107).
- A holiday look with no texture falls back to the Standard backpack (#106).
- Block names and the mushroom stew name appear translated.
- The worn backpack, the hat and the textures are built once, not on every frame.

Abilities and gear

- Bat night vision no longer makes the sky flicker (#102). Pigman fire resistance ends when you take the backpack off, and renews before it ends (#105).
- The Sunflower and the other timed abilities no longer change the backpack every tick, so the server sends less.
- Piston Boots give the step height back when you take them off while sprinting.
- The machete shears with the real shears item, not a new one for every block.
- Sleeping bag: deploys over tall grass and snow, and only when you are near the backpack (#104).
- Placed backpack: stale items are cleared when it loads, and its drop no longer flies away.

Flying packs

- The ceiling above Y 200 no longer gives full lift again.
- The jetpack boiler cools to the temperature of the biome, not to zero.
- The jetpack and copter pack send less data to the server and draw their smoke on each client.
