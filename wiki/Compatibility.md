# Compatibility

Adventure Backpack runs alone on Minecraft 1.7.10 with Forge. Every other mod here is optional: the mod finds it when it is loaded and does nothing when it is not.

| Mod | What changes |
|---|---|
| BuildCraft | The Copter Pack works |
| Railcraft | Steam and a better Steam Jetpack recipe |
| Baubles or Baubles Expanded | Wear the backpack in a bauble slot |
| OpenBlocks or GraveStone | The backpack goes into your grave |
| Smart Moving | A setting to hide the worn backpack |

## BuildCraft

The Copter Pack needs BuildCraft. Without it the pack is registered, so worlds that have one still load, but it has no recipe, is not in the creative tab and does nothing. BuildCraft also gives the copter its fuel (fuel and oil). The fuels and how fast each burns are in `copterpack.fuels`. See [Jetpack and Copter Pack](Jetpack-and-Copter-Pack).

BuildCraft fuel and oil also work as drinks in the hose, with bad effects. See [Fluids and Hose](Fluids-and-Hose).

## Railcraft and other steam mods

The Steam Jetpack boils a fluid named steam. If Railcraft, IndustrialCraft 2 or another mod has one, the jetpack uses it. If none does, Adventure Backpack adds its own, so the jetpack works without them. Turn that off with `steamjetpack.ownSteamFluid`; the jetpack then has no recipe and boils nothing when no other mod has steam. The setting needs a restart and must be the same on the server and the clients.

With Railcraft loaded the Steam Jetpack recipe asks for its solid fuel firebox and its low pressure boiler tank. Without Railcraft it asks for a furnace and a cauldron.

Railcraft's creosote is a copter fuel.

## Baubles and Baubles Expanded

The Adventure Backpack can go into a bauble slot and then works as if you wore it the usual way: its ability, the open key, the hose and the model on your back.

- With Baubles Expanded, the mod adds a Back slot. If another mod already added a slot named back, they share it. The slot layout can be changed in the Baubles Expanded config.
- With the original Baubles, which has no back slot, the backpack goes in the belt slot.
- You wear one backpack, copter pack or jetpack at a time. If you already wear one, the slot does not take a backpack, and one that ends up there anyway is returned to your inventory.
- The Copter Pack and the Steam Jetpack are not baubles.
- When you die with the backpack in a bauble slot, Baubles decides what happens to it, as with its other items.

The default key to open the backpack, B, is also the Baubles inventory key. Change one of them in Options > Controls.

To turn this off set `compat.baubles.enabled` to false. The backpack then does not fit a bauble slot. The Baubles Expanded slot still exists, so that clients and server stay the same.

## Grave mods

When you die, the backpack, copter pack or jetpack you wear goes into your grave if one is made, instead of being placed as a block or dropped.

- OpenBlocks: its grave takes the backpack with the rest of your drops. It works when the OpenBlocks grave is on.
- GraveStone: the backpack is moved to your inventory just before the grave is made, so the grave takes it. If your inventory has no free slot, the usual rules apply.
- Corail Tombstone does not exist for 1.7.10.

If no grave is made, the backpack is placed as a block where you died, or dropped, depending on `backpack.placeOnDeath`. A chat message tells you where it went. With the gamerule keepInventory on, you keep it.

Turn this off with `compat.graves.enabled`. See [FAQ](FAQ#what-happens-when-i-die).

## Smart Moving and other render mods

The worn backpack is drawn on the player model. Mods that change the player model or its back, like Smart Moving, can draw over each other. If that happens set `render.backpackOnPlayer` to false on the client. Your own backpack still works, only the drawing is off.

Capes, vanilla or from OptiFine, are hidden only while you wear a backpack, copter pack or jetpack.

## Other mods

- Tanks and machines of other mods: the hose sucks from and spills into any block that exposes a fluid tank through Forge's fluid interface.
- Fluid containers from other mods go in the bucket slots of the backpack tanks, if the mod registers them with Forge as fluid containers.
- Tools from other mods fit in the tool slots when their name says what they are: wrench, hammer, axe, shovel, grafter, scoop, crowbar, mattock or drill.
- If another mod already registers milk, melon juice, mushroom stew or steam, the backpack uses that fluid, so the fluid is the same in both.

## See also

[Configuration](Configuration), [FAQ](FAQ)
