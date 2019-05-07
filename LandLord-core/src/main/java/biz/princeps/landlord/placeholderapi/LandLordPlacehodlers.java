package biz.princeps.landlord.placeholderapi;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.persistent.LPlayer;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class LandLordPlacehodlers extends EZPlaceholderHook {

    private Landlord pl;

    public LandLordPlacehodlers(Landlord plugin) {
        super(plugin.getPluginInstance(), "ll");
        this.pl = plugin;
    }

    /**
     * Tries to replace in a string s placeholders of landlord.
     * It is looking for %ll_seebelow%
     *
     * @return a nice string
     */
    @Override
    public String onPlaceholderRequest(Player player, String s) {
        switch (s) {

            case "ownedlands":
                int landcount = pl.getWgproxy().getRegionCount(player.getUniqueId());
                return String.valueOf(landcount);

            case "claims":
                LPlayer player1 = pl.getPlayerManager().get(player.getUniqueId());
                if (player1 == null) {
                    pl.getLogger().warning("A placeholder is trying to load %ll_claims% before async loading of the player has finished!!! Use FinishedLoadingPlayerEvent!");
                    return "NaN";
                }
                return String.valueOf(player1.getClaims());

            case "currentLandOwner":
                IOwnedLand region = pl.getWgproxy().getRegion(player.getLocation());
                if (region != null) {
                    return region.getOwnersString();
                }

            case "currentLandName":
                return pl.getWgproxy().getLandName(player.getLocation().getChunk());

            case "nextLandPrice":
                return String.valueOf(pl.getCostManager().calculateCost(player.getUniqueId()));

            case "currentLandRefund":
                int regionCount = pl.getWgproxy().getRegionCount(player.getUniqueId());
                return String.valueOf(pl.getCostManager().calculateCost(regionCount - 1) * pl.getConfig().getDouble("Payback"));

        }
        return null;
    }
}
