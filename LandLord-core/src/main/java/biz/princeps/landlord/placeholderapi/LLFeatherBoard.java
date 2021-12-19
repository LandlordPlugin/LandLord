package biz.princeps.landlord.placeholderapi;

//import be.maximvdw.placeholderapi.PlaceholderAPI;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IWorldGuardManager;

public class LLFeatherBoard {

    private final IWorldGuardManager wg;

    public LLFeatherBoard(ILandLord plugin) {
        this.wg = plugin.getWGManager();
        registerPlaceholders(plugin);
    }

    private void registerPlaceholders(ILandLord plugin) {
        plugin.getLogger().warning("Featherboard Placeholders do not work. Please tell Maxim from Featherboard to take a look at the MVdWPlaceholderAPI repo.");
        /*
        PlaceholderAPI.registerPlaceholder(plugin, "ownedlands",
                e -> {
                    int landcount = wg.getRegionCount(e.getPlayer().getUniqueId());
                    return String.valueOf(landcount);
                }
        );
        PlaceholderAPI.registerPlaceholder(plugin, "claims",
                e -> {
                    IPlayer player1 = plugin.getPlayerManager().get(e.getPlayer().getUniqueId());
                    if (player1 == null) {
                        plugin.getLogger().warning("A placeholder is trying to load %ll_claims% before async loading of the " +
                                "player has finished!!! Use FinishedLoadingPlayerEvent!");
                        return "NaN";
                    }
                    return String.valueOf(player1.getClaims());
                }
        );
        PlaceholderAPI.registerPlaceholder(plugin, "currentLandOwner",
                e -> {
                    IOwnedLand region = wg.getRegion(e.getPlayer().getLocation());
                    if (region != null) {
                        return region.getOwnersString();
                    }
                    return "";
                }
        );
        PlaceholderAPI.registerPlaceholder(plugin, "currentLandName",
                e -> wg.getLandName(e.getPlayer().getLocation())
        );
        PlaceholderAPI.registerPlaceholder(plugin, "nextLandPrice",
                e -> String.valueOf(plugin.getCostManager().calculateCost(e.getPlayer().getUniqueId()))
        );
        PlaceholderAPI.registerPlaceholder(plugin, "currentLandRefund",
                e -> {
                    int regionCount = wg.getRegionCount(e.getPlayer().getUniqueId());
                    return String.valueOf(plugin.getCostManager().calculateCost(regionCount - 1) *
                            plugin.getConfig().getDouble("Payback"));
                }
        );
         */
    }
}
