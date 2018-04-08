package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.persistent.Offers;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.crossversion.CParticle;
import biz.princeps.lib.storage.requests.Conditions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/07/17
 */
public class Info extends LandlordCommand {

    private String free, owned, advertised, inactive;

    public Info() {
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
        sb = new StringBuilder();
        it = freeList.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append("\n");
        }
        free = sb.toString();

        List<String> advertisedList = plugin.getLangManager().getStringList("Commands.Info.advertised");
        sb = new StringBuilder();
        it = advertisedList.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append("\n");
        }
        advertised = sb.toString();

        List<String> inactiveList = plugin.getLangManager().getStringList("Commands.Info.inactive");
        sb = new StringBuilder();
        it = inactiveList.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append("\n");
        }
        inactive = sb.toString();
    }

    public void onInfo(Player player) {

        if (plugin.getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName())) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        OwnedLand land = plugin.getWgHandler().getRegion(chunk);

        new BukkitRunnable() {

            @Override
            public void run() {
                // claimed
                if (land != null) {
                    String lastseen;
                    LocalDateTime lastSeenDate = null;
                    OfflinePlayer op = Bukkit.getOfflinePlayer(land.getOwner());
                    if (op.isOnline()) {
                        lastseen = lm.getRawString("Commands.Info.online");
                    } else {
                        List<Object> list = plugin.getDatabaseAPI().retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("uuid", op.getUniqueId().toString()).create());
                        if (list.size() > 0) {
                            lastseen = ((LPlayer) list.get(0)).getLastSeenAsString();
                            lastSeenDate = ((LPlayer) list.get(0)).getLastSeen();
                        } else {
                            lastseen = lm.getRawString("Commands.Info.noLastSeen");
                        }
                    }

                    if (plugin.getPlayerManager().isInactive(lastSeenDate)) {
                        player.sendMessage(replaceInMessage(inactive, land.getName(), land.printOwners(), land.printMembers(), lastseen,
                                plugin.getVaultHandler().format(plugin.getCostManager().calculateCost(player.getUniqueId()))));
                        OwnedLand.highlightLand(player, CParticle.DRIPLAVA);
                        return;
                    }

                    Offers offer = plugin.getPlayerManager().getOffer(land.getName());
                    if (offer != null) {
                        // advertised land
                        player.sendMessage(replaceInMessage(advertised, land.getName(), land.printOwners(), land.printMembers(), lastseen,
                                plugin.getVaultHandler().format(offer.getPrice())));
                    } else {
                        // normal owned land
                        player.sendMessage(replaceInMessage(owned, land.getName(), land.printOwners(), land.printMembers(), lastseen, ""));
                    }
                    OwnedLand.highlightLand(player, CParticle.DRIPWATER);

                } else {
                    // unclaimed
                    player.sendMessage(replaceInMessage(free, OwnedLand.getName(chunk), "", "", "",
                            plugin.getVaultHandler().format(plugin.getCostManager().calculateCost(player.getUniqueId()))));
                    OwnedLand.highlightLand(player, CParticle.DRIPLAVA);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private String replaceInMessage(String original, String landID, String owner, String member, String lastseen, String price) {
        return original.replace("%landid%", landID)
                .replace("%owner%", owner)
                .replace("%member%", member.isEmpty() ? "-" : member)
                .replace("%lastseen%", lastseen)
                .replace("%price%", price);
    }
}
