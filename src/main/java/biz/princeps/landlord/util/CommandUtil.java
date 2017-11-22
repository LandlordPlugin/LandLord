package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.persistent.Offers;
import biz.princeps.lib.crossversion.CParticle;
import biz.princeps.lib.storage.requests.Conditions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 11/22/17
 */
public class CommandUtil {

    private static Landlord plugin = Landlord.getInstance();

    public static void onInfo(Location loc, Player player, LangManager lm) {
        if (plugin.getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName())) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        String free, owned;
        List<String> ownedList = plugin.getLangManager().getStringList("Commands.Info.owned");
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = ownedList.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append("\n");
        }
        owned = sb.toString();


        List<String> freeList = plugin.getLangManager().getStringList("Commands.Info.free");
        StringBuilder sb2 = new StringBuilder();
        Iterator<String> it2 = freeList.iterator();
        while (it2.hasNext()) {
            sb2.append(it2.next());
            if (it2.hasNext())
                sb2.append("\n");
        }
        free = sb2.toString();
        Chunk chunk = loc.getWorld().getChunkAt(loc);

        OwnedLand land = plugin.getWgHandler().getRegion(chunk);

        new BukkitRunnable() {

            @Override
            public void run() {
                // claimed
                if (land != null) {
                    String lastseen;
                    OfflinePlayer op = Bukkit.getOfflinePlayer(land.getOwner());
                    if (op.isOnline()) {
                        lastseen = lm.getRawString("Commands.Info.online");
                    } else {
                        List<Object> list = plugin.getDatabaseAPI().retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("uuid", op.getUniqueId().toString()).create());
                        lastseen = ((LPlayer) list.get(0)).getLastSeenAsString();
                    }
                    player.sendMessage(owned
                            .replace("%landid%", land.getName())
                            .replace("%owner%", land.printOwners())
                            .replace("%member%", land.printMembers().isEmpty() ? "-" : land.printMembers())
                            .replace("%lastseen%", lastseen));
                    OwnedLand.highlightLand(player, CParticle.DRIPWATER);

                    Offers offer = plugin.getPlayerManager().getOffer(land.getName());
                    if (offer != null) {
                        player.sendMessage(lm.getString("Commands.Info.advertise")
                                .replace("%price%", offer.getPrice() + ""));
                    }
                } else {
                    // unclaimed
                    player.sendMessage(free
                            .replace("%landid%", OwnedLand.getName(chunk))
                            .replace("%price%", (plugin.isVaultEnabled()? plugin.getVaultHandler().format(OwnedLand.calculateCost(player)) : "-1")));
                    OwnedLand.highlightLand(player, CParticle.DRIPLAVA);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
