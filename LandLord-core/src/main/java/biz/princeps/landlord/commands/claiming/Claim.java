package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.*;
import biz.princeps.landlord.api.events.LandPostClaimEvent;
import biz.princeps.landlord.api.events.LandPreClaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.commands.homes.SetHome;
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
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/07/17
 */
public class Claim extends LandlordCommand {

    private final boolean overrideConfirmations;
    private final IWorldGuardManager wg;
    private final IVaultManager vault;

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

        //Don't claim outside of the world
        if (!isInsideWorld(player, chunk)) {
            return;
        }

        IOwnedLand ol = wg.getRegion(chunk);
        String landName = wg.getLandName(chunk);
        String confirmcmd = PrincepsLib.getCommandManager().getCommand(Landlordbase.class)
                .getCommandString(Landlordbase.Confirm.class);

        int regionCount = wg.getRegionCount(player.getUniqueId());
        int worldRegionCount = wg.getRegionCount(player.getUniqueId(), player.getWorld());

        // First check, if outer conditions (conditions that are related more to the buyer as individual)
        // Check for hardcap based on permissions
        if (!plugin.getConfig().getBoolean("CommandSettings.Claim.allowOverlap", false) &&
                !wg.canClaim(player, chunk) && !(ol != null && plugin.getPlayerManager().isInactiveSync(ol.getOwner()))) {
            lm.sendMessage(player, lm.getString(player, "Commands.Claim.notAllowed"));
            return;
        }

        if (!hasLimitPermissions(player, regionCount)) {
            return;
        }

        // Check for claims
        if (!hasClaims(player, regionCount)) {
            return;
        }

        // Ckeck for adjacent claims
        if (!isAdjacentLandOwned(player, chunk, worldRegionCount)) {
            return;
        }

        // Check for gap between lands
        if (!isGapBetweenLands(player, chunk)) {
            return;
        }

