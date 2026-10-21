package com.darkona.adventurebackpack.contract;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The performance contract (notes/PERFORMANCE_CONTRACT.md), enforced on the sources. Every method is put in a tier
 * by its name, by the event it takes, or by the explicit list of methods reached from a tick ({@link #REACHED_FROM_TICK});
 * each tier forbids a set of calls ({@link Rule}). A hit fails the build unless {@link Allowed} lists it by
 * {@code file#method RULE} with its reason: {@code OPEN Pn} for an audit finding still to fix
 * (notes/audit-performance-2026-10-16.md), any other text for a justified exception. An entry that no longer matches
 * fails too, so the list only shrinks. Two rules apply to every method: no boxed collection type
 * ({@code Map<Integer, ...>}: Trove 3 is on the 1.7.10 classpath) and balanced GL pushes and pops.
 *
 * <p>The full list of hits, allowed or not, is written to build/reports/performance-contract.txt for audits.
 */
class PerformanceContractTest
{
    private static final Path ROOT = Paths.get("src/main/java/com/darkona/adventurebackpack");
    private static final Path REPORT = Paths.get("build/reports/performance-contract.txt");

    enum Tier { FRAME, TICK, ENTITY_EVENT, ANY }

    enum Rule
    {
        /** Deep copy of a stack or tag, or a whole inventory deserialised from a stack (built, or reloaded by openInventory). */
        COPY("new\\s+Inventory\\w+\\s*\\(|\\.copy\\s*\\(\\s*\\)|copyItemStack\\s*\\(|\\.openInventory\\s*\\(\\s*\\)", Tier.FRAME, Tier.TICK, Tier.ENTITY_EVENT),
        /** NBT serialised, parsed or compared deeply. */
        NBT("\\.writeToNBT\\s*\\(|\\.readFromNBT\\s*\\(|new\\s+NBTTag(?:Compound|List)\\s*\\(|loadItemStackFromNBT|loadFluidStackFromNBT"
                + "|\\bsaveToNBT\\s*\\(|\\bloadFromNBT\\s*\\(|CompressedStreamTools|areItemStacksEqual|areItemStackTagsEqual", Tier.FRAME, Tier.TICK, Tier.ENTITY_EVENT),
        /** A registry or dictionary searched by name. */
        LOOKUP("FluidRegistry\\.getFluid(?:Stack|ID|Name)?\\s*\\(|GameRegistry\\.find|itemRegistry\\.getObject|blockRegistry\\.getObject"
                + "|getBlockFromName|OreDictionary\\.\\w+\\s*\\(", Tier.FRAME, Tier.TICK, Tier.ENTITY_EVENT),
        /** Reflection or mod-presence checks. */
        REFLECT("Class\\.forName|getDeclared(?:Field|Method)|\\.getField\\s*\\(|\\.getMethod\\s*\\(|\\.invoke\\s*\\(|ReflectionHelper|Loader\\.isModLoaded",
                Tier.FRAME, Tier.TICK, Tier.ENTITY_EVENT),
        /** Entity searches, ray traces, walks over the world's lists, or a search of every crafting recipe. */
        SCAN("findMatchingRecipe|getRecipeList|getEntitiesWithinAABB\\w*|selectEntitiesWithinAABB|func_72872_a|rayTraceBlocks|func_147447_a|getClosest\\w*Player"
                + "|getPlayerEntityByName|getCollidingBoundingBoxes|\\.playerEntities|loadedEntityList|loadedTileEntityList|SpiralSearch\\.",
                Tier.FRAME, Tier.TICK, Tier.ENTITY_EVENT),
        /** A packet sent (or a block update that sends one). In a tick it must be guarded by a real change. */
        PACKET("\\.sendTo\\s*\\(|\\.sendToAll\\w*\\s*\\(|\\.sendToServer\\s*\\(|\\.sendToDimension\\s*\\(|func_151247_a|func_151248_b"
                + "|markBlockForUpdate\\s*\\(|sendPacketToAllPlayers|playerNetServerHandler\\.sendPacket|sendContainerAndContentsToPlayer",
                Tier.FRAME, Tier.TICK),
        /** Logging, console output or file I/O outside an error branch. */
        IO("LogHelper\\.\\w+\\s*\\(|System\\.(?:out|err)|printStackTrace|new\\s+File\\s*\\(|\\bFiles\\.|File(?:In|Out)putStream",
                Tier.FRAME, Tier.TICK, Tier.ENTITY_EVENT),
        /** A string built: format or concatenation with a literal. */
        STRING("String\\.format\\s*\\(|translateToLocalFormatted|I18n\\.format\\s*\\(|\"\"\\s*\\+|\\+\\s*\"\"", Tier.FRAME, Tier.TICK),
        /** A collection or stream built. */
        COLLECTION("new\\s+(?:ArrayList|LinkedList|HashMap|HashSet|TreeMap|TreeSet|LinkedHashMap|LinkedHashSet|EnumMap|ArrayDeque)\\b"
                + "|\\b(?:Lists|Maps|Sets)\\.new|Arrays\\.asList|ImmutableList\\.of|\\.stream\\s*\\(|Stream\\.of", Tier.FRAME, Tier.TICK),
        /** Per-frame allocation of game objects, resource names, vectors and arrays. */
        FRAME_NEW("new\\s+(?:ResourceLocation|ItemStack|FluidStack|FluidTank|AxisAlignedBB|Vec3|ChunkCoordinates|Vector3|Cuboid6)\\s*\\("
                + "|Vec3\\.createVectorHelper|AxisAlignedBB\\.getBoundingBox|new\\s+\\w+\\s*\\[", Tier.FRAME),
        /** Per-tick allocation of resource names and tanks (cheap objects such as a FluidStack are tolerated). */
        TICK_NEW("new\\s+(?:ResourceLocation|FluidTank)\\s*\\(", Tier.TICK);

        final Pattern pattern;
        final Set<Tier> tiers;

        Rule(String regex, Tier first, Tier... more)
        {
            this.pattern = Pattern.compile(regex);
            this.tiers = EnumSet.of(first, more);
        }
    }

    /** Runs on every client frame (render, model, GUI draw, HUD, tooltip, icons). */
    private static final Pattern FRAME_NAME = Pattern.compile(
            "render\\w*|doRender\\w*|draw\\w*|setRotationAngles|getIcon\\w*|getArmorModel|getArmorTexture|getWearableModel"
                    + "|getWearableTexture|addInformation|getColorFromItemStack|getRenderColor|colorMultiplier");
    private static final Pattern FRAME_EVENT = Pattern.compile(
            "Render\\w*Event|RenderTickEvent|DrawScreenEvent|ItemTooltipEvent|FOVUpdateEvent|DrawBlockHighlightEvent");

    /** Runs on every tick of a player, item, entity, tile, AI task, open container or sound. */
    private static final Pattern TICK_NAME = Pattern.compile(
            "onUpdate|onArmorTick|onEquippedUpdate|onWornTick|onPlacedTick|updateEntity|onLivingUpdate|onEntityUpdate|updateTask"
                    + "|shouldExecute|continueExecuting|detectAndSendChanges|onUsingTick|updateScreen|update|updateTick|randomDisplayTick"
                    + "|tick\\w*|onTick\\w*|on\\w*Tick|syncToNearIfChanged|executeWearableUpdateProtocol|executeAbility");
    private static final Pattern TICK_EVENT = Pattern.compile("TickEvent|LivingUpdateEvent");

    /** What pipes and hoppers of other mods call on a tile, on their own tick (IFluidHandler, ISidedInventory). */
    private static final Pattern PIPE_NAME = Pattern.compile(
            "fill|drain|canFill|canDrain|getTankInfo|canInsertItem|canExtractItem|getAccessibleSlotsFromSide|isItemValidForSlot");

    /** Fires for every living entity (or every entity) in the world, not only for players with gear. */
    private static final Pattern ENTITY_EVENT = Pattern.compile(
            "LivingHurtEvent|LivingAttackEvent|LivingFallEvent|LivingJumpEvent|EntityJoinWorldEvent|EntityConstructing"
                    + "|LivingDeathEvent|EntityStruckByLightningEvent|StartTracking|LivingSetAttackTargetEvent|LivingDropsEvent");

    /** Methods with another name that a tick calls every time (file#method). */
    static final Set<String> REACHED_FROM_FRAME = new TreeSet<String>(Arrays.asList(
            "client/gui/GuiTank.java#getTankTooltip"));

    static final Set<String> REACHED_FROM_TICK = new TreeSet<String>(Arrays.asList(
            "inventory/widget/classic/StorageWidget.java#accepts",
            "inventory/widget/classic/ToolsWidget.java#accepts",
            "inventory/widget/classic/TankWidget.java#accepts",
            "inventory/widget/classic/TankWidget.java#acceptsAt",
            "inventory/widget/classic/QuiverWidget.java#accepts",
            "inventory/widget/classic/RopeWidget.java#accepts",
            "playerProperties/BackpackProperty.java#sameAsSent",
            "playerProperties/BackpackProperty.java#send",
            "playerProperties/BackpackProperty.java#getSyncData",
            "playerProperties/ObserverView.java#properties",
            "playerProperties/ObserverView.java#wearable",
            "playerProperties/ObserverView.java#copyExcept",
            "playerProperties/ObserverView.java#toolsOnly",
            "common/SleepingBagSpawn.java#restoreWhenAwake",
            "item/ItemSteamJetpack.java#liveInventory",
            "item/ItemSteamJetpack.java#runFirebox",
            "item/ItemSteamJetpack.java#runHeater",
            "item/ItemSteamJetpack.java#runBoiler",
            "item/ItemSteamJetpack.java#clientUpdate",
            "item/ItemCopterPack.java#liveInventory",
            "inventory/InventorySteamJetpack.java#isCurrentFor",
            "inventory/InventoryBackpack.java#reloadIfChanged",
            "inventory/InventorySteamJetpack.java#flushChanges",
            "util/Wearing.java#isWearingWearable",
            "util/Wearing.java#getWearingWearable",
            "entity/ai/SpiderNavigate.java#onUpdateNavigation",
            "common/RenderInfo.java#matches"));

    private static final Pattern BOXED = Pattern.compile(
            "\\b(?:Map|HashMap|List|ArrayList|Set|HashSet|Collection|Queue|Deque|TreeMap|TreeSet|LinkedHashMap)\\s*<\\s*(?:Integer|Long|Short|Byte|Character|Double|Float)\\b"
                    + "|\\b(?:Map|HashMap|TreeMap|LinkedHashMap)\\s*<[^<>]*,\\s*(?:Integer|Long|Short|Byte|Double|Float)\\s*>");
    private static final Pattern GL_PUSH_MATRIX = Pattern.compile("glPushMatrix\\s*\\(");
    private static final Pattern GL_POP_MATRIX = Pattern.compile("glPopMatrix\\s*\\(");
    private static final Pattern GL_PUSH_ATTRIB = Pattern.compile("glPushAttrib\\s*\\(");
    private static final Pattern GL_POP_ATTRIB = Pattern.compile("glPopAttrib\\s*\\(");

    @Test
    void sourcesKeepThePerformanceContract() throws IOException
    {
        List<SourceScan.SourceFile> files = SourceScan.read(ROOT);
        Map<String, String> via = new TreeMap<String, String>();
        Map<String, Tier> hot = tiers(files, via);
        List<Hit> hits = new ArrayList<Hit>();
        for (SourceScan.SourceFile f : files) scan(f, hot, hits);
        writeReport(hits, hot, via);

        Map<String, Hit> byKey = new TreeMap<String, Hit>();
        for (Hit h : hits) if (!byKey.containsKey(h.key())) byKey.put(h.key(), h);
        List<String> violations = new ArrayList<String>();
        for (Map.Entry<String, Hit> e : byKey.entrySet())
        {
            if (!Allowed.ENTRIES.containsKey(e.getKey())) violations.add(e.getValue().describe());
        }
        TreeSet<String> stale = new TreeSet<String>(Allowed.ENTRIES.keySet());
        stale.removeAll(byKey.keySet());
        assertTrue(violations.isEmpty(), "performance contract broken (see notes/PERFORMANCE_CONTRACT.md; fix it, or add a justified"
                + " entry to PerformanceContractTest.Allowed):\n  " + String.join("\n  ", violations));
        assertTrue(stale.isEmpty(), "Allowed entries that no longer match, remove them:\n  " + String.join("\n  ", stale));
    }

    /** Every entry has a reason; an open finding names its audit id. */
    @Test
    void everyExceptionHasAReason()
    {
        for (Map.Entry<String, String> e : Allowed.ENTRIES.entrySet())
        {
            assertTrue(e.getValue().trim().length() > 8, "no reason for " + e.getKey());
            assertTrue(e.getKey().matches("[\\w/]+\\.java(#\\w+)? [A-Z_]+"), "bad key " + e.getKey());
        }
    }

    static final class Hit
    {
        final String rel, method;
        final Tier tier;
        final String rule, call;
        final int line;

        Hit(String rel, String method, Tier tier, String rule, String call, int line)
        {
            this.rel = rel;
            this.method = method;
            this.tier = tier;
            this.rule = rule;
            this.call = call;
            this.line = line;
        }

        String key()
        {
            return rel + (method == null ? "" : "#" + method) + " " + rule;
        }

        String describe()
        {
            return key() + "  [" + tier + ", line " + line + ": " + call + "]";
        }
    }

    static Tier tierOf(String rel, SourceScan.Method m)
    {
        if (FRAME_NAME.matcher(m.name).matches() || FRAME_EVENT.matcher(m.params).find() || REACHED_FROM_FRAME.contains(rel + "#" + m.name)) return Tier.FRAME;
        if (TICK_NAME.matcher(m.name).matches() || TICK_EVENT.matcher(m.params).find() || REACHED_FROM_TICK.contains(rel + "#" + m.name)
                || rel.startsWith("block/Tile") && PIPE_NAME.matcher(m.name).matches()) return Tier.TICK;
        if (ENTITY_EVENT.matcher(m.params).find()) return Tier.ENTITY_EVENT;
        return Tier.ANY;
    }

    private static final Pattern LOCAL_CALL = Pattern.compile("(?<![\\w.])(?:this\\.)?(\\w+)\\s*\\(");
    private static final Pattern STATIC_CALL = Pattern.compile("(?<![\\w.])([A-Z]\\w*)\\.(\\w+)\\s*\\(");

    /**
     * The tier of every method ({@code file#name}, overloads together): its own by {@link #tierOf}, or the hottest of
     * the methods that call it, followed through calls inside the same file and static calls {@code Class.method(}
     * to another file of the mod. Calls on instances of other classes are not followed: those go in
     * {@link #REACHED_FROM_TICK}.
     */
    static Map<String, Tier> tiers(List<SourceScan.SourceFile> files, Map<String, String> via)
    {
        Map<String, List<SourceScan.Method>> methods = new LinkedHashMap<String, List<SourceScan.Method>>();
        Map<String, String> fileOfClass = new LinkedHashMap<String, String>();
        Map<String, Tier> tiers = new TreeMap<String, Tier>();
        List<String> work = new ArrayList<String>();
        for (SourceScan.SourceFile f : files)
        {
            String base = f.rel.substring(f.rel.lastIndexOf('/') + 1).replace(".java", "");
            fileOfClass.put(base, f.rel);
            for (SourceScan.Method m : SourceScan.methods(f.code))
            {
                String key = f.rel + "#" + m.name;
                List<SourceScan.Method> list = methods.get(key);
                if (list == null) methods.put(key, list = new ArrayList<SourceScan.Method>());
                list.add(m);
                Tier t = tierOf(f.rel, m);
                if (t != Tier.ANY && hotter(t, tiers.get(key)))
                {
                    tiers.put(key, t);
                    work.add(key);
                }
            }
        }
        while (!work.isEmpty())
        {
            String caller = work.remove(work.size() - 1);
            Tier tier = tiers.get(caller);
            String rel = caller.substring(0, caller.indexOf('#'));
            for (SourceScan.Method m : methods.get(caller))
            {
                List<String> callees = new ArrayList<String>();
                Matcher local = LOCAL_CALL.matcher(m.body);
                while (local.find()) callees.add(rel + "#" + local.group(1));
                Matcher stat = STATIC_CALL.matcher(m.body);
                while (stat.find())
                {
                    String target = fileOfClass.get(stat.group(1));
                    if (target != null) callees.add(target + "#" + stat.group(2));
                }
                for (String callee : callees)
                {
                    if (methods.containsKey(callee) && !callee.equals(caller) && hotter(tier, tiers.get(callee)))
                    {
                        tiers.put(callee, tier);
                        via.put(callee, caller);
                        work.add(callee);
                    }
                }
            }
        }
        return tiers;
    }

    private static boolean hotter(Tier t, Tier than)
    {
        return than == null || t.ordinal() < than.ordinal();
    }

    static void scan(SourceScan.SourceFile f, Map<String, Tier> tiers, List<Hit> hits)
    {
        Matcher boxed = BOXED.matcher(f.code);
        while (boxed.find()) hits.add(new Hit(f.rel, null, Tier.ANY, "BOXED", clean(boxed.group()), f.line(boxed.start())));
        for (SourceScan.Method m : SourceScan.methods(f.code))
        {
            Tier tier = tiers.containsKey(f.rel + "#" + m.name) ? tiers.get(f.rel + "#" + m.name) : Tier.ANY;
            if (count(GL_PUSH_MATRIX, m.rawBody) != count(GL_POP_MATRIX, m.rawBody) || count(GL_PUSH_ATTRIB, m.rawBody) != count(GL_POP_ATTRIB, m.rawBody))
            {
                hits.add(new Hit(f.rel, m.name, tier, "GL", "pushes and pops differ", f.line(m.bodyStart)));
            }
            if (tier == Tier.ANY) continue;
            for (Rule rule : Rule.values())
            {
                if (!rule.tiers.contains(tier)) continue;
                Matcher bad = rule.pattern.matcher(m.body);
                while (bad.find())
                {
                    hits.add(new Hit(f.rel, m.name, tier, rule.name(), clean(bad.group()), f.line(m.bodyStart + bad.start())));
                }
            }
        }
    }

    private static int count(Pattern p, String s)
    {
        int n = 0;
        Matcher m = p.matcher(s);
        while (m.find()) n++;
        return n;
    }

    private static String clean(String call)
    {
        return call.replaceAll("\\s+", " ");
    }

    private static void writeReport(List<Hit> hits, Map<String, Tier> hot, Map<String, String> via) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Tier> e : hot.entrySet())
        {
            sb.append("HOT\t").append(e.getValue()).append('\t').append(e.getKey());
            if (via.containsKey(e.getKey())) sb.append("\tvia ").append(via.get(e.getKey()));
            sb.append('\n');
        }
        for (Hit h : hits)
        {
            String status = Allowed.ENTRIES.containsKey(h.key()) ? Allowed.ENTRIES.get(h.key()) : "NOT ALLOWED";
            sb.append(h.tier).append('\t').append(h.rule).append('\t').append(h.rel).append(':').append(h.line).append('\t')
                    .append(h.method == null ? "-" : h.method).append('\t').append(h.call).append('\t').append(status).append('\n');
        }
        Files.createDirectories(REPORT.getParent());
        Files.write(REPORT, sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /** The scanner itself, on a made-up class: each rule seen in its tier, nothing in comments, literals, catch blocks or cold methods. */
    @Test
    void scannerSeesEachRuleInItsTier()
    {
        String src = "class R {\n"
                + "  Map<Integer, String> byId;\n"
                + "  public void render(Entity e, double x) {\n"
                + "    ItemStack w = Wearing.getWearingWearable((EntityPlayer) e).copy();\n"
                + "    GL11.glPushMatrix();\n"
                + "    bindTexture(new ResourceLocation(\"a\" + x));\n"
                + "  }\n"
                + "  private void renderBackpack(float scale) { InventoryBackpack b = new InventoryBackpack(this.backpack); }\n"
                + "  @SubscribeEvent public void tip(ItemTooltipEvent event) { ItemStack s = ItemStack.copyItemStack(event.itemStack); }\n"
                + "  public void onUpdate(ItemStack s, World w) { s.stackTagCompound.setTag(\"x\", new NBTTagCompound());"
                + " List<EntityPlayer> l = w.getEntitiesWithinAABB(EntityPlayer.class, box); ModNetwork.net.sendToAll(m); }\n"
                + "  @SubscribeEvent public void hurt(LivingHurtEvent event) { Fluid f = FluidRegistry.getFluid(\"water\"); }\n"
                + "  public void onOpen(ItemStack s) { new InventoryBackpack(s); s.copy(); String.format(\"%d\", 1); }\n"
                + "  // public void render() { x.copy(); }\n"
                + "  public void update() { String t = \"new InventoryX(\"; try { a(); } catch (Exception e) { LogHelper.error(\"x\" + e); } }\n"
                + "}\n";
        List<Hit> hits = new ArrayList<Hit>();
        SourceScan.SourceFile file = new SourceScan.SourceFile("R.java", SourceScan.strip(src));
        scan(file, tiers(Arrays.asList(file), new TreeMap<String, String>()), hits);
        List<String> got = new ArrayList<String>();
        for (Hit h : hits) got.add(h.key() + "@" + h.line);
        assertEquals(Arrays.asList("R.java BOXED@2", "R.java#render GL@3", "R.java#render COPY@4", "R.java#render STRING@6",
                "R.java#render FRAME_NEW@6", "R.java#renderBackpack COPY@8", "R.java#tip COPY@9", "R.java#onUpdate NBT@10",
                "R.java#onUpdate SCAN@10", "R.java#onUpdate PACKET@10", "R.java#hurt LOOKUP@11"), got);
    }

    /** The tiers: names, events and the explicit list. */
    @Test
    void tiersFollowNamesEventsAndTheExplicitList()
    {
        assertEquals(Tier.FRAME, tierOf("A.java", new SourceScan.Method("drawGuiContainerForegroundLayer", "int a, int b", "{}", 0)));
        assertEquals(Tier.FRAME, tierOf("A.java", new SourceScan.Method("onOverlay", "RenderGameOverlayEvent.Post event", "{}", 0)));
        assertEquals(Tier.TICK, tierOf("A.java", new SourceScan.Method("tickPlayer", "TickEvent.PlayerTickEvent event", "{}", 0)));
        assertEquals(Tier.TICK, tierOf("playerProperties/BackpackProperty.java", new SourceScan.Method("sameAsSent", "", "{}", 0)));
        assertEquals(Tier.ENTITY_EVENT, tierOf("A.java", new SourceScan.Method("onFall", "LivingFallEvent event", "{}", 0)));
        assertEquals(Tier.ANY, tierOf("A.java", new SourceScan.Method("onBlockActivated", "World w, int x", "{}", 0)));
    }

    /** Justified exceptions and open findings, {@code file#method RULE} (or {@code file RULE} for BOXED) to reason. */
    static final class Allowed
    {
        static final Map<String, String> ENTRIES = new LinkedHashMap<String, String>();

        private static void ok(String key, String reason)
        {
            ENTRIES.put(key, reason);
        }

        private static void open(String key, String finding)
        {
            ENTRIES.put(key, "OPEN " + finding);
        }

        static
        {
            // ---- open findings (notes/audit-performance-2026-10-16.md): fix them and delete the line

            // ---- justified: not per frame/tick despite the tier, or guarded by a beat or a real change
            String beat = "runs only when its beat fires (TickThrottle/ParticleBeat/interval), not every tick";
            ok("util/FluidMigration.java#readTank NBT", "tile NBT load and the tooltip memo refill, not per frame/tick: " + beat);
            ok("item/ItemFlamethrowerPack.java#onEquippedUpdate COPY", "server, pilot fuel " + beat);
            ok("item/ItemFlamethrower.java#onUsingTick COPY", "server, one burst " + beat);
            ok("item/ItemFlamethrower.java#burst SCAN", "one entity search per burst, " + beat);
            ok("item/ItemFlamethrower.java#igniteBlock SCAN", "one ray per burst, " + beat);
            ok("item/ItemScubaGear.java#beat COPY", "the air beat (scuba.airIntervalTicks), " + beat);
            ok("block/TileAirCompressor.java#work COPY", "compressor beat (compressor.intervalTicks), " + beat);
            ok("common/ability/AbilityHelper.java#fillWhenWet COPY", "rain/water fill " + beat);
            ok("common/ability/AbilityHelper.java#makeFromWheat COPY", "Cow/Mooshroom eat beat: the batch makes the beat's share at once (MilkBatch), " + beat + " (P4 fixed)");
            ok("common/ability/CreeperAbility.java#onWornTick SCAN", "while sneaking, " + beat);
            ok("common/ability/OcelotAbility.java#onWornTick SCAN", beat);
            ok("common/ability/WolfAbility.java#onWornTick SCAN", beat);
            ok("item/ItemCopterPack.java#pushEntities SCAN", "airwave " + beat);
            ok("common/ability/CowAbility.java#onWornTick LOOKUP", "the milk FluidStack is built once and kept in a field");
            ok("common/ability/MooshroomAbility.java#onWornTick LOOKUP", "the stew FluidStack is built once and kept in a field");
            ok("entity/EntityGrapple.java#fly SCAN", "a flying projectile traces its own segment once per tick, like a vanilla arrow");
            ok("entity/EntityInflatableBoat.java#onUpdate SCAN", "vanilla boat collision push, same cost as EntityBoat");
            ok("entity/EntityInflatableBoat.java#getFuelTank TICK_NEW", "lazy: the tank is built once per boat");
            ok("entity/ai/EntityAIAvoidPlayerWithBackpack.java#shouldExecute SCAN", "AI tasks are evaluated every 3 ticks, like vanilla EntityAIAvoidEntity (P20 in the audit)");
            ok("entity/ai/EntityAISpiderFollowOwner.java#tryPlace SCAN", "only when the spider teleports to its owner");
            ok("item/ItemSteamJetpack.java#liveInventory COPY", "builds the live inventory only when the worn stack or its data changed (isCurrentFor)");
            ok("item/ItemCopterPack.java#liveInventory COPY", "builds the live inventory only when the worn stack or its data changed (isCurrentFor)");
            ok("item/ItemHose.java#onUpdate NBT", "the hose's own empty tag, once, on the first tick of a hose without one (P7 closed: the readout is written only on change)");
            String backpackMiss = "reached from the backpack window's tick only on a cache miss: InventoryBackpack.reloadIfChanged reads the stack again when another writer changed its tags (P1 fixed)";
            ok("inventory/BackpackNbt.java#readItems NBT", backpackMiss);
            ok("client/gui/GuiTank.java#getTankTooltip STRING", "the two lines are rebuilt only when the fluid or its amount changes while the mouse is over the tank; otherwise the cached list is returned");
            ok("inventory/BackpackNbt.java#readSlots NBT", backpackMiss);
            String userSort = "reached from the backpack window's tick only when the player pressed the sort button: the packet queues one request and the tick runs it once";
            ok("inventory/ContainerBackpack.java#sortStorage COPY", userSort);
            ok("common/HandRefill.java#sameKind NBT", userSort + " (and the merge and hand-refill paths)");
            ok("inventory/BackpackNbt.java#readSlots COPY", backpackMiss);
            ok("inventory/InventoryBackpack.java#openInventory NBT", backpackMiss);
            ok("inventory/InventoryBackpack.java#loadFromNBT NBT", backpackMiss);
            ok("inventory/HiddenTank.java#seal COPY", backpackMiss);
            ok("inventory/InventorySteamJetpack.java#flushChanges NBT", "writes only the keys whose value changed; a full write only when the data tag is missing");
            ok("inventory/InventorySteamJetpack.java#saveToNBT NBT", "reached only from flushChanges when the data tag is missing");
            ok("inventory/InventorySteamJetpack.java#dirtyInventory NBT", "only when a fuel item was burnt");
            ok("item/ItemSteamJetpack.java#clientUpdate PACKET", "local player only, on the change of the in-use state");
            ok("item/ItemSteamJetpack.java#runBoiler PACKET", "on the change to boiling or leaking");
            ok("item/ItemCopterPack.java#onEquippedUpdate PACKET", "local player only, on the change of the climbing state");
            ok("init/ModNetwork.java#sendToNearby PACKET", "callers send on a beat (Slime trail) or on an action; the Rainbow run draws its notes on each client (P22 fixed)");
            ok("playerProperties/BackpackProperty.java#send PACKET", "only when sameAsSent (StackSnapshot, identity only) says the state changed, at most once per backpack.wornSyncIntervalTicks");
            ok("playerProperties/BackpackProperty.java#getSyncData NBT", "only when a packet goes out");
            ok("playerProperties/BackpackProperty.java#write NBT", "only when a packet goes out or the player is saved");
            ok("playerProperties/ObserverView.java#wearable COPY", "only when a packet goes out");
            ok("playerProperties/ObserverView.java#wearable NBT", "only when a packet goes out");
            ok("playerProperties/ObserverView.java#copyExcept COPY", "only when a packet goes out");
            ok("playerProperties/ObserverView.java#copyExcept NBT", "only when a packet goes out");
            ok("playerProperties/ObserverView.java#toolsOnly COPY", "only when a packet goes out");
            ok("playerProperties/ObserverView.java#toolsOnly NBT", "only when a packet goes out");
            ok("client/gui/GuiSteamJetpack.java#refreshShown COPY", "only when the shown stack or its RenderInfo version changed");
            ok("client/gui/GuiCopterPack.java#refreshShown COPY", "only when the shown stack or its RenderInfo version changed");
            ok("client/gui/GuiAdvBackpack.java#refreshShown COPY", "only when the shown stack or its RenderInfo version changed (P9 closed)");
            ok("client/gui/GuiSteamJetpack.java#refreshLabels LOOKUP", "only when the tank's fluid or amount changed (ShownKey; P13 closed)");
            ok("client/gui/GearLabels.java#tank STRING", "called only when the shown value changed (refreshLabels; P13 closed)");
            ok("client/gui/GearLabels.java#temperature STRING", "called only when the temperature changed (refreshLabels; P13 closed)");
            ok("client/gui/GearLabels.java#consumption STRING", "called only when the fuel changed (GuiCopterPack.refreshLabels; P13 closed)");
            ok("handlers/ClientEventHandler.java#tankLine STRING", "only when the stack, its RenderInfo version or the language changed (P11 closed)");
            ok("item/ArmorAB.java#buildArmorTexture STRING", "once per item, kept in armorTexture (P12 closed)");
            ok("item/ArmorAB.java#buildUnlocalizedName STRING", "once per item, kept in unlocalizedName (P12 closed)");
            ok("util/Resources.java#modelTextureStringOf FRAME_NEW", "cache miss only (MODEL_TEXTURE_STRINGS; P12 closed)");
            ok("util/Resources.java#modelTextureStringOf STRING", "cache miss only (MODEL_TEXTURE_STRINGS; P12 closed)");
            ok("item/ItemBoltCartridge.java#displayName LOOKUP", "only when the ammo kind shown changes (tooltipAmmo memo; P11 closed)");
            ok("item/ItemBoltCartridge.java#displayName FRAME_NEW", "only when the ammo kind shown changes (tooltipAmmo memo; P11 closed)");
            ok("client/gui/GuiDistillery.java#refreshTip STRING", "only when the tank amount changed");
            ok("client/gui/GuiFlamethrowerPack.java#refresh STRING", "only when fuel or switch changed");
            ok("client/gui/GuiFuelStation.java#refreshTexts STRING", "only when the fluid or amount changed");
            ok("client/gui/GuiScubaGear.java#refresh STRING", "only when the air amount changed");
            ok("client/gui/GuiAirCompressor.java#drawGuiContainerForegroundLayer STRING", "one I18n lookup per frame for the title; cheap, vanilla GUIs do the same");
            ok("client/ClientActions.java#boatMotorSound STRING", "only when the motor starts or stops");
            ok("inventory/AirContainers.java#toStored COPY", "a FluidStack copy (small), per container move or pipe fill");
            ok("inventory/AirContainers.java#process COPY", "on the compressor beat, per container moved");
            ok("inventory/AirContainers.java#move COPY", "on the compressor beat, per container moved");
            ok("inventory/AirContainers.java#matches LOOKUP", "on the compressor beat, only with an item in the input slot");
            ok("inventory/AirContainers.java#fits NBT", "on the compressor beat, only with an item in the input slot");
            ok("inventory/DistilleryItems.java#matches LOOKUP", "only when the slots changed (slotsStale) or a batch ends");
            ok("inventory/Quiver.java#accepts LOOKUP", "a slot check (click or hopper), Tier 2: the ore lists are resolved once per filter and walked without allocation (P21 fixed)");
            ok("block/TileStation.java#fillPackStack COPY", "a FluidStack copy per transfer; the pipe path stops when the pack is full (packMayAccept)");
            ok("block/TileStation.java#put COPY", "a FluidStack copy when the tank was empty");
            ok("item/ItemBoltCartridge.java#write NBT", "onUpdate writes only a magazine without charges, once");
            ok("item/ItemBoltCartridge.java#resolveAmmo LOOKUP", "memoised: runs again only when crossbow.ammo.list changes");
            ok("item/ItemBoltCartridge.java#resolveAmmo IO", "memoised: runs again only when crossbow.ammo.list changes");
            ok("item/ItemBoltCartridge.java#resolveAmmo STRING", "memoised: runs again only when crossbow.ammo.list changes");
            ok("item/ItemBoltCartridge.java#resolveAmmo COLLECTION", "memoised: runs again only when crossbow.ammo.list changes");
            ok("item/ItemBoltCartridge.java#addInformation STRING", "tooltip formatting while hovered; lang-formatted lines need it");
            ok("item/ItemComponent.java#addInformation STRING", "tooltip formatting while hovered; lang-formatted lines need it");
            ok("block/ItemBlockStation.java#addInformation STRING", "tooltip formatting while hovered; lang-formatted lines need it");
            ok("item/ItemCrossbow.java#addInformation STRING", "tooltip formatting while hovered; lang-formatted lines need it");
            ok("item/ItemCrossbow.java#tag NBT", "a compound only for a crossbow without tag, once");
            ok("util/BackpackUtils.java#setTimeMark NBT", "a compound only for a stack without tag; marks are written when an ability fires");
            ok("util/BackpackUtils.java#setBackpackData NBT", "a compound only for a stack without tag");
            ok("reference/BackpackNames.java#getBackpackColorName NBT", "fallback for a stack without backpack data only");
            ok("reference/BackpackNames.java#setBackpackColorName NBT", "a compound only for a stack without backpack data");
            ok("util/Resources.java#backpackTextureFromString FRAME_NEW", "cache miss only (BACKPACK_TEXTURES)");
            ok("util/Resources.java#backpackTextureFromString STRING", "cache miss only (BACKPACK_TEXTURES)");
            ok("util/Resources.java#modelTextures FRAME_NEW", "cache miss only (MODEL_TEXTURES)");
            ok("util/Resources.java#modelTextures STRING", "cache miss only (MODEL_TEXTURES)");
            ok("util/Resources.java#getIconString STRING", "icon registration only (registerIcons), matched by name");
        }
    }
}
