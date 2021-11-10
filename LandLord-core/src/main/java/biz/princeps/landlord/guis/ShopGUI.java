package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IMaterialsManager;
import biz.princeps.landlord.api.IVaultManager;
import biz.princeps.landlord.manager.cost.ClaimsCostManager;
import biz.princeps.landlord.util.Skulls;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 25/7/17
 */
public class ShopGUI extends AbstractGUI {

    private final ILandLord plugin;
    private final ClaimsCostManager costManager;
    private final ILangManager lm;

    private final IMaterialsManager mats;
    private final IVaultManager vault;

    private int delta = 0;
    private double cost = 0;

    public ShopGUI(ILandLord plugin, Player player, String title) {
        super(plugin.getPlugin(), player, 54, title);
        this.plugin = plugin;
        this.costManager = new ClaimsCostManager(plugin);
        this.mats = plugin.getMaterialsManager();
        this.lm = plugin.getLangManager();
        this.vault = plugin.getVaultManager();
    }

    @Override
    protected void create() {
        int lands = plugin.getWGManager().getRegionCount(player.getUniqueId());
        int claims = plugin.getPlayerManager().get(player.getUniqueId()).getClaims();
        int max = plugin.getPlayerManager().getMaxClaimPermission(player);

        for (int i = 0; i < this.getSize(); i++) {
            Icon placehodler = new Icon(mats.getGreyStainedGlass());
            placehodler.setName(" ");
            this.setIcon(i, placehodler);
        }

        plugin.getLogger().log(Level.INFO, "Cl: " + lands + " | " + claims + "/" + max);

        Icon yourLands = new Icon(new ItemStack(mats.getGrass()));
        yourLands.setName(lm.getRawString("Shop.gui.lands.name"));
        yourLands.setLore(replaceLore(lm.getStringList("Shop.gui.lands.lore"), "%amount%", String.valueOf(lands)));
        this.setIcon(0, yourLands);

        List<Skulls> landsSkulls = Skulls.numToSkull(lands);
        for (int i = 0; i < landsSkulls.size(); i++) {
            Icon icon = new Icon(landsSkulls.get(i).getSkull(plugin));
            icon.setName(" ");
            this.setIcon(8 - landsSkulls.size() + 1 + i, icon);
        }

        ItemStack playerHead = mats.getPlayerHead(player.getUniqueId());
        Icon yourClaims = new Icon(playerHead);
        yourClaims.setName(lm.getRawString("Shop.gui.claims.name"));
        yourClaims.setLore(replaceLore(
                replaceLore(lm.getStringList("Shop.gui.claims.lore"),
                        "%amount%", String.valueOf(claims + delta)),
                "%max%", String.valueOf(max)));
        this.setIcon(9, yourClaims);
        List<Skulls> claimsSkulls = Skulls.numToSkull(claims + delta);
        for (int i = 0; i < claimsSkulls.size(); i++) {
            Icon icon = new Icon(claimsSkulls.get(i).getSkull(plugin));
            icon.setName(" ");
            this.setIcon(17 - claimsSkulls.size() + 1 + i, icon);
        }

        // cost line
        Icon costIcon = new Icon(Skulls.CASH.getSkull(plugin));
        this.setIcon(18, costIcon);
        List<Skulls> costSkulls = Skulls.numToSkull((int) cost);
        for (int i = 0; i < costSkulls.size(); i++) {
            Icon icon = new Icon(costSkulls.get(i).getSkull(plugin));
            icon.setName(" ");
            this.setIcon(26 - costSkulls.size() + 1 + i, icon);
        }

        Icon back1 = new Icon(Skulls.BACK1.getSkull(plugin));
        back1.setName(lm.getRawString("Shop.gui.decrease1"));
        back1.addClickAction((p) -> {
            if (claims + delta - 1 < 0) {
                return;
            }

            if (claims + delta - 1 < lands) {
                return;
            }

            delta -= 1;
            cost -= this.costManager.calculateCost(claims + delta);
            refresh();
        });
        this.setIcon(30, back1);
        Icon back5 = new Icon(Skulls.BACK5.getSkull(plugin));
        back5.setName(lm.getRawString("Shop.gui.decrease5"));
        back5.addClickAction((p) -> {
            if (claims + delta <= 0) {
                return;
            }

            if (claims + delta - 5 <= lands) {
                return;
            }

            if (claims + delta - 5 < 0) {
                cost -= this.costManager.calculateCost(claims + delta, -delta);
                delta = -claims;
            } else {
                cost -= this.costManager.calculateCost(claims + delta, -5);
                delta -= 5;
            }

            refresh();
        });
        this.setIcon(29, back5);
        Icon back10 = new Icon(Skulls.BACK10.getSkull(plugin));
        back10.setName(lm.getRawString("Shop.gui.decrease10"));
        back10.addClickAction((p) -> {
            if (claims + delta <= 0) {
                return;
            }

            if (claims + delta - 10 <= lands) {
                return;
            }

            if (claims + delta - 10 < 0) {
                cost -= this.costManager.calculateCost(claims + delta, -delta);
                delta = -claims;
            } else {
                cost -= this.costManager.calculateCost(claims + delta, -10);
                delta -= 10;
            }
            refresh();
        });
        this.setIcon(28, back10);

        this.setIcon(31, yourClaims);

        Icon forw1 = new Icon(Skulls.FORWARD1.getSkull(plugin));
        forw1.setName(lm.getRawString("Shop.gui.increase1"));
        forw1.addClickAction((p) -> {
            if (claims + delta + 1 > max) {
                return;
            }
            cost += this.costManager.calculateCost(claims + delta);
            delta += 1;

            refresh();
        });
        this.setIcon(32, forw1);
        Icon forw5 = new Icon(Skulls.FORWARD5.getSkull(plugin));
        forw5.setName(lm.getRawString("Shop.gui.increase5"));
        forw5.addClickAction((p) -> {
            if (claims + delta == max) {
                return;
            }

            if (claims + delta + 5 > max) {
                cost += this.costManager.calculateCost(claims + delta, max - claims - delta);
                delta = max - claims;
            } else {
                cost += this.costManager.calculateCost(claims + delta, 5);
                delta += 5;
            }

            refresh();
        });
        this.setIcon(33, forw5);
        Icon forw10 = new Icon(Skulls.FORWARD10.getSkull(plugin));
        forw10.setName(lm.getRawString("Shop.gui.increase10"));
        forw10.addClickAction((p) -> {
            if (claims + delta == max) {
                return;
            }

            if (claims + delta + 10 > max) {
                cost += this.costManager.calculateCost(claims + delta, max - claims - delta);
                delta = max - claims;
            } else {
                cost += this.costManager.calculateCost(claims + delta, 10);
                delta += 10;
            }

            refresh();
        });
        this.setIcon(34, forw10);

        // Transaction control items
        Icon abort = new Icon(Skulls.ABORT.getSkull(plugin));
        abort.setName(lm.getRawString("Shop.gui.abort"));
        abort.addClickAction((p) -> {
            p.closeInventory();
            lm.sendMessage(p, lm.getString(player, "Shop.abort"));
        });
        this.setIcon(45, abort);

        if (vault.getBalance(player) < cost) {
            Icon error = new Icon(Skulls.REDEXCLAMATIONMARK.getSkull(plugin));
            error.setName(lm.getRawString("Shop.gui.error.name"));
            error.setLore(replaceLore(
                    replaceLore(
                            lm.getStringList("Shop.gui.error.lore"), "%cost%", vault.format(cost)),
                    "%own%", "" + vault.format(vault.getBalance(player))
            ));
            error.addClickAction((p) -> lm.sendMessage(player, lm.getString(player, "Shop.notEnoughMoney")
                    .replace("%number%", String.valueOf(delta))
                    .replace("%cost%", vault.format(cost))));
            this.setIcon(53, error);
        } else {

            Icon confirm = new Icon(Skulls.CONFIRM.getSkull(plugin));
            confirm.addClickAction((p) -> {
                if (delta > 0) {
                    vault.take(p, cost);
                    plugin.getPlayerManager().get(p.getUniqueId()).addClaims(delta);
                    lm.sendMessage(p, lm.getString(p, "Shop.successBuy")
                            .replace("%number%", String.valueOf(delta))
                            .replace("%cost%", vault.format(cost)));
                } else {
                    vault.give(p, cost * -1);
                    // delta is negative, so it will subtract
                    plugin.getPlayerManager().get(p.getUniqueId()).addClaims(delta);

                    lm.sendMessage(p, lm.getString(p, "Shop.successSell")
                            .replace("%number%", String.valueOf(-1 * delta))
                            .replace("%cost%", vault.format(-1 * cost)));
                }
                p.closeInventory();

            });
            confirm.setName(lm.getRawString("Shop.gui.confirm.name"));
            confirm.setLore(replaceLore(
                    replaceLore(lm.getStringList("Shop.gui.confirm.lore"),
                            "%delta%", String.valueOf(delta)),
                    "%price%", vault.format(cost)));
            this.setIcon(53, confirm);
        }
    }

    private List<String> replaceLore(List<String> list, String toReplace, String newValue) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(s.replace(toReplace, newValue));
        }
        return newList;
    }
}
