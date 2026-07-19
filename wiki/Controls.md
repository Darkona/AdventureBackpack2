# Controls

Every key, button and mouse action of the mod. The keys are listed in Options > Controls under the category Adventure Backpack, and you can change all of them there.

## Keys

| Key (as listed in Controls) | Default | Does |
|---|---|---|
| Open Inventory Key | B | Opens the window of the backpack, copter pack or jetpack you wear. Pressing it again closes the window. |
| Toggle Action Key | N | Depends on what you hold and wear. See the table below. |
| Tool Cycle Modifier (none = sneak) | Sneak (Left Shift) | Hold it while turning the mouse wheel to swap tools. |
| Next Backpack Tool | not bound | Swaps to the next tool. |
| Previous Backpack Tool | not bound | Swaps to the previous tool. |

The defaults come from the config file (see `keys` in [Configuration](Configuration#keys-keys)). They are applied the first time. Once you change a key in the game, Minecraft keeps your choice.

## The toggle key

| You hold or wear | N does | Sneak + N does |
|---|---|---|
| A Backpack Hose in the hand | Switches between the left and the right tank | |
| A Copter Pack | Switches between normal mode and hover | Starts and stops the engine |
| A Steam Jetpack | Nothing | Turns the jetpack on and off |

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
| Jump, riding a spider from the Spider backpack | The spider jumps |
| Right click, crossbow in hand | Fires, or loads a cartridge if the magazine is empty. Hold it to fire the whole magazine. |
| Sneak and right click, crossbow in hand | Swaps the cartridge |
| Right click, hose in hand | Does what its mode says: suck, spill or drink |

## Swapping tools

With a backpack on, the tool in your hand can trade places with the two tool slots of the backpack. Each step rotates the three: the tool in your hand, the one in the upper slot and the one in the lower slot. The tool you hold goes into the backpack and one comes out.

- With the mouse wheel: hold the modifier key (Sneak by default) and turn the wheel. One direction brings the tool of the upper slot, the other the one of the lower slot.
- With keys: press Next Backpack Tool or Previous Backpack Tool. They are not bound by default, so bind them in Options > Controls first.

What counts as a tool: pickaxes, axes, shovels, hoes, shears, fishing rods, flint and steel, and tools from other mods like wrenches, hammers, scoops, grafters, crowbars, mattocks and drills. Swords, bows and buckets do not. With the Skeleton backpack, a bow does count.

With a hose in your hand the same wheel or keys change the hose mode instead: suck, spill, drink. If you hold something else, the wheel scrolls your hotbar as usual.

If the mouse wheel clashes with another mod, change the mode with `keys.toolcycle.mode`: `wheel`, `keys` or `both` (the default). `keys.toolcycle.invertWheel` reverses the direction, and `keys.toolcycle.enabled` turns the whole thing off.

## Conflicts

B is also the default key for the Baubles inventory. If you use Baubles, change one of the two in Options > Controls. See [Compatibility](Compatibility).

## See also

[Backpacks](Backpacks), [Fluids and Hose](Fluids-and-Hose), [Jetpack and Copter Pack](Jetpack-and-Copter-Pack), [Crossbow](Crossbow)
