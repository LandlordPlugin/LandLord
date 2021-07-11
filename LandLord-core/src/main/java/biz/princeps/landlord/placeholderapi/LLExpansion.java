package biz.princeps.landlord.placeholderapi;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IWorldGuardManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LLExpansion extends PlaceholderExpansion {

    private final ILandLord pl;
    private final IWorldGuardManager wg;

    private final Cache<String, String> cache;
    private final Cache<UUID, Integer> maxClaimPermissionCache;

    public LLExpansion(ILandLord pl) {
        this.pl = pl;
        this.wg = pl.getWGManager();

        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(500, TimeUnit.MILLISECONDS)
                .build();
        this.maxClaimPermissionCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String getIdentifier() {
        return "landlord";
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
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (player == null) {
            return null;
        }

        try {
            return cache.get(player.getName() + "_" + placeholder, () ->
                    parsePlaceholder(player, placeholder));
        } catch (ExecutionException e) {
            pl.getLogger().log(Level.SEVERE, "Could not parse placeholder: " + placeholder + " for " + player.getName() + "!", e);
        }
        return null;
    }

    private String parsePlaceholder(Player player, String placeholder) {
        switch (placeholder) {
            // The amount of claimed lands
            case "owned_lands":

                return String.valueOf(wg.getRegionCount(player.getUniqueId()));

            // The amounf of purchased claims
            case "claims":
                IPlayer iPlayer = pl.getPlayerManager().get(player.getUniqueId());
                if (iPlayer == null) {
                    pl.getLogger().warning("A placeholder is trying to load %landlord_claims% before async loading of the " +
                            "player has finished! Use FinishedLoadingPlayerEvent!");
                    return "NaN";
                }
                return String.valueOf(iPlayer.getClaims());

            // Remaining claims. Difference between owned_lands and claims
            case "remaining_claims":
                IPlayer iPlayer2 = pl.getPlayerManager().get(player.getUniqueId());
                if (iPlayer2 == null) {
                    pl.getLogger().warning("A placeholder is trying to load %landlord_remainingClaims% before async loading of the " +
                            "player has finished! Use FinishedLoadingPlayerEvent!");
                    return "NaN";
                }
                return String.valueOf(iPlayer2.getClaims() - wg.getRegionCount(player.getUniqueId()));

            // Owner of the current land
            case "current_land_owner":
                IOwnedLand region = wg.getRegion(player.getLocation());
                if (region != null && region.getOwner() != null) {
                    return region.getOwnersString();
                }
                return "∅";

            // Members of the current land
            case "current_land_members":
                String members;
                IOwnedLand region2 = wg.getRegion(player.getLocation());
                if (region2 != null && !(members = region2.getMembersString()).isEmpty()) {
                    return members;
                }
                return "∅";

            // name of the current land
            case "current_land_name":
                return wg.getLandName(player.getLocation().getChunk());

            // Price of the next land which will be purchased
            case "next_land_price":
                return String.valueOf(pl.getCostManager().calculateCost(player.getUniqueId()));

            // Amount which will be given when this land will be sold
            case "current_land_refund":
                int regionCount = wg.getRegionCount(player.getUniqueId());
                return String.valueOf(pl.getCostManager().calculateCost(regionCount - 1) * pl.getConfig().getDouble(
                        "Payback"));

            // Number of the permission, which indicates the amount of possible claims
            case "max_claim_permission":
                return String.valueOf(getMaxClaimPermission(player));

            // Number of free claims a user can still claim.
            case "remaining_free_lands":
                int landCount = wg.getRegionCount(player.getUniqueId());
                int freeLands = pl.getConfig().getInt("Freelands");

                if (landCount <= freeLands) {
                    return String.valueOf((Math.min(getMaxClaimPermission(player), freeLands)) - landCount);
                }
                return "0";

            default:
                return "";
        }
    }

    private int getMaxClaimPermission(Player player) {
        try {
            return maxClaimPermissionCache.get(player.getUniqueId(), () ->
                    pl.getPlayerManager().getMaxClaimPermission(player));
        } catch (ExecutionException e) {
            pl.getLogger().log(Level.SEVERE, "Could not get maxClaimPermission for " + player.getName() + "!", e);
        }
        return -1;
    }

}
