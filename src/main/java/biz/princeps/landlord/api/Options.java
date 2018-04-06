package biz.princeps.landlord.api;

import biz.princeps.landlord.Landlord;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/6/18
 */
public class Options {

    private static Landlord plugin = Landlord.getInstance();

    public static boolean enabled_inactiveBuyUp() {
        return plugin.getConfig().getBoolean("BuyUpInactive.enable");
    }

    public static boolean isVaultEnabled() {
        return plugin.getConfig().getBoolean("Economy.enable") && plugin.getVault() != null;
    }

    public static boolean enabled_borders() {
        return plugin.getConfig().getBoolean("Borders.enable");
    }

    public static boolean enabled_map() {
        return plugin.getConfig().getBoolean("Map.enable");
    }

    public static boolean enabled_shop() {
        return plugin.getConfig().getBoolean("Shop.enable");
    }

    public static boolean enabled_homes() {
        return plugin.getConfig().getBoolean("Homes.enable");
    }

}
