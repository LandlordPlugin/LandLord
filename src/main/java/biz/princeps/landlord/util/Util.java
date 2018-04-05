package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 4/5/18
 */
public class Util {

    private static Landlord plugin = Landlord.getInstance();

    public static String formatCash(double money){
        return plugin.isVaultEnabled() ? plugin.getVaultHandler().format(money) : "-1";
    }
}
