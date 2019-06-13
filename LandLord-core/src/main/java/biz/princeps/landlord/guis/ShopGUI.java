package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMaterialsManager;
import biz.princeps.landlord.manager.cost.ClaimsCostManager;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 25/7/17
 */
public class ShopGUI extends AbstractGUI {

    private ILandLord pl;
    private ClaimsCostManager costManager;

    private IMaterialsManager mats;

    public ShopGUI(ILandLord pl, Player player, String title) {
        super(player, 18, title);
        this.pl = pl;
        this.costManager = new ClaimsCostManager(pl);
        this.mats = pl.getMaterialsManager();
    }

    @Override
    protected void create() {
        int claims = pl.getPlayerManager().get(player.getUniqueId()).getClaims();
        int max = getMaxLimitPerm();

        ItemStack playerHead = mats.getPlayerHead(player.getUniqueId());
        this.setIcon(0, new Icon(playerHead));
        this.setIcon(9, new Icon(new ItemStack(Material.BARRIER)));
    }


    private int getMaxLimitPerm() {
        List<Integer> limitlist = pl.getConfig().getIntegerList("limits");

        if (!player.hasPermission("landlord.limit.override")) {
            // We need to find out, whats the maximum limit.x permission is a player has

            int highestAllowedLandCount = -1;
            for (Integer integer : limitlist) {
                if (player.hasPermission("landlord.limit." + integer)) {
                    highestAllowedLandCount = integer;
                }
            }

            return highestAllowedLandCount;
        }
        return Integer.MAX_VALUE;
    }
}
