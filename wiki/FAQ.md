# FAQ

## How do I put the backpack on?

Hold it and right click in the air. The window opens, and the Equip button puts it on your back. It works the same for the Copter Pack, the Steam Jetpack, the Scuba Gear and the Flamethrower Pack.

You can also break a placed backpack while sneaking: it goes straight onto your back.

If you already wear one, the button tells you so. Hold Sneak while pressing Equip to swap with the one you wear.

## How do I take it off?

Open the window with B and press the Unequip button. The backpack goes to your inventory, or drops at your feet if the inventory is full.

## How do I open the backpack?

Press B. It opens the one you wear, with its storage, tool slots, tanks and crafting grid. You can change the key in Options > Controls.

## How do I put the backpack down as a block?

Right click on a block with the backpack in your hand. Right click the placed backpack to open it. Break it to get the item back, with everything inside; break it while sneaking to put it on your back.

A placed backpack with an ability like the Cactus, the Melon, the Glowstone or the Redstone keeps doing its thing. See [Backpacks](Backpacks#placed-backpacks).

## Can I wear a backpack and a jetpack at once?

No. The backpack, the Copter Pack, the Steam Jetpack, the Scuba Gear and the Flamethrower Pack share one place on your back. With Baubles or Baubles Expanded installed, a backpack can sit in a bauble slot instead, and it still counts as the one you wear. See [Compatibility](Compatibility#baubles-and-baubles-expanded).

## Can I put a backpack inside a backpack?

No. The storage slots refuse backpacks.

## How do I use the sleeping bag?

Place a backpack, open it and press the sleeping bag button. The bag unrolls beside the backpack if the ground is free. Right click the bag to sleep. Press the button again to roll it up.

You can also lay a bag on the ground without a backpack: hold the Sleeping Bag item and right click the top of a block. Sneak and right click the bag to roll it up into your inventory.

Put a Campfire within 8 blocks of the bag before you sleep, and if you die you respawn at that campfire. See [Gear](Gear#campfire-and-sleeping-bag).

The sleeping bag button and the Sleeping Bag item do nothing if `enabled.sleepingBag` is off.

## Can I cook on the campfire?

Yes. Right click the campfire with raw food in your hand. It cooks in ten seconds per item, and the result comes to your inventory if you are within 4 blocks. See [Gear](Gear#cooking-on-the-campfire).

## How do I tame a spider?

Wear the Spider backpack and right click a wild spider with a spider eye. It works one time in three, and each try uses an eye. A tame spider is yours for good: it follows you, climbs walls and towers after you, and you can ride it while you wear the backpack. You can have two at a time. See [Backpacks](Backpacks#taming-spiders).

## How do I ride, sit, heal or set free my tame spider?

Right click it with the Spider backpack on to ride it. Sneak and right click it with an empty hand to make it sit down or stand up. Right click it with rotten flesh to heal it. Sneak and right click it with a spider eye to set it free. The table of gestures is in [Backpacks](Backpacks#taming-spiders), and the same list is in [Controls](Controls).

## Why does my tame spider not follow me?

It does not follow while it is sitting, or when you are in another dimension: it waits where you left it, because a tame spider is not set free when you die, log out or change dimension. Sneak and right click it with an empty hand to make it stand up. If it cannot reach you it climbs, and if it is stuck for a few seconds it jumps next to you. See [Configuration](Configuration#spider-backpack-abilitiesspider).

## How do I turn the abilities off?

Bind the Backpack Abilities On/Off key in Options > Controls, which has no default key, and press it. It turns off the abilities of the backpack you wear, only for you. Press it again to turn them on. See [Controls](Controls#the-abilities-key).

## How do I change the crossbow mode?

Hold the crossbow and press V. The modes go repetition, grappling hook, sniper. The mode shows in the name of the crossbow. See [Crossbow](Crossbow).

## How do I load the crossbow?

Hold a Crossbow Magazine and hold right click: it takes arrows or Crossbow Bolts from your inventory, one charge at a time, until it holds six. Then hold the crossbow and right click: when it has no bolts left it takes the first loaded magazine of your inventory, hotbar first. Sneak and right click swaps the magazine before it is empty. See [Crossbow](Crossbow#the-crossbow-magazine).

## Where did my empty Crossbow Magazine go?

It comes back to your inventory when the crossbow takes a new one, or falls at your feet if the inventory is full. A magazine is not used up: load it again with arrows or Crossbow Bolts. If the crossbow has no loaded magazine to take, a click gives you back the empty one. See [Crossbow](Crossbow#loading-the-crossbow).

## Why does my crossbow in sniper mode fire normal bolts?

The bipod is folded. Sneak and stand still on the ground to put it down. With the bipod down the view zooms and one click fires the strong bolt.

## Can I craft the Inflatable Boat?

Yes. It is made from Inflatable Chambers, a Hose Nozzle and string, and the Motorized one adds Hydro Blades and an engine. See [Gear](Gear#inflatable-boat).

## How do the tanks work?

A backpack has two tanks of 4000 mB each. Put a bucket or a bottle in a tank's input slot, in the window, to fill it or empty it. With the Backpack Hose you can fill a tank from a water or lava block, pour it out again, or drink from it. See [Fluids and Hose](Fluids-and-Hose).

Some backpacks fill their own tanks: the Cactus with water, the Melon with melon juice, the Cow with milk from wheat and the Mooshroom with mushroom stew from wheat.

## What happens when I die?

It depends on what you wear and on the settings.

| You wear | And | What happens |
|---|---|---|
| A backpack | Default settings | The backpack is placed as a block close to where you died, with its contents. If there is no room within about 12 blocks, it drops as an item. |
| A backpack | `backpack.placeOnDeath` is off | It drops as an item. |
| A copter pack, a jetpack, scuba gear or a flamethrower pack | Always | It drops as an item. |
| Anything | OpenBlocks or GraveStone makes a grave | It goes into the grave. |
| Anything, in a bauble slot | Baubles | Baubles handles it like its other items. |
| Anything | The gamerule keepInventory is on | You keep it. |

After you respawn, a chat message tells you where it went, with coordinates and dimension. You can turn the message off with `backpack.deathMessage`.

The Creeper backpack explodes when you die. The explosion does not break blocks.

## Why does my Copter Pack do nothing?

It needs a fuel, and the fuels come from other mods: BuildCraft, Forestry, Railcraft, Thermal Foundation or GregTech bring one. With none of them loaded the item has no recipe and does nothing. With one, put fuel in the tank of the pack with a bucket, in its window, or fill it at a Fuel Station. See [Compatibility](Compatibility#fuel-mods) and [Jetpack and Copter Pack](Jetpack-and-Copter-Pack#fuel).

## How do I breathe under water?

Wear the Scuba Gear and fill its tanks. An Air Compressor does that: pump it by hand with an empty hand, or burn a furnace fuel in it, then put the gear in its slot. While your head is under water the gear keeps your air bar full. See [Scuba Gear](Scuba-Gear).

## Why does my Scuba Gear not use air?

It spends air only when your air bar would go down. With a Water Breathing potion, or a Squid or Dragon backpack, you breathe for free and the tanks stay full. Out of the water it spends nothing either.

## Why does my Air Compressor not make air?

The hand pump and the furnace need open air: the block above the compressor must be air, so it makes nothing buried or under water. A furnace fuel is needed in the fuel slot, and the tank must have room. Air from a pipe is taken anywhere. See [Scuba Gear](Scuba-Gear#air-compressor).

## How do I light the Flamethrower?

Wear the pack, fill its tanks and press Sneak + N. A message says the pilot is lit. Then hold the Flamethrower in your hand and hold right click. If it does not fire, the pilot is out: it goes out when the tanks run dry, when you take the pack off and when you die. See [Flamethrower](Flamethrower).

## Why does the Flamethrower not hurt other players?

By default it does not: `flamethrower.pvp` is off. It does not set blocks on fire either, which `flamethrower.ignitesBlocks` turns on. Both are for the server owner to change. See [Configuration](Configuration#flamethrower-flamethrower).

## How do I start the boat motor?

Fill the tank at a Fuel Station: sit in the Motorized Inflatable Boat and right click the station. Bind the Boat Motor On/Off key in Options > Controls, which has no default key, and press it while you sit in the boat on water. The motor starts with the pull of a cord and the boat goes ahead by itself; press the key again to stop it. The motor stops by itself when it runs out of fuel, when you get off, or when the boat leaves the water. See [Gear](Gear#motorized-inflatable-boat).

## Why does the boat motor make no sound, or no message?

The motor is told by sound, not by chat: a looping putt-putt while it runs, a cord pull when it starts and a cough when it stops. If you hear nothing, check that `inflatableboat.motor.sound` is on and that `inflatableboat.motor.soundVolume` is not 0, and that the sound volume of the game is up. For players who cannot hear, `inflatableboat.motor.messages` brings back the chat messages. See [Configuration](Configuration#boat-motor-inflatableboatmotor).

## What fuel can I use?

The fluids of the fuel list: BuildCraft fuel and oil, Forestry bioethanol and biomass, Railcraft creosote and GregTech diesel, and others. The same list serves the Copter Pack, the Flamethrower Pack, the boat motor and the Fuel Station. See [Fluids and Hose](Fluids-and-Hose#fuels).

## Why did the Standard backpack change its look?

On some dates of the year the Standard backpack wears a holiday look, using your computer's date: New Year's Day and Eve, Valentine's Day, Saint Patrick's Day, December 22 to 26 and a few more. It goes back to normal the next day.

## I changed my backpack to another type and lost nothing. Is that right?

Yes. Crafting a backpack into another type keeps its contents and the fluids in its tanks.

## Which backpacks have no recipe?

Bat, Iron Golem, Villager, Pigman, Electric and Deluxe. See [Backpacks](Backpacks#other-ways-to-get-a-backpack).

## Do I need the same settings on the server and on my client?

For the settings marked in [Configuration](Configuration) as "same on server and clients" (tank sizes, abilities, the steam and air fluids, the fuel list), yes. The others are read by whoever uses them: the display and key settings only matter on the client.

## Where is the config file?

`config/adventurebackpack.cfg`, created the first time the game starts. Change it and restart the game. The Mods list also has a config screen. Settings from older versions are moved to the new layout the first time.

## See also

[Home](Home), [Backpacks](Backpacks), [Controls](Controls)
