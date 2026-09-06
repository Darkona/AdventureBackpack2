# Crossbow

The Clockwork Crossbow is a repeating crossbow with three modes. In repetition mode it fires a magazine of six bolts one after another while you hold the right mouse button. In grappling hook mode it shoots a hook that pulls you to a block. In sniper mode, with a bipod down, it fires one strong, straight bolt. You load it from Bolt Cartridges, which you craft from arrows, and you change the mode with a key.

| | |
|---|---|
| Type | Weapon |
| Stack size | 1 (Bolt Cartridge: 16) |
| Obtained by | Crafting |
| Key | Crossbow Mode (V) |
| Config section | `crossbow`, `crossbow.grapple`, `crossbow.sniper`, `enabled.crossbow` |

## Crafting

The crossbow is built around a clock, a bow, strings and iron. Any planks work.

> Screenshot: the crafting table with the Clockwork Crossbow recipe. File: images/crossbow-1.png

A Bolt Cartridge takes six arrows, an iron ingot and two sticks. There is only one kind of cartridge.

> Screenshot: the crafting table with the Bolt Cartridge recipe. File: images/crossbow-2.png

## Loading

Hold the crossbow and right click. If the magazine is empty, the crossbow loads the first Bolt Cartridge it finds in your inventory, hotbar first. Loading takes a second and a half, and it does not fire while it loads.

A cartridge holds six bolts, the same as a full magazine. The tooltip of the crossbow shows how many bolts are left and the current mode.

To change the cartridge before the magazine is empty, sneak and right click. The bolts left in the magazine come back to your inventory as a partly used cartridge, and the crossbow loads a new one.

Without a cartridge you hear a click and get the message "out of bolts" at most once a second. In creative mode you do not need cartridges.

## Changing the mode

Press the Crossbow Mode key (V by default) with the crossbow in your hand. The modes go in a circle: repetition, grappling hook, sniper, and back to repetition. The mode is kept in the crossbow, shows in its name in the hotbar, and the tooltip tells you which key changes it.

A mode that a server has turned off is skipped. A crossbow left in a mode that is off fires in repetition mode.

> Screenshot: the three tooltips of the crossbow, one for each mode. File: images/crossbow-3.png

## Repetition mode

This is the default mode. Hold the right click and the crossbow fires a bolt every fourth tick, five a second, until the magazine is empty. When it is empty, the next click loads another cartridge.

A bolt flies as fast as a fully drawn bow and does 4 damage, two hearts. Every bolt of a burst hits, even against a mob that was just hurt. A bolt stuck in a block cannot be picked up again by default, so a cartridge is spent ammunition.

> Screenshot: a player firing the crossbow in third person, with bolts in the air. File: images/crossbow-4.png

## Grappling hook mode

Right click and the crossbow shoots a hook with a rope. The hook flies straight, without falling, and catches the first solid block it meets. Then it pulls you towards that spot, faster than you can sprint, until you let go or arrive.

- The hook reaches 32 blocks. If it gets that far without catching anything, it comes back.
- The hook lets go when you get close to it, when you sneak, when you jump, after three seconds of pulling, when the block it holds is gone, and when you change the mode or hold something else.
- While the hook is out, clicking does nothing. Let go first.
- Each shot costs one bolt from the magazine, and after it you wait a second before the crossbow can fire or reload again.
- You take no fall damage from the pull, and a fall that starts when the hook lets go counts from there.

> Screenshot: a player in the air, pulled by the hook towards the side of a cliff, with the rope drawn. File: images/crossbow-5.png

## Sniper mode

Sneak and stand still on the ground to put the bipod down. The view zooms in about three times. While the bipod is down, right click fires one bolt that is stronger and faster than a normal one and flies straight, without spread: 12 damage, six hearts, and about a second and a half until the next shot.

The bipod folds when you move, jump, stop sneaking or ride something. Walking while sneaking is too fast for it. With the bipod folded, the sniper mode fires normal bolts, like repetition mode. With the bipod down, sneak + right click fires and does not swap the cartridge.

> Screenshot: a player lying behind the crossbow with the bipod down, zoomed in on a distant mob. File: images/crossbow-6.png

## Tips

- Keep cartridges in the hotbar. The crossbow looks there first.
- The crossbow does not break by default. A server can set a number of shots after which it breaks.
- Crossbows and cartridges from 1.0 still work. The elemental cartridges that 1.0 had load as normal ones.

## Configuration

The magazine size, the pause between bolts, the reload time, damage, speed, the recoverable bolts and the wear are in [Configuration](Configuration#clockwork-crossbow-crossbow). The hook has its own numbers (range, speed, cooldown, cost) in [Grappling hook](Configuration#grappling-hook-crossbowgrapple), and the sniper mode in [Sniper mode](Configuration#sniper-mode-crossbowsniper). Each mode has a switch.

## See also

[Controls](Controls), [Configuration](Configuration)
