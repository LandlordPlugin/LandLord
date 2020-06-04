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

import java.util.HashSet;
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

        if (plugin.getConfig().getBoolean("ConfirmationDialog.onUnclaimAll")) {
            String guiMsg = lm.getRawString("Commands.UnclaimAll.confirm");

            PrincepsLib.getConfirmationManager().drawGUI(player, guiMsg,
                    (p) -> {
                        performUnclaimAll(player);
                        player.closeInventory();
                    },
                    (p2) -> player.closeInventory(), null);
        } else {
            performUnclaimAll(player);
        }
    }

    //TODO an unclaim all with a world option whould be convenient
    public void performUnclaimAll(Player player) {
        for (World world : Bukkit.getWorlds()) {
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
