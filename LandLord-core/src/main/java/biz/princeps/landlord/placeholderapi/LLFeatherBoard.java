package biz.princeps.landlord.placeholderapi;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IWorldGuardManager;

public class LLFeatherBoard {

    private IWorldGuardManager wg;

    public LLFeatherBoard(ILandLord pl) {
        this.wg = pl.getWGManager();
        registerPlaceholders(pl);
    }

    private void registerPlaceholders(ILandLord pl) {

        PlaceholderAPI.registerPlaceholder(pl.getPlugin(), "ownedlands",
                e -> {
                    int landcount = wg.getRegionCount(e.getPlayer().getUniqueId());
                    return String.valueOf(landcount);
                }
        );
        PlaceholderAPI.registerPlaceholder(pl.getPlugin(), "claims",
                e -> {
                    IPlayer player1 = pl.getPlayerManager().get(e.getPlayer().getUniqueId());
                    if (player1 == null) {
                        pl.getLogger().warning("A placeholder is trying to load %ll_claims% before async loading of the " +
                                "player has finished!!! Use FinishedLoadingPlayerEvent!");
                        return "NaN";
                    }
                    return String.valueOf(player1.getClaims());
                }
        );
        PlaceholderAPI.registerPlaceholder(pl.getPlugin(), "currentLandOwner",
                e -> {
                    IOwnedLand region = wg.getRegion(e.getPlayer().getLocation());
                    if (region != null) {
                        return region.getOwnersString();
                    }
                    return "";
                }
        );
        PlaceholderAPI.registerPlaceholder(pl.getPlugin(), "currentLandName",
                e -> wg.getLandName(e.getPlayer().getLocation().getChunk())
        );
        PlaceholderAPI.registerPlaceholder(pl.getPlugin(), "nextLandPrice",
                e -> String.valueOf(pl.getCostManager().calculateCost(e.getPlayer().getUniqueId()))
        );
        PlaceholderAPI.registerPlaceholder(pl.getPlugin(), "currentLandRefund",
                e -> {
                    int regionCount = wg.getRegionCount(e.getPlayer().getUniqueId());
                    return String.valueOf(pl.getCostManager().calculateCost(regionCount - 1) *
                            pl.getConfig().getDouble("Payback"));
                }
        );
    }
}
