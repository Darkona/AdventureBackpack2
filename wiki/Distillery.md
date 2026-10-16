# Distillery

A block that turns crops into ethanol, a liquid fuel. Put it next to a [Fuel Station](Fuel-Station) and the station fills by itself: no other mod is needed to fly the Copter Pack, light the Flamethrower or run the boat motor.

| | |
|---|---|
| Type | Block |
| Obtained by | Crafting |
| Config section | `distillery`, `enabled.distillery`, `fuels` |

## Crafting

| | | |
|---|---|---|
| Iron Ingot | Bucket | Iron Ingot |
| Glass Pane | Cauldron | Glass Pane |
| Iron Ingot | Furnace | Iron Ingot |

## How it works

Right click the Distillery to open its window. It needs three things at the same time, and it makes ethanol in batches:

- **Organics** in the 3x3 grid: wheat, potatoes, carrots, sugar cane, apples, melon slices, pumpkins, poisonous potatoes, seeds, and the corn, rice, barley, rye and sugar beet of farming mods. Each batch takes one item from every slot of the grid, so nine different crops make the most ethanol at once.
- **A catalyst** in the slot above the flame. By default it is sugar: one per batch. The empty slot shows the catalyst it takes.
- **Water** in the tank on the left: 1000 mB per batch.

Heat comes from the fuel slot under the flame. Coal and charcoal burn for 3200 ticks (eight batches), a block of coal for ten times that, and any other furnace fuel for its furnace time. The Distillery only works while the flame burns, and it only lights a new fuel item when there is a batch to make. The front of the block shows the fire and gives light while it burns.

A batch takes 20 seconds of flame. The arrow fills while it runs, and the ethanol goes into the tank on the right, which holds 8000 mB. Without flame, water, catalyst or organics, or with the ethanol tank full, it stops and waits. An idle Distillery does no work at all until something changes.

| Crop | Ethanol per item |
|---|---|
| Pumpkin | 250 mB |
| Potato, sugar cane, apple, corn, sugar beet | 150 mB |
| Carrot | 120 mB |
| Wheat, poisonous potato, rice, barley, rye | 100 mB |
| Melon slice | 40 mB |
| Seeds | 20 mB |

## Water

There are three ways to fill the water tank:

- **Buckets.** Right click the Distillery with a water bucket, or put full water containers in the slot above the arrow: the water goes in and the empty container comes out in the slot below.
- **Water source blocks.** Each water source block next to the Distillery (on its four sides or under it) gives 50 mB per second. Two sources keep it running without buckets.
- **Pipes.** A pipe or pump of another mod can push water in from any side.

## The chain: Distillery and Fuel Station

The Distillery pushes its ethanol out of its four sides, once a second, into any block next to it that takes fluid: the [Fuel Station](Fuel-Station), or a pipe or tank of another mod. The Fuel Station takes it from any side because ethanol is on the [fuel list](Fluids-and-Hose#fuels). A pipe can also pull the ethanol out of the Distillery.

A chain that runs by itself, with vanilla blocks only:

1. Dig two water source blocks next to the Distillery.
2. Place a Fuel Station against another side.
3. Feed the grid and the catalyst from the top with a hopper, and the fuel from a side with another hopper.

Then fill your Copter Pack, Flamethrower Pack or boat at the station.

Ethanol burns a little faster than BuildCraft fuel and slower than oil (`bioethanol=1.2` in `fuels.list`).

## Hoppers and pipes

| Side | Items | Fluids |
|---|---|---|
| Top | Organics and catalyst go in | Water in, ethanol out |
| Sides | Fuel and full water containers go in | Water in, ethanol out (and pushed) |
| Bottom | Empty containers come out | Water in, ethanol out |

The window works with Inventory Tweaks (its sort key sorts the organic grid) and Mouse Tweaks.

## Other mods

The fluid is called `bioethanol`, the name Forestry, Railcraft and GregTech already use. With one of them loaded the Distillery makes their ethanol, with their texture and their buckets, and Adventure Backpack registers no fluid of its own. Without them it registers its own Ethanol, which has no block and no bucket: it lives in tanks, pipes and fuel packs.

If you break the Distillery, its slots drop. The water and the ethanol in its tanks are lost.

## For pack makers

Everything is in the `distillery` section of the [configuration](Configuration#distillery-distillery):

- `organics`: which crops it takes and how much ethanol each one makes, one per line as `<item>=<mB>`, for example `ore:cropTomato=80` or `item:Natura:barleyFood:0=100`.
- `catalysts` and `catalystPerBatch`: what the catalyst slot takes and how many per batch (0 makes it free).
- `solidFuels` and `furnaceFuelMultiplier`: burn time of each fuel in ticks, and of every other furnace fuel.
- `waterPerBatch`, `ticksPerBatch`, `outputCapacity`, `waterCapacity`, `waterPerSource`: the numbers of a batch.
- `pushIntervalTicks` and `pushAmount`: how often and how much it pushes (0 leaves the ethanol for pipes to pull).
- `outputFluid`: make another fluid instead, for example a pack's own fuel.
- `enabled.distillery` false: the block stays in the game, but it has no recipe, is hidden and does nothing, for packs that make fuel with another mod.

---

[Fuel Station](Fuel-Station), [Fluids and Hose](Fluids-and-Hose), [Flamethrower](Flamethrower), [Jetpack and Copter Pack](Jetpack-and-Copter-Pack), [Configuration](Configuration)
