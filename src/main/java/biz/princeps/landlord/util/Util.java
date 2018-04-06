package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.lib.storage.requests.Conditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/5/18
 */
public class Util {

    private static Landlord plugin = Landlord.getInstance();

    /**
     * Formats a given money double to the vault style with the currency e.g. 100 Dollars
     *
     * @param money the amount which should be formatted
     * @return a formatted string
     */
    public static String formatCash(double money) {
        return Options.isVaultEnabled() ? plugin.getVaultHandler().format(money) : "-1";
    }

    /**
     * Measures if a player is inactive based on the date he was seen the last time.
     * If this date + the timegate is before right now, he is inactive
     *
     * @param lastSeenDate the date the player was last seen
     * @return if the player is inactive or not
     */
    public static boolean isInactive(LocalDateTime lastSeenDate) {
        if (!Options.enabled_inactiveBuyUp()) return false;

        if (lastSeenDate == null) {
            return false;
        }

        int days = plugin.getConfig().getInt("BuyUpInactive.timegate");

        // yes, this guy is inactive
        return lastSeenDate.plusDays(days).isBefore(LocalDateTime.now());
    }


    public static boolean isInactive(UUID id) {
        //TODO add proper async operation!!

        // This might causes lag, but idc
        List<Object> list = plugin.getDatabaseAPI().retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("uuid", id.toString()).create());
        if (list.size() > 0) {
            return isInactive((((LPlayer) list.get(0)).getLastSeen()));
        } else {
            return false;
        }
    }

    public static long getInactiveRemainingDays(UUID owner) {

        //TODO add proper async operation
        long days = plugin.getConfig().getInt("BuyUpInactive.timegate");

        List<Object> list = plugin.getDatabaseAPI().retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("uuid", owner.toString()).create());
        if (list.size() > 0) {
            return days - (Duration.between(LocalDateTime.now(), ((LPlayer) list.get(0)).getLastSeen()).toDays());
        } else {
            return -1;
        }

    }
}
