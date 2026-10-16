# Controls

Every key, button and mouse action of the mod. The keys are listed in Options > Controls under the category Adventure Backpack, and you can change all of them there.

## Keys

The mod has four keys. Each one looks at what you ride, hold and wear, and does the first thing in its table that fits.

| Key (as listed in Controls) | Default | Does |
|---|---|---|
| Open Gear | B | Opens the window of the backpack, copter pack, jetpack, scuba gear or flamethrower pack you wear. Pressing it again closes the window. |
| Gear On/Off | N | Turns the gear you wear on and off: backpack abilities, copter engine, jetpack firebox, flamethrower pilot. |
| Change Mode | G | Boat motor, copter hover, hose tank. |
| Change State | V | Crossbow mode, hose mode, backpack tools. Sneak + the key goes backwards. |

The defaults come from the config file (see `keys` in [Configuration](Configuration#keys-keys)). They are applied the first time. Once you change a key in the game, Minecraft keeps your choice.

## Gear On/Off

| You wear | N does |
|---|---|
| A backpack | Turns its abilities off and on. See below. |
| A Copter Pack | Starts and stops the engine |
| A Steam Jetpack | Lets the firebox burn fuel and make steam, or stops it |
| A Flamethrower Pack | Lights and puts out the pilot |
| Scuba Gear | Nothing: it always works |

The windows of the copter pack, the jetpack and the flamethrower pack have an ON/OFF button that does the same, green when the gear is on and red when it is off. The window of a backpack you wear has one too, when the backpack has an ability. A chat message tells you what happened.

## Change Mode

| Order | When | G does |
|---|---|---|
| 1 | You ride the Motorized Inflatable Boat | Starts and stops the motor (in the default toggle mode). See [Gear](Gear#motorized-inflatable-boat). |
| 2 | You wear a Copter Pack with the engine on | Switches between normal mode and hover |
| 3 | You hold a Backpack Hose | Switches between the left and the right tank |

## Change State

| Order | When | V does |
|---|---|---|
| 1 | You hold the Clockwork Crossbow | Next mode: repetition, grappling hook, sniper |
| 2 | You hold a Backpack Hose | Next hose mode: suck, spill, drink. Sneak + V goes back. |
| 3 | You wear a backpack and hold a tool its tool slots take | Swaps the tool with the tool slots. Sneak + V swaps the other way. See [Swapping tools](#swapping-tools). |

## The backpack abilities

Press Gear On/Off with a backpack on, or click the ON/OFF button in its window, and the abilities of the backpack switch off; do it again and they switch on.

- It is yours alone. Other players keep theirs, and the choice is kept when you log out.
- Turning them off takes away effects the backpack gave you, like night vision or fire resistance. The backpack keeps working as a backpack.
- If abilities are turned off in the server config, the key only tells you so.
- The chat message can be turned off with `abilities.toggleMessage`.

## Other controls

| Control | Does |
|---|---|
| Right click, backpack in hand, in the air | Opens the window of the backpack in your hand. It has the Equip button. |
| Right click a block, backpack in hand | Places the backpack |
| Right click a placed backpack | Opens its window, with the sleeping bag button |
| Break a placed backpack | Drops it as an item with its contents |
| Sneak and break a placed backpack | Puts it on your back |
| Equip button, with Sneak held | Swaps with the backpack you already wear |
| Jump, wearing the copter pack with the engine on | Climbs |
| Sneak, wearing the copter pack with the engine on | Descends |
| Jump, wearing the jetpack on | Thrust |
| Right click, scuba gear or flamethrower pack in hand | Opens its window. It has the Equip button. |
| Hold right click, Flamethrower in hand, wearing the lit pack | Fires flame in a cone. See [Flamethrower](Flamethrower). |
| Right click, empty hand, on an Air Compressor | Pumps air in by hand. See [Scuba Gear](Scuba-Gear#air-compressor). |
| Sneak and right click, empty hand, on an Air Compressor | Fills the Scuba Gear you wear |
| Sneak and right click, Scuba Gear in hand, on an Air Compressor | Fills the gear in your hand |
| Right click a Fuel Station | Opens its window. See [Fuel Station](Fuel-Station). |
| Sneak and right click a Fuel Station, flamethrower pack or copter pack in hand or worn | Fills the pack |
| Right click a Fuel Station, riding the Motorized Inflatable Boat | Fills the boat's tank |
| Change Mode key (G), riding the Motorized Inflatable Boat | Starts the motor, with the pull of a cord, and the boat goes ahead by itself. Press it again to stop it. |
| Right click a wild spider, Spider backpack on, spider eye in hand | Tries to tame it. See [Taming spiders](Backpacks#taming-spiders). |
| Right click your tame spider, Spider backpack on | Gets on it. Without the backpack, nobody can ride it. |
| Jump, riding a spider | The spider jumps |
| Sneak and right click your tame spider, empty hand | Makes it sit down, or stand up again |
| Right click your tame spider, rotten flesh in hand | Heals it, if it is hurt |
| Sneak and right click your tame spider, spider eye in hand | Sets it free |
| Right click, crossbow in hand | Fires, or puts in a loaded Crossbow Magazine if the crossbow is empty. Hold it to fire the whole magazine. In grappling hook mode it shoots the hook. In sniper mode, with the bipod down, it fires the strong bolt. |
| Sneak and right click, crossbow in hand | Swaps the magazine for another loaded one; the first comes back with its charges left. In sniper mode, with the bipod down, it fires. |
| Hold right click, Crossbow Magazine in hand | Loads the magazine with arrows or Crossbow Bolts from your inventory, one charge at a time. See [Crossbow](Crossbow#the-crossbow-magazine). |
| Sneak and stand still, crossbow in sniper mode | Puts the bipod down and zooms in |
| Sneak or jump, hanging on the hook | Lets go of the hook |
| Right click the top of a block, Sleeping Bag in hand | Lays the bag on the ground as a bed |
| Sneak and right click a bag you laid yourself | Rolls it up into your inventory |
| Right click a campfire, raw food in hand | Puts the food on the fire to cook |
| Right click, hose in hand | Does what its mode says: suck, spill or drink |

## Swapping tools

With a backpack on, the tool in your hand can trade places with the two tool slots of the backpack. Each step rotates the three: the tool in your hand, the one in the upper slot and the one in the lower slot. The tool you hold goes into the backpack and one comes out.

- With the key: press Change State (V). Sneak + V swaps the other way.
- With the mouse wheel: hold Sneak and turn the wheel. One direction brings the tool of the upper slot, the other the one of the lower slot.

What counts as a tool: pickaxes, axes, shovels, hoes, shears, fishing rods, flint and steel, and tools from other mods like wrenches, hammers, scoops, grafters, crowbars, mattocks and drills. Swords, bows and buckets do not. With the Skeleton backpack, a bow does count.

A modpack or a server can change this with a list: only the items on it, or every tool except the ones on it. The list applies to the tool slots and to the swap. See [Configuration](Configuration#tool-slots-tweakstoolcycle).

With a hose in your hand the same key or wheel changes the hose mode instead: suck, spill, drink. If you hold something else, the wheel scrolls your hotbar as usual.

If the mouse wheel clashes with another mod, change the mode with `keys.toolcycle.mode`: `wheel`, `keys` or `both` (the default). `keys.toolcycle.invertWheel` reverses the direction, and `keys.toolcycle.enabled` turns the whole thing off.

## Conflicts

B is also the default key for the Baubles inventory. If you use Baubles, change one of the two in Options > Controls. See [Compatibility](Compatibility). N, G and V are free in vanilla Minecraft; if another mod uses one of them, Options > Controls shows it in red.

## See also

[Backpacks](Backpacks), [Fluids and Hose](Fluids-and-Hose), [Jetpack and Copter Pack](Jetpack-and-Copter-Pack), [Crossbow](Crossbow), [Scuba Gear](Scuba-Gear), [Flamethrower](Flamethrower), [Fuel Station](Fuel-Station), [Gear](Gear)
