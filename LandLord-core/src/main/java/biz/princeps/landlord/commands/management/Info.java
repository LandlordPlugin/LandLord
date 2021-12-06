package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
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

    private final String free;
    private final String owned;
    private final String advertised;
    private final String inactive;
    private final IWorldGuardManager wg;

    public Info(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Info.name"),
                plugin.getConfig().getString("CommandSettings.Info.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Info.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Info.aliases")));
        this.wg = plugin.getWGManager();

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


    private String replaceInMessage(String original, String landID, String owner, String member, String lastseen,
                                    String price) {
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
        if (isDisabledWorld(player)) {
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        IOwnedLand land = wg.getRegion(chunk);

        if (land != null) {
            plugin.getPlayerManager().getOffline(land.getOwner(), (owner) -> {
                String lastseen, owners = land.getOwnersString(), friends = land.getMembersString();
                LocalDateTime lastSeenDate = null;
                OfflinePlayer op = plugin.getServer().getOfflinePlayer(land.getOwner());
                if (op.isOnline()) {
                    lastseen = lm.getRawString("Commands.Info.online");
                } else {
                    if (owner != null) {
                        lastseen = owner.getLastSeen().toString();
                        lastSeenDate = owner.getLastSeen();
                    } else {
                        lastseen = lm.getRawString("Commands.Info.noLastSeen");
                    }
                }

                if (plugin.getPlayerManager().isInactive(lastSeenDate)) {
                    lm.sendMessage(player, replaceInMessage(inactive, land.getName(), owners, friends, lastseen,
                            plugin.getVaultManager().format(plugin.getCostManager().calculateCost(player.getUniqueId()))));
                    if (plugin.getConfig().getBoolean("Particles.info"))
                        land.highlightLand(player, Particle.valueOf(plugin.getConfig().getString("Particles.info" +
                                ".inactive").toUpperCase()));
                    return;
                }

                if (land.getPrice() != -1) {
                    // advertised land
                    lm.sendMessage(player, replaceInMessage(advertised, land.getName(), owners, friends, lastseen,
                            plugin.getVaultManager().format(land.getPrice())));
                } else {
                    // normal owned land
                    lm.sendMessage(player, replaceInMessage(owned, land.getName(), owners, friends, lastseen, ""));
                }
                if (plugin.getConfig().getBoolean("Particles.info")) {
                    land.highlightLand(player,
                            Particle.valueOf(plugin.getConfig().getString("Particles.info.claimed").toUpperCase()));
                }
            });
        } else {
            // unclaimed
            if (!plugin.getConfig().getBoolean("CommandSettings.Claim.allowOverlap", false) &&
                    !wg.canClaim(player, chunk)) {
                lm.sendMessage(player, lm.getString(player, "Commands.Claim.notAllowed"));
                return;
            } else {
                lm.sendMessage(player, replaceInMessage(free, wg.getLandName(chunk), "", "", "",
                        (Options.isVaultEnabled() ? plugin.getVaultManager().format(
                                plugin.getCostManager().calculateCost(player.getUniqueId())) : "")));
            }

            if (plugin.getConfig().getBoolean("Particles.info")) {
                wg.highlightLand(chunk, player,
                        Particle.valueOf(plugin.getConfig().getString("Particles.info.unclaimed").toUpperCase()), 4, false);
            }
        }
    }
}
