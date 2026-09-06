# Changelog

## 1.1.0 (unreleased)

For Minecraft 1.7.10 with Forge.

The Clockwork Crossbow gets two new modes and loses its elemental bolts, spiders become pets you tame, the Blaze backpack gets its ability, the sleeping bag and the Inflatable Boat can be crafted and used on their own, and the campfire cooks. Numbers in brackets are issues in the GitHub repository.

### Added

- **Crossbow modes.** A key, V by default, changes the mode of the crossbow in your hand: repetition, grappling hook and sniper. See [Crossbow](Crossbow).
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
- The config file has 188 keys now. See [Configuration](Configuration).

### Changed

- **Spiders are not friends on their own any more.** In 1.0.0 a spider you right clicked with the Spider backpack on became a spider to ride. Now you tame it with a spider eye, you can have two, and they go free when you die, log out or change dimension. Friendly spiders saved by 1.0.0 have no owner and turn back into normal spiders.
- **Blaze backpack.** It had no ability before.
- **Sleeping Bag.** The item can be placed on its own, and sleeping in a bag can be set not to move your spawn point (`sleepingbag.setsSpawn`).
- **Inflatable Boat.** It was only in the creative tab. The two boats, the Inflatable Chamber and the Hydro Blades now have crafting recipes.
- **Bolt Cartridge.** There is a single kind of cartridge with six bolts, made of arrows, an iron ingot and sticks.

### Removed

- **Elemental bolts.** The Blazing, Freezing, Poison and Stunning cartridges and bolts are gone, with their recipes and the `crossbow.elemental` settings, which leave the file on the first start. Cartridges and crossbows loaded with them work as normal ones.

### Fixed

- Tamed spiders no longer pile up in the world. They never despawned and had no owner.
- The sleeping bag no longer stays marked as occupied after you wake up.

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
