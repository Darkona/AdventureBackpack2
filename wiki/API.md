# API for addon authors

Adventure Backpack has a small public API in the Java package `adventurebackpack.api`, for mods that add a fluid effect, a wearable for the back, a backpack ability, or that need to read what the player wears.

The API is a separate jar, `adventurebackpack-api-1.7.10-<version>.jar`, with sources and javadoc next to it. It holds only the package `adventurebackpack.api`: no mod, no `mcmod.info`, nothing of the mod itself. Use it only to compile your addon (`compileOnly`) and never put it in `mods/`: the same classes are already inside the Adventure Backpack jar, which works alone. In a RetroFuturaGradle project, put the jar in `libs/` and add `compileOnly rfg.deobf(files('libs/adventurebackpack-api-1.7.10-1.3.0.jar'))`; the `-dev` jar is the same one with the development names.

## Declaring the dependency

Declare the dependency in your `@Mod` so Forge loads your mod after Adventure Backpack and the mod has installed its API before your preInit runs. For an addon that needs it, use `dependencies = "required-after:adventurebackpack"`; for one that works without it, use `"after:adventurebackpack"` and keep every call to the API in a class that loads only when `Loader.isModLoaded("adventurebackpack")` says yes. To also require a version of the API, Forge lists it as the mod `AdventureBackpackAPI`: `"required-after:AdventureBackpackAPI@[1.0,2.0)"`.

## Versions

The API follows semantic versioning and has its own number, `api_version`, apart from the version of the mod: a new method or class raises the minor number, and removing or changing one raises the major number. Your addon compiled against 1.2 runs on any 1.x from 1.2 on. Ask `AdventureBackpackAPI.getVersion()` for the running version: it is a method, not a constant, because the compiler copies a constant into your addon and it would then report the version you compiled with.

## The entry point

`AdventureBackpackAPI` has static methods; call them from preInit or later.

