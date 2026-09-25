# Compatibility

Adventure Backpack runs alone on Minecraft 1.7.10 with Forge. Every other mod here is optional: the mod finds it when it is loaded and does nothing when it is not.

| Mod | What changes |
|---|---|
| BuildCraft, Forestry, Railcraft, Thermal Foundation, GregTech | Fuels for the Copter Pack, the Flamethrower Pack, the boat motor and the Fuel Station, by fluid name |
| GregTech, Galacticraft, Mekanism | Air and oxygen for the Scuba Gear and the Air Compressor, by fluid name |
| IndustrialCraft 2 | Compressed Air Cell for the Scuba Gear |
| Railcraft | Steam and a better Steam Jetpack recipe |
| Baubles or Baubles Expanded | Wear the backpack in a bauble slot |
| OpenBlocks or GraveStone | The backpack goes into your grave |
| Smart Moving | A setting to hide the worn backpack |
| IndustrialCraft 2, GregTech, Thermal Expansion | Rubber, electric motors and metal plates give more recipes |

## Fuel mods

The Copter Pack, the Flamethrower Pack, the motor of the Motorized Inflatable Boat and the Fuel Station all burn the fluids of one list, `fuels.list`. A fuel is known by the name of its fluid, never by the mod, so none of these mods is needed on its own and any one of them is enough:

| Fluid | Brought by |
|---|---|
| `fuel` | BuildCraft, Railcraft, GregTech (diesel) |
| `oil` | BuildCraft |
| `bioethanol` | Forestry, Railcraft, GregTech |
| `biofuel` | Railcraft |
| `creosote` | Railcraft, GregTech |
| `coal` | Thermal Foundation, Railcraft |
| `biomass` | Forestry |

