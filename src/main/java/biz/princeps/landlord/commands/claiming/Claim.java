package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.api.events.LandPostClaimEvent;
import biz.princeps.landlord.api.events.LandPreClaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offer;
import biz.princeps.landlord.util.Delimitation;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.item.DataStack;
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
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        OwnedLand pr = plugin.getWgHandler().getRegion(chunk);
        String landname = chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
        String confirmcmd = "/" + plugin.getConfig().getString("CommandSettings.Main.name") + " confirm";

        TaskChain<?> chain = Landlord.newChain();

        chain.asyncFirst(() -> {
            if (pr == null) {
                chain.setTaskData("inactive", false);
                chain.setTaskData("inactiveDays", -1);
            } else {
                chain.setTaskData("inactive", plugin.getPlayerManager().isInactive(pr.getOwner()));
                chain.setTaskData("inactiveDays", plugin.getPlayerManager().getInactiveRemainingDays(pr.getOwner()));
            }
            return null;
        }).sync(() -> {

            boolean inactive = chain.getTaskData("inactive");
            int inactiveDays = chain.getTaskData("inactiveDays");

            // Check if there is an overlapping wg-region
            if (!plugin.getWgHandler().canClaim(player, chunk)) {
                if (pr == null || (plugin.getOfferManager().getOffer(landname) == null && !inactive)) {
                    player.sendMessage(lm.getString("Commands.Claim.notAllowed"));
                    return;
                }
            }
            // Checks for the case if its not a nullland, but its not buyable
            if (pr != null) {
                if (pr.getOwner().equals(player.getUniqueId())) {
                    // cannot buy own land
                    player.sendMessage(lm.getString("Commands.Claim.alreadyClaimed")
                            .replace("%owner%", pr.printOwners()));
                    return;
                }

                Offer offer = plugin.getOfferManager().getOffer(pr.getName());
                if (!plugin.getPlayerManager().isInactive(pr.getOwner()) && offer == null) {
                    player.sendMessage(lm.getString("Commands.Claim.notYetInactive")
                            .replace("%owner%", pr.printOwners())
                            .replace("%days%", "" + inactiveDays));
                    return;
                }
            }

            int regionCount = plugin.getWgHandler().getRegionCountOfPlayer(player.getUniqueId());
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
                    if (pr != null && inactive) {
                        // Inactive sale
                        double costForBuyer = plugin.getCostManager().calculateCost(player.getUniqueId());
                        double payBackForInactive = plugin.getCostManager().calculateCost(pr.getOwner());
                        String originalOwner = Bukkit.getOfflinePlayer(pr.getOwner()).getName();

                        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), costForBuyer)) {

                            if (plugin.getConfig().getBoolean("ConfirmationDialog.onBuyUp")) {
                                String sellDesc = pr.getName() + " | " + plugin.getVaultHandler().format(costForBuyer);
                                String chatDesc = lm.getString("Commands.Claim.confirmation");

                                PrincepsLib.getConfirmationManager().draw(player, sellDesc, chatDesc,
                                        (p) -> {
                                            plugin.getVaultHandler().take(player.getUniqueId(), costForBuyer);
                                            plugin.getVaultHandler().give(pr.getOwner(), payBackForInactive);

                                            pr.getWGLand().getOwners().clear();
                                            pr.getWGLand().getOwners().addPlayer(player.getUniqueId());

                                            player.sendMessage(lm.getString("Commands.Claim.boughtUp")
                                                    .replace("%player%", originalOwner)
                                                    .replace("%price%", plugin.getVaultHandler().format(costForBuyer))
                                                    .replace("%chunk%", pr.getName()));

                                            OwnedLand.highlightLand(player, Particle.VILLAGER_HAPPY);
                                            plugin.getMapManager().updateAll();

                                            player.closeInventory();
                                        }, (p) -> {
                                            player.sendMessage(lm.getString("Commands.Claim.aborted"));
                                            player.closeInventory();
                                        }, confirmcmd);

                            } else {
                                plugin.getVaultHandler().take(player.getUniqueId(), costForBuyer);
                                plugin.getVaultHandler().give(pr.getOwner(), payBackForInactive);

                                pr.getWGLand().getOwners().clear();
                                pr.getWGLand().getOwners().addPlayer(player.getUniqueId());

                                player.sendMessage(lm.getString("Commands.Claim.boughtUp")
                                        .replace("%player%", originalOwner)
                                        .replace("%price%", plugin.getVaultHandler().format(costForBuyer))
                                        .replace("%chunk%", pr.getName()));

                                OwnedLand.highlightLand(player, Particle.VILLAGER_HAPPY);
                                plugin.getMapManager().updateAll();
                            }
                            return;
                        } else {
                            // Not enough money
                            player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                                    .replace("%money%", plugin.getVaultHandler().format(costForBuyer))
                                    .replace("%chunk%", OwnedLand.getName(chunk)));
                            return;
                        }
                    }

                    Offer offer = plugin.getOfferManager().getOffer(landname);
                    if (offer != null && pr != null) {
                        // Player 2 player sale
                        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), offer.getPrice())) {

                            String sellDesc = pr.getName() + " | " + plugin.getVaultHandler().format(offer.getPrice());
                            String chatDesc = lm.getString("Commands.Claim.confirmation");

                            PrincepsLib.getConfirmationManager().draw(player, sellDesc, chatDesc, (p) -> {
                                plugin.getVaultHandler().take(player.getUniqueId(), offer.getPrice());
                                plugin.getVaultHandler().give(offer.getSeller(), offer.getPrice());

                                plugin.getOfferManager().removeOffer(offer.getLandname());

                                pr.getWGLand().getOwners().clear();
                                pr.getWGLand().getOwners().addPlayer(player.getUniqueId());

                                player.sendMessage(lm.getString("Commands.Claim.success")
                                        .replace("%chunk%", OwnedLand.getName(chunk))
                                        .replace("%world%", chunk.getWorld().getName()));

                                if (Bukkit.getPlayer(offer.getSeller()).isOnline()) {
                                    Bukkit.getPlayer(offer.getSeller()).sendMessage(lm.getString("Commands.Claim.p2pSuccess")
                                            .replace("%player%", p.getName())
                                            .replace("%chunk%", OwnedLand.getName(chunk))
                                            .replace("%world%", chunk.getWorld().getName())
                                            .replace("%price%", plugin.getVaultHandler().format(offer.getPrice())));
                                }

                                OwnedLand.highlightLand(player, Particle.VILLAGER_HAPPY);
                                plugin.getMapManager().updateAll();

                                player.closeInventory();
                            }, (p) -> {
                                player.sendMessage(lm.getString("Commands.Claim.aborted"));
                                player.closeInventory();
                            }, confirmcmd);

                        } else {
                            // Not enough money
                            player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                                    .replace("%money%", plugin.getVaultHandler().format(offer.getPrice()))
                                    .replace("%chunk%", OwnedLand.getName(chunk)));
                            return;
                        }
                    } else {
                        // Normal sale
                        double calculatedCost = plugin.getCostManager().calculateCost(player.getUniqueId());
                        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), calculatedCost)) {
                            String guiDesc = landname + " | " + plugin.getVaultHandler().format(calculatedCost);
                            String chatDesc = lm.getString("Commands.Claim.confirmation")
                                    .replace("%chunk%", landname)
                                    .replace("%price%", plugin.getVaultHandler().format(calculatedCost));

                            if (plugin.getConfig().getBoolean("ConfirmationDialog.onNormalClaim") && !overrideConfirmations) {
                                PrincepsLib.getConfirmationManager().draw(player, guiDesc, chatDesc,
                                        (p) -> {
                                            plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
                                            if (calculatedCost > 0)
                                                player.sendMessage(lm.getString("Commands.Claim.moneyTook")
                                                        .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                                        .replace("%chunk%", OwnedLand.getName(chunk)));
                                            performClaim(player, chunk);
                                            p.closeInventory();
                                        },
                                        (p) -> {
                                            player.sendMessage(lm.getString("Commands.Claim.aborted"));
                                            p.closeInventory();
                                        }, confirmcmd);
                            } else {
                                plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
                                if (calculatedCost > 0)
                                    player.sendMessage(lm.getString("Commands.Claim.moneyTook")
                                            .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                            .replace("%chunk%", OwnedLand.getName(chunk)));
                                performClaim(player, chunk);
                            }

                            return;
                        } else {
                            // NOT ENOUGH MONEY
                            player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                                    .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                    .replace("%chunk%", OwnedLand.getName(chunk)));
                            return;
                        }
                    }
                }
                performClaim(player, chunk);
            }
        }).execute();
    }

    private void performClaim(Player player, Chunk chunk) {
        plugin.getWgHandler().claim(chunk, player.getUniqueId());

        player.sendMessage(lm.getString("Commands.Claim.success")
                .replace("%chunk%", OwnedLand.getName(chunk))
                .replace("%world%", chunk.getWorld().getName()));

        if (plugin.getConfig().getBoolean("Particles.claim"))
            OwnedLand.highlightLand(player, Particle.VILLAGER_HAPPY);

        if (plugin.getConfig().getBoolean("Homes.enable")) {
            if (plugin.getPlayerManager().get(player.getUniqueId()).getHome() == null)
                Bukkit.dispatchCommand(player, "ll sethome");
        }

        if (plugin.getConfig().getBoolean("CommandSettings.Claim.enableDelimit")) {
            Delimitation.delimit(chunk);
        }

        plugin.getMapManager().updateAll();

        LandPostClaimEvent postEvent = new LandPostClaimEvent(player, plugin.getLand(chunk));
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
                player.sendMessage(lm.getString("Commands.Claim.hardcap").replace("%regions%", highestAllowedLandCount + ""));
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

                player.spigot().sendMessage(builder.create());
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
                World world = player.getWorld();
                OwnedLand[] adjLands = new OwnedLand[4];
                adjLands[0] = plugin.getLand(world.getChunkAt(chunk.getX() + 1, chunk.getZ()));
                adjLands[1] = plugin.getLand(world.getChunkAt(chunk.getX() - 1, chunk.getZ()));
                adjLands[2] = plugin.getLand(world.getChunkAt(chunk.getX(), chunk.getZ() + 1));
                adjLands[3] = plugin.getLand(world.getChunkAt(chunk.getX(), chunk.getZ() - 1));

                boolean hasNearbyLand = false;
                for (OwnedLand adjLand : adjLands) {
                    if (adjLand != null) {
                        if (adjLand.isOwner(player.getUniqueId())) {
                            hasNearbyLand = true;
                            break;
                        }
                    }
                }

                if (!hasNearbyLand) {
                    // no nearby land is already claimed => Display error msg
                    player.sendMessage(lm.getString("Commands.Claim.onlyClaimAdjacentChunks").replace("%land%", OwnedLand.getName(chunk)));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isGapBetweenLands(Player player, Chunk chunk) {
        if (plugin.getConfig().getBoolean("CommandSettings.Claim.needsGapBetweenOwners")) {
            // Get adjacent lands of the land, which a player wants to claim.
            // Only when all of the 4 adj lands are either owned by the player or are free => allow the claim
            World world = player.getWorld();
            OwnedLand[] adjLands = new OwnedLand[4];
            adjLands[0] = plugin.getLand(world.getChunkAt(chunk.getX() + 1, chunk.getZ()));
            adjLands[1] = plugin.getLand(world.getChunkAt(chunk.getX() - 1, chunk.getZ()));
            adjLands[2] = plugin.getLand(world.getChunkAt(chunk.getX(), chunk.getZ() + 1));
            adjLands[3] = plugin.getLand(world.getChunkAt(chunk.getX(), chunk.getZ() - 1));

            boolean differentOwner = false;
            for (OwnedLand adjLand : adjLands) {
                if (adjLand != null) {
                    if (!adjLand.isOwner(player.getUniqueId())) {
                        differentOwner = true;
                        break;
                    }
                }
            }

            if (differentOwner) {
                // one of the nearby lands is not owned by the player nor its free
                player.sendMessage(lm.getString("Commands.Claim.needsGap").replace("%land%", OwnedLand.getName(chunk)));
                return false;
            }
        }
        return true;
    }

    private void delimit(Chunk chunk) {
        DataStack s = new DataStack(plugin.getConfig().getString("CommandSettings.Claim.delimitMaterial"));
        World w = chunk.getWorld();

        if (s.getMaterial() != null) {
            for (int i = 0; i < 16; i++) {
                s.place(w, w.getHighestBlockAt(chunk.getBlock(i, 0, 0).getLocation().add(0, 1, 0)).getLocation());
                s.place(w, w.getHighestBlockAt(chunk.getBlock(0, 0, i).getLocation().add(0, 1, 0)).getLocation());
                s.place(w, w.getHighestBlockAt(chunk.getBlock(15 - i, 0, 15).getLocation().add(0, 1, 0)).getLocation());
                s.place(w, w.getHighestBlockAt(chunk.getBlock(15, 0, 15 - i).getLocation().add(0, 1, 0)).getLocation());
            }
        }
    }
}