        LandPreClaimEvent event = new LandPreClaimEvent(player, chunk);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        // is free land
        if (ol == null) {
            double calculatedCost = Options.isVaultEnabled() ? plugin.getCostManager().calculateCost(player.getUniqueId()) : 0;

            String Desc = lm.getString(player, "Commands.Claim.confirmation")
                    .replace("%chunk%", landName)
                    .replace("%price%", vault.format(calculatedCost));

            if (!hasMoney(player, calculatedCost, landName, chunk)) {
                return;
            }

            // if the player des not have enough money, the claim process is terminated before this line.
            if (plugin.getConfig().getBoolean("ConfirmationDialog.onNormalClaim") && !overrideConfirmations) {
                PrincepsLib.getConfirmationManager().draw(player, Desc, Desc,
                        (p) -> {
                            performNormalClaim(player, chunk, calculatedCost, landName);
                            p.closeInventory();
                        },
                        (p) -> {
                            lm.sendMessage(player, lm.getString(player, "Commands.Claim.aborted"));
                            p.closeInventory();
                        }, confirmcmd);
            } else {
                performNormalClaim(player, chunk, calculatedCost, landName);
            }

        } else {
            // either inactive or advertised or not claimable or own land
            if (ol.getPrice() > -1 && Options.isVaultEnabled()) {
                // Advertised land
                double calculatedCost = ol.getPrice();

                String Desc = lm.getString(player, "Commands.Claim.confirmation")
                        .replace("%chunk%", landName)
                        .replace("%price%", vault.format(calculatedCost));

                if (!hasMoney(player, calculatedCost, landName, chunk)) {
                    return;
                }

                // if the player des not have enough money, the claim process is terminated before this line.
                if (plugin.getConfig().getBoolean("ConfirmationDialog.onNormalClaim") && !overrideConfirmations) {
                    PrincepsLib.getConfirmationManager().draw(player, Desc, Desc,
                            (p) -> {
                                performAdvertisedClaim(player, ol);
                                p.closeInventory();
                            },
                            (p) -> {
                                lm.sendMessage(player, lm.getString(player, "Commands.Claim.aborted"));
                                p.closeInventory();
                            }, confirmcmd);
                } else {
                    performAdvertisedClaim(player, ol);
                }

            } else {
                // either inactive or unclaimable or own land
                if (ol.isOwner(player.getUniqueId())) {
                    // cannot buy own land
                    lm.sendMessage(player, lm.getString(player, "Commands.Claim.alreadyClaimed")
                            .replace("%owner%", ol.getOwnersString()));
                    return;
                }

                // inactive or unclaimable
                plugin.getPlayerManager().getOffline(ol.getOwner(), (offline) -> {
                    boolean isInactive = plugin.getPlayerManager().isInactive(offline.getLastSeen());
                    int inactiveDays = plugin.getPlayerManager().getInactiveRemainingDays(offline.getLastSeen());

                    if (isInactive) {
                        // inactive
                        double costForBuyer = plugin.getCostManager().calculateCost(player.getUniqueId());
                        double payBackForInactive = plugin.getCostManager().calculateCost(ol.getOwner());

                        String Desc = lm.getString(player, "Commands.Claim.confirmation")
                                .replace("%chunk%", landName)
                                .replace("%price%", vault.format(costForBuyer));

                        String originalOwner = Bukkit.getOfflinePlayer(ol.getOwner()).getName();

                        if (!hasMoney(player, costForBuyer, landName, chunk)) {
                            return;
                        }

                        if (plugin.getConfig().getBoolean("ConfirmationDialog.onBuyUp") && !overrideConfirmations) {
                            PrincepsLib.getConfirmationManager().draw(player, Desc, Desc,
                                    (p) -> {
                                        handleInactiveClaim(player, ol, costForBuyer, payBackForInactive,
                                                originalOwner);
                                        player.closeInventory();
                                    }, (p) -> {
                                        lm.sendMessage(player, lm.getString(player, "Commands.Claim.aborted"));
                                        player.closeInventory();
                                    }, confirmcmd);

                        } else {
                            handleInactiveClaim(player, ol, costForBuyer, payBackForInactive,
                                    originalOwner);
                        }

                    } else {
                        // unclaimable
                        lm.sendMessage(player, lm.getString(player, "Commands.Claim.notYetInactive")
                                .replace("%owner%", ol.getOwnersString())
                                .replace("%days%", "" + inactiveDays));
                    }
                });
            }
        }
    }

    private boolean hasMoney(Player player, double costForBuyer, String landName, Chunk chunk) {
        if (Options.isVaultEnabled()) {
            if (vault.hasBalance(player, costForBuyer)) {
                // Enough money
                return true;
            } else {
                // not enough money
                lm.sendMessage(player, lm.getString(player, "Commands.Claim.notEnoughMoney")
                        .replace("%money%", vault.format(costForBuyer))
                        .replace("%chunk%", landName)
                        .replace("%location%", wg.formatLocation(chunk))
                );
                return false;
            }
        }
        return true;
    }

    private void handleInactiveClaim(Player player, IOwnedLand ol, double costForBuyer, double payBackForInactive,
                                     String originalOwner) {
        vault.take(player, costForBuyer);
        vault.give(ol.getOwner(), payBackForInactive, player.getWorld());

        ol.replaceOwner(player.getUniqueId());
        lm.sendMessage(player, lm.getString(player, "Commands.Claim.boughtUp")
                .replace("%player%", originalOwner)
                .replace("%price%", vault.format(costForBuyer))
                .replace("%chunk%", ol.getName())
                .replace("%location%", wg.formatLocation(ol.getChunk()))
        );

        ol.highlightLand(player,
                Particle.valueOf(plugin.getConfig().getString("Particles.claim.particle").toUpperCase()));
        plugin.getMapManager().updateAll();

        LandPostClaimEvent postEvent = new LandPostClaimEvent(player, ol, ClaimType.INACTIVE);
        Bukkit.getScheduler().runTask(plugin.getPlugin(), () -> Bukkit.getPluginManager().callEvent(postEvent));
    }


    private void performAdvertisedClaim(Player player, IOwnedLand ol) {
        Chunk chunk = ol.getChunk();

        vault.take(player, ol.getPrice());
        vault.give(ol.getOwner(), ol.getPrice(), player.getWorld());
        Player pp = Bukkit.getPlayer(ol.getOwner());

        ol.replaceOwner(player.getUniqueId());

        lm.sendMessage(player, lm.getString(player, "Commands.Claim.success")
                .replace("%chunk%", ol.getName())
                .replace("%location%", wg.formatLocation(chunk))
                .replace("%world%", chunk.getWorld().getName()));

        if (pp.isOnline()) {
            lm.sendMessage(pp, lm.getString(pp, "Commands.Claim.p2pSuccess")
                    .replace("%player%", player.getName())
                    .replace("%chunk%", ol.getName())
                    .replace("%location%", wg.formatLocation(chunk))
                    .replace("%world%", chunk.getWorld().getName())
                    .replace("%price%", vault.format(ol.getPrice())));
        }

        ol.highlightLand(player,
                Particle.valueOf(plugin.getConfig().getString("Particles.claim.particle").toUpperCase()));
        plugin.getMapManager().updateAll();

        LandPostClaimEvent postEvent = new LandPostClaimEvent(player, ol, ClaimType.ADVERTISED);
        Bukkit.getPluginManager().callEvent(postEvent);
    }

    private void performNormalClaim(Player player, Chunk chunk, double calculatedCost, String landName) {
        if (Options.isVaultEnabled() && calculatedCost > 0) {
            vault.take(player, calculatedCost);
            lm.sendMessage(player, lm.getString(player, "Commands.Claim.moneyTook")
                    .replace("%money%", vault.format(calculatedCost))
                    .replace("%chunk%", landName)
                    .replace("%location%", wg.formatLocation(chunk))
            );
        }

        IOwnedLand claim = wg.claim(chunk, player.getUniqueId());

        lm.sendMessage(player, lm.getString(player, "Commands.Claim.success")
                .replace("%chunk%", claim.getName())
                .replace("%location%", wg.formatLocation(chunk))
                .replace("%world%", chunk.getWorld().getName()));

        if (plugin.getConfig().getBoolean("Particles.claim.enabled"))
            claim.highlightLand(player,
                    Particle.valueOf(plugin.getConfig().getString("Particles.claim.particle").toUpperCase()));

        if (Options.enabled_homes() && plugin.getConfig().getBoolean("Homes.enableAutoSetHome", false)) {
            if (plugin.getPlayerManager().get(player.getUniqueId()).getHome() == null) {
                Bukkit.dispatchCommand(player,
                        PrincepsLib.getCommandManager().getCommand(Landlordbase.class)
                                .getCommandString(SetHome.class).substring(1));
            }
        }

        if (plugin.getConfig().getBoolean("CommandSettings.Claim.enableDelimit")) {
            plugin.getDelimitationManager().delimit(player, chunk);
        }

        plugin.getMapManager().updateAll();

        LandPostClaimEvent postEvent = new LandPostClaimEvent(player, claim, ClaimType.FREE_LAND);
        Bukkit.getPluginManager().callEvent(postEvent);
    }

    private boolean hasLimitPermissions(Player player, int regionCount) {
        final int highestAllowedLandCount = plugin.getPlayerManager().getMaxClaimPermission(player);

        if (regionCount >= highestAllowedLandCount) {
            lm.sendMessage(player, lm.getString(player, "Commands.Claim.hardcap").replace("%regions%",
                    highestAllowedLandCount + ""));
            return false;
        }
        return true;
    }

    private boolean hasClaims(Player player, int regionCount) {
        if (plugin.getConfig().getBoolean("Claims.enable") && Options.isVaultEnabled()) {
            int claims = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();

            if (regionCount >= claims) {
                ComponentBuilder builder = new ComponentBuilder(lm.getString(player, "Commands.Claim.limit")
                        .replace("%regions%", regionCount + "")
                        .replace("%claims%", claims + ""))
                        .color(ChatColor.YELLOW)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, PrincepsLib.getCommandManager()
                                .getCommand(Landlordbase.class).getCommandString(Shop.class)));

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
                IOwnedLand[] surroundings = wg.getSurroundings(chunk);
                for (int i = 1; i < surroundings.length; i++) {
                    IOwnedLand adjLand = surroundings[i];
                    if (adjLand != null) {
                        if (adjLand.isOwner(player.getUniqueId())) {
                            hasNearbyLand = true;
                            break;
                        }
                    }
                }

                if (!hasNearbyLand) {
                    // no nearby land is already claimed => Display error msg
                    lm.sendMessage(player, lm.getString(player, "Commands.Claim.onlyClaimAdjacentChunks")
                            .replace("%land%", wg.getLandName(chunk)));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isGapBetweenLands(Player player, Chunk chunk) {
        if (!plugin.getConfig().getBoolean("CommandSettings.Claim.needsGapBetweenOwners")) {
            return true;
        }
        // Get adjacent lands of the land, which a player wants to claim.
        // Only when all of the 4 adj lands are either owned by the player or are free => allow the claim

        int radius = plugin.getConfig().getInt("CommandSettings.Claim.customGapRadius");

        boolean differentOwner = false;
        Map<Chunk, IOwnedLand> nearbyLands = wg.getNearbyLands(chunk, radius, radius);
        for (IOwnedLand adjLand : nearbyLands.values()) {
            if (adjLand != null) {
                if (!adjLand.isOwner(player.getUniqueId())) {
                    differentOwner = true;
                    break;
                }
            }
        }

        if (differentOwner) {
            // one of the nearby lands is not owned by the player nor its free
            lm.sendMessage(player, lm.getString(player, "Commands.Claim.needsGap")
                    .replace("%land%", wg.getLandName(chunk)));
            return false;
        }
        return true;
    }
}

