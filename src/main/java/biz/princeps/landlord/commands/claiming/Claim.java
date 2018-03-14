package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.events.LandPostClaimEvent;
import biz.princeps.landlord.api.events.LandPreClaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offers;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.crossversion.CParticle;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.item.DataStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
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
                player.sendMessage(lm.getString("Commands.Claim.notAllowed"));
                return;
            }
        }

        if (pr != null) {
            Offers offer = plugin.getPlayerManager().getOffer(pr.getName());
            if (offer == null || pr.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(lm.getString("Commands.Claim.alreadyClaimed")
                        .replace("%owner%", pr.printOwners()));
                return;
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

        LandPreClaimEvent event = new LandPreClaimEvent(player, chunk);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (plugin.getConfig().getBoolean("CommandSettings.Claim.claimOnlyAdjacent")) {
                // Only allow claiming of adjacent chunks
                int amountOfOwnedLands = plugin.getWgHandler().getRegionCountOfPlayer(player.getUniqueId());

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
                        return;
                    }
                }
            }

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
                    player.sendMessage(lm.getString("Commands.Claim.needsGap"). replace("%land%", OwnedLand.getName(chunk)));
                    return;
                }
            }


            boolean moneyFlag = false;
            // Money stuff
            if (plugin.isVaultEnabled()) {
                Offers offer = plugin.getPlayerManager().getOffer(landname);
                if (offer != null && pr != null) {
                    // Player 2 player sale
                    if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), offer.getPrice())) {

                        String sellDesc = pr.getName() + " | " + plugin.getVaultHandler().format(offer.getPrice());

                        ConfirmationGUI confirm = new ConfirmationGUI(player, sellDesc, (player1, icon) -> {
                            plugin.getVaultHandler().take(player.getUniqueId(), offer.getPrice());
                            plugin.getVaultHandler().give(offer.getSeller(), offer.getPrice());

                            plugin.getPlayerManager().removeOffer(offer.getLandname());

                            pr.getWGLand().getOwners().clear();
                            pr.getWGLand().getOwners().addPlayer(player.getUniqueId());

                            player.sendMessage(lm.getString("Commands.Claim.success")
                                    .replace("%chunk%", OwnedLand.getName(chunk))
                                    .replace("%world%", chunk.getWorld().getName()));

                            if (Bukkit.getPlayer(offer.getSeller()).isOnline()) {
                                Bukkit.getPlayer(offer.getSeller()).sendMessage(lm.getString("Commands.Claim.p2pSuccess")
                                        .replace("%player%", player1.getName())
                                        .replace("%chunk%", OwnedLand.getName(chunk))
                                        .replace("%world%", chunk.getWorld().getName())
                                        .replace("%price%", plugin.getVaultHandler().format(offer.getPrice())));
                            }


                            OwnedLand.highlightLand(player, CParticle.VILLAGERHAPPY);
                            plugin.getMapManager().updateAll();

                            player.closeInventory();
                        }, (player12, ic2) -> {
                            player.sendMessage(lm.getString("Commands.Claim.aborted"));
                            player.closeInventory();
                        }, null);
                        confirm.display();

                    } else {
                        // Not enough money
                        player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                                .replace("%money%", plugin.getVaultHandler().format(offer.getPrice()))
                                .replace("%chunk%", OwnedLand.getName(chunk)));
                        return;
                    }
                } else {
                    // Normal sale
                    moneyFlag = true;
                    double calculatedCost = OwnedLand.calculateCost(player);
                    if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), calculatedCost)) {
                        plugin.getVaultHandler().take(player.getUniqueId(), calculatedCost);
                        if (calculatedCost > 0)
                            player.sendMessage(lm.getString("Commands.Claim.moneyTook")
                                    .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                    .replace("%chunk%", OwnedLand.getName(chunk)));

                    } else {
                        // NOT ENOUGH MONEY
                        player.sendMessage(lm.getString("Commands.Claim.notEnoughMoney")
                                .replace("%money%", plugin.getVaultHandler().format(calculatedCost))
                                .replace("%chunk%", OwnedLand.getName(chunk)));
                        return;
                    }
                }
            } else {
                // flag is always true, if eco is disabled
                moneyFlag = true;
            }

            if (moneyFlag) {
                plugin.getWgHandler().claim(chunk, player.getUniqueId());

                player.sendMessage(lm.getString("Commands.Claim.success")
                        .replace("%chunk%", OwnedLand.getName(chunk))
                        .replace("%world%", chunk.getWorld().getName()));

                OwnedLand.highlightLand(player, CParticle.VILLAGERHAPPY);

                if (plugin.getConfig().getBoolean("Homes.enable")) {
                    if (plugin.getPlayerManager().get(player.getUniqueId()).getHome() == null)
                        Bukkit.dispatchCommand(player, "ll sethome");
                }

                if (plugin.getConfig().getBoolean("CommandSettings.Claim.enableDelimit")) {
                    delimit(chunk);
                }

                plugin.getMapManager().updateAll();

                LandPostClaimEvent postEvent = new LandPostClaimEvent(player, plugin.getLand(chunk));
                Bukkit.getPluginManager().callEvent(postEvent);
            }
        }
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

