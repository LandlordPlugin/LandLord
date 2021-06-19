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
        final int maxClaimPermission = pl.getPlayerManager().getMaxClaimPermission(player);
        final int landcount = wg.getRegionCount(player.getUniqueId());
        final IOwnedLand region = wg.getRegion(player.getLocation());

        switch (s) {
            case "ownedlands":
                return String.valueOf(landcount);

            case "claims":
                final IPlayer iPlayer = pl.getPlayerManager().get(player.getUniqueId());
                if (iPlayer == null) {
                    pl.getLogger().warning("A placeholder is trying to load %ll_claims% before async loading of the " +
                            "player has finished! Use FinishedLoadingPlayerEvent!");
                    return "NaN";
                }
                return String.valueOf(iPlayer.getClaims());

            case "currentLandOwner":
                if (region != null) {
                    return region.getOwnersString();
                }
                return "∅";

            case "currentLandMembers":
                if (region != null) {
                    return region.getMembersString();
                }
                return "∅";

            case "currentLandName":
                return wg.getLandName(player.getLocation().getChunk());

            case "nextLandPrice":
                return String.valueOf(pl.getCostManager().calculateCost(player.getUniqueId()));

            case "currentLandRefund":
                final int regionCount = wg.getRegionCount(player.getUniqueId());
                return String.valueOf(pl.getCostManager().calculateCost(regionCount - 1) * pl.getConfig().getDouble(
                        "Payback"));

            case "maxLimitPermission":
                return String.valueOf(maxClaimPermission);

            case "remainingFreeLands":
                final int freelands = pl.getConfig().getInt("Freelands");

                if (landcount <= freelands) {
                    return String.valueOf((Math.min(maxClaimPermission, freelands)) - landcount);
                } else {
                    return "0";
                }

            default:
                return null;
        }
    }

}
