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
}
