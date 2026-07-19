# Crossbow

The Clockwork Crossbow is a repeating crossbow. It holds a magazine of six bolts and fires them one after another while you hold the right mouse button. You load it from Bolt Cartridges, which you craft from arrows.

| | |
|---|---|
| Type | Weapon |
| Stack size | 1 (Bolt Cartridge: 16) |
| Obtained by | Crafting |
| Config section | `crossbow`, `crossbow.elemental`, `enabled.crossbow` |

## Crafting

The crossbow is built around a clock, a bow, strings and iron.

> Screenshot: the crafting table with the Clockwork Crossbow recipe. File: images/crossbow-1.png

A Bolt Cartridge takes six arrows and an iron ingot, plus two more items that decide the type. Any planks work for the crossbow.

| Cartridge | Extra item (two of them) |
|---|---|
| Bolt Cartridge | Stick |
| Blazing Bolt Cartridge | Blaze powder |
| Freezing Bolt Cartridge | Snowball |
| Poison Bolt Cartridge | Spider eye |
| Stunning Bolt Cartridge | Slimeball |

> Screenshot: the crafting table with the Bolt Cartridge recipe, and the four elemental cartridges next to the crossbow. File: images/crossbow-2.png

## Using it

Hold the crossbow and:

1. Right click. If the magazine is empty, the crossbow loads the first Bolt Cartridge it finds in your inventory, hotbar first. Loading takes a second and a half, and it does not fire while it loads.
2. Hold the right click. The crossbow fires a bolt every fourth tick, five a second, until the magazine is empty.
3. When it is empty, the next click loads another cartridge.

A cartridge holds six bolts, the same as a full magazine. The tooltip of the crossbow shows how many bolts are left and what type they are.

> Screenshot: a player firing the crossbow in third person, with bolts in the air. File: images/crossbow-3.png

To change the cartridge before the magazine is empty, sneak and right click. The bolts left in the magazine come back to your inventory as a partly used cartridge, and the crossbow loads a new one. Bolts of different types are never mixed in one magazine.

Without a cartridge you hear a click and get the message "out of bolts" at most once a second. In creative mode you do not need cartridges.

## Bolts

A bolt flies as fast as a fully drawn bow and does 4 damage, two hearts. Every bolt of a burst hits, even against a mob that was just hurt. A bolt stuck in a block cannot be picked up again by default, so a cartridge is spent ammunition.

| Cartridge | Effect on the target |
|---|---|
| Normal | Damage only. |
| Blazing | Sets the target on fire for 5 seconds. |
| Freezing | Slowness III for 3 seconds. |
| Poison | Poison I for 5 seconds. |
| Stunning | Slowness V for 1.5 seconds, which leaves it almost still. Players also get Nausea. |

## Tips

- Keep cartridges in the hotbar. The crossbow looks there first.
- The crossbow does not break by default. A server can set a number of bolts after which it breaks.

## Configuration

The magazine size, the pause between bolts, the reload time, damage, speed, the recoverable bolts, the wear and every elemental effect are in [Configuration](Configuration#clockwork-crossbow-crossbow). With `crossbow.elemental.enabled` off, the elemental cartridges have no recipe and fire plain bolts.

## See also

[Controls](Controls), [Configuration](Configuration)
