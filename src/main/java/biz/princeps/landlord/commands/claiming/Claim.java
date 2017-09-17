package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.events.LandClaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offers;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.crossversion.CParticle;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.simple.ClickAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by spatium on 16.07.17.
 */
public class Claim extends LandlordCommand {

    public void onClaim(Player player) {

        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
        OwnedLand pr = plugin.getWgHandler().getRegion(chunk);
        String landname = chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();

        // Check if there is an overlapping wg-region
        if (!plugin.getWgHandler().canClaim(player, chunk)) {
            if (plugin.getPlayerManager().getOffer(landname) == null) {
                LandClaimEvent event = new LandClaimEvent(player, player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), LandClaimEvent.ClaimState.OVERLAPPINGREGION);
                plugin.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    player.sendMessage(lm.getString("Commands.Claim.notAllowed"));
                    return;
                }
            }
        }

        if (pr != null) {
            Offers offer = plugin.getPlayerManager().getOffer(pr.getLandName());
            if (offer == null || pr.getOwner().equals(player.getUniqueId())) {
                LandClaimEvent event = new LandClaimEvent(player, player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), LandClaimEvent.ClaimState.ALREADYCLAIMED);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    player.sendMessage(lm.getString("Commands.Claim.alreadyClaimed")
                            .replace("%owner%", pr.printOwners()));
                    return;
                }
            }
        }


        int regionCount = plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(plugin.getWgHandler().getWG().wrapPlayer(player));
        List<Integer> limitlist = plugin.getConfig().getIntegerList("limits");

        if (!player.hasPermission("landlord.limit.override")) {

            int highestAllowedLandCount = -1;
            for (Integer integer : limitlist) {
                if (regionCount <= integer)
                    if (player.hasPermission("landlord.limit." + integer)) {
                        highestAllowedLandCount = integer;
                    }
            }

            if (regionCount >= highestAllowedLandCount) {
                player.sendMessage(lm.getString("Commands.Claim.hardcap").replace("%regions%", highestAllowedLandCount + ""));
                return;
            }
        }

        if (plugin.getConfig().getBoolean("Shop.enable") && plugin.isVaultEnabled()) {
            int claims = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();

            if (regionCount >= claims) {
                ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Claim.limit")
                        .replace("%regions%", regionCount + "")
                        .replace("%claims%", claims + ""))
                        .color(ChatColor.YELLOW)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll shop"));
                player.spigot().sendMessage(builder.create());
                return;
            }
        }

        boolean flag = false;
        // Money stuff
        if (plugin.isVaultEnabled()) {
            Offers offer = plugin.getPlayerManager().getOffer(landname);
            if (offer != null) {
                // Player 2 player sale
                if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), offer.getPrice())) {

                    ConfirmationGUI confirm = new ConfirmationGUI(player, pr.getLandName(), player1 -> {
                        plugin.getVaultHandler().take(player.getUniqueId(), offer.getPrice());
                        plugin.getVaultHandler().give(offer.getSeller(), offer.getPrice());

                        plugin.getPlayerManager().removeOffer(offer.getLandname());

                        pr.getLand().getOwners().clear();
                        pr.getLand().getOwners().addPlayer(player.getUniqueId());

                        player.sendMessage(lm.getString("Commands.Claim.success")
                                .replace("%chunk%", OwnedLand.getLandName(chunk))
                                .replace("%world%", chunk.getWorld().getName()));

                        OwnedLand.highlightLand(player, CParticle.VILLAGERHAPPY);
                        plugin.getMapManager().updateAll();

                        player.closeInventory();
                    }, player12 -> {
                        player.sendMessage(lm.getString("Commands.Claim.aborted"));
                        player.closeInventory();
                        return;
                    }, null);
                    confirm.display();

                } else {
                    // Not enough money
                    player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                            .replace("%money%", plugin.getVaultHandler().format(offer.getPrice()))
                            .replace("%chunk%", OwnedLand.getLandName(chunk)));
                    return;
                }
            } else {
                // Normal sale
                flag = true;
                double calculatedCost = OwnedLand.calculateCost(player);
                if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), calculatedCost)) {
                    plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
                    if (calculatedCost > 0)
                        player.sendMessage(lm.getString("Commands.Claim.moneyTook")
                                .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                .replace("%chunk%", OwnedLand.getLandName(chunk)));

                } else {
                    // NOT ENOUGH MONEY
                    LandClaimEvent event = new LandClaimEvent(player, player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), LandClaimEvent.ClaimState.NOTENOUGHMONEY);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                                .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                .replace("%chunk%", OwnedLand.getLandName(chunk)));
                        return;
                    }
                }
            }
        }
        if (flag) {
            plugin.getWgHandler().claim(chunk, player.getUniqueId());
            player.sendMessage(lm.getString("Commands.Claim.success")
                    .replace("%chunk%", OwnedLand.getLandName(chunk))
                    .replace("%world%", chunk.getWorld().getName()));

            OwnedLand.highlightLand(player, CParticle.VILLAGERHAPPY);

            if (plugin.getConfig().getBoolean("Homes.enable")) {
                if (plugin.getPlayerManager().get(player.getUniqueId()).getHome() == null)
                    Bukkit.dispatchCommand(player, "ll sethome");
            }


            plugin.getMapManager().updateAll();
        }
    }


}

