package biz.princeps.landlord.commands.claiming;

import biz.princeps.landlord.api.events.LandClaimEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Offers;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.crossversion.CParticle;
import biz.princeps.lib.gui.ConfirmationGUI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

        boolean flag = false;
        // Money stuff
        if (plugin.isVaultEnabled()) {
            Offers offer = plugin.getPlayerManager().getOffer(landname);
            if (offer != null && plugin.isVaultEnabled()) {
                // Player 2 player sale
                if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), offer.getPrice())) {

                    ConfirmationGUI confirm = new ConfirmationGUI(player, pr.getName(), (player1, icon) -> {
                        plugin.getVaultHandler().take(player.getUniqueId(), offer.getPrice());
                        plugin.getVaultHandler().give(offer.getSeller(), offer.getPrice());

                        plugin.getPlayerManager().removeOffer(offer.getLandname());

                        pr.getWGLand().getOwners().clear();
                        pr.getWGLand().getOwners().addPlayer(player.getUniqueId());

                        player.sendMessage(lm.getString("Commands.Claim.success")
                                .replace("%chunk%", OwnedLand.getName(chunk))
                                .replace("%world%", chunk.getWorld().getName()));

                        OwnedLand.highlightLand(player, CParticle.VILLAGERHAPPY);
                        plugin.getMapManager().updateAll();

                        player.closeInventory();
                    }, (player12, ic2) -> {
                        player.sendMessage(lm.getString("Commands.Claim.aborted"));
                        player.closeInventory();
                        return;
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
                flag = true;
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
            flag = true;
        }

        if (flag) {
            plugin.getWgHandler().claim(chunk, player.getUniqueId());

            // Run task later, to give wg time to register the land properly ?? No clue why this doesnt work
            new BukkitRunnable(){

                @Override
                public void run() {
                    OwnedLand landy = plugin.getWgHandler().getRegion(chunk);
                    LandClaimEvent event = new LandClaimEvent(player, landy);
                    Bukkit.getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        plugin.getWgHandler().unclaim(landy.getWorld(), landy.getName());
                        return;
                    }

                    player.sendMessage(lm.getString("Commands.Claim.success")
                            .replace("%chunk%", OwnedLand.getName(chunk))
                            .replace("%world%", chunk.getWorld().getName()));

                    OwnedLand.highlightLand(player, CParticle.VILLAGERHAPPY);

                    if (plugin.getConfig().getBoolean("Homes.enable")) {
                        if (plugin.getPlayerManager().get(player.getUniqueId()).getHome() == null)
                            Bukkit.dispatchCommand(player, "ll sethome");
                    }

                    if (plugin.getConfig().getBoolean("CommandSettings.Claim.enableDelimit"))
                        delimit(chunk);

                    plugin.getMapManager().updateAll();
                }
            }.runTaskLater(plugin, 5L);
        }
    }

    private void delimit(Chunk chunk) {
        String s = plugin.getConfig().getString("CommandSettings.Claim.delimitMaterial");
        Material mat = Material.getMaterial(s);

        World w = chunk.getWorld();
        if (mat != null) {
            for (int i = 0; i < 16; i++) {
                w.getHighestBlockAt(chunk.getBlock(i, 0, 0).getLocation().add(0, 1, 0)).setType(mat);
                w.getHighestBlockAt(chunk.getBlock(0, 0, i).getLocation().add(0, 1, 0f)).setType(mat);
                w.getHighestBlockAt(chunk.getBlock(15 - i, 0, 15).getLocation().add(0, 1, 0)).setType(mat);
                w.getHighestBlockAt(chunk.getBlock(15, 0, 15 - i).getLocation().add(0, 1, 0)).setType(mat);
            }

        } else {
            plugin.getLogger().warning("Invalid delimiting Material detected!! Value: " + s);
        }
    }


}

