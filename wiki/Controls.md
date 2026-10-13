# Controls

Every key, button and mouse action of the mod. The keys are listed in Options > Controls under the category Adventure Backpack, and you can change all of them there.

## Keys

| Key (as listed in Controls) | Default | Does |
|---|---|---|
| Open Inventory Key | B | Opens the window of the backpack, copter pack, jetpack, scuba gear or flamethrower pack you wear. Pressing it again closes the window. |
| Toggle Action Key | N | Depends on what you hold and wear. See the table below. |
| Tool Cycle Modifier (none = sneak) | Sneak (Left Shift) | Hold it while turning the mouse wheel to swap tools. |
| Next Backpack Tool | not bound | Swaps to the next tool. |
| Previous Backpack Tool | not bound | Swaps to the previous tool. |
| Crossbow Mode | V | Changes the mode of the Clockwork Crossbow in your hand: repetition, grappling hook, sniper. |
| Backpack Abilities On/Off | not bound | Turns the abilities of the backpack you wear off and on. |
| Boat Motor On/Off | not bound | Turns the outboard motor of the Motorized Inflatable Boat you ride on and off, and with it the forward push of the boat (in the default toggle mode). The motor sounds while it runs. See [Gear](Gear#motorized-inflatable-boat). |

The defaults come from the config file (see `keys` in [Configuration](Configuration#keys-keys)). They are applied the first time. Once you change a key in the game, Minecraft keeps your choice.

## The abilities key

The Backpack Abilities On/Off key is not bound by default: pick a key for it in Options > Controls. Press it and the abilities of the backpack you wear switch off, press it again and they switch on. A chat message tells you which one happened.

- It is yours alone. Other players keep theirs, and the choice is kept when you log out.
- Turning them off takes away effects the backpack gave you, like night vision or fire resistance. The backpack keeps working as a backpack.
- If abilities are turned off in the server config, the key only tells you so.
- The chat message can be turned off with `abilities.toggleMessage`.

## The toggle key

| You hold or wear | N does | Sneak + N does |
|---|---|---|
| A Backpack Hose in the hand | Switches between the left and the right tank | |
| A Copter Pack | Switches between normal mode and hover | Starts and stops the engine |
| A Steam Jetpack | Nothing | Turns the jetpack on and off |
| A Flamethrower Pack | Nothing | Lights and puts out the pilot |

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
| Boat Motor On/Off key, riding the Motorized Inflatable Boat | Starts the motor, with the pull of a cord, and the boat goes ahead by itself. Press it again to stop it. |
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

- With the mouse wheel: hold the modifier key (Sneak by default) and turn the wheel. One direction brings the tool of the upper slot, the other the one of the lower slot.
- With keys: press Next Backpack Tool or Previous Backpack Tool. They are not bound by default, so bind them in Options > Controls first.

What counts as a tool: pickaxes, axes, shovels, hoes, shears, fishing rods, flint and steel, and tools from other mods like wrenches, hammers, scoops, grafters, crowbars, mattocks and drills. Swords, bows and buckets do not. With the Skeleton backpack, a bow does count.

A modpack or a server can change this with a list: only the items on it, or every tool except the ones on it. The list applies to the tool slots and to the swap. See [Configuration](Configuration#tool-slots-tweakstoolcycle).

With a hose in your hand the same wheel or keys change the hose mode instead: suck, spill, drink. If you hold something else, the wheel scrolls your hotbar as usual.

If the mouse wheel clashes with another mod, change the mode with `keys.toolcycle.mode`: `wheel`, `keys` or `both` (the default). `keys.toolcycle.invertWheel` reverses the direction, and `keys.toolcycle.enabled` turns the whole thing off.

## Conflicts

B is also the default key for the Baubles inventory. If you use Baubles, change one of the two in Options > Controls. See [Compatibility](Compatibility).

## See also

[Backpacks](Backpacks), [Fluids and Hose](Fluids-and-Hose), [Jetpack and Copter Pack](Jetpack-and-Copter-Pack), [Crossbow](Crossbow), [Scuba Gear](Scuba-Gear), [Flamethrower](Flamethrower), [Fuel Station](Fuel-Station), [Gear](Gear)
