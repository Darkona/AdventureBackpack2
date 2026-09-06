# Gear

The smaller items of the mod: boots, a machete, a hat, the campfire and sleeping bag that make a camp, and the inflatable boat. The backpacks, the hose, the crossbow and the flying packs have their own pages.

## Piston Boots

| | |
|---|---|
| Type | Boots |
| Obtained by | Crafting |
| Config section | `pistonboots`, `enabled.pistonBoots` |

> Screenshot: the crafting table with the Piston Boots recipe. File: images/gear-1.png

Wearing them you:

- step up full blocks while sprinting, without jumping (a server can let the boots step up at any pace, walking included)
- get Speed I while sprinting
- jump a little higher, with a piston sound
- take no damage from falls shorter than 8 blocks

They give 2 armor points, like iron boots.

The step height, the speed, the fall distance and when the boots step up are in [Configuration](Configuration#piston-boots-pistonboots).

## Machete

| | |
|---|---|
| Type | Tool |
| Obtained by | Crafting |
| Config section | none |

> Screenshot: the crafting table with the Machete Handle recipe, then the Machete recipe. File: images/gear-2.png

The machete is made of two iron ingots and a Machete Handle, and the handle has its own recipe.

It cuts through plants fast: leaves, grass, flowers, cobwebs, pumpkins, melons, mushrooms, cactus, cocoa, sugar cane, lily pads, hay bales, wool and crops. It also shears leaves, grass and vines the way shears do, so you get the blocks themselves.

## Adventurer's Fedora

| | |
|---|---|
| Type | Helmet |
| Obtained by | Crafting |
| Config section | none |

> Screenshot: the crafting table with the Adventurer's Fedora recipe. File: images/gear-3.png

A hat. It gives 2 armor points, the same as an iron helmet, and has no other effect.

## Campfire and sleeping bag

| | |
|---|---|
| Type | Blocks |
| Obtained by | Campfire: crafting. Sleeping Bag: crafting |
| Config section | `enabled.sleepingBag`, `sleepingbag`, `enabled.campfire`, `campfire.cooking` |

> Screenshot: the crafting table with the Campfire recipe, then the Sleeping Bag recipe. File: images/gear-4.png

### Making a camp

There are two ways to get a sleeping bag on the ground.

- From a placed backpack. Place the backpack and open it. Press the sleeping bag button, and the bag unrolls next to the backpack, if there is free space and solid ground for it. You get a chat message if it does not fit. Press the same button to roll it up again. If you pick up the backpack, the bag rolls up with it.
- On its own. Hold the Sleeping Bag item and right click the top of a block. The bag lies on the ground like a bed, with its head away from you. It needs two free blocks with solid ground under them. Break it to get the item back, or sneak and right click it to roll it up into your inventory.

Then:

1. Place a Campfire within 8 blocks of the bag.
2. Right click the bag to sleep.

> Screenshot: a placed backpack with the sleeping bag unrolled and a campfire next to it, at night. File: images/gear-5.png

> Screenshot: a Sleeping Bag laid on the ground with no backpack, next to a campfire. File: images/gear-6.png

After you wake up in a bag, if you die you respawn at the campfire you slept next to. If there is no campfire, you respawn at the bag, as with a bed. If you sleep in a normal bed, the usual bed rules come back.

A server can make the bags not move your spawn point, so you keep the spawn you had. See [Configuration](Configuration#sleeping-bag-sleepingbag).

The Sleeping Bag item is also one of the parts of the Standard backpack recipe.

### Cooking on the campfire

Right click the campfire with raw food in your hand and the food goes on the fire. It cooks in ten seconds, one item after another, and the result comes into your inventory if you stand within 4 blocks. If you are farther away, it pops out on top of the fire. The campfire takes up to 8 items of one kind at a time. The rest stays in your hand. It cooks what a furnace cooks into food: raw meat, fish and potatoes. Other recipes, like ores, are off by default.

> Screenshot: a campfire with smoke rising and a player holding raw porkchop in front of it. File: images/gear-7.png

If the fire is full or busy with another item, you get a chat message. If you break the campfire, the raw items on it drop. The campfire does not need fuel.

The time, the number of items, the range and whether it cooks more than food are in [Configuration](Configuration#campfire-cooking-campfirecooking).

## Inflatable boat

| | |
|---|---|
| Type | Item |
| Obtained by | Crafting |
| Config section | `inflatableboat`, `enabled.inflatableBoat`, `recipes.boat` |

The Inflatable Boat is built from parts, and the parts have their own recipes: Inflatable Chambers, a Hose Nozzle as the valve, and string. The Motorized one adds an outboard engine and Hydro Blades.

> Screenshot: the crafting table with the Inflatable Chamber recipe and the Inflatable Boat recipe, side by side. File: images/gear-8.png

> Screenshot: the crafting table with the Hydro Blades recipe and the Motorized Inflatable Boat recipe, side by side. File: images/gear-9.png

With only vanilla items, a chamber is made of leather and a slimeball, and the motor is a piston and a furnace. If another mod brings rubber or an electric motor, there are more recipes:

- Three rubber make two Inflatable Chambers. Rubber is searched by the names `itemRubber`, `plateRubber` and `ingotRubber`, which IndustrialCraft 2 and GregTech use.
- The Motorized Inflatable Boat can be made from an Inflatable Boat, Hydro Blades and an electric motor, instead of a piston and a furnace. The motors it looks for are the IndustrialCraft 2 Electric Motor, the GregTech LV Motor, any item named `craftingElectricMotor`, and the Thermal Expansion Redstone Reception Coil.

A pack can choose between the vanilla recipes only, both, or the mod parts replacing the vanilla ones, and change the lists. See [Configuration](Configuration#extra-recipes-recipes).

Right click on water to put a boat down. Its top speed is the same as the vanilla boat by default and can be raised in [Configuration](Configuration#inflatable-boat-inflatableboat).

## See also

[Backpacks](Backpacks), [Configuration](Configuration)
