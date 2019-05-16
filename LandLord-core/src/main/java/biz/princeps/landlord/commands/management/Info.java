package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.ALandLord;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOffer;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.persistent.Offer;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import co.aikar.taskchain.TaskChain;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

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

    public Info(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Info.name"),
                pl.getConfig().getString("CommandSettings.Info.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Info.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Info.aliases")));

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


    private String replaceInMessage(String original, String landID, String owner, String member, String lastseen, String price) {
        return original.replace("%landid%", landID)
                .replace("%owner%", owner)
                .replace("%member%", member.isEmpty() ? "-" : member)
                .replace("%lastseen%", lastseen)
                .replace("%price%", price);
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();
        if (isDisabledWorld(player)) return;

        Chunk chunk = player.getLocation().getChunk();
        IOwnedLand land = plugin.getWGProxy().getRegion(chunk);

        TaskChain<?> chain = ((ALandLord) plugin).newChain();
        chain.asyncFirst(() -> chain.setTaskData("lp", land != null ? plugin.getPlayerManager().getOfflinePlayerSync(land.getOwner()) : null))
                .sync(() -> {
                    // claimed
                    if (land != null) {
                        String lastseen, owners = land.getOwnersString(), friends = land.getMembersString();
                        LocalDateTime lastSeenDate = null;
                        OfflinePlayer op = Bukkit.getOfflinePlayer(land.getOwner());
                        if (op.isOnline()) {
                            lastseen = lm.getRawString("Commands.Info.online");
                        } else {
                            LPlayer lp = chain.getTaskData("lp");
                            if (lp != null) {
                                lastseen = lp.getLastSeen().toString();
                                lastSeenDate = lp.getLastSeen();
                            } else {
                                lastseen = lm.getRawString("Commands.Info.noLastSeen");
                            }
                        }

                        if (plugin.getPlayerManager().isInactive(lastSeenDate)) {
                            lm.sendMessage(player, replaceInMessage(inactive, land.getName(), owners, friends, lastseen,
                                    plugin.getVaultManager().format(plugin.getCostManager().calculateCost(player.getUniqueId()))));
                            if (plugin.getConfig().getBoolean("Particles.info"))
                                land.highlightLand(player, Particle.valueOf(plugin.getConfig().getString("Particles.info.inactive").toUpperCase()));
                            return;
                        }

                        IOffer offer = plugin.getOfferManager().getOffer(land.getName());
                        if (offer != null) {
                            // advertised land
                            lm.sendMessage(player, replaceInMessage(advertised, land.getName(), owners, friends, lastseen,
                                    plugin.getVaultManager().format(offer.getPrice())));
                        } else {
                            // normal owned land
                            lm.sendMessage(player, replaceInMessage(owned, land.getName(), owners, friends, lastseen, ""));
                        }
                        if (plugin.getConfig().getBoolean("Particles.info"))
                            land.highlightLand(player,
                                    Particle.valueOf(plugin.getConfig().getString("Particles.info.claimed").toUpperCase()));

                    } else {
                        // unclaimed
                        lm.sendMessage(player, replaceInMessage(free, plugin.getWGProxy().getLandName(chunk), "", "", "",
                                (Options.isVaultEnabled() ? plugin.getVaultManager().format(
                                        plugin.getCostManager().calculateCost(player.getUniqueId())) : "")));
                        if (plugin.getConfig().getBoolean("Particles.info"))
                            plugin.getWGProxy().highlightLand(chunk, player,
                                    Particle.valueOf(plugin.getConfig().getString("Particles.info.unclaimed").toUpperCase()), 4);
                    }


                });
        chain.execute();

    }
}
