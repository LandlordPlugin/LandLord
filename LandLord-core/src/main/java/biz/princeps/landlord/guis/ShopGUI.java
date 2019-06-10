package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.commands.admin.GiveClaims;
import biz.princeps.landlord.commands.management.Manage;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 25/7/17
 */
public class ShopGUI extends AbstractGUI {

    private ILandLord pl;
    private List<String> rawList;
    private List<Buyable> claims;

    public ShopGUI(ILandLord pl, Player player, String title) {
        super(player, 18, title);
        this.pl = pl;
        this.rawList = pl.getConfig().getStringList("Shop.extras");
        this.claims = new ArrayList<>();

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
            setIcon(i, new Icon(new ItemStack(buyable.mat))
                    .setName(pl.getLangManager().getRawString("Shop.item.header").replace("%number%", buyable.amount + ""))
                    .setLore(list)
                    .addClickAction((p) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            PrincepsLib.getCommandManager().getCommand(Landlordbase.class)
                                    .getCommandString(GiveClaims.class).substring(1) +
                                    " " + p.getName() + " " + buyable.price + " " + buyable.amount)
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