- `getWornWearable(player)` returns a copy of the stack the player wears on the back (a backpack, a jetpack, a copter pack, scuba gear...), or `null`.
- `getWornBackpack(player)` returns a read-only snapshot, `IBackpackView`, of the backpack the player wears, or `null`: its colour name, its two tanks (fluid and capacity) and its slots. It is a copy taken at the call; it reads NBT, so ask on a player action and not every tick.
- `registerFluidEffect(effect)` and `registerAbility(colour, ability)` are explained below.
- `registerFuel(fluidName, multiplier)` and `registerAirFluid(fluidName)` add a fuel and a breathable fluid; see [Fuels and air from code](#fuels-and-air-from-code).
- `enableAbilityEvents()` turns on `BackpackAbilityEvent`; see [Events](#events).

## A fluid effect

Extend `FluidEffect` to say what happens when a player drinks a fluid from a tank with the hose. Creating the effect already registers it, as it always did, so addons written for the old API keep working; `registerFluidEffect` does nothing for an effect that is already registered. An effect made before Adventure Backpack's preInit (an addon that loads first) is kept and registered when the mod starts. `affectDrinker` runs on the server only: potion effects, fire and food reach the client the usual way, so do not draw or play sounds from it.

```java
public class HoneyEffect extends FluidEffect {
    public HoneyEffect(Fluid honey) {
        super(honey, 10); // the effect lasts 10 seconds: timeInSeconds and timeInTicks
    }

    @Override
    public void affectDrinker(World world, Entity entity) {
        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).getFoodStats().addStats(4, 0.3F);
        }
    }
}
// in your preInit, after your fluid exists: new HoneyEffect(honeyFluid);
```

## A wearable for the back

An item that implements `IBackWearableItem` can be worn in the back slot, like the jetpack. The hooks run when it is equipped, unequipped, every tick while worn and when the player dies; the two client methods give the model and the texture. A wearable with a liquid fuel tank can also implement `IFuelPack`, so the Fuel Station fills it. Forge fires `WearableEvent.EquipWearableEvent` and `WearableEvent.UnequipWearableEvent` on the Forge event bus whenever a player puts one on or takes it off.

```java
@SubscribeEvent
public void onEquip(WearableEvent.EquipWearableEvent event) {
    if (event.wearable.getItem() instanceof MyPack) {
        // event.entityPlayer is the player, event.wearable the stack
    }
}
```

The contracts for inventories with tanks, `IInventoryTanks`, `IAsynchronousInventory` and `IWearableContainer`, are in the same package for addons that build their own containers.

## A backpack ability

`IBackpackAbility` is the ability of one backpack colour. It has three tick hooks, all optional (a fourth, `getShape`, is described below): `onWornTick` every tick while the backpack is worn, `onPlacedTick` every tick while it stands in the world as a block, and `onRemoval` when it is taken off. Implement only the ones you need, and keep the work cheap: the tick hooks run every tick. An ability is shared by every backpack of its colour, so it keeps no state of its own; use the NBT of the stack.

```java
// "MyColour" is the colour name of a backpack: a colour without an ability, or one your addon makes; the 17 colours that already have one (Bat, Cow...) cannot get a second
AdventureBackpackAPI.registerAbility("MyColour", new IBackpackAbility() {
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack) {
        if (world.getTotalWorldTime() % 100 == 0) {
            // your effect, once every five seconds
        }
    }
});
```

Register in preInit or init: after postInit the registry is frozen and the call throws `IllegalStateException`, as it does when the colour already has an ability.

Since API 1.2.0 an ability can also give its colour a bigger window with a fourth optional hook, `getShape`. It receives the shape the config gives the colour (`backpacks.<colour>.rows` and `columns`, 6 by 8 by default) and returns the one to use. The storage has rows x columns - 9 slots, because the crafting grid takes the bottom right corner; the mod places everything else and draws the background, so nothing else is needed. The mod keeps the shape inside its limits (4 to 10 rows, 8 to 12 columns) and asks once per colour, so the answer must depend only on the colour. A backpack never loses slots: one that holds more than the new shape keeps its size.

```java
AdventureBackpackAPI.registerAbility("MyColour", new IBackpackAbility() {
    @Override
    public BackpackShape getShape(BackpackShape configured) {
        return configured.withRows(configured.getRows() + 1).withColumns(9); // 7 by 9: 54 slots
    }
});
```

Since API 1.3.0 a shape can also carry a quiver: `configured.withQuiverSlots(4)` gives the colour one tank and a quiver of 4 slots in the place of the right tank, like the Skeleton Backpack has by config. The slots take the items of `backpacks.skeleton.quiver.items`; 0 quiver slots keeps the two tanks.

In the backpack's slots (`IBackpackView.getSlot`), 0 to 38 are storage, 39 and 40 the tool slots, 41 to 44 the bucket slots, and from 45 on the extra storage of a bigger backpack. The quiver slots come last, after the storage.

## Fuels and air from code

`AdventureBackpackAPI.registerFuel(fluidName, multiplier)` adds a liquid fuel to the one list that the Copter Pack, the Flamethrower Pack, the boat motor and the Fuel Station read. The name is the registered name of the fluid (`"oil"`, `"bioethanol"`), and the multiplier is how fast it burns: every user multiplies its own cost by it, so a higher number burns faster. `AdventureBackpackAPI.registerAirFluid(fluidName)` adds a fluid that the Scuba Gear tanks and the Air Compressor take as air, for example `"oxygen"`.

What you register is added to the config, never subtracted from it. The config has the last word: when `fuels.list` already names your fuel, its multiplier stays, and a fluid that a modpack took out of `fuels.list` or `scuba.breathableFluids` cannot be told from one that was never there, so your addon puts it back. Two addons that register the same fuel: the first multiplier stays. Names are matched in lower case. The fluid does not have to exist: a fuel of a mod that is not loaded is simply never used. Items that hold air without being fluid containers still go in the config key `scuba.airItems`.

```java
// in your preInit or init
AdventureBackpackAPI.registerFuel("rocketfuel", 2.0);
AdventureBackpackAPI.registerAirFluid("breathablegas");
```

Register in preInit or init. The tables freeze when the postInit of Adventure Backpack starts, which is before the postInit of your addon: a later call throws `IllegalStateException`. A name that is empty, or a multiplier that is not a positive number, throws `IllegalArgumentException`. No new config key is needed.

## Events

The API has three events, all cancelable, all posted on `MinecraftForge.EVENT_BUS` by the server. They are posted only when something real happens, never once per tick for no reason; the exception is the ability event, which has a switch (below). Register your listener with `MinecraftForge.EVENT_BUS.register(...)` and annotate the method with `@SubscribeEvent`. A canceled event stops the action it announces. Each event extends Forge's `PlayerEvent`, so `event.entityPlayer` is the player.

- `FluidDrinkEvent` is posted when a player drinks a fluid from a backpack tank with the hose, before any `FluidEffect` runs. `fluid` is the fluid and `amount` the millibuckets the tank loses. Cancel it and nothing is applied and nothing is taken from the tank. It is posted only for a fluid that has at least one effect.
- `BackpackAbilityEvent` is posted before the worn hook of a backpack ability, with the `colorName` and the `backpack` stack. Cancel it and the ability does not run that tick. It is posted only for a colour that has a worn ability and only while the player's abilities are on. The hook runs every tick for every player who wears such a backpack, so the event is not posted at all until an addon calls `AdventureBackpackAPI.enableAbilityEvents()` once, in preInit or init; it cannot be turned off again. Keep that listener cheap: compare and return, never search or allocate. It affects the server side only: what an ability does on the client is not canceled, such as the Bat's night vision, the Rainbow's run and the Slime's slide, which the client keeps showing.
- `BackpackOpenEvent` is posted when a player opens the inventory screen of a backpack: worn (the key), held in the hand, or placed as a block. `source` says which (`WEARING`, `HOLDING`, `PLACED`), `backpack` is the stack (`null` for a placed one) and `x`, `y`, `z` are the position of the placed block. Cancel it and the screen does not open; tell the player why. The screens of the copter pack, the jetpack, the scuba gear, the flamethrower pack and the fuel station do not post it.

```java
@SubscribeEvent
public void onDrink(FluidDrinkEvent event) {
    if (event.fluid == MyMod.poison && event.entityPlayer.capabilities.isCreativeMode) {
        event.setCanceled(true);
    }
}
```
