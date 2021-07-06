package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.api.events.LandUnclaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 */
public class Unclaim extends LandlordCommand {

    private final IWorldGuardManager wg;

    public Unclaim(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Unclaim.name"),
                pl.getConfig().getString("CommandSettings.Unclaim.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Unclaim.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Unclaim.aliases")));
        this.wg = pl.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isPlayer()) {
            try {
                onUnclaim(properties.getPlayer(), arguments.get(0));
            } catch (ArgumentsOutOfBoundsException e) {
                onUnclaim(properties.getPlayer(), "null");
            }
        }
    }

    public void onUnclaim(Player player, String chunkname) {

        IOwnedLand ol;
        if (chunkname.equals("null")) {
            Chunk chunk = player.getLocation().getChunk();
            ol = wg.getRegion(chunk);
            chunkname = wg.getLandName(chunk);
        } else {
            if (!wg.isLLRegion(chunkname)) {
                lm.sendMessage(player, lm.getString(player, "Commands.Unclaim.notOwnFreeLand"));
                return;
            }

            ol = wg.getRegion(chunkname);
        }

        if (isDisabledWorld(player, wg.getWorld(chunkname))) return;

        if (ol == null) {
            lm.sendMessage(player, lm.getString(player, "Commands.Unclaim.notOwnFreeLand"));
            return;
        }

        // is admin - allowed to unclaim
        boolean isAdmin = false;
        if (!player.hasPermission("landlord.admin.unclaim")) {
            if (!ol.isOwner(player.getUniqueId())) {
                lm.sendMessage(player, lm.getString(player, "Commands.Unclaim.notOwn")
                        .replace("%owner%", ol.getOwnersString()));
                return;
            }
        } else {
            isAdmin = true;
        }

        // Normal unclaim
        LandUnclaimEvent event = new LandUnclaimEvent(player, ol);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (plugin.getConfig().getBoolean("ConfirmationDialog.onUnclaim")) {
                String guiMsg = lm.getRawString("Commands.Unclaim.confirm")
                        .replace("%land%", chunkname);
                boolean finalIsAdmin = isAdmin;
                PrincepsLib.getConfirmationManager().drawGUI(player, guiMsg,
                        (p) -> {
                            performUnclaim(finalIsAdmin, ol, player);
                            player.closeInventory();
                        },
                        (p2) -> player.closeInventory(), null);
            } else {
                performUnclaim(isAdmin, ol, player);
            }
        }

    }

    public void performUnclaim(boolean isAdmin, IOwnedLand ol, Player player) {
        double payback = -1;
        if (!isAdmin || ol.isOwner(player.getUniqueId())) {
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
                        plugin.getVaultManager().give(player, payback);
                    }
                }
            }
        }
        wg.unclaim(ol.getWorld(), ol.getName());

        if (plugin.getConfig().getBoolean("CommandSettings.Unclaim.regenerate")) {
            plugin.getRegenerationManager().regenerateChunk(ol.getALocation());
        }

        // remove possible homes
        IPlayer lPlayer = plugin.getPlayerManager().get(ol.getOwner());
        if (lPlayer != null) {
            Location home = lPlayer.getHome();
            if (home != null) {
                if (ol.contains(home.getBlockX(), home.getBlockY(), home.getBlockZ())) {
                    lm.sendMessage(player, lm.getString(player, "Commands.SetHome.removed"));
                    plugin.getPlayerManager().get(ol.getOwner()).setHome(null);
                }
            }
        }

        if (plugin.getConfig().getBoolean("Particles.unclaim.enabled", false)) {
            ol.highlightLand(player,
                    Particle.valueOf(plugin.getConfig().getString("Particles.unclaim.particle").toUpperCase()));
        }

        lm.sendMessage(player, lm.getString(player, "Commands.Unclaim.success")
                .replace("%chunk%", ol.getName())
                .replace("%location%", wg.formatLocation(ol.getChunk()))
                .replace("%world%", ol.getWorld().getName())
                .replace("%money%", (Options.isVaultEnabled() ? plugin.getVaultManager().format(payback) : "-eco disabled-")));


        plugin.getMapManager().updateAll();
    }
}
