package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.api.events.LandPostClaimEvent;
import biz.princeps.landlord.api.events.LandPreClaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offer;
import biz.princeps.landlord.util.Delimitation;
import biz.princeps.landlord.util.Util;
import biz.princeps.lib.PrincepsLib;
import co.aikar.taskchain.TaskChain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/07/17
 */
public class Claim extends LandlordCommand {

    private boolean overrideConfirmations;

    public Claim(boolean overrideConfirmations) {
        this.overrideConfirmations = overrideConfirmations;
    }

    public void onClaim(Player player, Chunk chunk) {

        if (this.worldDisabled(player)) {
            lm.sendMessage(player, lm.getString("Disabled-World"));
            return;
        }
        IOwnedLand ol = plugin.getWgproxy().getRegion(chunk);
        String landname = ol.getName();
        String confirmcmd = "/" + plugin.getConfig().getString("CommandSettings.Main.name") + " confirm";

        TaskChain<?> chain = Landlord.newChain();

        chain.asyncFirst(() -> {
            if (ol == null) {
                chain.setTaskData("inactive", false);
                chain.setTaskData("inactiveDays", -1);
            } else {
                chain.setTaskData("inactive", plugin.getPlayerManager().isInactive(ol.getOwner()));
                chain.setTaskData("inactiveDays", plugin.getPlayerManager().getInactiveRemainingDays(ol.getOwner()));
            }
            return null;
        }).sync(() -> {

            boolean inactive = chain.getTaskData("inactive");
            int inactiveDays = chain.getTaskData("inactiveDays");

            // Check if there is an overlapping wg-region
            if (!plugin.getWgproxy().canClaim(player, chunk)) {
                if (ol == null || (plugin.getOfferManager().getOffer(landname) == null && !inactive)) {
                    lm.sendMessage(player, lm.getString("Commands.Claim.notAllowed"));
                    return;
                }
            }
            // Checks for the case if its not a nullland, but its not buyable
            if (ol != null) {
                if (ol.isOwner(player.getUniqueId())) {
                    // cannot buy own land
                    lm.sendMessage(player, lm.getString("Commands.Claim.alreadyClaimed")
                            .replace("%owner%", ol.getOwnersString()));
                    return;
                }

                Offer offer = plugin.getOfferManager().getOffer(ol.getName());
                if (!plugin.getPlayerManager().isInactive(ol.getOwner()) && offer == null) {
                    lm.sendMessage(player, lm.getString("Commands.Claim.notYetInactive")
                            .replace("%owner%", ol.getOwnersString())
                            .replace("%days%", "" + inactiveDays));
                    return;
                }
            }

            int regionCount = plugin.getWgproxy().getRegionCountOfPlayer(player.getUniqueId());
            // Ckeck for hardcap based on permissions
            if (!hasLimitPermissions(player, regionCount)) {
                return;
            }

            // Check for claims
            if (!hasClaims(player, regionCount)) {
                return;
            }

            // Ckeck for adjacent claims
            if (!isAdjacentLandOwned(player, chunk, regionCount)) {
                return;
            }

            // Check for gap between lands
            if (!isGapBetweenLands(player, chunk)) {
                return;
            }

            LandPreClaimEvent event = new LandPreClaimEvent(player, chunk);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                // Money stuff
                if (Options.isVaultEnabled()) {
                    if (ol != null && inactive) {
                        // Inactive sale
                        double costForBuyer = plugin.getCostManager().calculateCost(player.getUniqueId());
                        double payBackForInactive = plugin.getCostManager().calculateCost(ol.getOwner());
                        String originalOwner = Bukkit.getOfflinePlayer(ol.getOwner()).getName();

                        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), costForBuyer)) {

                            if (plugin.getConfig().getBoolean("ConfirmationDialog.onBuyUp")) {
                                String sellDesc = ol.getName() + " | " + plugin.getVaultHandler().format(costForBuyer);
                                String chatDesc = lm.getString("Commands.Claim.confirmation");

                                PrincepsLib.getConfirmationManager().draw(player, sellDesc, chatDesc,
                                        (p) -> {
                                            handleInactiveSell(player, ol, costForBuyer, payBackForInactive, originalOwner, chunk);
                                            player.closeInventory();
                                        }, (p) -> {
                                            lm.sendMessage(player, lm.getString("Commands.Claim.aborted"));
                                            player.closeInventory();
                                        }, confirmcmd);

                            } else {
                                handleInactiveSell(player, ol, costForBuyer, payBackForInactive, originalOwner, chunk);
                            }
                            return;
                        } else {
                            // Not enough money
                            lm.sendMessage(player, lm.getString("Commands.Claim.notEnoughMoney")
                                    .replace("%money%", plugin.getVaultHandler().format(costForBuyer))
                                    .replace("%chunk%", ol.getName())
                                    .replace("%location%", Util.getLocationFormatted(chunk))
                            );
                            return;
                        }
                    }

                    Offer offer = plugin.getOfferManager().getOffer(landname);
                    if (offer != null && ol != null) {
                        // Player 2 player sale
                        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), offer.getPrice())) {

                            String sellDesc = ol.getName() + " | " + plugin.getVaultHandler().format(offer.getPrice());
                            String chatDesc = lm.getString("Commands.Claim.confirmation");

                            PrincepsLib.getConfirmationManager().draw(player, sellDesc, chatDesc, (p) -> {
                                plugin.getVaultHandler().take(player.getUniqueId(), offer.getPrice());
                                plugin.getVaultHandler().give(offer.getSeller(), offer.getPrice());

                                plugin.getOfferManager().removeOffer(offer.getLandname());

                                ol.replaceOwner(player.getUniqueId());

                                lm.sendMessage(player, lm.getString("Commands.Claim.success")
                                        .replace("%chunk%", ol.getName())
                                        .replace("%location%", Util.getLocationFormatted(chunk))
                                        .replace("%world%", chunk.getWorld().getName()));

                                Player pp = Bukkit.getPlayer(offer.getSeller());
                                if (pp.isOnline()) {
                                    lm.sendMessage(pp, lm.getString("Commands.Claim.p2pSuccess")
                                            .replace("%player%", p.getName())
                                            .replace("%chunk%", ol.getName())
                                            .replace("%location%", Util.getLocationFormatted(chunk))
                                            .replace("%world%", chunk.getWorld().getName())
                                            .replace("%price%", plugin.getVaultHandler().format(offer.getPrice())));
                                }

                                ol.highlightLand(player, Particle.VILLAGER_HAPPY);
                                plugin.getMapManager().updateAll();

                                player.closeInventory();
                            }, (p) -> {
                                lm.sendMessage(player, lm.getString("Commands.Claim.aborted"));
                                player.closeInventory();
                            }, confirmcmd);

                        } else {
                            // Not enough money
                            lm.sendMessage(player, lm.getString("Commands.Claim.notEnoughMoney")
                                    .replace("%money%", plugin.getVaultHandler().format(offer.getPrice()))
                                    .replace("%chunk%", ol.getName())
                                    .replace("%location%", Util.getLocationFormatted(chunk))
                            );
                            return;
                        }
                    } else {
                        // Normal sale
                        double calculatedCost = plugin.getCostManager().calculateCost(player.getUniqueId());
                        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), calculatedCost)) {
                            String guiDesc = landname + " | " + plugin.getVaultHandler().format(calculatedCost);
                            String chatDesc = lm.getString("Commands.Claim.confirmation")
                                    .replace("%chunk%", landname)
                                    .replace("%location%", Util.getLocationFormatted(chunk))
                                    .replace("%price%", plugin.getVaultHandler().format(calculatedCost));

                            if (plugin.getConfig().getBoolean("ConfirmationDialog.onNormalClaim") && !overrideConfirmations) {
                                PrincepsLib.getConfirmationManager().draw(player, guiDesc, chatDesc,
                                        (p) -> {
                                            plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
                                            if (calculatedCost > 0)
                                                lm.sendMessage(player, lm.getString("Commands.Claim.moneyTook")
                                                        .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                                        .replace("%chunk%", ol.getName()));
                                            performClaim(player, chunk);
                                            p.closeInventory();
                                        },
                                        (p) -> {
                                            lm.sendMessage(player, lm.getString("Commands.Claim.aborted"));
                                            p.closeInventory();
                                        }, confirmcmd);
                            } else {
                                plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
                                if (calculatedCost > 0)
                                    lm.sendMessage(player, lm.getString("Commands.Claim.moneyTook")
                                            .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                            .replace("%chunk%", ol.getName())
                                            .replace("%location%", Util.getLocationFormatted(chunk))
                                    );
                                performClaim(player, chunk);
                            }

                            return;
                        } else {
                            // NOT ENOUGH MONEY
                            lm.sendMessage(player, lm.getString("Commands.Claim.notEnoughMoney")
                                    .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                    .replace("%chunk%", ol.getName())
                                    .replace("%location%", Util.getLocationFormatted(chunk))
                            );
                            return;
                        }
                    }
                }
                performClaim(player, chunk);
            }
        }).execute();
    }

    private void handleInactiveSell(Player player, IOwnedLand ol, double costForBuyer, double payBackForInactive,
                                    String originalOwner, Chunk chunk){
        plugin.getVaultHandler().take(player.getUniqueId(), costForBuyer);
        plugin.getVaultHandler().give(ol.getOwner(), payBackForInactive);

        ol.replaceOwner(player.getUniqueId());
        lm.sendMessage(player, lm.getString("Commands.Claim.boughtUp")
                .replace("%player%", originalOwner)
                .replace("%price%", plugin.getVaultHandler().format(costForBuyer))
                .replace("%chunk%", ol.getName())
                .replace("%location%", Util.getLocationFormatted(chunk))
        );

        ol.highlightLand(player, Particle.VILLAGER_HAPPY);
        plugin.getMapManager().updateAll();
    }

    private void performClaim(Player player, Chunk chunk) {
        IOwnedLand claim = plugin.getWgproxy().claim(chunk, player.getUniqueId());

        lm.sendMessage(player, lm.getString("Commands.Claim.success")
                .replace("%chunk%", claim.getName())
                .replace("%location%", Util.getLocationFormatted(chunk))
                .replace("%world%", chunk.getWorld().getName()));

        if (plugin.getConfig().getBoolean("Particles.claim.enabled"))
            claim.highlightLand(player,
                    Particle.valueOf(plugin.getConfig().getString("Particles.claim.particle").toUpperCase()));

        if (Options.enabled_homes() && plugin.getConfig().getBoolean("Homes.enableAutoSetHome", false)) {
            if (plugin.getPlayerManager().get(player.getUniqueId()).getHome() == null) {
                Bukkit.dispatchCommand(player, "ll sethome");
            }
        }

        if (plugin.getConfig().getBoolean("CommandSettings.Claim.enableDelimit")) {
            Delimitation.delimit(player, chunk);
        }

        plugin.getMapManager().updateAll();

        LandPostClaimEvent postEvent = new LandPostClaimEvent(player, claim);
        Bukkit.getPluginManager().callEvent(postEvent);
    }

    private boolean hasLimitPermissions(Player player, int regionCount) {
        List<Integer> limitlist = plugin.getConfig().getIntegerList("limits");

        if (!player.hasPermission("landlord.limit.override")) {
            // We need to find out, whats the maximum limit.x permission is a player has

            int highestAllowedLandCount = -1;
            for (Integer integer : limitlist) {
                if (player.hasPermission("landlord.limit." + integer)) {
                    highestAllowedLandCount = integer;
                }
            }

            if (regionCount >= highestAllowedLandCount) {
                lm.sendMessage(player, lm.getString("Commands.Claim.hardcap").replace("%regions%", highestAllowedLandCount + ""));
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    private boolean hasClaims(Player player, int regionCount) {
        if (plugin.getConfig().getBoolean("Shop.enable") && Options.isVaultEnabled()) {
            int claims = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();

            if (regionCount >= claims) {
                ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Claim.limit")
                        .replace("%regions%", regionCount + "")
                        .replace("%claims%", claims + ""))
                        .color(ChatColor.YELLOW)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll shop"));

                plugin.getUtilsProxy().send_basecomponent(player , builder.create());
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean isAdjacentLandOwned(Player player, Chunk chunk, int amountOfOwnedLands) {
        if (plugin.getConfig().getBoolean("CommandSettings.Claim.claimOnlyAdjacent")) {
            // Only allow claiming of adjacent chunks
            if (amountOfOwnedLands > 0) {
                // Get adjacent lands of the land, which a player wants to claim.
                // Only when one of the 4 adjacent is already owned, allow to claim
                boolean hasNearbyLand = false;
                for (IOwnedLand adjLand : getSurroundings(chunk)) {
                    if (adjLand != null) {
                        if (adjLand.isOwner(player.getUniqueId())) {
                            hasNearbyLand = true;
                            break;
                        }
                    }
                }

                if (!hasNearbyLand) {
                    // no nearby land is already claimed => Display error msg
                    lm.sendMessage(player, lm.getString("Commands.Claim.onlyClaimAdjacentChunks")
                            .replace("%land%", plugin.getWgproxy().getLandName(chunk)));
                    return false;
                }
            }
        }
        return true;
    }

    private IOwnedLand[] getSurroundings(Chunk chunk){
        World world = chunk.getWorld();
        IOwnedLand[] adjLands = new IOwnedLand[4];
        adjLands[0] = plugin.getWgproxy().getRegion(world.getChunkAt(chunk.getX() + 1, chunk.getZ()));
        adjLands[1] = plugin.getWgproxy().getRegion(world.getChunkAt(chunk.getX() - 1, chunk.getZ()));
        adjLands[2] = plugin.getWgproxy().getRegion(world.getChunkAt(chunk.getX(), chunk.getZ() + 1));
        adjLands[3] = plugin.getWgproxy().getRegion(world.getChunkAt(chunk.getX(), chunk.getZ() - 1));
        return adjLands;
    }

    private boolean isGapBetweenLands(Player player, Chunk chunk) {
        if (plugin.getConfig().getBoolean("CommandSettings.Claim.needsGapBetweenOwners")) {
            // Get adjacent lands of the land, which a player wants to claim.
            // Only when all of the 4 adj lands are either owned by the player or are free => allow the claim
            boolean differentOwner = false;
            for (IOwnedLand adjLand : getSurroundings(chunk)) {
                if (adjLand != null) {
                    if (!adjLand.isOwner(player.getUniqueId())) {
                        differentOwner = true;
                        break;
                    }
                }
            }

            if (differentOwner) {
                // one of the nearby lands is not owned by the player nor its free
                lm.sendMessage(player, lm.getString("Commands.Claim.needsGap")
                        .replace("%land%", plugin.getWgproxy().getLandName(chunk)));
                return false;
            }
        }
        return true;
    }
}

