package biz.princeps.landlord.guis;

import biz.princeps.landlord.Landlord;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spatium on 25.07.17.
 */
public class ShopGUI extends AbstractGUI {

    private Landlord pl = Landlord.getInstance();
    private List<String> rawList = pl.getConfig().getStringList("Shop.extras");
    private List<Buyable> claims;

    public ShopGUI(Player player, String title) {
        super(player, 18, title);
        claims = new ArrayList<>();

        for (String s : rawList) {
            String[] splitted = s.split(":");
            int number = -1;
            double price = -1;
            Material material = Material.AIR;
            try {
                material = Material.valueOf(splitted[0]);
                number = Integer.parseInt(splitted[1]);
                price = Double.parseDouble(splitted[2]);
            } catch (NumberFormatException e) {
                pl.getLogger().warning("Your landlord config contains an illegal statement: " + s);
            }

            if (number > 0 && price > 0 && material != Material.AIR) {
                claims.add(new Buyable(number, price, material));
            }
        }

        setSize((int) Math.ceil((double) rawList.size() / 9.0) * 9 + 9);
    }

    @Override
    protected void create() {
        int i = 0;
        for (Buyable buyable : claims) {
            List<String> listraw = pl.getLangManager().getStringList("Shop.item.lore");
            List<String> list = new ArrayList<>();
            for (String s : listraw) {
                s = s.replace("%number%", buyable.amount + "").replace("%cost%", buyable.price + "");
                list.add(s);
            }
            double cost = buyable.price;
            setIcon(i, new Icon(new ItemStack(buyable.mat))
                    .setName(pl.getLangManager().getRawString("Shop.item.header").replace("%number%", buyable.amount + ""))
                    .setLore(list)
                    .addClickAction((p) -> {
                                if (!player.hasPermission("landlord.limit.override")) {
                                    // int regionCount = pl.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegionCountOfPlayer(pl.getWgHandler().getWG().wrapPlayer(player));
                                    int claimcount = pl.getPlayerManager().get(player.getUniqueId()).getClaims();
                                    List<Integer> limitlist = pl.getConfig().getIntegerList("limits");

                                    int highestAllowedLandCount = -1;
                                    for (Integer integer : limitlist) {
                                        if (claimcount + buyable.amount <= integer)
                                            if (player.hasPermission("landlord.limit." + integer)) {
                                                highestAllowedLandCount = integer;
                                            }
                                    }
                                    if (claimcount + buyable.amount > highestAllowedLandCount) {
                                        player.sendMessage(pl.getLangManager().getString("Shop.notAllowed"));
                                        p.closeInventory();
                                        return;
                                    }
                                }


                                if (pl.getVaultHandler().hasBalance(p.getUniqueId(), cost)) {
                                    pl.getVaultHandler().take(p.getUniqueId(), cost);
                                    pl.getPlayerManager().get(p.getUniqueId()).addClaims(buyable.amount);
                                    p.sendMessage(pl.getLangManager().getString("Shop.success")
                                            .replace("%number%", buyable.amount + "")
                                            .replace("%cost%", pl.getVaultHandler().format(cost)));
                                    p.closeInventory();
                                } else {
                                    p.sendMessage(pl.getLangManager().getString("Shop.notEnoughMoney")
                                            .replace("%number%", buyable.amount + "")
                                            .replace("%cost%", pl.getVaultHandler().format(cost)));
                                    p.closeInventory();
                                }
                            }
                    ));
            i++;
        }

        setIcon((int) Math.ceil((double) rawList.size() / 9.0) * 9 + 8, new Icon(new ItemStack(Material.BARRIER)).
                setName(ChatColor.RED + "Close").addClickAction(HumanEntity::closeInventory));
    }

    class Buyable {
        int amount;
        double price;
        Material mat;

        public Buyable(int amount, double price, Material mat) {
            this.amount = amount;
            this.price = price;
            this.mat = mat;
        }
    }
}
