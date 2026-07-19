# Fluids and Hose

Every backpack has two tanks. You fill and empty them with buckets and bottles in the backpack window, or with the Backpack Hose, which also lets you drink what is in them.

| | |
|---|---|
| Type | Item (Backpack Hose) |
| Stack size | 1 |
| Obtained by | Crafting |
| Config section | `hose`, `enabled.hose`, `backpack.tankCapacity` |

## The tanks

Each tank holds 4000 mB by default, which is four buckets (1000 mB is one bucket). The value is in [Configuration](Configuration#backpack-backpack). Each tank holds one fluid at a time.

Every tank has two container slots in the backpack window:

- Put a full bucket, bottle or other container in the input slot and the tank takes the fluid. The empty container appears in the output slot.
- Put an empty container in the input slot and the tank fills it, if it has fluid. The full container appears in the output slot.

It works with any container that Forge knows as a fluid container, including the ones from other mods.

> Screenshot: the backpack window with a water bucket in a tank's input slot and the tank filling. File: images/fluids-1.png

Four backpacks make their own fluid: the Cactus makes water, the Melon makes melon juice, the Cow makes milk and the Mooshroom makes mushroom stew. See [Backpacks](Backpacks#with-an-ability).

The mod adds three fluids of its own, Milk, Melon Juice and Mushroom Stew, plus Steam for the jetpack. If another mod already has a fluid with the same name, that one is used. The Melon Juice Bottle is the container for melon juice: craft it from a melon slice and a water bottle. A bowl of mushroom stew and a bucket of milk work as containers too. Each holds 1000 mB.

## The Backpack Hose

> Screenshot: the crafting table with the Hose Nozzle recipe, then the Backpack Hose recipe. File: images/fluids-2.png

The hose works on the tanks of the backpack you wear. Without a worn backpack it is called "Hose: Useless / No Backpack" and does nothing. It has two settings: which tank it uses (left or right), and which mode.

> Screenshot: the hose in the hotbar with its tooltip showing the tank, the mode and the fluid. File: images/fluids-3.png

| Mode | Right click on... | What happens |
|---|---|---|
| Suck | A source block of water, lava or another fluid | Takes the whole bucket into the tank. It is skipped if the bucket does not fit, and flowing blocks never count. |
| Suck | A tank or machine of another mod | Pulls up to 1000 mB per click into the backpack tank. |
| Spill | A block | Puts the fluid down as a block. It needs a full bucket in the tank. Water spilled in the Nether evaporates. |
| Spill | A tank or machine of another mod | Pushes up to 1000 mB per click into it. |
| Any | A cow or a mooshroom | Adds 1000 mB of milk or mushroom stew to the tank per click. |
| Drink | Anywhere | Hold the right click. Each sip takes 1.2 seconds and 1000 mB, and gives the fluid's effect. |

Drinking works only with fluids that have an effect. Water only affects you in some places. If the tank is empty, or has less than a sip, nothing happens.

The hose follows the same permission checks as a bucket: spawn protection and areas where you cannot build apply to it.

### What each fluid does when you drink it

| Fluid | Effect |
|---|---|
| Water | In a hot or dry biome such as a desert, savanna or mesa, in the Nether and in wastelands: puts you out if you are burning, or gives Regeneration I for 7 seconds. Elsewhere, nothing. |
| Milk | Clears all your potion effects. |
| Melon Juice | Haste I for 30 seconds, plus the water effect. |
| Mushroom Stew | Restores 3 drumsticks of hunger. |
| Lava | Sets you on fire for 15 seconds, and gives Speed III, Jump Boost I and Mining Fatigue IV for 90 seconds. |
| BuildCraft fuel | Nausea III and Slowness II for 20 seconds, and Poison I for 8 seconds. |
| BuildCraft oil | Nausea III for 20 seconds, and Poison II for 10 seconds. |

The BuildCraft fluids only have an effect when BuildCraft is loaded.

The sips, the transfer amounts and the drink time are in [Configuration](Configuration#hose-hose).

## Controls

| Action | Control |
|---|---|
| Change the tank | Press N with the hose in your hand |
| Change the mode | Turn the mouse wheel with the tool modifier held (Left Shift by default), or use the next and previous tool keys |

See [Controls](Controls) for the details and how to change the keys.

## Tips

- The window shows what each tank holds. With `render.tankHoverText` on, it shows as hovering text instead of under the tank.
- The hose does not fit in the tool slots of the backpack. Keep it in your hotbar.

## See also

[Backpacks](Backpacks), [Controls](Controls), [Configuration](Configuration)
