package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.api.events.LandUnclaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.util.OwnedLand;
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

    public void onUnclaim(Player player, String chunkname) {

        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = null;
        if (chunkname.equals("null")) {
            chunk = player.getWorld().getChunkAt(player.getLocation());
        } else {
            String[] split = chunkname.split("_");
            try {
                if (split.length != 3) {
                    Bukkit.dispatchCommand(player, "/ll help");
                    return;
                }
                int x = Integer.valueOf(split[1]);
                int z = Integer.valueOf(split[2]);
                chunk = Bukkit.getWorld(split[0]).getChunkAt(x, z);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        OwnedLand pr = plugin.getWgHandler().getRegion(chunk);

        if (pr == null) {
            player.sendMessage(lm.getString("Commands.Unclaim.notOwnFreeLand"));
            return;
        }

        // is admin - allowed to unclaim
        boolean isAdmin = false;
        if (!player.hasPermission("landlord.admin.unclaim")) {
            if (!pr.isOwner(player.getUniqueId())) {
                player.sendMessage(lm.getString("Commands.Unclaim.notOwn")
                        .replace("%owner%", pr.printOwners()));
                return;
            }
        } else
            isAdmin = true;

        // Normal unclaim
        LandUnclaimEvent event = new LandUnclaimEvent(player, pr);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            double payback = -1;
            if (!isAdmin || pr.isOwner(player.getUniqueId())) {
                int regionCount = plugin.getWgHandler().getRegionCountOfPlayer(player.getUniqueId());
                int freeLands = plugin.getConfig().getInt("Freelands");

                // System.out.println("regionCount: " + regionCount + " freeLands: " + freeLands);

                if (Options.isVaultEnabled()) {
                    if (regionCount <= freeLands)
                        payback = 0;
                    else {
                        payback = plugin.getCostManager().calculateCost(regionCount - 1) * plugin.getConfig().getDouble("Payback");
                        // System.out.println(payback);
                        if (payback > 0)
                            plugin.getVaultHandler().give(player.getUniqueId(), payback);
                    }
                }
            }
            plugin.getWgHandler().unclaim(player.getWorld(), pr.getName());

            // remove possible homes
            LPlayer lPlayer = plugin.getPlayerManager().get(pr.getOwner());
            if (lPlayer != null) {
                Location home = lPlayer.getHome();
                if (home != null) {
                    if (pr.getWGLand().contains(home.getBlockX(), home.getBlockY(), home.getBlockZ())) {
                        player.sendMessage(lm.getString("Commands.SetHome.removed"));
                        plugin.getPlayerManager().get(pr.getOwner()).setHome(null);
                    }
                }
            }

            if (plugin.getConfig().getBoolean("Particles.unclaim"))
                OwnedLand.highlightLand(player, Particle.DRIP_LAVA);

            // Remove possible advertisements
            plugin.getOfferManager().removeOffer(pr.getName());

            player.sendMessage(lm.getString("Commands.Unclaim.success")
                    .replace("%chunk%", OwnedLand.getName(chunk))
                    .replace("%world%", chunk.getWorld().getName())
                    .replace("%money%", (Options.isVaultEnabled() ? plugin.getVaultHandler().format(payback) : "-eco disabled-")));


            plugin.getMapManager().updateAll();
        }

    }

}
