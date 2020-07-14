package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.*;
import biz.princeps.landlord.api.events.LandUnclaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 * <p>
 */
public class UnclaimAll extends LandlordCommand {

    private final IWorldGuardManager wg;

    public UnclaimAll(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.UnclaimAll.name"),
                pl.getConfig().getString("CommandSettings.UnclaimAll.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.UnclaimAll.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.UnclaimAll.aliases")));
        this.wg = plugin.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();
        List<World> worlds;

        if (arguments.size() == 1) {
            String worldName = arguments.get(0);
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                lm.sendMessage(player, lm.getString("Commands.UnclaimAll.invalidWorld"));
                return;
            } else {
                worlds = Collections.singletonList(world);
            }
        } else {
            worlds = Bukkit.getWorlds();
        }

        if (plugin.getConfig().getBoolean("ConfirmationDialog.onUnclaimAll")) {
            String guiMsg = lm.getRawString("Commands.UnclaimAll.confirm");

            PrincepsLib.getConfirmationManager().drawGUI(player, guiMsg,
                    (p) -> {
                        performUnclaimAll(player, worlds);
                        player.closeInventory();
                    },
                    (p2) -> player.closeInventory(), null);
        } else {
            performUnclaimAll(player, worlds);
        }
    }

    //TODO an unclaim all with a world option whould be convenient
    //TODO Async (remove region)

    /*
    >.... [12:25:18 ERROR]: --- DO NOT REPORT THIS TO PAPER - THIS IS NOT A BUG OR A CRASH - git-Paper-334 (MC: 1.15.2) ---
>.... [12:25:18 ERROR]: The server has not responded for 10 seconds! Creating thread dump
>.... [12:25:18 ERROR]: ------------------------------
>.... [12:25:18 ERROR]: Server thread dump (Look for plugins here before reporting to Paper!):
>.... [12:25:18 ERROR]: ------------------------------
>.... [12:25:18 ERROR]: Current Thread: Server thread
>.... [12:25:18 ERROR]: PID: 34 | Suspended: false | Native: false | State: RUNNABLE
>.... [12:25:18 ERROR]: Stack:
>.... [12:25:18 ERROR]: java.util.Collections$UnmodifiableCollection$1.hasNext(Collections.java:1041)
>.... [12:25:18 ERROR]: org.khelekore.prtree.LeafBuilder.buildLeafs(LeafBuilder.java:30)
>.... [12:25:18 ERROR]: org.khelekore.prtree.PRTree.load(PRTree.java:49)
>.... [12:25:18 ERROR]: com.sk89q.worldguard.protection.managers.index.PriorityRTreeIndex.rebuildIndex(PriorityRTreeIndex.java:65)
>.... [12:25:18 ERROR]: com.sk89q.worldguard.protection.managers.index.HashMapIndex.remove(HashMapIndex.java:177)
>.... [12:25:18 ERROR]: com.sk89q.worldguard.protection.managers.index.ChunkHashTable.remove(ChunkHashTable.java:214)
>.... [12:25:18 ERROR]: com.sk89q.worldguard.protection.managers.RegionManager.removeRegion(RegionManager.java:292)
>.... [12:25:18 ERROR]: com.sk89q.worldguard.protection.managers.RegionManager.removeRegion(RegionManager.java:280)
>.... [12:25:18 ERROR]: biz.princeps.landlord.manager.WorldGuardManager.unclaim(WorldGuardManager.java:157)
>.... [12:25:18 ERROR]: biz.princeps.landlord.commands.claiming.UnclaimAll.onCommand(UnclaimAll.java:80)
>.... [12:25:18 ERROR]: biz.princeps.lib.command.MainCommand.execute(MainCommand.java:80)
>.... [12:25:18 ERROR]: org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:159)
>.... [12:25:18 ERROR]: org.bukkit.craftbukkit.v1_15_R1.CraftServer.dispatchCommand(CraftServer.java:761)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnection.handleCommand(PlayerConnection.java:1860)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnection.a(PlayerConnection.java:1668)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.PacketPlayInChat.a(PacketPlayInChat.java:47)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.PacketPlayInChat.a(PacketPlayInChat.java:5)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnectionUtils.lambda$ensureMainThread$0(PlayerConnectionUtils.java:23)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnectionUtils$$Lambda$4617/1945115525.run(Unknown Source)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.TickTask.run(SourceFile:18)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeTask(IAsyncTaskHandler.java:136)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandlerReentrant.executeTask(SourceFile:23)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeNext(IAsyncTaskHandler.java:109)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.ba(MinecraftServer.java:1097)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.executeNext(MinecraftServer.java:1090)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeAll(IAsyncTaskHandler.java:95)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.a(MinecraftServer.java:1224)
>.... [12:25:18 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.run(MinecraftServer.java:962)
>.... [12:25:18 ERROR]: java.lang.Thread.run(Thread.java:748)
>.... [12:25:18 ERROR]: ------------------------------
>.... [12:25:18 ERROR]: --- DO NOT REPORT THIS TO PAPER - THIS IS NOT A BUG OR A CRASH ---
>.... [12:25:18 ERROR]: ------------------------------
>.... [12:25:23 ERROR]: --- DO NOT REPORT THIS TO PAPER - THIS IS NOT A BUG OR A CRASH - git-Paper-334 (MC: 1.15.2) ---
>.... [12:25:23 ERROR]: The server has not responded for 15 seconds! Creating thread dump
>.... [12:25:23 ERROR]: ------------------------------
>.... [12:25:23 ERROR]: Server thread dump (Look for plugins here before reporting to Paper!):
>.... [12:25:23 ERROR]: ------------------------------
>.... [12:25:23 ERROR]: Current Thread: Server thread
>.... [12:25:23 ERROR]: PID: 34 | Suspended: false | Native: false | State: RUNNABLE
>.... [12:25:23 ERROR]: Stack:
>.... [12:25:23 ERROR]: org.khelekore.prtree.LeafBuilder$Noder.getNextNode(LeafBuilder.java:131)
>.... [12:25:23 ERROR]: org.khelekore.prtree.LeafBuilder$Noder.access$200(LeafBuilder.java:109)
>.... [12:25:23 ERROR]: org.khelekore.prtree.LeafBuilder.getLeafs(LeafBuilder.java:71)
>.... [12:25:23 ERROR]: org.khelekore.prtree.LeafBuilder.buildLeafs(LeafBuilder.java:44)
>.... [12:25:23 ERROR]: org.khelekore.prtree.PRTree.load(PRTree.java:49)
>.... [12:25:23 ERROR]: com.sk89q.worldguard.protection.managers.index.PriorityRTreeIndex.rebuildIndex(PriorityRTreeIndex.java:65)
>.... [12:25:23 ERROR]: com.sk89q.worldguard.protection.managers.index.HashMapIndex.remove(HashMapIndex.java:177)
>.... [12:25:23 ERROR]: com.sk89q.worldguard.protection.managers.index.ChunkHashTable.remove(ChunkHashTable.java:214)
>.... [12:25:23 ERROR]: com.sk89q.worldguard.protection.managers.RegionManager.removeRegion(RegionManager.java:292)
>.... [12:25:23 ERROR]: com.sk89q.worldguard.protection.managers.RegionManager.removeRegion(RegionManager.java:280)
>.... [12:25:23 ERROR]: biz.princeps.landlord.manager.WorldGuardManager.unclaim(WorldGuardManager.java:157)
>.... [12:25:23 ERROR]: biz.princeps.landlord.commands.claiming.UnclaimAll.onCommand(UnclaimAll.java:80)
>.... [12:25:23 ERROR]: biz.princeps.lib.command.MainCommand.execute(MainCommand.java:80)
>.... [12:25:23 ERROR]: org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:159)
>.... [12:25:23 ERROR]: org.bukkit.craftbukkit.v1_15_R1.CraftServer.dispatchCommand(CraftServer.java:761)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnection.handleCommand(PlayerConnection.java:1860)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnection.a(PlayerConnection.java:1668)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.PacketPlayInChat.a(PacketPlayInChat.java:47)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.PacketPlayInChat.a(PacketPlayInChat.java:5)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnectionUtils.lambda$ensureMainThread$0(PlayerConnectionUtils.java:23)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnectionUtils$$Lambda$4617/1945115525.run(Unknown Source)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.TickTask.run(SourceFile:18)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeTask(IAsyncTaskHandler.java:136)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandlerReentrant.executeTask(SourceFile:23)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeNext(IAsyncTaskHandler.java:109)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.ba(MinecraftServer.java:1097)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.executeNext(MinecraftServer.java:1090)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeAll(IAsyncTaskHandler.java:95)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.a(MinecraftServer.java:1224)
>.... [12:25:23 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.run(MinecraftServer.java:962)
>.... [12:25:23 ERROR]: java.lang.Thread.run(Thread.java:748)
>.... [12:25:23 ERROR]: ------------------------------
>.... [12:25:23 ERROR]: --- DO NOT REPORT THIS TO PAPER - THIS IS NOT A BUG OR A CRASH ---
>.... [12:25:23 ERROR]: ------------------------------
>.... [12:25:28 ERROR]: --- DO NOT REPORT THIS TO PAPER - THIS IS NOT A BUG OR A CRASH - git-Paper-334 (MC: 1.15.2) ---
>.... [12:25:28 ERROR]: The server has not responded for 20 seconds! Creating thread dump
>.... [12:25:28 ERROR]: ------------------------------
>.... [12:25:28 ERROR]: Server thread dump (Look for plugins here before reporting to Paper!):
>.... [12:25:28 ERROR]: ------------------------------
>.... [12:25:28 ERROR]: Current Thread: Server thread
>.... [12:25:28 ERROR]: PID: 34 | Suspended: false | Native: false | State: RUNNABLE
>.... [12:25:28 ERROR]: Stack:
>.... [12:25:28 ERROR]: org.khelekore.prtree.LeafBuilder$NodeUsageComparator.compare(LeafBuilder.java:105)
>.... [12:25:28 ERROR]: org.khelekore.prtree.LeafBuilder$NodeUsageComparator.compare(LeafBuilder.java:96)
>.... [12:25:28 ERROR]: java.util.TimSort.mergeLo(TimSort.java:717)
>.... [12:25:28 ERROR]: java.util.TimSort.mergeAt(TimSort.java:514)
>.... [12:25:28 ERROR]: java.util.TimSort.mergeCollapse(TimSort.java:439)
>.... [12:25:28 ERROR]: java.util.TimSort.sort(TimSort.java:245)
>.... [12:25:28 ERROR]: java.util.Arrays.sort(Arrays.java:1512)
>.... [12:25:28 ERROR]: java.util.ArrayList.sort(ArrayList.java:1462)
>.... [12:25:28 ERROR]: java.util.Collections.sort(Collections.java:175)
>.... [12:25:28 ERROR]: org.khelekore.prtree.LeafBuilder.addGetterAndSplitter(LeafBuilder.java:51)
>.... [12:25:28 ERROR]: org.khelekore.prtree.LeafBuilder.buildLeafs(LeafBuilder.java:37)
>.... [12:25:28 ERROR]: org.khelekore.prtree.PRTree.load(PRTree.java:49)
>.... [12:25:28 ERROR]: com.sk89q.worldguard.protection.managers.index.PriorityRTreeIndex.rebuildIndex(PriorityRTreeIndex.java:65)
>.... [12:25:28 ERROR]: com.sk89q.worldguard.protection.managers.index.HashMapIndex.remove(HashMapIndex.java:177)
>.... [12:25:28 ERROR]: com.sk89q.worldguard.protection.managers.index.ChunkHashTable.remove(ChunkHashTable.java:214)
>.... [12:25:28 ERROR]: com.sk89q.worldguard.protection.managers.RegionManager.removeRegion(RegionManager.java:292)
>.... [12:25:28 ERROR]: com.sk89q.worldguard.protection.managers.RegionManager.removeRegion(RegionManager.java:280)
>.... [12:25:28 ERROR]: biz.princeps.landlord.manager.WorldGuardManager.unclaim(WorldGuardManager.java:157)
>.... [12:25:28 ERROR]: biz.princeps.landlord.commands.claiming.UnclaimAll.onCommand(UnclaimAll.java:80)
>.... [12:25:28 ERROR]: biz.princeps.lib.command.MainCommand.execute(MainCommand.java:80)
>.... [12:25:28 ERROR]: org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:159)
>.... [12:25:28 ERROR]: org.bukkit.craftbukkit.v1_15_R1.CraftServer.dispatchCommand(CraftServer.java:761)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnection.handleCommand(PlayerConnection.java:1860)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnection.a(PlayerConnection.java:1668)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.PacketPlayInChat.a(PacketPlayInChat.java:47)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.PacketPlayInChat.a(PacketPlayInChat.java:5)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnectionUtils.lambda$ensureMainThread$0(PlayerConnectionUtils.java:23)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.PlayerConnectionUtils$$Lambda$4617/1945115525.run(Unknown Source)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.TickTask.run(SourceFile:18)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeTask(IAsyncTaskHandler.java:136)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandlerReentrant.executeTask(SourceFile:23)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeNext(IAsyncTaskHandler.java:109)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.ba(MinecraftServer.java:1097)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.executeNext(MinecraftServer.java:1090)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.IAsyncTaskHandler.executeAll(IAsyncTaskHandler.java:95)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.a(MinecraftServer.java:1224)
>.... [12:25:28 ERROR]: net.minecraft.server.v1_15_R1.MinecraftServer.run(MinecraftServer.java:962)
>.... [12:25:28 ERROR]: java.lang.Thread.run(Thread.java:748)
>.... [12:25:28 ERROR]: ------------------------------
>.... [12:25:28 ERROR]: --- DO NOT REPORT THIS TO PAPER - THIS IS NOT A BUG OR A CRASH ---
>.... [12:25:28 ERROR]: ------------------------------
>.... [12:25:31 WARN]: Can't keep up! Is the server overloaded? Running 23335ms or 466 ticks behind
     */
    public void performUnclaimAll(Player player, List<World> worlds) {
        for (World world : worlds) {
            if (isDisabledWorld(world)) {
                continue;
            }

            Set<IOwnedLand> landsOfPlayer = new HashSet<>(plugin.getWGManager().getRegions(player.getUniqueId(), world));

            if (landsOfPlayer.isEmpty()) {
                lm.sendMessage(player, lm.getString("Commands.UnclaimAll.notOwnFreeLand") + " (" + world.getName() + ")");
                continue;
            }

            int unclaimedLands = 0;
            double totalPayBack = 0;

            for (IOwnedLand ol : landsOfPlayer) {
                LandUnclaimEvent event = new LandUnclaimEvent(player, ol);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    double payback = -1;
                    int regionCount = wg.getRegionCount(player.getUniqueId());
                    int freeLands = plugin.getConfig().getInt("Freelands");

                    // System.out.println("regionCount: " + regionCount + " freeLands: " + freeLands);

                    if (Options.isVaultEnabled()) {
                        if (regionCount <= freeLands) {
                            payback = 0;
                        } else {
                            payback = plugin.getCostManager().calculateCost(regionCount - 1) * plugin.getConfig().getDouble("Payback");
                            // System.out.println(payback);
                            if (payback > 0) {
                                plugin.getVaultManager().give(player.getUniqueId(), payback);
                            }
                        }
                        totalPayBack += payback;
                    }
                    Location location = ol.getALocation();
                    wg.unclaim(ol.getWorld(), ol.getName());
                    if (plugin.getConfig().getBoolean("CommandSettings.Unclaim.regenerate", false)) {
                        plugin.getRegenerationManager().regenerateChunk(location);
                    }
                    unclaimedLands++;

                    // remove possible homes
                    IPlayer lPlayer = plugin.getPlayerManager().get(ol.getOwner());
                    if (lPlayer != null) {
                        Location home = lPlayer.getHome();
                        if (home != null) {
                            if (ol.contains(home.getBlockX(), home.getBlockY(), home.getBlockZ())) {
                                lm.sendMessage(player, lm.getString("Commands.SetHome.removed"));
                                plugin.getPlayerManager().get(ol.getOwner()).setHome(null);
                            }
                        }
                    }
                }
            }

            lm.sendMessage(player, lm.getString("Commands.UnclaimAll.success")
                    .replace("%amount%", "" + unclaimedLands)
                    .replace("%world%", "" + world.getName())
                    .replace("%money%", (Options.isVaultEnabled() ? plugin.getVaultManager().format(totalPayBack) : "-eco disabled-")));

            plugin.getMapManager().updateAll();
        }
    }

}
