package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.*;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AManage extends AbstractGUI {

    private List<IOwnedLand> regions;
    private ILangManager lm;
    private ILandLord plugin;
    private int flagPage = 0;

    AManage(ILandLord pl, Player player, String header, List<IOwnedLand> land) {
        super(player, 54, header);
        this.plugin = pl;
        this.regions = land;
        this.lm = plugin.getLangManager();
    }

    AManage(ILandLord pl, Player player, MultiPagedGUI landGui, String header, List<IOwnedLand> land) {
        super(player, Options.getManageSize() + 9, header, landGui);
        this.regions = land;
        this.plugin = pl;
        this.lm = plugin.getLangManager();
    }

    @Override
    protected void create() {
        createFrame();
        createWGFlags();
        createGeneralOptions();
    }

    private void createFrame() {
        List<String> strings = formatList(lm.getStringList("Commands.Manage.info.description"),
                "%land%", regions.get(0).getName());
        Icon info = new Icon(new ItemStack(Material.ITEM_FRAME));
        info.setName(lm.getRawString("Commands.Manage.info.title"));
        info.setLore(strings);
        this.setIcon(0, info);

        Icon friends = new Icon(plugin.getMatProxy().getPlayerHead(player.getUniqueId()));
        friends.setName(lm.getRawString("Commands.Manage.friends.title"));
        friends.setLore(lm.getStringList("Commands.Manage.friends.description"));
        this.setIcon(9, friends);

        Icon everyone = new Icon(new ItemStack(Material.SKULL_ITEM, 1, (short) 1));
        everyone.setName(lm.getRawString("Commands.Manage.everyone.title"));
        everyone.setLore(lm.getStringList("Commands.Manage.everyone.description"));
        this.setIcon(18, everyone);
    }

    private void createWGFlags() {
        IOwnedLand land = regions.get(0);

        List<Icon[]> flags = new ArrayList<>();
        for (ILLFlag flag : land.getFlags()) {
            String flagName = flag.getName();
            if (plugin.getConfig().getBoolean("Manage." + flagName + ".enable") &&
                    player.hasPermission("landlord.player.manage." + flagName)) {
                flags.add(getIcons(flag));
            }
        }

        if (flagPage * 8 > flags.size()) {
            throw new RuntimeException("Invalid page!!");
        }

        for (int i = flagPage * 8; i < flagPage * 8 + 8; i++) {
            if (flags.size() <= i) break;
            Icon[] flagArray = flags.get(i);
            setIcon(i - flagPage * 8 + 1, flagArray[0]);
            setIcon(i - flagPage * 8 + 10, flagArray[1]);
            setIcon(i - flagPage * 8 + 19, flagArray[2]);
        }

        if (flags.size() > 8) {
            // add navigation items
            Icon prev = new Icon(new ItemStack(Material.ARROW));
            prev.addClickAction((p) -> {
                if (flagPage > 0) {
                    flagPage--;
                    refresh();
                }
            });
            setIcon(34, prev);
            Icon next = new Icon(new ItemStack(Material.ARROW));
            next.addClickAction((p) -> {
                if (flagPage * 8 < flags.size()) {
                    flagPage++;
                    refresh();
                }
            });
            setIcon(35, next);
        }

    }

    private Icon[] getIcons(ILLFlag flag) {
        Icon[] icons = new Icon[3];
        String flagname = flag.getName();
        String title = lm.getRawString("Commands.Manage.Allow" + flagname.substring(0, 1).toUpperCase() + flagname.substring(1) + ".title");
        List<String> description = lm.getStringList("Commands.Manage.Allow" + flagname.substring(0, 1).toUpperCase() + flagname.substring(1) + ".description");

        Icon item = new Icon(new ItemStack(flag.getMaterial()));
        item.setLore(description);
        item.setName(title);
        icons[0] = item;

        boolean isFriend = flag.getFriendStatus();
        boolean isAll = flag.getAllStatus();

        Icon friend = new Icon(isFriend ? plugin.getMatProxy().getLimeWool() : plugin.getMatProxy().getRedWool());
        friend.addClickAction((p) -> {
            if (flag.toggleFriends()) {
                refresh();
            }
        });
        friend.setName(isFriend ? lm.getRawString("Commands.Manage.allow") : lm.getRawString("Commands.Manage.deny"));
        icons[1] = friend;

        Icon all = new Icon(isAll ? plugin.getMatProxy().getLimeWool() : plugin.getMatProxy().getRedWool());
        all.addClickAction((p) -> {
            if (flag.toggleAll()) {
                refresh();
            }
        });
        all.setName(isAll ? lm.getRawString("Commands.Manage.allow") : lm.getRawString("Commands.Manage.deny"));
        icons[2] = all;
        return icons;
    }

    private void createGeneralOptions() {

    }

    private List<String> formatList(List<String> list, String toReplace, String newValue) {
        List<String> newList = new ArrayList<>();
        list.forEach(s -> newList.add(s.replace(toReplace, newValue)));
        return newList;
    }

}

