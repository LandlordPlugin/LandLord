package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.events.LandUnclaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 17.07.17.
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
            if (!isAdmin) {
                int regionCount = plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(plugin.getWgHandler().getWG().wrapPlayer(player));
                int freeLands = plugin.getConfig().getInt("Freelands");

                if (plugin.isVaultEnabled()) {
                    if (regionCount <= freeLands)
                        payback = 0;
                    else
                        payback = OwnedLand.calculateCost(player) * plugin.getConfig().getDouble("Payback");

                    plugin.getVaultHandler().give(player.getUniqueId(), payback);
                }
            }
            plugin.getWgHandler().unclaim(player.getWorld(), pr.getName());

            // remove possible homes
            Location home = plugin.getPlayerManager().get(pr.getOwner()).getHome();
            if (home != null)
                if (pr.getWGLand().contains(home.getBlockX(), home.getBlockY(), home.getBlockZ())) {
                    player.sendMessage(lm.getString("Commands.SetHome.removed"));
                    plugin.getPlayerManager().get(pr.getOwner()).setHome(null);
                }

            player.sendMessage(lm.getString("Commands.Unclaim.success")
                    .replace("%chunk%", OwnedLand.getName(chunk))
                    .replace("%world%", chunk.getWorld().getName())
                    .replace("%money%", (plugin.isVaultEnabled() ? plugin.getVaultHandler().format(payback) : "-1")));


            plugin.getMapManager().updateAll();
        }

    }

}
