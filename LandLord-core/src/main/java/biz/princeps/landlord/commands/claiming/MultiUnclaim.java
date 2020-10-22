package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.*;
import biz.princeps.landlord.api.events.LandUnclaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.MultiMode;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class MultiUnclaim extends LandlordCommand {

    private final IWorldGuardManager wg;

    public MultiUnclaim(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.MultiUnclaim.name"),
                pl.getConfig().getString("CommandSettings.MultiUnclaim.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiUnclaim.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.MultiUnclaim.aliases")));
        this.wg = plugin.getWGManager();
    }

    /**
     * Executed when a player enters /land multiunclaim
     * Expected parameters is
     * /land multiunclaim <option>
     * Option is either circular or rectangular!
     * <p>
     * All the individual claims are redirected to the function that handles /land claim
     *
     * @param properties the player who wants to claim
     * @param arguments  option
     */
    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            properties.sendMessage("Player command only!");
            return;
        }
        if (arguments.size() != 2) {
            properties.sendUsage();
            return;
        }

        Player player = properties.getPlayer();
        if (isDisabledWorld(player)) {
            return;
        }

        MultiMode mode = null;
        int radius = -1;
        try {
            mode = MultiMode.valueOf(arguments.get()[0].toUpperCase());
            radius = arguments.getInt(1);
        } catch (IllegalArgumentException | ArgumentsOutOfBoundsException ex) {
            properties.sendUsage();
        }

        if (plugin.getConfig().getBoolean("ConfirmationDialog.onMultiUnclaim")) {
            String guiMsg = lm.getRawString("Commands.MultiUnclaim.confirm");

            MultiMode finalMode = mode;
            int finalRadius = radius;
            PrincepsLib.getConfirmationManager().drawGUI(player, guiMsg,
                    (p) -> {
                        performMultiUnclaim(player, finalMode, finalRadius);
                        player.closeInventory();
                    },
                    (p) -> player.closeInventory(), null);
        } else {
            performMultiUnclaim(player, mode, radius);
        }
    }

    public void performMultiUnclaim(Player player, MultiMode mode, int radius) {
        final int maxSize = Bukkit.getViewDistance() + 2;

        // Implementation of this to avoid latencies with MultiUnclaim, because getChunk methode generates the chunk if is not :/
        if (radius > maxSize) { // +2 for marge value. Unless server has a huge render distance (16 for example), won't cause any trouble
            lm.sendMessage(player, lm.getString("Commands.MultiUnclaim.hugeSize")
                    .replace("%max_size%", maxSize + ""));
            return;
        }

        final Set<IOwnedLand> toUnclaim = mode.getLandsOf(radius, player.getLocation(), player.getUniqueId(), wg);

        if (toUnclaim.isEmpty()) {
            lm.sendMessage(player, lm.getString("Commands.MultiUnclaim.notOwnFreeLand"));
            return;
        }

        int unclaimedLands = 0;
        double totalPayBack = 0;

        for (IOwnedLand ol : toUnclaim) {
            LandUnclaimEvent event = new LandUnclaimEvent(player, ol);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                double payback;
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

        lm.sendMessage(player, lm.getString("Commands.MultiUnclaim.success")
                .replace("%amount%", "" + unclaimedLands)
                .replace("%money%", (Options.isVaultEnabled() ? plugin.getVaultManager().format(totalPayBack) : "-eco disabled-")));

        plugin.getMapManager().updateAll();
    }

}
