# FAQ

## How do I put the backpack on?

Hold it and right click in the air. The window opens, and the Equip button puts it on your back. It works the same for the Copter Pack and the Steam Jetpack.

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

No. The backpack, the Copter Pack and the Steam Jetpack share one place on your back. With Baubles or Baubles Expanded installed, a backpack can sit in a bauble slot instead, and it still counts as the one you wear. See [Compatibility](Compatibility#baubles-and-baubles-expanded).

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

Wear the Spider backpack and right click a spider with a spider eye. It works about one time in three, and each try uses an eye. A tame spider follows you, and only you can ride it, while you wear the backpack. You can have two at a time. Sneak and right click one to let it go. See [Backpacks](Backpacks#taming-spiders).

## Why does my tame spider stand still?

It follows and carries you only while you wear the Spider backpack. Put it on again and it follows. When you die, log out or change dimension, your tame spiders go free and become normal spiders.

## How do I turn the abilities off?

Bind the Backpack Abilities On/Off key in Options > Controls, which has no default key, and press it. It turns off the abilities of the backpack you wear, only for you. Press it again to turn them on. See [Controls](Controls#the-abilities-key).

## How do I change the crossbow mode?

Hold the crossbow and press V. The modes go repetition, grappling hook, sniper. The mode shows in the name of the crossbow. See [Crossbow](Crossbow).

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
| A copter pack or a jetpack | Always | It drops as an item. |
| Anything | OpenBlocks or GraveStone makes a grave | It goes into the grave. |
| Anything, in a bauble slot | Baubles | Baubles handles it like its other items. |
| Anything | The gamerule keepInventory is on | You keep it. |

After you respawn, a chat message tells you where it went, with coordinates and dimension. You can turn the message off with `backpack.deathMessage`.

The Creeper backpack explodes when you die. The explosion does not break blocks.

## Why does my Copter Pack do nothing?

It needs BuildCraft. Without BuildCraft the item is there but does nothing. See [Compatibility](Compatibility#buildcraft).

## Why did the Standard backpack change its look?

On some dates of the year the Standard backpack wears a holiday look, using your computer's date: New Year's Day and Eve, Valentine's Day, Saint Patrick's Day, December 22 to 26 and a few more. It goes back to normal the next day.

## I changed my backpack to another type and lost nothing. Is that right?

Yes. Crafting a backpack into another type keeps its contents and the fluids in its tanks.

## Which backpacks have no recipe?

Bat, Iron Golem, Villager, Pigman, Electric and Deluxe. See [Backpacks](Backpacks#other-ways-to-get-a-backpack).

## Do I need the same settings on the server and on my client?

For the settings marked in [Configuration](Configuration) as "same on server and clients" (tank sizes, abilities, the steam fluid), yes. The others are read by whoever uses them: the display and key settings only matter on the client.

## Where is the config file?

`config/adventurebackpack.cfg`, created the first time the game starts. Change it and restart the game. The Mods list also has a config screen. Settings from older versions are moved to the new layout the first time.

## See also

[Home](Home), [Backpacks](Backpacks), [Controls](Controls)
