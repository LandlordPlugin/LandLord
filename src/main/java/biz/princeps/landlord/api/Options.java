package biz.princeps.landlord.api;

import biz.princeps.landlord.Landlord;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/6/18
 */
public class Options {

    private static Landlord plugin = Landlord.getInstance();

    public static boolean isInactiveClaimingEnabled() {
        return plugin.getConfig().getBoolean("BuyUpInactive.enable");
    }

    public static boolean isVaultEnabled(){
        return plugin.getConfig().getBoolean("Economy.enable") && plugin.getVault() != null;
    }


}
