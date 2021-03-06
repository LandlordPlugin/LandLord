package biz.princeps.landlord.placeholderapi;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IWorldGuardManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class LLExpansion extends PlaceholderExpansion {

    private final ILandLord pl;
    private final IWorldGuardManager wg;

    public LLExpansion(ILandLord pl) {
        this.pl = pl;
        this.wg = pl.getWGManager();
    }

    @Override
    public String getIdentifier() {
        return "LandLord";
    }

    @Override
    public String getPlugin() {
        return pl.getPlugin().getName();
    }

    @Override
    public String getAuthor() {
        return String.valueOf(pl.getPlugin().getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return pl.getPlugin().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if (player == null) {
            return null;
        }
        int maxClaimPermission = pl.getPlayerManager().getMaxClaimPermission(player);
        int landcount = wg.getRegionCount(player.getUniqueId());
        IOwnedLand region = wg.getRegion(player.getLocation());

        switch (s) {
            case "ownedlands":
                return String.valueOf(landcount);

            case "claims":
                IPlayer player1 = pl.getPlayerManager().get(player.getUniqueId());
                if (player1 == null) {
                    pl.getLogger().warning("A placeholder is trying to load %ll_claims% before async loading of the " +
                            "player has finished!!! Use FinishedLoadingPlayerEvent!");
                    return "NaN";
                }
                return String.valueOf(player1.getClaims());

            case "currentLandOwner":
                if (region != null) {
                    return region.getOwnersString();
                }
                return "∅";

            case "currentLandName":
                return wg.getLandName(player.getLocation().getChunk());

            case "nextLandPrice":
                return String.valueOf(pl.getCostManager().calculateCost(player.getUniqueId()));

            case "currentLandRefund":
                int regionCount = wg.getRegionCount(player.getUniqueId());
                return String.valueOf(pl.getCostManager().calculateCost(regionCount - 1) * pl.getConfig().getDouble(
                        "Payback"));

            case "maxLimitPermission":
                return String.valueOf(maxClaimPermission);

            case "remainingFreeLands":
                int freelands = pl.getConfig().getInt("Freelands");

                if (landcount <= freelands) {
                    return String.valueOf((Math.min(maxClaimPermission, freelands)) - landcount);
                } else {
                    return "0";
                }
        }

        return null;
    }

}
