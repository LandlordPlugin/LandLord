package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.*;
import biz.princeps.landlord.api.events.LandPostClaimEvent;
import biz.princeps.landlord.api.events.LandPreClaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
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
    private IWorldGuardManager wg;
    private IVaultManager vault;

    public Claim(ILandLord pl, boolean overrideConfirmations) {
        super(pl, pl.getConfig().getString("CommandSettings.Claim.name"),
                pl.getConfig().getString("CommandSettings.Claim.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Claim.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Claim.aliases")));

        this.overrideConfirmations = overrideConfirmations;
        this.wg = plugin.getWGManager();
        this.vault = plugin.getVaultManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isPlayer()) {
            Chunk chunk = properties.getPlayer().getWorld().getChunkAt(properties.getPlayer().getLocation());
            onClaim(properties.getPlayer(), chunk);
        }
    }

    public void onClaim(Player player, Chunk chunk) {
        if (isDisabledWorld(player)) {
            return;
        }

        IOwnedLand ol = wg.getRegion(chunk);
        String landName = wg.getLandName(chunk);
        String confirmcmd = "/" + plugin.getConfig().getString("CommandSettings.Main.name") + " confirm";

        Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
            boolean inactive = ol != null && plugin.getPlayerManager().isInactiveSync(ol.getOwner());
            int inactiveDays = ol == null ? -1 : plugin.getPlayerManager().getInactiveRemainingDaysSync(ol.getOwner());


            // Check if there is an overlapping wg-region
            if (!wg.canClaim(player, chunk)) {
                if (ol == null || !inactive) {
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

                if (!inactive) {
                    lm.sendMessage(player, lm.getString("Commands.Claim.notYetInactive")
                            .replace("%owner%", ol.getOwnersString())
                            .replace("%days%", "" + inactiveDays));
                    return;
                }
            }

            int regionCount = wg.getRegionCount(player.getUniqueId());
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

            //In 1.14.x, an event can't be fired by an async thread
            Bukkit.getScheduler().runTask(plugin.getPlugin(), () -> {
                Bukkit.getPluginManager().callEvent(event);
            });

            if (event.isCancelled()) {
                return;
            }

            // Money stuff
            if (Options.isVaultEnabled()) {
                if (ol != null && inactive) {
                    // Inactive sale
                    if (!handleInactiveSale(player, ol, chunk, confirmcmd)) {
                        // unsuccessful
                        return;
                    }
                }

                if (ol != null && ol.getPrice() > -1) {
                    // Player 2 player sale
                    if (!handlePlayer2PlayerSell(player, ol, chunk, confirmcmd)) {
                        // unsuccessful
                        return;
                    }
                } else {
                    // Normal sale
                    if (!handleNormalSell(player, landName, chunk, confirmcmd)) {
                        return;
                    }
                }
            }
            performClaim(player, chunk);
        });
    }


    private void performClaim(Player player, Chunk chunk) {
        IOwnedLand claim = wg.claim(chunk, player.getUniqueId());

        lm.sendMessage(player, lm.getString("Commands.Claim.success")
                .replace("%chunk%", claim.getName())
                .replace("%location%", wg.formatLocation(chunk))
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
            plugin.getDelimitationManager().delimit(player, chunk);
        }

        plugin.getMapManager().updateAll();

        LandPostClaimEvent postEvent = new LandPostClaimEvent(player, claim);

        //In 1.14.x, an event can't be fired by an async thread
        Bukkit.getScheduler().runTask(plugin.getPlugin(), () -> {
            Bukkit.getPluginManager().callEvent(postEvent);
        });
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
                lm.sendMessage(player, lm.getString("Commands.Claim.hardcap").replace("%regions%",
                        highestAllowedLandCount + ""));
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

                plugin.getUtilsManager().sendBasecomponent(player, builder.create());
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
                            .replace("%land%", wg.getLandName(chunk)));
                    return false;
                }
            }
        }
        return true;
    }

    private IOwnedLand[] getSurroundings(Chunk chunk) {
        World world = chunk.getWorld();
        IOwnedLand[] adjLands = new IOwnedLand[4];
        adjLands[0] = wg.getRegion(world.getChunkAt(chunk.getX() + 1, chunk.getZ()));
        adjLands[1] = wg.getRegion(world.getChunkAt(chunk.getX() - 1, chunk.getZ()));
        adjLands[2] = wg.getRegion(world.getChunkAt(chunk.getX(), chunk.getZ() + 1));
        adjLands[3] = wg.getRegion(world.getChunkAt(chunk.getX(), chunk.getZ() - 1));
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
                        .replace("%land%", wg.getLandName(chunk)));
                return false;
            }
        }
        return true;
    }

    private boolean handleInactiveSale(Player player, IOwnedLand ol, Chunk chunk, String confirmcmd) {
        double costForBuyer = plugin.getCostManager().calculateCost(player.getUniqueId());
        double payBackForInactive = plugin.getCostManager().calculateCost(ol.getOwner());
        String originalOwner = Bukkit.getOfflinePlayer(ol.getOwner()).getName();

        if (vault.hasBalance(player.getUniqueId(), costForBuyer)) {

            if (plugin.getConfig().getBoolean("ConfirmationDialog.onBuyUp")) {
                String sellDesc = ol.getName() + " | " + vault.format(costForBuyer);
                String chatDesc = lm.getString("Commands.Claim.confirmation");

                PrincepsLib.getConfirmationManager().draw(player, sellDesc, chatDesc,
                        (p) -> {
                            handleInactiveSell(player, ol, costForBuyer, payBackForInactive,
                                    originalOwner, chunk);
                            player.closeInventory();
                        }, (p) -> {
                            lm.sendMessage(player, lm.getString("Commands.Claim.aborted"));
                            player.closeInventory();
                        }, confirmcmd);

            } else {
                handleInactiveSell(player, ol, costForBuyer, payBackForInactive, originalOwner, chunk);
            }
            return true;
        } else {
            // Not enough money
            lm.sendMessage(player, lm.getString("Commands.Claim.notEnoughMoney")
                    .replace("%money%", vault.format(costForBuyer))
                    .replace("%chunk%", ol.getName())
                    .replace("%location%", wg.formatLocation(chunk))
            );
            return false;
        }
    }

    private void handleInactiveSell(Player player, IOwnedLand ol, double costForBuyer, double payBackForInactive,
                                    String originalOwner, Chunk chunk) {
        vault.take(player.getUniqueId(), costForBuyer);
        vault.give(ol.getOwner(), payBackForInactive);

        ol.replaceOwner(player.getUniqueId());
        lm.sendMessage(player, lm.getString("Commands.Claim.boughtUp")
                .replace("%player%", originalOwner)
                .replace("%price%", vault.format(costForBuyer))
                .replace("%chunk%", ol.getName())
                .replace("%location%", wg.formatLocation(chunk))
        );

        ol.highlightLand(player, Particle.VILLAGER_HAPPY);
        plugin.getMapManager().updateAll();
    }

    private boolean handlePlayer2PlayerSell(Player player, IOwnedLand ol, Chunk chunk, String confirmcmd) {
        if (vault.hasBalance(player.getUniqueId(), ol.getPrice())) {

            String sellDesc = ol.getName() + " | " + vault.format(ol.getPrice());
            String chatDesc = lm.getString("Commands.Claim.confirmation");

            PrincepsLib.getConfirmationManager().draw(player, sellDesc, chatDesc, (p) -> {
                vault.take(player.getUniqueId(), ol.getPrice());
                vault.give(ol.getOwner(), ol.getPrice());
                Player pp = Bukkit.getPlayer(ol.getOwner());

                ol.replaceOwner(player.getUniqueId());

                lm.sendMessage(player, lm.getString("Commands.Claim.success")
                        .replace("%chunk%", ol.getName())
                        .replace("%location%", wg.formatLocation(chunk))
                        .replace("%world%", chunk.getWorld().getName()));

                if (pp.isOnline()) {
                    lm.sendMessage(pp, lm.getString("Commands.Claim.p2pSuccess")
                            .replace("%player%", p.getName())
                            .replace("%chunk%", ol.getName())
                            .replace("%location%", wg.formatLocation(chunk))
                            .replace("%world%", chunk.getWorld().getName())
                            .replace("%price%", vault.format(ol.getPrice())));
                }

                ol.highlightLand(player, Particle.VILLAGER_HAPPY);
                plugin.getMapManager().updateAll();

                player.closeInventory();
            }, (p) -> {
                lm.sendMessage(player, lm.getString("Commands.Claim.aborted"));
                player.closeInventory();
            }, confirmcmd);
            return true;
        } else {
            // Not enough money
            lm.sendMessage(player, lm.getString("Commands.Claim.notEnoughMoney")
                    .replace("%money%", vault.format(ol.getPrice()))
                    .replace("%chunk%", ol.getName())
                    .replace("%location%", wg.formatLocation(chunk))
            );
            return false;
        }
    }


    private boolean handleNormalSell(Player player, String landName, Chunk chunk, String confirmcmd) {
        double calculatedCost = plugin.getCostManager().calculateCost(player.getUniqueId());
        if (vault.hasBalance(player.getUniqueId(), calculatedCost)) {
            String guiDesc = landName + " | " + vault.format(calculatedCost);
            String chatDesc = lm.getString("Commands.Claim.confirmation")
                    .replace("%chunk%", landName)
                    .replace("%location%", wg.formatLocation(chunk))
                    .replace("%price%", vault.format(calculatedCost));

            if (plugin.getConfig().getBoolean("ConfirmationDialog.onNormalClaim") && !overrideConfirmations) {
                PrincepsLib.getConfirmationManager().draw(player, guiDesc, chatDesc,
                        (p) -> {
                            vault.take(player.getUniqueId(), calculatedCost);
                            if (calculatedCost > 0)
                                lm.sendMessage(player, lm.getString("Commands.Claim.moneyTook")
                                        .replace("%money%", vault.format(calculatedCost))
                                        .replace("%chunk%", landName));
                            performClaim(player, chunk);
                            p.closeInventory();
                        },
                        (p) -> {
                            lm.sendMessage(player, lm.getString("Commands.Claim.aborted"));
                            p.closeInventory();
                        }, confirmcmd);
            } else {
                vault.take(player.getUniqueId(), calculatedCost);
                if (calculatedCost > 0)
                    lm.sendMessage(player, lm.getString("Commands.Claim.moneyTook")
                            .replace("%money%", vault.format(calculatedCost))
                            .replace("%chunk%", landName)
                            .replace("%location%", wg.formatLocation(chunk))
                    );
                performClaim(player, chunk);
            }

            return false;
        } else {
            // NOT ENOUGH MONEY
            lm.sendMessage(player, lm.getString("Commands.Claim.notEnoughMoney")
                    .replace("%money%", vault.format(calculatedCost))
                    .replace("%chunk%", landName)
                    .replace("%location%", wg.formatLocation(chunk))
            );
            return false;
        }
    }
}

