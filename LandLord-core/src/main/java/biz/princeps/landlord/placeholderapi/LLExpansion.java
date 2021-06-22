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
        final int landCount = wg.getRegionCount(player.getUniqueId());
        final IOwnedLand region = wg.getRegion(player.getLocation());

        switch (s) {
            case "ownedLands":
                return String.valueOf(landCount);

            case "claims":
                final IPlayer iPlayer = pl.getPlayerManager().get(player.getUniqueId());
                if (iPlayer == null) {
                    pl.getLogger().warning("A placeholder is trying to load %landlord_claims% before async loading of the " +
                            "player has finished! Use FinishedLoadingPlayerEvent!");
                    return "NaN";
                }
                return String.valueOf(iPlayer.getClaims());

            case "remainingClaims":
                final int freeClaims = pl.getConfig().getInt("Claims.free");

                if (landCount <= freeClaims) {
                    final IPlayer iPlayer2 = pl.getPlayerManager().get(player.getUniqueId());
                    if (iPlayer2 == null) {
                        pl.getLogger().warning("A placeholder is trying to load %landlord_remainingClaims% before async loading of the " +
                                "player has finished! Use FinishedLoadingPlayerEvent!");
                        return "NaN";
                    }
                    return String.valueOf(iPlayer2.getClaims() - landCount);
                } else {
                    return "0";
                }

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
                final int freeLands = pl.getConfig().getInt("Freelands");

                if (landCount <= freeLands) {
                    return String.valueOf((Math.min(maxClaimPermission, freeLands)) - landCount);
                } else {
                    return "0";
                }

            default:
                return null;
        }
    }

}