Without any of them the Copter Pack is registered, so worlds that have one still load, but it has no recipe and does nothing, and the Flamethrower Pack and the Flamethrower have no recipe and are hidden. The burn rate of each fuel is in [Fluids and Hose](Fluids-and-Hose#fuels). To add a fuel from another mod, add its fluid name to `fuels.list`. See [Jetpack and Copter Pack](Jetpack-and-Copter-Pack), [Flamethrower](Flamethrower) and [Fuel Station](Fuel-Station).

The Fuel Station takes fuel from any pipe or machine that pushes fluids through Forge's fluid interface, and from any bucket, can or cell that Forge knows as a fluid container.

BuildCraft fuel and oil also work as drinks in the hose, with bad effects, but only when BuildCraft is loaded. See [Fluids and Hose](Fluids-and-Hose).

## Air and oxygen mods

The [Scuba Gear](Scuba-Gear) and the Air Compressor use fluids called `air` and `oxygen`, the names in `scuba.breathableFluids`.

- **GregTech** registers air. The mod then uses it, and does not add its own. GregTech Air Cells go in the container slots of the gear and the compressor.
- **Galacticraft and Mekanism** register oxygen. A pipe or machine that pushes oxygen into the compressor fills it, and a container of oxygen works in the slots of the gear. Oxygen is stored as air.
- **IndustrialCraft 2** has no air fluid. Its Compressed Air Cell is an item, listed in `scuba.airItems`, and works in the container slots. The empty cell comes back out.

If no loaded mod brings air, Adventure Backpack adds its own. Turn that off with `scuba.ownAirFluid`; the gear and the compressor then have no recipe unless some other mod brings air or oxygen. To add another breathable fluid or another item that holds air, add it to `scuba.breathableFluids` or `scuba.airItems`.

## Railcraft and other steam mods

The Steam Jetpack boils a fluid named steam. If Railcraft, IndustrialCraft 2 or another mod has one, the jetpack uses it. If none does, Adventure Backpack adds its own, so the jetpack works without them. Turn that off with `steamjetpack.ownSteamFluid`; the jetpack then has no recipe and boils nothing when no other mod has steam. The setting needs a restart and must be the same on the server and the clients.

With Railcraft loaded the Steam Jetpack recipe asks for its solid fuel firebox and its low pressure boiler tank. Without Railcraft it asks for a furnace and a cauldron.

Railcraft's creosote is a fuel. See [Fuel mods](#fuel-mods).

## Baubles and Baubles Expanded

The Adventure Backpack can go into a bauble slot and then works as if you wore it the usual way: its ability, the open key, the hose and the model on your back.

- With Baubles Expanded, the mod adds a Back slot. If another mod already added a slot named back, they share it. The slot layout can be changed in the Baubles Expanded config.
- With the original Baubles, which has no back slot, the backpack goes in the belt slot.
- You wear one backpack, copter pack, jetpack, scuba gear or flamethrower pack at a time. If you already wear one, the slot does not take a backpack, and one that ends up there anyway is returned to your inventory.
- The Copter Pack, the Steam Jetpack, the Scuba Gear and the Flamethrower Pack are not baubles.
- When you die with the backpack in a bauble slot, Baubles decides what happens to it, as with its other items.

The default key to open the backpack, B, is also the Baubles inventory key. Change one of them in Options > Controls.

To turn this off set `compat.baubles.enabled` to false. The backpack then does not fit a bauble slot. The Baubles Expanded slot still exists, so that clients and server stay the same.

## Grave mods

When you die, the backpack, copter pack, jetpack, scuba gear or flamethrower pack you wear goes into your grave if one is made, instead of being placed as a block or dropped.

- OpenBlocks: its grave takes the backpack with the rest of your drops. It works when the OpenBlocks grave is on.
- GraveStone: the backpack is moved to your inventory just before the grave is made, so the grave takes it. If your inventory has no free slot, the usual rules apply.
- Corail Tombstone does not exist for 1.7.10.

If no grave is made, the backpack is placed as a block where you died, or dropped, depending on `backpack.placeOnDeath`. A chat message tells you where it went. With the gamerule keepInventory on, you keep it.

Turn this off with `compat.graves.enabled`. See [FAQ](FAQ#what-happens-when-i-die).

## Rubber, electric motors and metal plates

Everything below can be crafted with vanilla items alone. A loaded mod gives extra recipes, and a pack can make them replace the vanilla ones. If a mod does not have one of the items, that recipe does not exist, and nothing else changes.

- **Rubber** makes Inflatable Chambers (the Inflatable Boat), and a second recipe of the Diving Mask and the Pyro Mask with rubber in place of leather. By default the mod looks for rubber named `itemRubber`, `plateRubber` and `ingotRubber`.
- **Electric motors** make the Motorized Inflatable Boat from an Inflatable Boat, Hydro Blades and the motor, instead of a piston and a furnace. By default the mod looks for the IndustrialCraft 2 Electric Motor, the GregTech LV Motor, anything named `craftingElectricMotor` and the Thermal Expansion Redstone Reception Coil.
- **Metal plates** make the Fuel Station, the Flamethrower Pack and the Flamethrower with plates in place of iron ingots. By default the mod looks for plates named `plateSteel` and `plateIron`.

All the lists can be changed. See [Configuration](Configuration#extra-recipes-recipes), [Gear](Gear#inflatable-boat), [Scuba Gear](Scuba-Gear), [Flamethrower](Flamethrower) and [Fuel Station](Fuel-Station).

## Smart Moving and other render mods

The worn backpack is drawn on the player model. Mods that change the player model or its back, like Smart Moving, can draw over each other. If that happens set `render.backpackOnPlayer` to false on the client. Your own backpack still works, only the drawing is off.

Capes, vanilla or from OptiFine, are hidden only while you wear something on your back from this mod: a backpack, the copter pack, the jetpack, the scuba gear or the flamethrower pack.

## Other mods

- Tanks and machines of other mods: the hose sucks from and spills into any block that exposes a fluid tank through Forge's fluid interface.
- Fluid containers from other mods go in the bucket slots of the backpack tanks, if the mod registers them with Forge as fluid containers.
- Tools from other mods fit in the tool slots when their name says what they are: wrench, hammer, axe, shovel, grafter, scoop, crowbar, mattock or drill. A whitelist or a blacklist in the config can add or remove items: see [Configuration](Configuration#tool-slots-tweakstoolcycle).
- If another mod already registers milk, melon juice, mushroom stew or steam, the backpack uses that fluid, so the fluid is the same in both.

## See also

[Configuration](Configuration), [FAQ](FAQ), [Fluids and Hose](Fluids-and-Hose)
