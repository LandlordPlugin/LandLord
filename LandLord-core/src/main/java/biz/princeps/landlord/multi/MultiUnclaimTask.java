package biz.princeps.landlord.multi;

import biz.princeps.landlord.api.AMultiTask;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.ManageMode;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.api.events.LandUnclaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

public class MultiUnclaimTask extends AMultiTask<IOwnedLand> {

    private final IWorldGuardManager wgManager;
    private final ILangManager lgManager;
    private final Player player;

    private final IPlayer lPlayer;
    private final int freeLands;
    private final int unclaimedLands;
    private final World world;
    private final ManageMode manageMode;
    private double totalPayBack;

    public MultiUnclaimTask(ILandLord plugin, Player player, Collection<IOwnedLand> operations, World world, ManageMode manageMode) {
        super(plugin, operations);

        this.wgManager = plugin.getWGManager();
        this.lgManager = plugin.getLangManager();
        this.player = player;

        this.lPlayer = plugin.getPlayerManager().get(player.getUniqueId());
        this.freeLands = plugin.getConfig().getInt("Freelands");
        this.unclaimedLands = operations.size();
        this.world = world;
        this.manageMode = manageMode;
        this.totalPayBack = 0;
    }

    @Override
    public boolean process(IOwnedLand ownedLand) {
        LandUnclaimEvent event = new LandUnclaimEvent(player, ownedLand);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (Options.isVaultEnabled()) {
                double payback;
                int regionCount = wgManager.getRegionCount(player.getUniqueId());

                // System.out.println("regionCount: " + regionCount + " freeLands: " + freeLands);

                if (regionCount <= freeLands) {
                    payback = 0;
                } else {
                    payback = plugin.getCostManager().calculateCost(regionCount - 1) * plugin.getConfig().getDouble("Payback");
                    // System.out.println(payback);
                    if (payback > 0) {
                        plugin.getVaultManager().give(player, payback);
                    }
                }
                totalPayBack += payback;
            }

            Location location = ownedLand.getALocation();
            wgManager.unclaim(ownedLand.getWorld(), ownedLand.getName());
            if (plugin.getConfig().getBoolean("CommandSettings.Unclaim.regenerate", false)) {
                plugin.getRegenerationManager().regenerateChunk(location);
            }

            // remove possible homes
            if (lPlayer != null) {
                Location home = lPlayer.getHome();
                if (home != null) {
                    if (ownedLand.contains(home.getBlockX(), home.getBlockY(), home.getBlockZ())) {
                        lgManager.sendMessage(player, lgManager.getString(player, "Commands.SetHome.removed"));
                        plugin.getPlayerManager().get(ownedLand.getOwner()).setHome(null);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean canProcess() {
        return player.isOnline();
    }

    @Override
    public void complete() {
        switch (manageMode) {
            case MULTI:
                lgManager.sendMessage(player, lgManager.getString(player, "Commands.MultiUnclaim.success")
                        .replace("%amount%", "" + unclaimedLands)
                        .replace("%money%", (Options.isVaultEnabled() ? plugin.getVaultManager().format(totalPayBack) : "-eco disabled-")));
                break;
            case ALL:
                lgManager.sendMessage(player, lgManager.getString(player, "Commands.UnclaimAll.success")
                        .replace("%amount%", "" + unclaimedLands)
                        .replace("%world%", world.getName())
                        .replace("%money%", (Options.isVaultEnabled() ? plugin.getVaultManager().format(totalPayBack) : "-eco disabled-")));
                break;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getPlugin(), () -> plugin.getMapManager().updateAll());
    }

}
